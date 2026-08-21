package com.ntb.bookstore.controller.QuanTriVien;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.ntb.bookstore.dto.ApiResponse;
import com.ntb.bookstore.dto.PageResponse;
import com.ntb.bookstore.dto.DonHang.CapNhatTrangThaiDonHangRequest;
import com.ntb.bookstore.dto.DonHang.DonHangResponse;
import com.ntb.bookstore.entity.enums.TrangThaiDonHang;
import com.ntb.bookstore.service.DonHangService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api")
@PreAuthorize("hasAnyAuthority('ROLE_QUAN_TRI_VIEN')")
@AllArgsConstructor
public class QuanTriDonHangController {

    private final DonHangService donHangService;

    @GetMapping("/quan-tri/don-hang")
    public ResponseEntity<ApiResponse<PageResponse<DonHangResponse>>> danhSachDonHangQuanTri(
            @RequestParam(required = false) String trangThai,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String baseUrl = ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString();
        return ResponseEntity.ok(ApiResponse.of(true, "Lấy danh sách đơn hàng quản trị thành công",
                LocalDateTime.now(), donHangService.danhSachDonHangQuanTri(trangThai, page, size, baseUrl)));
    }

    @PutMapping("/don-hang/{maDonHang}/trang-thai")
    public ResponseEntity<ApiResponse<DonHangResponse>> capNhatTrangThai(@PathVariable Long maDonHang,
            @RequestBody CapNhatTrangThaiDonHangRequest request) {
        return ResponseEntity.ok(ApiResponse.of(true, "Cập nhật trạng thái đơn hàng thành công", LocalDateTime.now(),
                donHangService.capNhatTrangThaiDonHang(maDonHang, TrangThaiDonHang.valueOf(request.getTrangThai()))));
    }
}
