package com.ntb.bookstore.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ntb.bookstore.dto.PageResponse;
import com.ntb.bookstore.dto.DanhGia.DanhGiaResponse;
import com.ntb.bookstore.entity.DanhGia;
import com.ntb.bookstore.entity.NguoiDung;
import com.ntb.bookstore.entity.Sach;
import com.ntb.bookstore.entity.enums.LoaiThongBao;
import com.ntb.bookstore.entity.enums.TrangThaiDanhGia;
import com.ntb.bookstore.entity.enums.VaiTro;
import com.ntb.bookstore.exception.HethongLoiException;
import com.ntb.bookstore.exception.KhongCoDuLieuException;
import com.ntb.bookstore.repository.DanhGiaRepository;
import com.ntb.bookstore.repository.DonHangRepository;
import com.ntb.bookstore.repository.NguoiDungRepository;
import com.ntb.bookstore.repository.SachRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class DanhGiaService {
        private final DanhGiaRepository danhGiaRepository;
        private final SachRepository sachRepository;
        private final NguoiDungRepository nguoiDungRepository;
        private final DonHangRepository donHangRepository;
        private final ThongBaoService thongBaoService;

        public PageResponse<DanhGiaResponse> layDanhGiaSach(Long maSach, Long maNguoiDung,
                        int page, int size, String baseUrl) {
                Sach sach = sachRepository.findById(maSach)
                                .orElseThrow(() -> new KhongCoDuLieuException("Không tìm thấy sách", maSach));
                Pageable pageable = PageRequest.of(page, size);
                Page<DanhGia> danhGiaPage;
                if (maNguoiDung != null) {
                        NguoiDung nguoiDung = nguoiDungRepository.findById(maNguoiDung)
                                        .orElseThrow(() -> new KhongCoDuLieuException("Không tìm thấy người dùng",
                                                        maNguoiDung));
                        if (nguoiDung.getVaiTro().equals(VaiTro.QUAN_TRI_VIEN)) {
                                danhGiaPage = danhGiaRepository.findBySach(sach, pageable);
                        } else {
                                danhGiaPage = danhGiaRepository.findDanhGiaHienThiChoNguoiDung(sach,
                                                TrangThaiDanhGia.DA_DUYET, nguoiDung, pageable);
                        }
                } else {
                        danhGiaPage = danhGiaRepository.findBySachAndTrangThai(sach, TrangThaiDanhGia.DA_DUYET,
                                        pageable);
                }
                return new PageResponse<>(danhGiaPage.map(this::toResponse), baseUrl);
        }

        public PageResponse<DanhGiaResponse> danhSachDanhGiaQuanTri(String trangThai, int page, int size,
                        String baseUrl) {
                Pageable pageable = PageRequest.of(page, size);
                Page<DanhGia> danhGiaPage;
                if (trangThai == null || trangThai.isBlank()) {
                        danhGiaPage = danhGiaRepository.findAll(pageable);
                } else {
                        try {
                                TrangThaiDanhGia trangThaiDanhGia = TrangThaiDanhGia.valueOf(trangThai.toUpperCase());
                                danhGiaPage = danhGiaRepository.findByTrangThai(trangThaiDanhGia, pageable);
                        } catch (IllegalArgumentException e) {
                                throw new HethongLoiException("Trạng thái đánh giá không hợp lệ: " + trangThai);
                        }
                }
                return new PageResponse<>(danhGiaPage.map(this::toResponse), baseUrl);
        }

        public DanhGiaResponse themDanhGia(Long maNguoiDung, Long maSach, Integer soSao, String noiDung) {
                kiemTraSoSao(soSao);
                NguoiDung nguoiDung = nguoiDungRepository.findById(maNguoiDung)
                                .orElseThrow(() -> new KhongCoDuLieuException("Không tìm thấy người dùng",
                                                maNguoiDung));
                Sach sach = sachRepository.findById(maSach)
                                .orElseThrow(() -> new KhongCoDuLieuException("Không tìm thấy sách", maSach));

                boolean daMua = donHangRepository.findByNguoiDung(nguoiDung).stream()
                                .flatMap(donHang -> donHang.getChiTietDonHangs().stream())
                                .anyMatch(chiTiet -> chiTiet.getSach().getMaSach().equals(maSach));
                if (!daMua) {
                        throw new HethongLoiException("Chỉ người dùng đã mua sách mới được đánh giá");
                }

                if (danhGiaRepository.findByNguoiDungAndSach(nguoiDung, sach).isPresent()) {
                        throw new HethongLoiException("Bạn đã đánh giá sách này rồi");
                }

                DanhGia danhGia = DanhGia.builder()
                                .nguoiDung(nguoiDung)
                                .sach(sach)
                                .soSao(soSao)
                                .noiDung(noiDung)
                                .trangThai(TrangThaiDanhGia.CHO_DUYET)
                                .phanHoi(null)
                                .build();
                danhGia = danhGiaRepository.save(danhGia);
                thongBaoService.guiChoTatCaQuanTriVien(
                                "Có đánh giá chờ duyệt",
                                "Sách " + sach.getTenSach() + " có đánh giá mới cần duyệt",
                                LoaiThongBao.DANH_GIA,
                                "/quan-tri/danh-gia");
                return toResponse(danhGia);
        }

        public DanhGiaResponse capNhatDanhGia(Long maNguoiDung, Long maDanhGia, Integer soSao, String noiDung) {
                kiemTraSoSao(soSao);
                DanhGia danhGia = layDanhGia(maDanhGia);
                NguoiDung nguoiDung = nguoiDungRepository.findById(maNguoiDung)
                                .orElseThrow(() -> new KhongCoDuLieuException("Không tìm thấy người dùng",
                                                maNguoiDung));
                if (danhGia.getNguoiDung().equals(nguoiDung)) {
                        danhGia.setSoSao(soSao);
                        danhGia.setNoiDung(noiDung);
                        danhGia.setTrangThai(TrangThaiDanhGia.CHO_DUYET);
                        danhGia.setPhanHoi(null);
                        DanhGia daLuu = danhGiaRepository.save(danhGia);
                        thongBaoService.guiChoTatCaQuanTriVien(
                                        "Có đánh giá gửi lại",
                                        "Đánh giá sách " + danhGia.getSach().getTenSach()
                                                        + " đã được gửi lại và cần duyệt",
                                        LoaiThongBao.DANH_GIA,
                                        "/quan-tri/danh-gia");
                        return toResponse(daLuu);
                } else {
                        throw new HethongLoiException("Bạn không có quyền cập nhật đánh giá này");
                }

        }

        public void xoaDanhGia(Long maNguoiDung, Long maDanhGia) {
                DanhGia danhGia = layDanhGia(maDanhGia);
                NguoiDung nguoiDung = nguoiDungRepository.findById(maNguoiDung)
                                .orElseThrow(() -> new KhongCoDuLieuException("Không tìm thấy người dùng",
                                                maNguoiDung));
                if (danhGia.getNguoiDung().equals(nguoiDung) || nguoiDung.getVaiTro().equals(VaiTro.QUAN_TRI_VIEN)) {
                        danhGiaRepository.delete(danhGia);
                } else {
                        throw new HethongLoiException("Bạn không có quyền xóa đánh giá này");
                }
        }

        public DanhGiaResponse duyetDanhGia(Long maDanhGia, String phanHoi) {
                return capNhatTrangThaiDanhGia(maDanhGia, TrangThaiDanhGia.DA_DUYET, phanHoi);
        }

        public DanhGiaResponse tuChoiDanhGia(Long maDanhGia, String phanHoi) {
                return capNhatTrangThaiDanhGia(maDanhGia, TrangThaiDanhGia.TU_CHOI, phanHoi);
        }

        private DanhGiaResponse capNhatTrangThaiDanhGia(Long maDanhGia, TrangThaiDanhGia trangThai, String phanHoi) {
                DanhGia danhGia = layDanhGia(maDanhGia);
                danhGia.setTrangThai(trangThai);
                danhGia.setPhanHoi(phanHoi);
                danhGia = danhGiaRepository.save(danhGia);
                String hanhDong = trangThai == TrangThaiDanhGia.DA_DUYET ? "được duyệt" : "bị từ chối";
                String noiDung = "Đánh giá của bạn cho sách " + danhGia.getSach().getTenSach() + " đã " + hanhDong;
                if (phanHoi != null && !phanHoi.isBlank()) {
                        noiDung = noiDung + ". Phản hồi: " + phanHoi;
                }
                thongBaoService.guiChoNguoiDung(
                                danhGia.getNguoiDung(),
                                "Cập nhật đánh giá",
                                noiDung,
                                LoaiThongBao.DANH_GIA,
                                "/danh-gia/" + danhGia.getMaDanhGia());
                return toResponse(danhGia);
        }

        private DanhGia layDanhGia(Long maDanhGia) {
                return danhGiaRepository.findById(maDanhGia)
                                .orElseThrow(() -> new KhongCoDuLieuException("Không tìm thấy đánh giá", maDanhGia));
        }

        private void kiemTraSoSao(Integer soSao) {
                if (soSao == null || soSao < 1 || soSao > 5) {
                        throw new HethongLoiException("Số sao phải nằm trong khoảng 1 đến 5");
                }
        }

        private DanhGiaResponse toResponse(DanhGia danhGia) {
                return DanhGiaResponse.builder()
                                .maDanhGia(danhGia.getMaDanhGia())
                                .maNguoiDung(danhGia.getNguoiDung().getMaNguoiDung())
                                .maSach(danhGia.getSach().getMaSach())
                                .tenSach(danhGia.getSach().getTenSach())
                                .tenNguoiDung(danhGia.getNguoiDung().getHoVaTen())
                                .anhDaiDienNguoiDung(danhGia.getNguoiDung().getAnhDaiDien())
                                .soSao(danhGia.getSoSao())
                                .noiDung(danhGia.getNoiDung())
                                .trangThai(danhGia.getTrangThai().name())
                                .phanHoi(danhGia.getPhanHoi())
                                .build();
        }
}
