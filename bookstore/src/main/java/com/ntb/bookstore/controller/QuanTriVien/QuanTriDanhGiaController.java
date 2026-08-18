package com.ntb.bookstore.controller.QuanTriVien;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.ntb.bookstore.dto.ApiResponse;
import com.ntb.bookstore.dto.PageResponse;
import com.ntb.bookstore.dto.DanhGia.DanhGiaResponse;
import com.ntb.bookstore.dto.DanhGia.XuLyDanhGiaRequest;
import com.ntb.bookstore.service.DanhGiaService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/quan-tri/danh-gia")
@PreAuthorize("hasAnyAuthority('ROLE_QUAN_TRI_VIEN')")
@AllArgsConstructor
public class QuanTriDanhGiaController {

    private final DanhGiaService danhGiaService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<DanhGiaResponse>>> danhSachDanhGiaQuanTri(
            @RequestParam(required = false) String trangThai,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String baseUrl = ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString();
        return ResponseEntity.ok(ApiResponse.of(true, "Lay danh sach danh gia quan tri thanh cong",
                LocalDateTime.now(), danhGiaService.danhSachDanhGiaQuanTri(trangThai, page, size, baseUrl)));
    }

    @PatchMapping("/{maDanhGia}/duyet")
    public ResponseEntity<ApiResponse<DanhGiaResponse>> duyetDanhGia(@PathVariable Long maDanhGia,
            @RequestBody(required = false) XuLyDanhGiaRequest request) {
        String phanHoi = request == null ? null : request.getPhanHoi();
        return ResponseEntity.ok(ApiResponse.of(true, "Duyet danh gia thanh cong", LocalDateTime.now(),
                danhGiaService.duyetDanhGia(maDanhGia, phanHoi)));
    }

    @PatchMapping("/{maDanhGia}/tu-choi")
    public ResponseEntity<ApiResponse<DanhGiaResponse>> tuChoiDanhGia(@PathVariable Long maDanhGia,
            @RequestBody(required = false) XuLyDanhGiaRequest request) {
        String phanHoi = request == null ? null : request.getPhanHoi();
        return ResponseEntity.ok(ApiResponse.of(true, "Tu choi danh gia thanh cong", LocalDateTime.now(),
                danhGiaService.tuChoiDanhGia(maDanhGia, phanHoi)));
    }
}
