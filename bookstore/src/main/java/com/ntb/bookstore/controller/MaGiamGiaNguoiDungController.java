package com.ntb.bookstore.controller;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ntb.bookstore.dto.ApiResponse;
import com.ntb.bookstore.dto.MaGiamGia.KiemTraMaGiamGiaRequest;
import com.ntb.bookstore.dto.MaGiamGia.KiemTraMaGiamGiaResponse;
import com.ntb.bookstore.service.DonHangService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/ma-giam-gia")
@PreAuthorize("hasAnyAuthority('ROLE_NGUOI_DUNG')")
@AllArgsConstructor
public class MaGiamGiaNguoiDungController {

    private final DonHangService donHangService;

    @PostMapping("/kiem-tra")
    public ResponseEntity<ApiResponse<KiemTraMaGiamGiaResponse>> kiemTraMaGiamGia(
            @RequestBody @Valid KiemTraMaGiamGiaRequest request) {
        return ResponseEntity.ok(ApiResponse.of(true, "Kiểm tra mã giảm giá thành công", LocalDateTime.now(),
                donHangService.kiemTraMaGiamGia(request.getMaGiamGia(), request.getTongTien())));
    }
}
