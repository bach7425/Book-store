package com.ntb.bookstore.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

import com.ntb.bookstore.dto.ApiResponse;

@RestControllerAdvice
public class XuLyException {
     @ExceptionHandler(HethongLoiException.class)
    public ResponseEntity<ApiResponse<Void>> handleSystemException(
            HethongLoiException ex) {

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .thanhCong(false)
                .thongBao(ex.getMessage())
                .duLieu(null)
                .thoiGian(LocalDateTime.now())
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<Void>> handleMaxUploadSizeExceededException(
            MaxUploadSizeExceededException ex) {

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .thanhCong(false)
                .thongBao("File upload vuot qua dung luong cho phep")
                .duLieu(null)
                .thoiGian(LocalDateTime.now())
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<ApiResponse<Void>> handleMultipartException(
            MultipartException ex) {

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .thanhCong(false)
                .thongBao("Du lieu upload khong hop le")
                .duLieu(null)
                .thoiGian(LocalDateTime.now())
                .build();

        return ResponseEntity.badRequest().body(response);
    }
    @ExceptionHandler(KhongCoDuLieuException.class)
    public ResponseEntity<ApiResponse<Void>> handleKhongCoDuLieuException(
        KhongCoDuLieuException ex) {

    ApiResponse<Void> response = ApiResponse.<Void>builder()
            .thanhCong(false)
            .thongBao(ex.getMessage())
            .build();

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
}
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidation(
            MethodArgumentNotValidException ex) {

        Map<String, String> loi = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
            loi.put(error.getField(), error.getDefaultMessage())
        );

        ApiResponse<?> response = ApiResponse.builder()
                .thanhCong(false)
                .thongBao("Dữ liệu không hợp lệ")
                .duLieu(loi)
                .build();

        return ResponseEntity.badRequest().body(response);
    }
}
