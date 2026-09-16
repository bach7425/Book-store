package com.ntb.bookstore.service;

import java.math.BigDecimal;
import java.util.Comparator;
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
            String doTuoi, String tenNhaCungCap, String nguoiDich, String ngonNgu, Integer trongLuongGram,
            String kichThuocBaoBi, Integer soTrang, String hinhThuc,
            Long maTacGia, List<Long> maTheLoais, Integer soLuongTon) {
        kiemTraThongTinSach(gia, soLuongTon, trongLuongGram, soTrang);
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
                .doTuoi(doTuoi)
                .tenNhaCungCap(tenNhaCungCap)
                .nguoiDich(nguoiDich)
                .ngonNgu(ngonNgu)
                .trongLuongGram(trongLuongGram)
                .kichThuocBaoBi(kichThuocBaoBi)
                .soTrang(soTrang)
                .hinhThuc(hinhThuc)
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
            String nhaXuatBan, String doTuoi, String tenNhaCungCap, String nguoiDich, String ngonNgu,
            Integer trongLuongGram, String kichThuocBaoBi, Integer soTrang, String hinhThuc,
            Long maTacGia, List<Long> maTheLoais, Integer soLuongTon) {
        kiemTraThongTinSach(gia, soLuongTon, trongLuongGram, soTrang);
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
        if (doTuoi != null)
            sach.setDoTuoi(doTuoi);
        if (tenNhaCungCap != null)
            sach.setTenNhaCungCap(tenNhaCungCap);
        if (nguoiDich != null)
            sach.setNguoiDich(nguoiDich);
        if (ngonNgu != null)
            sach.setNgonNgu(ngonNgu);
        if (trongLuongGram != null)
            sach.setTrongLuongGram(trongLuongGram);
        if (kichThuocBaoBi != null)
            sach.setKichThuocBaoBi(kichThuocBaoBi);
        if (soTrang != null)
            sach.setSoTrang(soTrang);
        if (hinhThuc != null)
            sach.setHinhThuc(hinhThuc);
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
        Sach saved = sachRepository.save(sach);
        ragService.themSachVaoVectorStore(saved);
        return toResponse(saved);
    }

    private void kiemTraThongTinSach(BigDecimal gia, Integer soLuongTon, Integer trongLuongGram, Integer soTrang) {
        if (gia != null && gia.compareTo(BigDecimal.ZERO) < 0) {
            throw new HethongLoiException("Giá sách không được âm");
        }
        if (soLuongTon != null && soLuongTon < 0) {
            throw new HethongLoiException("Số lượng tồn kho không được âm");
        }
        if (trongLuongGram != null && trongLuongGram < 0) {
            throw new HethongLoiException("Trọng lượng không được âm");
        }
        if (soTrang != null && soTrang < 0) {
            throw new HethongLoiException("Số trang không được âm");
        }
    }

    public List<SachResponse> sachLienQuan(Long maSach, int size) {
        Sach sach = sachRepository.findById(maSach)
                .orElseThrow(() -> new KhongCoDuLieuException("Không tìm thấy sách", maSach));
        int gioiHan = Math.max(1, Math.min(size, 12));
        List<Long> maTheLoais = sach.getTheLoais().stream().map(TheLoai::getMaTheLoai).collect(Collectors.toList());
        return sachRepository.findAllForRag().stream()
                .filter(item -> !item.getMaSach().equals(maSach))
                .filter(item -> cungTacGia(sach, item) || coTheLoaiChung(item, maTheLoais))
                .sorted(Comparator
                        .comparing((Sach item) -> cungTacGia(sach, item) ? 0 : 1)
                        .thenComparing(Sach::getTenSach, String.CASE_INSENSITIVE_ORDER))
                .limit(gioiHan)
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private boolean cungTacGia(Sach sach, Sach sachKhac) {
        return sach.getTacGia() != null && sachKhac.getTacGia() != null
                && sach.getTacGia().getMaTacGia().equals(sachKhac.getTacGia().getMaTacGia());
    }

    private boolean coTheLoaiChung(Sach sach, List<Long> maTheLoais) {
        return sach.getTheLoais().stream().anyMatch(theLoai -> maTheLoais.contains(theLoai.getMaTheLoai()));
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

    public TacGiaResponse themTacGia(String ten, String tieuSu) {
        if (ten == null || ten.isBlank()) {
            throw new HethongLoiException("Tên tác giả không được để trống");
        }
        TacGia tacGia = TacGia.builder().ten(ten).tieuSu(tieuSu).build();
        return toTacGiaResponse(tacGiaRepository.save(tacGia));
    }

    public TacGiaResponse capNhatTacGia(Long maTacGia, String ten, String tieuSu) {
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
                .doTuoi(sach.getDoTuoi())
                .tenNhaCungCap(sach.getTenNhaCungCap())
                .nguoiDich(sach.getNguoiDich())
                .ngonNgu(sach.getNgonNgu())
                .trongLuongGram(sach.getTrongLuongGram())
                .kichThuocBaoBi(sach.getKichThuocBaoBi())
                .soTrang(sach.getSoTrang())
                .hinhThuc(sach.getHinhThuc())
                .ngayXuatBan(sach.getNgayXuatBan())
                .tacGia(sach.getTacGia() == null ? null
                        : TacGiaResponse.builder().maTacGia(sach.getTacGia().getMaTacGia())
                                .ten(sach.getTacGia().getTen()).tieuSu(sach.getTacGia().getTieuSu())
                                .build())
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
                .build();
    }

    private TheLoaiResponse toTheLoaiResponse(TheLoai theLoai) {
        return TheLoaiResponse.builder().maTheLoai(theLoai.getMaTheLoai()).ten(theLoai.getTen()).moTa(theLoai.getMoTa())
                .build();
    }

}
