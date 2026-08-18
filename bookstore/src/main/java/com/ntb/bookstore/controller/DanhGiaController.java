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
import com.ntb.bookstore.dto.PageResponse;
import com.ntb.bookstore.dto.DanhGia.DanhGiaResponse;
import com.ntb.bookstore.dto.DanhGia.ThemDanhGiaRequest;
import com.ntb.bookstore.service.DanhGiaService;
import com.ntb.bookstore.service.XacThucService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class DanhGiaController {

    private final DanhGiaService danhGiaService;
    private final XacThucService xacThucService;

    @GetMapping("/sach/{maSach}/danh-gia")
    public ResponseEntity<ApiResponse<PageResponse<DanhGiaResponse>>> layDanhGiaSach(
            @PathVariable Long maSach,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        var nguoiDung = xacThucService.layNguoiDungHienTaiNeuCo();
        Long maNguoiDung = nguoiDung == null ? null : nguoiDung.getMaNguoiDung();
        String baseUrl = ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString();
        return ResponseEntity.ok(ApiResponse.of(true, "Lay danh gia thanh cong", LocalDateTime.now(),
                danhGiaService.layDanhGiaSach(maSach, maNguoiDung, page, size, baseUrl)));
    }

    @PostMapping("/sach/{maSach}/danh-gia")
    @PreAuthorize("hasAnyAuthority('ROLE_NGUOI_DUNG')")
    public ResponseEntity<ApiResponse<DanhGiaResponse>> themDanhGia(@PathVariable Long maSach,
            @RequestBody ThemDanhGiaRequest request) {
        Long maNguoiDung = xacThucService.layMaNguoiDungHienTai();
        return ResponseEntity.ok(ApiResponse.of(true, "Gui danh gia thanh cong", LocalDateTime.now(),
                danhGiaService.themDanhGia(maNguoiDung, maSach, request.getSoSao(), request.getNoiDung())));
    }

    @PutMapping("/danh-gia/{maDanhGia}")
    @PreAuthorize("hasAnyAuthority('ROLE_NGUOI_DUNG')")
    public ResponseEntity<ApiResponse<DanhGiaResponse>> capNhatDanhGia(@PathVariable Long maDanhGia,
            @RequestBody ThemDanhGiaRequest request) {
        Long maNguoiDung = xacThucService.layMaNguoiDungHienTai();
        return ResponseEntity.ok(ApiResponse.of(true, "Cap nhat danh gia thanh cong", LocalDateTime.now(),
                danhGiaService.capNhatDanhGia(maNguoiDung, maDanhGia, request.getSoSao(), request.getNoiDung())));
    }

    @DeleteMapping("/danh-gia/{maDanhGia}")
    @PreAuthorize("hasAnyAuthority('ROLE_NGUOI_DUNG', 'ROLE_QUAN_TRI_VIEN')")
    public ResponseEntity<ApiResponse<Void>> xoaDanhGia(@PathVariable Long maDanhGia) {
        Long maNguoiDung = xacThucService.layMaNguoiDungHienTai();
        danhGiaService.xoaDanhGia(maNguoiDung, maDanhGia);
        return ResponseEntity.ok(ApiResponse.of(true, "Xoa danh gia thanh cong", LocalDateTime.now(), null));
    }

}
