package com.ntb.bookstore.service;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ntb.bookstore.dto.PageResponse;
import com.ntb.bookstore.dto.DonHang.DonHangResponse;
import com.ntb.bookstore.dto.QuanTri.KhachHangResponse;
import com.ntb.bookstore.entity.DonHang;
import com.ntb.bookstore.entity.NguoiDung;
import com.ntb.bookstore.entity.enums.TrangThaiDonHang;
import com.ntb.bookstore.entity.enums.TrangThaiThanhToan;
import com.ntb.bookstore.entity.enums.VaiTro;
import com.ntb.bookstore.exception.KhongCoDuLieuException;
import com.ntb.bookstore.repository.DonHangRepository;
import com.ntb.bookstore.repository.NguoiDungRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class QuanTriKhachHangService {

    private final NguoiDungRepository nguoiDungRepository;
    private final DonHangRepository donHangRepository;
    private final DonHangService donHangService;

    public PageResponse<KhachHangResponse> danhSachKhachHang(String tuKhoa, int page, int size, String baseUrl) {
        Pageable pageable = PageRequest.of(page, size);
        Page<NguoiDung> khachHangPage = nguoiDungRepository.timKiemTheoVaiTro(VaiTro.NGUOI_DUNG, tuKhoa, pageable);
        return new PageResponse<>(khachHangPage.map(this::toResponse), baseUrl);
    }

    public KhachHangResponse chiTietKhachHang(Long maNguoiDung) {
        NguoiDung nguoiDung = layKhachHang(maNguoiDung);
        return toResponse(nguoiDung);
    }

    public PageResponse<DonHangResponse> danhSachDonHangTheoKhach(Long maNguoiDung, String trangThai, int page,
            int size, String baseUrl) {
        NguoiDung nguoiDung = layKhachHang(maNguoiDung);
        Pageable pageable = PageRequest.of(page, size);
        Page<DonHang> donHangPage;
        if (trangThai == null || trangThai.isBlank()) {
            donHangPage = donHangRepository.findByNguoiDung(nguoiDung, pageable);
        } else {
            donHangPage = donHangRepository.findByNguoiDungAndTrangThai(nguoiDung,
                    TrangThaiDonHang.valueOf(trangThai.toUpperCase()), pageable);
        }
        return new PageResponse<>(donHangPage.map(donHangService::toResponse), baseUrl);
    }

    private NguoiDung layKhachHang(Long maNguoiDung) {
        return nguoiDungRepository.findByMaNguoiDungAndVaiTro(maNguoiDung, VaiTro.NGUOI_DUNG)
                .orElseThrow(() -> new KhongCoDuLieuException("Không tìm thấy khách hàng", maNguoiDung));
    }

    private KhachHangResponse toResponse(NguoiDung nguoiDung) {
        BigDecimal tongChiTieu = donHangRepository.tinhTongChiTieu(nguoiDung, TrangThaiThanhToan.DA_THANH_TOAN);
        return KhachHangResponse.builder()
                .maNguoiDung(nguoiDung.getMaNguoiDung())
                .hoVaTen(nguoiDung.getHoVaTen())
                .email(nguoiDung.getEmail())
                .tenDangNhap(nguoiDung.getTenDangNhap())
                .soDienThoai(nguoiDung.getSoDienThoai())
                .anhDaiDien(nguoiDung.getAnhDaiDien())
                .vaiTro(nguoiDung.getVaiTro().name())
                .soDonHang(donHangRepository.countByNguoiDung(nguoiDung))
                .tongChiTieu(tongChiTieu == null ? BigDecimal.ZERO : tongChiTieu)
                .build();
    }
}
