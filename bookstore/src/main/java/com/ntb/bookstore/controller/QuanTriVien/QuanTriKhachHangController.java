package com.ntb.bookstore.controller.QuanTriVien;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.ntb.bookstore.dto.ApiResponse;
import com.ntb.bookstore.dto.PageResponse;
import com.ntb.bookstore.dto.DonHang.DonHangResponse;
import com.ntb.bookstore.dto.QuanTri.KhachHangResponse;
import com.ntb.bookstore.service.QuanTriKhachHangService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/quan-tri/khach-hang")
@PreAuthorize("hasAnyAuthority('ROLE_QUAN_TRI_VIEN')")
@AllArgsConstructor
public class QuanTriKhachHangController {

    private final QuanTriKhachHangService quanTriKhachHangService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<KhachHangResponse>>> danhSachKhachHang(
            @RequestParam(required = false) String tuKhoa,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String baseUrl = ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString();
        return ResponseEntity.ok(ApiResponse.of(true, "Lấy danh sách khách hàng thành công", LocalDateTime.now(),
                quanTriKhachHangService.danhSachKhachHang(tuKhoa, page, size, baseUrl)));
    }

    @GetMapping("/{maNguoiDung}")
    public ResponseEntity<ApiResponse<KhachHangResponse>> chiTietKhachHang(@PathVariable Long maNguoiDung) {
        return ResponseEntity.ok(ApiResponse.of(true, "Lấy chi tiết khách hàng thành công", LocalDateTime.now(),
                quanTriKhachHangService.chiTietKhachHang(maNguoiDung)));
    }

    @GetMapping("/{maNguoiDung}/don-hang")
    public ResponseEntity<ApiResponse<PageResponse<DonHangResponse>>> danhSachDonHangTheoKhach(
            @PathVariable Long maNguoiDung,
            @RequestParam(required = false) String trangThai,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String baseUrl = ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString();
        return ResponseEntity.ok(ApiResponse.of(true, "Lấy danh sách đơn hàng của khách hàng thành công",
                LocalDateTime.now(),
                quanTriKhachHangService.danhSachDonHangTheoKhach(maNguoiDung, trangThai, page, size, baseUrl)));
    }
}
