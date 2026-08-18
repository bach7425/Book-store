package com.ntb.bookstore.controller.QuanTriVien;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import com.ntb.bookstore.dto.MaGiamGia.CapNhatMaGiamGiaRequest;
import com.ntb.bookstore.dto.MaGiamGia.MaGiamGiaResponse;
import com.ntb.bookstore.dto.MaGiamGia.TaoMaGiamGiaRequest;
import com.ntb.bookstore.service.MaGiamGiaService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/quan-tri/ma-giam-gia")
@PreAuthorize("hasAnyAuthority('ROLE_QUAN_TRI_VIEN')")
@AllArgsConstructor
public class MaGiamGiaController {

    private final MaGiamGiaService maGiamGiaService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<MaGiamGiaResponse>>> danhSachMaGiamGia(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String trangThai) {
        String baseUrl = ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString();
        return ResponseEntity.ok(ApiResponse.of(true, "Lay danh sach ma giam gia thanh cong", LocalDateTime.now(),
                maGiamGiaService.danhSachMaGiamGia(trangThai, page, size, baseUrl)));
    }

    @GetMapping("/{maGiamGia}")
    public ResponseEntity<ApiResponse<MaGiamGiaResponse>> chiTietMaGiamGia(@PathVariable Long maGiamGia) {
        return ResponseEntity.ok(ApiResponse.of(true, "Lay chi tiet ma giam gia thanh cong", LocalDateTime.now(),
                maGiamGiaService.chiTietMaGiamGia(maGiamGia)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MaGiamGiaResponse>> taoMaGiamGia(@RequestBody TaoMaGiamGiaRequest request) {
        return ResponseEntity.ok(ApiResponse.of(true, "Tao ma giam gia thanh cong", LocalDateTime.now(),
                maGiamGiaService.taoMaGiamGia(request)));
    }

    @PutMapping("/{maGiamGia}")
    public ResponseEntity<ApiResponse<MaGiamGiaResponse>> capNhatMaGiamGia(@PathVariable Long maGiamGia,
            @RequestBody CapNhatMaGiamGiaRequest request) {
        return ResponseEntity.ok(ApiResponse.of(true, "Cap nhat ma giam gia thanh cong", LocalDateTime.now(),
                maGiamGiaService.capNhatMaGiamGia(maGiamGia, request)));
    }

    @DeleteMapping("/{maGiamGia}")
    public ResponseEntity<ApiResponse<Void>> xoaMaGiamGia(@PathVariable Long maGiamGia) {
        maGiamGiaService.xoaMaGiamGia(maGiamGia);
        return ResponseEntity.ok(ApiResponse.of(true, "Mã giảm giá đã ngưng hoạt động", LocalDateTime.now(), null));
    }
}
