package com.ntb.bookstore.controller;

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
import com.ntb.bookstore.dto.GioHang.CapNhatSoLuongGioHangRequest;
import com.ntb.bookstore.dto.GioHang.ChiTietGioHangResponse;
import com.ntb.bookstore.dto.GioHang.GioHangResponse;
import com.ntb.bookstore.dto.GioHang.ThemVaoGioHangRequest;
import com.ntb.bookstore.service.GioHangService;
import com.ntb.bookstore.service.XacThucService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/gio-hang")
@AllArgsConstructor
@PreAuthorize("hasAnyAuthority('ROLE_NGUOI_DUNG','ROLE_QUAN_TRI_VIEN')")
public class GioHangController {

        private final GioHangService gioHangService;
        private final XacThucService xacThucService;

        @GetMapping
        public ResponseEntity<ApiResponse<GioHangResponse>> layGioHang(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {
                Long maNguoiDung = xacThucService.layMaNguoiDungHienTai();
                String baseUrl = ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString();
                return ResponseEntity.ok(ApiResponse.of(true, "Lấy giỏ hàng thành công", LocalDateTime.now(),
                                gioHangService.layGioHang(maNguoiDung, page, size, baseUrl)));
        }

        @PostMapping("/san-pham")
        public ResponseEntity<ApiResponse<ChiTietGioHangResponse>> themSanPham(
                        @RequestBody @Valid ThemVaoGioHangRequest request) {
                Long maNguoiDung = xacThucService.layMaNguoiDungHienTai();
                return ResponseEntity.ok(ApiResponse.of(true, "Thêm sản phẩm vào giỏ hàng thành công",
                                LocalDateTime.now(),
                                gioHangService.themSanPham(maNguoiDung, request.getMaSach(), request.getSoLuong())));
        }

        @PutMapping("/san-pham/{sanPhamId}")
        public ResponseEntity<ApiResponse<ChiTietGioHangResponse>> capNhatSoLuong(
                        @PathVariable Long sanPhamId,
                        @RequestBody @Valid CapNhatSoLuongGioHangRequest request) {
                Long maNguoiDung = xacThucService.layMaNguoiDungHienTai();
                return ResponseEntity.ok(ApiResponse.of(true, "Cập nhật số lượng thành công", LocalDateTime.now(),
                                gioHangService.capNhatSoLuong(maNguoiDung, sanPhamId, request.getSoLuong())));
        }

        @DeleteMapping("/san-pham/{sanPhamId}")
        public ResponseEntity<ApiResponse<Void>> xoaSanPham(@PathVariable Long sanPhamId) {
                Long maNguoiDung = xacThucService.layMaNguoiDungHienTai();
                gioHangService.xoaSanPham(maNguoiDung, sanPhamId);
                return ResponseEntity
                                .ok(ApiResponse.of(true, "Xóa sản phẩm khỏi giỏ hàng thành công", LocalDateTime.now(),
                                                null));
        }

}
