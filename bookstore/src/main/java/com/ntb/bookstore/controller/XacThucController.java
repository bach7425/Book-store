package com.ntb.bookstore.controller;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ntb.bookstore.dto.ApiResponse;
import com.ntb.bookstore.dto.NguoiDung.NguoiDungResponse;
import com.ntb.bookstore.dto.XacThuc.DangKyRequest;
import com.ntb.bookstore.dto.XacThuc.DangNhapRequest;
import com.ntb.bookstore.dto.XacThuc.LamMoiTokenRequest;
import com.ntb.bookstore.dto.XacThuc.LamMoiTokenResponse;
import com.ntb.bookstore.dto.XacThuc.XacThucRespone;
import com.ntb.bookstore.service.XacThucService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/xac-thuc")
@AllArgsConstructor
public class XacThucController {
    private final XacThucService xacThucService;

    @PostMapping("/dang-ky")
    public ResponseEntity<ApiResponse<NguoiDungResponse>> dangKy(@RequestBody @Valid DangKyRequest request) {
        NguoiDungResponse tao = xacThucService.dangKy(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.of(true, "Đăng ký thành công", LocalDateTime.now(), tao));
    }

    @PostMapping("/dang-nhap")
    public ResponseEntity<ApiResponse<XacThucRespone>> dangNhap(@RequestBody @Valid DangNhapRequest request) {
        XacThucRespone response = xacThucService.dangNhap(request);
        return ResponseEntity.ok(ApiResponse.of(true, "Đăng nhập thành công", LocalDateTime.now(), response));
    }

    @PostMapping("/lam-moi-token")
    public ResponseEntity<ApiResponse<LamMoiTokenResponse>> lamMoiToken(
            @RequestBody @Valid LamMoiTokenRequest request) {
        String maTruyCap = xacThucService.taoAccessTokenMoi(request.getMaLamMoi());
        LamMoiTokenResponse response = LamMoiTokenResponse.builder()
                .maTruyCap(maTruyCap)
                .loaiMa("Bearer")
                .build();
        return ResponseEntity.ok(ApiResponse.of(true, "Làm mới token thành công", LocalDateTime.now(), response));
    }
}
