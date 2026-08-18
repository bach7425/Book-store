package com.ntb.bookstore.dto;

import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private boolean thanhCong;
    private String thongBao;
    private LocalDateTime thoiGian;
    private T duLieu;

    public static <T> ApiResponse<T> of(boolean thanhCong, String thongBao, LocalDateTime thoiGian, T duLieu) {
        return ApiResponse.<T>builder()
                .thanhCong(thanhCong)
                .thongBao(thongBao)
                .thoiGian(thoiGian)
                .duLieu(duLieu)
                .build();
    }
}