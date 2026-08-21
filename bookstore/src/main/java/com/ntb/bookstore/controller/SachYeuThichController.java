package com.ntb.bookstore.controller;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.ntb.bookstore.dto.ApiResponse;
import com.ntb.bookstore.dto.PageResponse;
import com.ntb.bookstore.dto.Sach.SachResponse;
import com.ntb.bookstore.service.SachYeuThichService;
import com.ntb.bookstore.service.XacThucService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/sach-yeu-thich")
@AllArgsConstructor
public class SachYeuThichController {

    private final SachYeuThichService sachYeuThichService;
    private final XacThucService xacThucService;

    @PostMapping("/{maSach}")
    @PreAuthorize("hasAnyAuthority('ROLE_NGUOI_DUNG')")
    public ResponseEntity<ApiResponse<SachResponse>> themSachYeuThich(@PathVariable Long maSach) {
        Long maNguoiDung = xacThucService.layMaNguoiDungHienTai();
        return ResponseEntity.ok(ApiResponse.of(true, "Thêm sách yêu thích thành công", LocalDateTime.now(),
                sachYeuThichService.themSachYeuThich(maNguoiDung, maSach)));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_NGUOI_DUNG')")
    public ResponseEntity<ApiResponse<PageResponse<SachResponse>>> danhSachSachYeuThich(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long maNguoiDung = xacThucService.layMaNguoiDungHienTai();
        String baseUrl = ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString();
        return ResponseEntity.ok(ApiResponse.of(true, "Lấy danh sách sách yêu thích thành công", LocalDateTime.now(),
                sachYeuThichService.danhSachSachYeuThich(maNguoiDung, page, size, baseUrl)));
    }

    @DeleteMapping("/{maSach}")
    @PreAuthorize("hasAnyAuthority('ROLE_NGUOI_DUNG')")
    public ResponseEntity<ApiResponse<Void>> xoaSachYeuThich(@PathVariable Long maSach) {
        Long maNguoiDung = xacThucService.layMaNguoiDungHienTai();
        sachYeuThichService.xoaSachYeuThich(maNguoiDung, maSach);
        return ResponseEntity.ok(ApiResponse.of(true, "Xóa sách yêu thích thành công", LocalDateTime.now(), null));
    }
}
