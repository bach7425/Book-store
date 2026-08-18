package com.ntb.bookstore.dto.Sach;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThemSachRequest {
    @NotBlank(message = "Tên sách không được để trống")
    private String tenSach;
    private String moTa;
    @NotNull(message = "Giá sách không được để trống")
    @DecimalMin(value = "0.00", message = "Giá sách không được âm")
    private BigDecimal gia;
    private String nhaXuatBan;
    private Long maTacGia;
    private List<Long> maTheLoai;
    @Min(value = 0, message = "Tồn kho không được âm")
    private Integer soLuongTon;
        
}
