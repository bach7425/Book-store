package com.ntb.bookstore.controller.QuanTriVien;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ntb.bookstore.dto.ApiResponse;
import com.ntb.bookstore.dto.Sach.TacGiaResponse;
import com.ntb.bookstore.dto.Sach.ThemTacGiaRequest;
import com.ntb.bookstore.service.SachService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/quan-tri/tac-gia")
@PreAuthorize("hasAnyAuthority('ROLE_QUAN_TRI_VIEN')")
@AllArgsConstructor
public class QuanTriTacGiaController {

    private final SachService sachService;

    @PostMapping
    public ResponseEntity<ApiResponse<TacGiaResponse>> themTacGia(@RequestBody ThemTacGiaRequest request) {
        return ResponseEntity.ok(ApiResponse.of(true, "Them tac gia thanh cong", LocalDateTime.now(),
                sachService.themTacGia(request.getTen(), request.getTieuSu(), request.getAnhDaiDien())));
    }

    @PutMapping("/{maTacGia}")
    public ResponseEntity<ApiResponse<TacGiaResponse>> capNhatTacGia(@PathVariable Long maTacGia,
            @RequestBody ThemTacGiaRequest request) {
        return ResponseEntity.ok(ApiResponse.of(true, "Cap nhat tac gia thanh cong", LocalDateTime.now(),
                sachService.capNhatTacGia(maTacGia, request.getTen(), request.getTieuSu(), request.getAnhDaiDien())));
    }

    @PostMapping("/{maTacGia}/anh-dai-dien")
    public ResponseEntity<ApiResponse<TacGiaResponse>> capNhatAnhDaiDienTacGia(@PathVariable Long maTacGia,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(ApiResponse.of(true, "Cap nhat anh dai dien tac gia thanh cong", LocalDateTime.now(),
                sachService.capNhatAnhDaiDienTacGia(maTacGia, file)));
    }
}
