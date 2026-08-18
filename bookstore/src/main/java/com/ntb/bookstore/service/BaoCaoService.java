package com.ntb.bookstore.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ntb.bookstore.dto.PageResponse;
import com.ntb.bookstore.dto.QuanTri.BaoCaoDoanhThuResponse;
import com.ntb.bookstore.dto.QuanTri.SachBanChayResponse;
import com.ntb.bookstore.entity.enums.TrangThaiDonHang;
import com.ntb.bookstore.entity.enums.TrangThaiThanhToan;
import com.ntb.bookstore.repository.ChiTietDonHangRepository;
import com.ntb.bookstore.repository.ThanhToanRepository;
import com.ntb.bookstore.repository.projection.SachBanChayProjection;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class BaoCaoService {

    private final ThanhToanRepository thanhToanRepository;
    private final ChiTietDonHangRepository chiTietDonHangRepository;

    public BaoCaoDoanhThuResponse baoCaoDoanhThu(LocalDate tuNgay, LocalDate denNgay) {
        KhoangNgay khoangNgay = taoKhoangNgay(tuNgay, denNgay);
        BigDecimal tongDoanhThu = thanhToanRepository.tinhTongDoanhThuDonDaGiao(
                TrangThaiThanhToan.DA_THANH_TOAN, TrangThaiDonHang.DA_GIAO, khoangNgay.tuNgayGio(),
                khoangNgay.denNgayGio());
        Long soDonDaThanhToan = thanhToanRepository
                .countByTrangThaiAndDonHangTrangThaiAndThoiGianThanhToanGreaterThanEqualAndThoiGianThanhToanLessThan(
                        TrangThaiThanhToan.DA_THANH_TOAN, TrangThaiDonHang.DA_GIAO, khoangNgay.tuNgayGio(),
                        khoangNgay.denNgayGio());
        return BaoCaoDoanhThuResponse.builder()
                .tongDoanhThu(tongDoanhThu == null ? BigDecimal.ZERO : tongDoanhThu)
                .soDonDaThanhToan(soDonDaThanhToan == null ? 0L : soDonDaThanhToan)
                .tuNgay(khoangNgay.tuNgay())
                .denNgay(khoangNgay.denNgay())
                .build();
    }

    public PageResponse<SachBanChayResponse> thongKeSachBanChay(LocalDate tuNgay, LocalDate denNgay, int page,
            int size, String baseUrl) {
        KhoangNgay khoangNgay = taoKhoangNgay(tuNgay, denNgay);
        Pageable pageable = PageRequest.of(page, size);
        Page<SachBanChayProjection> sachPage = chiTietDonHangRepository.thongKeSachBanChay(
                TrangThaiThanhToan.DA_THANH_TOAN, TrangThaiDonHang.DA_GIAO, khoangNgay.tuNgayGio(),
                khoangNgay.denNgayGio(), pageable);
        return new PageResponse<>(sachPage.map(this::toSachBanChayResponse), baseUrl);
    }

    private KhoangNgay taoKhoangNgay(LocalDate tuNgay, LocalDate denNgay) {
        LocalDate ngayBatDau = tuNgay;
        LocalDate ngayKetThuc = denNgay;
        LocalDate homNay = LocalDate.now();
        if (ngayBatDau == null && ngayKetThuc == null) {
            ngayBatDau = homNay.withDayOfMonth(1);
            ngayKetThuc = homNay;
        } else if (ngayBatDau == null) {
            ngayBatDau = ngayKetThuc.withDayOfMonth(1);
        } else if (ngayKetThuc == null) {
            ngayKetThuc = homNay;
        }
        return new KhoangNgay(ngayBatDau, ngayKetThuc, ngayBatDau.atStartOfDay(),
                ngayKetThuc.plusDays(1).atStartOfDay());
    }

    private SachBanChayResponse toSachBanChayResponse(SachBanChayProjection projection) {
        return SachBanChayResponse.builder()
                .maSach(projection.getMaSach())
                .tenSach(projection.getTenSach())
                .anhBia(projection.getAnhBia())
                .tacGia(projection.getTacGia())
                .soLuongBan(projection.getSoLuongBan() == null ? 0L : projection.getSoLuongBan())
                .doanhThu(projection.getDoanhThu() == null ? BigDecimal.ZERO : projection.getDoanhThu())
                .build();
    }

    private record KhoangNgay(LocalDate tuNgay, LocalDate denNgay, LocalDateTime tuNgayGio,
            LocalDateTime denNgayGio) {
    }
}
