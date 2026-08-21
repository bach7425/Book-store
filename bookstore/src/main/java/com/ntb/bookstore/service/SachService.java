package com.ntb.bookstore.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ntb.bookstore.dto.PageResponse;
import com.ntb.bookstore.dto.Sach.SachResponse;
import com.ntb.bookstore.dto.Sach.TacGiaResponse;
import com.ntb.bookstore.dto.Sach.TheLoaiResponse;
import com.ntb.bookstore.entity.Sach;
import com.ntb.bookstore.entity.TacGia;
import com.ntb.bookstore.entity.TheLoai;
import com.ntb.bookstore.entity.TonKho;
import com.ntb.bookstore.entity.enums.TrangThaiDanhGia;
import com.ntb.bookstore.exception.HethongLoiException;
import com.ntb.bookstore.exception.KhongCoDuLieuException;
import com.ntb.bookstore.repository.SachRepository;
import com.ntb.bookstore.repository.DanhGiaRepository;
import com.ntb.bookstore.repository.TacGiaRepository;
import com.ntb.bookstore.repository.TheLoaiRepository;
import com.ntb.bookstore.repository.TonKhoRepository;
import com.ntb.bookstore.service.AI_tool.RagService;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class SachService {
    private final SachRepository sachRepository;
    private final TacGiaRepository tacGiaRepository;
    private final TheLoaiRepository theLoaiRepository;
    private final TonKhoRepository tonKhoRepository;
    private final DanhGiaRepository danhGiaRepository;
    private final UploadService uploadService;
    private final RagService ragService;

    public PageResponse<SachResponse> danhSachSach(String tuKhoa, Long tacGiaId, Long theLoaiId, BigDecimal giaMin,
            BigDecimal giaMax, String sort, int page, int size, String baseUrl) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).ascending());
        Page<Sach> pageSach = sachRepository.timKiemSach(tuKhoa, tacGiaId, theLoaiId, giaMin, giaMax, pageable);
        return new PageResponse<>(pageSach.map(this::toResponse), baseUrl);
    }

    public SachResponse chiTietSach(Long maSach) {
        Sach sach = sachRepository.findById(maSach)
                .orElseThrow(() -> new KhongCoDuLieuException("Không tìm thấy sách", maSach));
        return toResponse(sach);
    }

    public SachResponse themSach(String tenSach, String moTa, BigDecimal gia, String nhaXuatBan,
            Long maTacGia, List<Long> maTheLoais, Integer soLuongTon) {
        kiemTraGiaVaTonKho(gia, soLuongTon);
        TacGia tacGia = tacGiaRepository.findById(maTacGia)
                .orElseThrow(() -> new KhongCoDuLieuException("Không tìm thấy tác giả", maTacGia));
        List<TheLoai> theLoais = theLoaiRepository.findAllById(maTheLoais);
        if (theLoais.size() != maTheLoais.size()) {
            throw new HethongLoiException("Một số thể loại không tồn tại");
        }
        Sach sach = Sach.builder()
                .tenSach(tenSach)
                .moTa(moTa)
                .gia(gia)
                .nhaXuatBan(nhaXuatBan)
                .tacGia(tacGia)
                .theLoais(theLoais)
                .build();
        Sach saved = sachRepository.save(sach);
        TonKho tonKho = TonKho.builder()
                .sach(saved)
                .soLuong(soLuongTon == null ? 0 : soLuongTon)
                .build();
        tonKhoRepository.save(tonKho);
        ragService.themSachVaoVectorStore(saved);
        return toResponse(saved);
    }

    public SachResponse capNhatSach(Long maSach, String tenSach, String moTa, BigDecimal gia,
            String nhaXuatBan, Long maTacGia, List<Long> maTheLoais, Integer soLuongTon) {
        kiemTraGiaVaTonKho(gia, soLuongTon);
        Sach sach = sachRepository.findById(maSach)
                .orElseThrow(() -> new KhongCoDuLieuException("Không tìm thấy sách", maSach));
        if (tenSach != null && !tenSach.isBlank())
            sach.setTenSach(tenSach);
        if (moTa != null)
            sach.setMoTa(moTa);
        if (gia != null)
            sach.setGia(gia);
        if (nhaXuatBan != null)
            sach.setNhaXuatBan(nhaXuatBan);
        if (maTacGia != null) {
            TacGia tacGia = tacGiaRepository.findById(maTacGia)
                    .orElseThrow(() -> new KhongCoDuLieuException("Không tìm thấy tác giả", maTacGia));
            sach.setTacGia(tacGia);
        }
        if (maTheLoais != null) {
            List<TheLoai> theLoais = theLoaiRepository.findAllById(maTheLoais);
            if (theLoais.size() != maTheLoais.size()) {
                throw new HethongLoiException("Một số thể loại không tồn tại");
            }
            sach.setTheLoais(theLoais);
        }
        if (soLuongTon != null) {
            TonKho tonKho = tonKhoRepository.findBySachMaSach(maSach)
                    .orElse(TonKho.builder().sach(sach).soLuong(0).build());
            tonKho.setSoLuong(soLuongTon);
            tonKhoRepository.save(tonKho);
        }
        return toResponse(sachRepository.save(sach));
    }

    public SachResponse capNhatTonKho(Long maSach, Integer soLuong) {
        if (soLuong == null || soLuong < 0) {
            throw new HethongLoiException("Số lượng tồn kho không được âm");
        }
        Sach sach = sachRepository.findById(maSach)
                .orElseThrow(() -> new KhongCoDuLieuException("Không tìm thấy sách", maSach));
        TonKho tonKho = tonKhoRepository.findBySachMaSach(maSach)
                .orElse(TonKho.builder().sach(sach).soLuong(0).build());
        tonKho.setSoLuong(soLuong);
        tonKhoRepository.save(tonKho);
        return toResponse(sach);
    }

    private void kiemTraGiaVaTonKho(BigDecimal gia, Integer soLuongTon) {
        if (gia != null && gia.compareTo(BigDecimal.ZERO) < 0) {
            throw new HethongLoiException("Giá sách không được âm");
        }
        if (soLuongTon != null && soLuongTon < 0) {
            throw new HethongLoiException("Số lượng tồn kho không được âm");
        }
    }

    public SachResponse capNhatAnhBia(Long maSach, MultipartFile file) {
        Sach sach = sachRepository.findById(maSach)
                .orElseThrow(() -> new KhongCoDuLieuException("Không tìm thấy sách", maSach));
        String duongDanAnh = uploadService.luuAnh(file, "sach");
        sach.setAnhBia(duongDanAnh);
        return toResponse(sachRepository.save(sach));
    }

    public PageResponse<TacGiaResponse> danhSachTacGia(String sort, int page, int size, String baseUrl) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).ascending());
        Page<TacGia> pageTacGia = tacGiaRepository.findAll(pageable);
        return new PageResponse<>(pageTacGia.map(this::toTacGiaResponse), baseUrl);
    }

    public TacGiaResponse themTacGia(String ten, String tieuSu, String anhDaiDien) {
        if (ten == null || ten.isBlank()) {
            throw new HethongLoiException("Tên tác giả không được để trống");
        }
        TacGia tacGia = TacGia.builder().ten(ten).tieuSu(tieuSu).anhDaiDien(anhDaiDien).build();
        return toTacGiaResponse(tacGiaRepository.save(tacGia));
    }

    public TacGiaResponse capNhatTacGia(Long maTacGia, String ten, String tieuSu, String anhDaiDien) {
        TacGia tacGia = tacGiaRepository.findById(maTacGia)
                .orElseThrow(() -> new KhongCoDuLieuException("Không tìm thấy tác giả", maTacGia));
        if (ten != null) {
            if (ten.isBlank()) {
                throw new HethongLoiException("Tên tác giả không được để trống");
            }
            tacGia.setTen(ten);
        }
        if (tieuSu != null) {
            tacGia.setTieuSu(tieuSu);
        }
        if (anhDaiDien != null) {
            tacGia.setAnhDaiDien(anhDaiDien);
        }
        return toTacGiaResponse(tacGiaRepository.save(tacGia));
    }

    public TacGiaResponse capNhatAnhDaiDienTacGia(Long maTacGia, MultipartFile file) {
        TacGia tacGia = tacGiaRepository.findById(maTacGia)
                .orElseThrow(() -> new KhongCoDuLieuException("Không tìm thấy tác giả", maTacGia));
        String duongDanAnh = uploadService.luuAnh(file, "tac-gia");
        tacGia.setAnhDaiDien(duongDanAnh);
        return toTacGiaResponse(tacGiaRepository.save(tacGia));
    }

    public List<TheLoaiResponse> danhSachTheLoai() {
        return theLoaiRepository.findAll().stream().map(this::toTheLoaiResponse).collect(Collectors.toList());
    }

    public TheLoaiResponse themTheLoai(String ten, String moTa) {
        if (ten == null || ten.isBlank()) {
            throw new HethongLoiException("Tên thể loại không được để trống");
        }
        TheLoai theLoai = TheLoai.builder().ten(ten).moTa(moTa).build();
        return toTheLoaiResponse(theLoaiRepository.save(theLoai));
    }

    public TheLoaiResponse capNhatTheLoai(Long maTheLoai, String ten, String moTa) {
        TheLoai theLoai = theLoaiRepository.findById(maTheLoai)
                .orElseThrow(() -> new KhongCoDuLieuException("Không tìm thấy thể loại", maTheLoai));
        if (ten != null) {
            if (ten.isBlank()) {
                throw new HethongLoiException("Tên thể loại không được để trống");
            }
            theLoai.setTen(ten);
        }
        if (moTa != null) {
            theLoai.setMoTa(moTa);
        }
        return toTheLoaiResponse(theLoaiRepository.save(theLoai));
    }

    public SachResponse toResponse(Sach sach) {
        Integer soLuongTon = tonKhoRepository.findBySachMaSach(sach.getMaSach()).map(TonKho::getSoLuong).orElse(0);
        Double diemDanhGiaTrungBinh = danhGiaRepository.tinhDiemDanhGiaTrungBinh(sach.getMaSach(),
                TrangThaiDanhGia.DA_DUYET);
        Long soLuongDanhGia = danhGiaRepository.countBySachMaSachAndTrangThai(sach.getMaSach(),
                TrangThaiDanhGia.DA_DUYET);
        return SachResponse.builder()
                .maSach(sach.getMaSach())
                .tenSach(sach.getTenSach())
                .moTa(sach.getMoTa())
                .gia(sach.getGia())
                .anhBia(sach.getAnhBia())
                .nhaXuatBan(sach.getNhaXuatBan())
                .ngayXuatBan(sach.getNgayXuatBan())
                .tacGia(sach.getTacGia() == null ? null
                        : TacGiaResponse.builder().maTacGia(sach.getTacGia().getMaTacGia())
                                .ten(sach.getTacGia().getTen()).tieuSu(sach.getTacGia().getTieuSu())
                                .anhDaiDien(sach.getTacGia().getAnhDaiDien()).build())
                .theLoais(sach.getTheLoais().stream()
                        .map(theLoai -> TheLoaiResponse.builder().maTheLoai(theLoai.getMaTheLoai())
                                .ten(theLoai.getTen()).moTa(theLoai.getMoTa()).build())
                        .collect(Collectors.toList()))
                .soLuongTon(soLuongTon)
                .diemDanhGiaTrungBinh(diemDanhGiaTrungBinh == null ? 0.0 : diemDanhGiaTrungBinh)
                .soLuongDanhGia(soLuongDanhGia == null ? 0L : soLuongDanhGia)
                .build();
    }

    private TacGiaResponse toTacGiaResponse(TacGia tacGia) {
        return TacGiaResponse.builder().maTacGia(tacGia.getMaTacGia()).ten(tacGia.getTen()).tieuSu(tacGia.getTieuSu())
                .anhDaiDien(tacGia.getAnhDaiDien())
                .build();
    }

    private TheLoaiResponse toTheLoaiResponse(TheLoai theLoai) {
        return TheLoaiResponse.builder().maTheLoai(theLoai.getMaTheLoai()).ten(theLoai.getTen()).moTa(theLoai.getMoTa())
                .build();
    }

}
