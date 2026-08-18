package com.ntb.bookstore.dto.MaGiamGia;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KiemTraMaGiamGiaRequest {
    @NotBlank(message = "Mã giảm giá không được để trống")
    private String maGiamGia;

    @NotNull(message = "Tổng tiền không được để trống")
    private BigDecimal tongTien;
}
