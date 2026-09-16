package com.ntb.bookstore.controller.QuanTriVien;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.ntb.bookstore.dto.ApiResponse;
import com.ntb.bookstore.dto.PageResponse;
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
}
