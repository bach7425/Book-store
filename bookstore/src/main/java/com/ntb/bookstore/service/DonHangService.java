package com.ntb.bookstore.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ntb.bookstore.dto.PageResponse;
import com.ntb.bookstore.dto.DonHang.ChiTietDonHangResponse;
import com.ntb.bookstore.dto.DonHang.DonHangResponse;
import com.ntb.bookstore.dto.MaGiamGia.KiemTraMaGiamGiaResponse;
import com.ntb.bookstore.entity.ChiTietDonHang;
import com.ntb.bookstore.entity.DiaChi;
import com.ntb.bookstore.entity.DonHang;
import com.ntb.bookstore.entity.GioHang;
import com.ntb.bookstore.entity.MaGiamGia;
import com.ntb.bookstore.entity.NguoiDung;
import com.ntb.bookstore.entity.TonKho;
import com.ntb.bookstore.entity.enums.VaiTro;
import com.ntb.bookstore.entity.ThanhToan;
import com.ntb.bookstore.entity.enums.LoaiThongBao;
import com.ntb.bookstore.entity.enums.LoaiGiamGia;
import com.ntb.bookstore.entity.enums.PhuongThucThanhToan;
import com.ntb.bookstore.entity.enums.TrangThaiDonHang;
import com.ntb.bookstore.entity.enums.TrangThaiMaGiamGia;
import com.ntb.bookstore.entity.enums.TrangThaiThanhToan;
import com.ntb.bookstore.exception.HethongLoiException;
import com.ntb.bookstore.exception.KhongCoDuLieuException;
import com.ntb.bookstore.repository.DiaChiRepository;
import com.ntb.bookstore.repository.DonHangRepository;
import com.ntb.bookstore.repository.GioHangRepository;
import com.ntb.bookstore.repository.MaGiamGiaRepository;
import com.ntb.bookstore.repository.NguoiDungRepository;
import com.ntb.bookstore.repository.TonKhoRepository;
import com.ntb.bookstore.repository.ChiTietGioHangRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class DonHangService {
    private static final BigDecimal PHI_VAN_CHUYEN_MAC_DINH = BigDecimal.valueOf(30000L);

    private final DonHangRepository donHangRepository;
    private final GioHangRepository gioHangRepository;
    private final ChiTietGioHangRepository chiTietGioHangRepository;
    private final DiaChiRepository diaChiRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final TonKhoRepository tonKhoRepository;
    private final MaGiamGiaRepository maGiamGiaRepository;
    private final ThongBaoService thongBaoService;

    public DonHangResponse taoDonHang(Long maNguoiDung, Long maDiaChi, String phuongThucThanhToan,
            String maGiamGiaCode) {

        NguoiDung nguoiDung = nguoiDungRepository.findById(maNguoiDung)
                .orElseThrow(() -> new KhongCoDuLieuException("Không tìm thấy người dùng", maNguoiDung));

        DiaChi diaChi = diaChiRepository.findById(maDiaChi)
                .orElseThrow(() -> new KhongCoDuLieuException("Không tìm thấy địa chỉ", maDiaChi));
        if (!diaChi.getNguoiDung().getMaNguoiDung().equals(maNguoiDung)) {
            throw new HethongLoiException("Địa chỉ không thuộc về người dùng");
        }
        PhuongThucThanhToan phuongThucThanhToan2 = null;
        try {
            phuongThucThanhToan2 = PhuongThucThanhToan
                    .valueOf(phuongThucThanhToan.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new HethongLoiException("Phương thức thanh toán không hợp lệ");
        }
        GioHang gioHang = gioHangRepository.findByNguoiDungMaNguoiDung(maNguoiDung)
                .orElseThrow(() -> new HethongLoiException("Giỏ hàng không tồn tại "));

        if (gioHang.getChiTietGioHangs().isEmpty()) {
            throw new HethongLoiException("Giỏ hàng trống");
        }

        BigDecimal tongTien = BigDecimal.ZERO;
        for (var item : gioHang.getChiTietGioHangs()) {
            TonKho tonKho = tonKhoRepository.findBySachMaSach(item.getSach().getMaSach())
                    .orElse(TonKho.builder().sach(item.getSach()).soLuong(0).build());
            if (tonKho.getSoLuong() <= 0) {
                chiTietGioHangRepository.delete(item);
                throw new HethongLoiException("Sách " + item.getSach().getTenSach() + " đã hết hàng");
            }
            if (tonKho.getSoLuong() < item.getSoLuong()) {
                item.setSoLuong(tonKho.getSoLuong());
                chiTietGioHangRepository.save(item);
                throw new HethongLoiException("Số lượng tồn kho không đủ cho sách " + item.getSach().getTenSach());
            }
            tongTien = tongTien.add(item.getSach().getGia().multiply(BigDecimal.valueOf(item.getSoLuong())));
        }

        ApDungMaGiamGiaResult apDungMaGiamGiaResult = apDungMaGiamGia(maGiamGiaCode, tongTien);
        BigDecimal phiVanChuyen = PHI_VAN_CHUYEN_MAC_DINH;
        BigDecimal soTienGiam = apDungMaGiamGiaResult.soTienGiam();
        BigDecimal tongTienThanhToan = tongTien.subtract(soTienGiam).add(phiVanChuyen);

        DonHang donHang = DonHang.builder()
                .nguoiDung(nguoiDung)
                .diaChi(diaChi)
                .tongTien(tongTien)
                .phiVanChuyen(phiVanChuyen)
                .soTienGiam(soTienGiam)
                .tongTienThanhToan(tongTienThanhToan)
                .maGiamGia(apDungMaGiamGiaResult.maGiamGia())
                .trangThai(TrangThaiDonHang.CHO_XU_LY)
                .build();
        donHang = donHangRepository.save(donHang);

        MaGiamGia maGiamGia = apDungMaGiamGiaResult.maGiamGia();
        if (maGiamGia != null) {
            maGiamGia.setSoLuongDaDung(maGiamGia.getSoLuongDaDung() + 1);
        }

        for (var item : gioHang.getChiTietGioHangs()) {
            TonKho tonKho = tonKhoRepository.findBySachMaSach(item.getSach().getMaSach())
                    .orElse(TonKho.builder().sach(item.getSach()).soLuong(0).build());
            tonKho.setSoLuong(tonKho.getSoLuong() - item.getSoLuong());
            tonKhoRepository.save(tonKho);

            ChiTietDonHang chiTietDonHang = ChiTietDonHang.builder()
                    .donHang(donHang)
                    .sach(item.getSach())
                    .soLuong(item.getSoLuong())
                    .donGia(item.getSach().getGia())
                    .thanhTien(item.getSach().getGia().multiply(BigDecimal.valueOf(item.getSoLuong())))
                    .build();
            donHang.getChiTietDonHangs().add(chiTietDonHang);
        }

        ThanhToan thanhToan = ThanhToan.builder()
                .donHang(donHang)
                .phuongThuc(phuongThucThanhToan2 != null ? phuongThucThanhToan2 : PhuongThucThanhToan.TIEN_MAT)
                .trangThai(TrangThaiThanhToan.CHO_THANH_TOAN)
                .soTien(donHang.getTongTienThanhToan())
                .ngayTao(LocalDateTime.now())
                .build();
        donHang.setThanhToan(thanhToan);
        donHangRepository.save(donHang);

        chiTietGioHangRepository.deleteByGioHangMaGioHang(gioHang.getMaGioHang());
        gioHang.getChiTietGioHangs().clear();

        return toResponse(donHang);
    }

    private ApDungMaGiamGiaResult apDungMaGiamGia(String maGiamGiaCode, BigDecimal tongTien) {
        if (maGiamGiaCode == null || maGiamGiaCode.isBlank()) {
            return new ApDungMaGiamGiaResult(null, BigDecimal.ZERO);
        }

        MaGiamGia maGiamGia = maGiamGiaRepository.findByMaCodeIgnoreCase(maGiamGiaCode.trim())
                .orElseThrow(() -> new KhongCoDuLieuException("Không tìm thấy mã giảm giá"));
        LocalDateTime now = LocalDateTime.now();
        if (maGiamGia.getTrangThai() != TrangThaiMaGiamGia.HOAT_DONG) {
            throw new HethongLoiException("Mã giảm giá không hoạt động");
        }
        if (now.isBefore(maGiamGia.getNgayBatDau()) || now.isAfter(maGiamGia.getNgayKetThuc())) {
            throw new HethongLoiException("Mã giảm giá đã hết hạn hoặc chưa bắt đầu");
        }
        if (maGiamGia.getSoLuongDaDung() >= maGiamGia.getSoLuong()) {
            throw new HethongLoiException("Mã giảm giá đã hết lượt sử dụng");
        }
        BigDecimal donToiThieu = maGiamGia.getDonToiThieu() == null ? BigDecimal.ZERO : maGiamGia.getDonToiThieu();
        if (tongTien.compareTo(donToiThieu) < 0) {
            throw new HethongLoiException("Đơn hàng chưa đạt giá trị tối thiểu để áp dụng mã giảm giá");
        }

        BigDecimal soTienGiam;
        if (maGiamGia.getLoaiGiam() == LoaiGiamGia.PHAN_TRAM) {
            soTienGiam = tongTien.multiply(maGiamGia.getGiaTri())
                    .divide(BigDecimal.valueOf(100L), 2, RoundingMode.HALF_UP);
            if (maGiamGia.getGiamToiDa() != null && soTienGiam.compareTo(maGiamGia.getGiamToiDa()) > 0) {
                soTienGiam = maGiamGia.getGiamToiDa();
            }
        } else {
            soTienGiam = maGiamGia.getGiaTri();
        }

        if (soTienGiam.compareTo(tongTien) > 0) {
            soTienGiam = tongTien;
        }
        return new ApDungMaGiamGiaResult(maGiamGia, soTienGiam.setScale(2, RoundingMode.HALF_UP));
    }

    public KiemTraMaGiamGiaResponse kiemTraMaGiamGia(String maGiamGiaCode, BigDecimal tongTien) {
        if (tongTien == null || tongTien.compareTo(BigDecimal.ZERO) < 0) {
            throw new HethongLoiException("Tổng tiền không hợp lệ");
        }
        ApDungMaGiamGiaResult ketQua = apDungMaGiamGia(maGiamGiaCode, tongTien);
        MaGiamGia maGiamGia = ketQua.maGiamGia();
        BigDecimal soTienGiam = ketQua.soTienGiam();
        BigDecimal tongTienThanhToan = tongTien.subtract(soTienGiam).add(PHI_VAN_CHUYEN_MAC_DINH);
        return KiemTraMaGiamGiaResponse.builder()
                .maGiamGia(maGiamGia.getMaCode())
                .loaiGiam(maGiamGia.getLoaiGiam().name())
                .giaTri(maGiamGia.getGiaTri())
                .soTienGiam(soTienGiam)
                .phiVanChuyen(PHI_VAN_CHUYEN_MAC_DINH)
                .tongTien(tongTien)
                .tongTienThanhToan(tongTienThanhToan)
                .thongBao("Áp dụng mã giảm giá thành công")
                .build();
    }

    public PageResponse<DonHangResponse> danhSachDonHang(Long maNguoiDung, String status, int page, int size,
            String baseUrl) {
        Pageable pageable = PageRequest.of(page, size);
        NguoiDung nguoiDung = nguoiDungRepository.findById(maNguoiDung)
                .orElseThrow(() -> new KhongCoDuLieuException("Không tìm thấy người dùng", maNguoiDung));
        Page<DonHang> donHangPage;
        if (status == null || status.isBlank()) {
            donHangPage = donHangRepository.findByNguoiDung(nguoiDung, pageable);
        } else {
            TrangThaiDonHang trangThaiDonHang = TrangThaiDonHang.valueOf(status.toUpperCase());
            donHangPage = donHangRepository.findByNguoiDungAndTrangThai(nguoiDung, trangThaiDonHang, pageable);
        }
        return new PageResponse<>(donHangPage.map(this::toResponse), baseUrl);
    }

    public PageResponse<DonHangResponse> lichSuDonHang(Long maNguoiDung, int page, int size, String baseUrl) {
        Pageable pageable = PageRequest.of(page, size);
        NguoiDung nguoiDung = nguoiDungRepository.findById(maNguoiDung)
                .orElseThrow(() -> new KhongCoDuLieuException("Không tìm thấy người dùng", maNguoiDung));
        Page<DonHang> donHangPage = donHangRepository.findByNguoiDung(nguoiDung, pageable);
        return new PageResponse<>(donHangPage.map(this::toResponse), baseUrl);
    }

    public PageResponse<DonHangResponse> danhSachDonHangQuanTri(String trangThai, int page, int size, String baseUrl) {
        Pageable pageable = PageRequest.of(page, size);
        Page<DonHang> donHangPage;
        if (trangThai == null || trangThai.isBlank()) {
            donHangPage = donHangRepository.findAllForQuanTri(pageable);
        } else {
            TrangThaiDonHang trangThaiDonHang = TrangThaiDonHang.valueOf(trangThai.toUpperCase());
            donHangPage = donHangRepository.findByTrangThaiForQuanTri(trangThaiDonHang, pageable);
        }
        return new PageResponse<>(donHangPage.map(this::toResponse), baseUrl);
    }

    public DonHangResponse chiTietDonHang(Long maNguoiDung, Long maDonHang) {
        DonHang donHang = donHangRepository.findByIdWithChiTiet(maDonHang)
                .orElseThrow(() -> new KhongCoDuLieuException("Không tìm thấy đơn hàng", maDonHang));
        NguoiDung nguoiDung = nguoiDungRepository.findById(maNguoiDung)
                .orElseThrow(() -> new KhongCoDuLieuException("Không tìm thấy người dùng", maNguoiDung));
        if (!donHang.getNguoiDung().getMaNguoiDung().equals(nguoiDung.getMaNguoiDung())
                && !nguoiDung.getVaiTro().equals(VaiTro.QUAN_TRI_VIEN)) {
            throw new HethongLoiException("Bạn không có quyền xem đơn hàng này");
        }
        return toResponse(donHang);
    }

    public DonHangResponse capNhatTrangThaiDonHang(Long maDonHang, TrangThaiDonHang newStatus) {
        DonHang donHang = donHangRepository.findById(maDonHang)
                .orElseThrow(() -> new KhongCoDuLieuException("Không tìm thấy đơn hàng", maDonHang));
        if (donHang.getTrangThai() == TrangThaiDonHang.DA_HUY) {
            throw new HethongLoiException("Đơn hàng đã hủy, không thể cập nhật tiếp");
        }
        if (!kiemTraChuyenTrangThai(donHang.getTrangThai(), newStatus)) {
            throw new HethongLoiException("Chuyển trạng thái không hợp lệ");
        }
        donHang.setTrangThai(newStatus);
        if (newStatus == TrangThaiDonHang.DA_GIAO) {
            danhDauDaThanhToanKhiDaGiao(donHang);
        }
        donHangRepository.save(donHang);
        thongBaoService.guiChoNguoiDung(
                donHang.getNguoiDung(),
                "Cap nhat trang thai don hang",
                "Don hang #" + donHang.getMaDonHang() + " da duoc cap nhat sang trang thai " + newStatus.name(),
                LoaiThongBao.DON_HANG,
                "/don-hang/" + donHang.getMaDonHang());
        return toResponse(donHang);
    }

    public DonHangResponse huyDonHang(Long maNguoiDung, Long maDonHang) {
        DonHang donHang = donHangRepository.findById(maDonHang)
                .orElseThrow(() -> new KhongCoDuLieuException("Khong tim thay don hang", maDonHang));
        if (!donHang.getNguoiDung().getMaNguoiDung().equals(maNguoiDung)) {
            throw new HethongLoiException("Ban khong co quyen huy don hang nay");
        }
        if (donHang.getTrangThai() != TrangThaiDonHang.CHO_XU_LY
                && donHang.getTrangThai() != TrangThaiDonHang.DA_XAC_NHAN) {
            throw new HethongLoiException("Chi co the huy don hang cho xu ly hoac da xac nhan");
        }

        for (ChiTietDonHang chiTiet : donHang.getChiTietDonHangs()) {
            TonKho tonKho = tonKhoRepository.findBySachMaSach(chiTiet.getSach().getMaSach())
                    .orElse(TonKho.builder().sach(chiTiet.getSach()).soLuong(0).build());
            tonKho.setSoLuong(tonKho.getSoLuong() + chiTiet.getSoLuong());
            tonKhoRepository.save(tonKho);
        }
        MaGiamGia maGiamGia = donHang.getMaGiamGia();
        if (maGiamGia != null && maGiamGia.getSoLuongDaDung() > 0) {
            maGiamGia.setSoLuongDaDung(maGiamGia.getSoLuongDaDung() - 1);
        }

        donHang.setTrangThai(TrangThaiDonHang.DA_HUY);
        donHangRepository.save(donHang);
        thongBaoService.guiChoNguoiDung(
                donHang.getNguoiDung(),
                "Huy don hang thanh cong",
                "Don hang #" + donHang.getMaDonHang() + " da duoc huy",
                LoaiThongBao.DON_HANG,
                "/don-hang/" + donHang.getMaDonHang());
        return toResponse(donHang);
    }

    public DonHangResponse thanhToanDonHang(Long maNguoiDung, Long maDonHang) {
        DonHang donHang = donHangRepository.findById(maDonHang)
                .orElseThrow(() -> new KhongCoDuLieuException("Không tìm thấy đơn hàng", maDonHang));
        if (!donHang.getNguoiDung().getMaNguoiDung().equals(maNguoiDung)) {
            throw new HethongLoiException("Bạn không có quyền thanh toán đơn hàng này");
        }
        if (donHang.getTrangThai() == TrangThaiDonHang.DA_HUY) {
            throw new HethongLoiException("Đơn hàng đã hủy, không thể thanh toán");
        }
        ThanhToan thanhToan = donHang.getThanhToan();
        if (thanhToan == null) {
            throw new HethongLoiException("Đơn hàng chưa có thông tin thanh toán");
        }
        if (thanhToan.getTrangThai() == TrangThaiThanhToan.DA_THANH_TOAN) {
            throw new HethongLoiException("Đơn hàng đã được thanh toán");
        }
        if (thanhToan.getTrangThai() == TrangThaiThanhToan.THAT_BAI) {
            throw new HethongLoiException("Thanh toán đơn hàng đã thất bại");
        }

        danhDauDaThanhToan(thanhToan);
        donHangRepository.save(donHang);
        thongBaoService.guiChoNguoiDung(
                donHang.getNguoiDung(),
                "Thanh toan thanh cong",
                "Don hang #" + donHang.getMaDonHang() + " da duoc thanh toan thanh cong",
                LoaiThongBao.DON_HANG,
                "/don-hang/" + donHang.getMaDonHang());
        return toResponse(donHang);
    }

    private boolean kiemTraChuyenTrangThai(TrangThaiDonHang current, TrangThaiDonHang next) {
        if (current == null)
            return next == TrangThaiDonHang.CHO_XU_LY;
        return switch (current) {
            case CHO_XU_LY -> next == TrangThaiDonHang.DA_XAC_NHAN || next == TrangThaiDonHang.DA_HUY;
            case DA_XAC_NHAN -> next == TrangThaiDonHang.DANG_GIAO;
            case DANG_GIAO -> next == TrangThaiDonHang.DA_GIAO;
            case DA_GIAO, DA_HUY -> false;
        };
    }

    private void danhDauDaThanhToanKhiDaGiao(DonHang donHang) {
        if (donHang.getThanhToan() == null) {
            return;
        }
        danhDauDaThanhToan(donHang.getThanhToan());
    }

    private void danhDauDaThanhToan(ThanhToan thanhToan) {
        thanhToan.setTrangThai(TrangThaiThanhToan.DA_THANH_TOAN);
        if (thanhToan.getThoiGianThanhToan() == null) {
            thanhToan.setThoiGianThanhToan(LocalDateTime.now());
        }
    }

    public DonHangResponse toResponse(DonHang donHang) {
        NguoiDung nguoiDung = donHang.getNguoiDung();
        DiaChi diaChi = donHang.getDiaChi();
        ThanhToan thanhToan = donHang.getThanhToan();
        List<ChiTietDonHangResponse> items = donHang.getChiTietDonHangs().stream()
                .map(item -> ChiTietDonHangResponse.builder()
                        .maSach(item.getSach().getMaSach())
                        .tenSach(item.getSach().getTenSach())
                        .soLuong(item.getSoLuong())
                        .donGia(item.getDonGia())
                        .thanhTien(item.getThanhTien())
                        .build())
                .collect(Collectors.toList());
        return DonHangResponse.builder()
                .maDonHang(donHang.getMaDonHang())
                .maNguoiDung(nguoiDung == null ? null : nguoiDung.getMaNguoiDung())
                .tenKhachHang(nguoiDung == null ? null : nguoiDung.getHoVaTen())
                .emailKhachHang(nguoiDung == null ? null : nguoiDung.getEmail())
                .soDienThoaiKhachHang(nguoiDung == null ? null : nguoiDung.getSoDienThoai())
                .nguoiNhan(diaChi == null ? null : diaChi.getNguoiNhan())
                .soDienThoaiNhan(diaChi == null ? null : diaChi.getSoDienThoai())
                .diaChiGiaoHang(diaChi == null ? null : diaChi.getDiaChiChiTiet())
                .trangThai(donHang.getTrangThai().name())
                .tongTien(donHang.getTongTien())
                .phiVanChuyen(donHang.getPhiVanChuyen())
                .soTienGiam(donHang.getSoTienGiam())
                .tongTienThanhToan(donHang.getTongTienThanhToan())
                .maGiamGia(donHang.getMaGiamGia() != null ? donHang.getMaGiamGia().getMaCode() : null)
                .phuongThucThanhToan(thanhToan == null ? null : thanhToan.getPhuongThuc().name())
                .trangThaiThanhToan(thanhToan == null ? null : thanhToan.getTrangThai().name())
                .soTienThanhToan(thanhToan == null ? null : thanhToan.getSoTien())
                .ngayTao(thanhToan == null ? null : thanhToan.getNgayTao())
                .thoiGianThanhToan(thanhToan == null ? null : thanhToan.getThoiGianThanhToan())
                .items(items)
                .build();
    }

    private record ApDungMaGiamGiaResult(MaGiamGia maGiamGia, BigDecimal soTienGiam) {
    }
}
