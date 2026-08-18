package com.ntb.bookstore.controller.QuanTriVien;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ntb.bookstore.dto.ApiResponse;
import com.ntb.bookstore.dto.Sach.TheLoaiResponse;
import com.ntb.bookstore.dto.Sach.ThemTheLoaiRequest;
import com.ntb.bookstore.service.SachService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/quan-tri/the-loai")
@PreAuthorize("hasAnyAuthority('ROLE_QUAN_TRI_VIEN')")
@AllArgsConstructor
public class QuanTriTheLoaiController {

    private final SachService sachService;

    @PostMapping
    public ResponseEntity<ApiResponse<TheLoaiResponse>> themTheLoai(@RequestBody ThemTheLoaiRequest request) {
        return ResponseEntity.ok(ApiResponse.of(true, "Them the loai thanh cong", LocalDateTime.now(),
                sachService.themTheLoai(request.getTen(), request.getMoTa())));
    }

    @PutMapping("/{maTheLoai}")
    public ResponseEntity<ApiResponse<TheLoaiResponse>> capNhatTheLoai(@PathVariable Long maTheLoai,
            @RequestBody ThemTheLoaiRequest request) {
        return ResponseEntity.ok(ApiResponse.of(true, "Cap nhat the loai thanh cong", LocalDateTime.now(),
                sachService.capNhatTheLoai(maTheLoai, request.getTen(), request.getMoTa())));
    }
}
