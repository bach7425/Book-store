package com.ntb.bookstore.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ntb.bookstore.dto.PageResponse;
import com.ntb.bookstore.dto.ThongBao.ThongBaoResponse;
import com.ntb.bookstore.entity.NguoiDung;
import com.ntb.bookstore.entity.ThongBao;
import com.ntb.bookstore.entity.enums.LoaiThongBao;
import com.ntb.bookstore.entity.enums.VaiTro;
import com.ntb.bookstore.exception.HethongLoiException;
import com.ntb.bookstore.exception.KhongCoDuLieuException;
import com.ntb.bookstore.repository.NguoiDungRepository;
import com.ntb.bookstore.repository.ThongBaoRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class ThongBaoService {

    private final ThongBaoRepository thongBaoRepository;
    private final NguoiDungRepository nguoiDungRepository;

    public void guiChoNguoiDung(NguoiDung nguoiDung, String tieuDe, String noiDung, LoaiThongBao loai,
            String duongDan) {
        taoThongBao(nguoiDung, tieuDe, noiDung, loai, duongDan);
    }

    public void guiChoTatCaNguoiDung(String tieuDe, String noiDung, LoaiThongBao loai, String duongDan) {
        nguoiDungRepository.findByVaiTro(VaiTro.NGUOI_DUNG)
                .forEach(nguoiDung -> taoThongBao(nguoiDung, tieuDe, noiDung, loai, duongDan));
    }

    public void guiChoTatCaQuanTriVien(String tieuDe, String noiDung, LoaiThongBao loai, String duongDan) {
        nguoiDungRepository.findByVaiTro(VaiTro.QUAN_TRI_VIEN)
                .forEach(nguoiDung -> taoThongBao(nguoiDung, tieuDe, noiDung, loai, duongDan));
    }

    public void guiThongBaoQuanTri(Long maNguoiDung, boolean guiTatCa, String tieuDe, String noiDung, String loai,
            String duongDan) {
        LoaiThongBao loaiThongBao = chuyenLoaiThongBao(loai);
        if (guiTatCa) {
            guiChoTatCaNguoiDung(tieuDe, noiDung, loaiThongBao, duongDan);
            return;
        }
        if (maNguoiDung == null) {
            throw new HethongLoiException("Ma nguoi dung la bat buoc khi khong gui tat ca");
        }
        NguoiDung nguoiDung = nguoiDungRepository.findById(maNguoiDung)
                .orElseThrow(() -> new KhongCoDuLieuException("Khong tim thay nguoi dung", maNguoiDung));
        guiChoNguoiDung(nguoiDung, tieuDe, noiDung, loaiThongBao, duongDan);
    }

    public PageResponse<ThongBaoResponse> danhSachThongBao(Long maNguoiDung, int page, int size, String baseUrl) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(maNguoiDung)
                .orElseThrow(() -> new KhongCoDuLieuException("Khong tim thay nguoi dung", maNguoiDung));
        Pageable pageable = PageRequest.of(page, size);
        Page<ThongBao> thongBaoPage = thongBaoRepository.findByNguoiDungOrderByNgayTaoDesc(nguoiDung, pageable);
        return new PageResponse<>(thongBaoPage.map(this::toResponse), baseUrl);
    }

    public Long demThongBaoChuaDoc(Long maNguoiDung) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(maNguoiDung)
                .orElseThrow(() -> new KhongCoDuLieuException("Khong tim thay nguoi dung", maNguoiDung));
        return thongBaoRepository.countByNguoiDungAndDaDocFalse(nguoiDung);
    }

    public ThongBaoResponse danhDauDaDoc(Long maNguoiDung, Long maThongBao) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(maNguoiDung)
                .orElseThrow(() -> new KhongCoDuLieuException("Khong tim thay nguoi dung", maNguoiDung));
        ThongBao thongBao = thongBaoRepository.findById(maThongBao)
                .orElseThrow(() -> new KhongCoDuLieuException("Khong tim thay thong bao", maThongBao));
        if (!thongBao.getNguoiDung().getMaNguoiDung().equals(nguoiDung.getMaNguoiDung())) {
            throw new HethongLoiException("Ban khong co quyen cap nhat thong bao nay");
        }
        thongBao.setDaDoc(true);
        return toResponse(thongBaoRepository.save(thongBao));
    }

    public void danhDauTatCaDaDoc(Long maNguoiDung) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(maNguoiDung)
                .orElseThrow(() -> new KhongCoDuLieuException("Khong tim thay nguoi dung", maNguoiDung));
        thongBaoRepository.findByNguoiDungAndDaDocFalse(nguoiDung).forEach(thongBao -> {
            thongBao.setDaDoc(true);
            thongBaoRepository.save(thongBao);
        });
    }

    private ThongBao taoThongBao(NguoiDung nguoiDung, String tieuDe, String noiDung, LoaiThongBao loai,
            String duongDan) {
        return thongBaoRepository.save(ThongBao.builder()
                .nguoiDung(nguoiDung)
                .tieuDe(tieuDe)
                .noiDung(noiDung)
                .loai(loai == null ? LoaiThongBao.QUAN_TRI : loai)
                .duongDan(duongDan)
                .build());
    }

    private LoaiThongBao chuyenLoaiThongBao(String loai) {
        if (loai == null || loai.isBlank()) {
            return LoaiThongBao.QUAN_TRI;
        }
        try {
            return LoaiThongBao.valueOf(loai.toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new HethongLoiException("Loai thong bao khong hop le: " + loai);
        }
    }

    private ThongBaoResponse toResponse(ThongBao thongBao) {
        return ThongBaoResponse.builder()
                .maThongBao(thongBao.getMaThongBao())
                .tieuDe(thongBao.getTieuDe())
                .noiDung(thongBao.getNoiDung())
                .loai(thongBao.getLoai().name())
                .daDoc(thongBao.getDaDoc())
                .ngayTao(thongBao.getNgayTao())
                .duongDan(thongBao.getDuongDan())
                .build();
    }
}
