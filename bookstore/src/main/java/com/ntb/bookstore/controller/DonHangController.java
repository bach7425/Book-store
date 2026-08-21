package com.ntb.bookstore.controller;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.ntb.bookstore.dto.ApiResponse;
import com.ntb.bookstore.dto.PageResponse;
import com.ntb.bookstore.dto.DonHang.DonHangResponse;
import com.ntb.bookstore.dto.DonHang.TaoDonHangRequest;
import com.ntb.bookstore.service.DonHangService;
import com.ntb.bookstore.service.XacThucService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/don-hang")
@AllArgsConstructor
public class DonHangController {

    private final DonHangService donHangService;
    private final XacThucService xacThucService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_NGUOI_DUNG')")
    public ResponseEntity<ApiResponse<DonHangResponse>> taoDonHang(@RequestBody @Valid TaoDonHangRequest request) {
        Long maNguoiDung = xacThucService.layMaNguoiDungHienTai();
        DonHangResponse response = donHangService.taoDonHang(
                maNguoiDung,
                request.getMaDiaChi(),
                request.getPhuongThucThanhToan(),
                request.getMaGiamGia());
        return ResponseEntity.ok(ApiResponse.of(true, "Tạo đơn hàng thành công", LocalDateTime.now(), response));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_QUAN_TRI_VIEN','ROLE_NGUOI_DUNG')")
    public ResponseEntity<ApiResponse<PageResponse<DonHangResponse>>> danhSachDonHang(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long maNguoiDung = xacThucService.layMaNguoiDungHienTai();
        String baseUrl = ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString();
        return ResponseEntity.ok(ApiResponse.of(true, "Lấy danh sách đơn hàng thành công", LocalDateTime.now(),
                donHangService.danhSachDonHang(maNguoiDung, status, page, size, baseUrl)));
    }

    @GetMapping("/lich-su")
    @PreAuthorize("hasAnyAuthority('ROLE_NGUOI_DUNG')")
    public ResponseEntity<ApiResponse<PageResponse<DonHangResponse>>> lichSuDonHang(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long maNguoiDung = xacThucService.layMaNguoiDungHienTai();
        String baseUrl = ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString();
        return ResponseEntity.ok(ApiResponse.of(true, "Lấy lịch sử đơn hàng thành công", LocalDateTime.now(),
                donHangService.lichSuDonHang(maNguoiDung, page, size, baseUrl)));
    }

    @GetMapping("/{maDonHang}")
    @PreAuthorize("hasAnyAuthority('ROLE_QUAN_TRI_VIEN','ROLE_NGUOI_DUNG')")
    public ResponseEntity<ApiResponse<DonHangResponse>> chiTietDonHang(@PathVariable Long maDonHang) {
        Long maNguoiDung = xacThucService.layMaNguoiDungHienTai();
        return ResponseEntity.ok(ApiResponse.of(true, "Lấy chi tiết đơn hàng thành công", LocalDateTime.now(),
                donHangService.chiTietDonHang(maNguoiDung, maDonHang)));
    }

    @PostMapping("/{maDonHang}/huy")
    @PreAuthorize("hasAnyAuthority('ROLE_NGUOI_DUNG')")
    public ResponseEntity<ApiResponse<DonHangResponse>> huyDonHang(@PathVariable Long maDonHang) {
        Long maNguoiDung = xacThucService.layMaNguoiDungHienTai();
        return ResponseEntity.ok(ApiResponse.of(true, "Hủy đơn hàng thành công", LocalDateTime.now(),
                donHangService.huyDonHang(maNguoiDung, maDonHang)));
    }

    @PutMapping("/{maDonHang}/thanh-toan")
    @PreAuthorize("hasAnyAuthority('ROLE_NGUOI_DUNG')")
    public ResponseEntity<ApiResponse<DonHangResponse>> thanhToanDonHang(@PathVariable Long maDonHang) {
        Long maNguoiDung = xacThucService.layMaNguoiDungHienTai();
        return ResponseEntity.ok(ApiResponse.of(true, "Thanh toán đơn hàng thành công", LocalDateTime.now(),
                donHangService.thanhToanDonHang(maNguoiDung, maDonHang)));
    }
}
