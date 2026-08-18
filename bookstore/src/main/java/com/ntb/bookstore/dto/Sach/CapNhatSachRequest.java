package com.ntb.bookstore.dto.Sach;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CapNhatSachRequest {
    private String tenSach;
    private String moTa;
    @DecimalMin(value = "0.00", message = "Giá sách không được âm")
    private BigDecimal gia;
    private String nhaXuatBan;
    private Long maTacGia;
    private List<Long> maTheLoai;
    @Min(value = 0, message = "Tồn kho không được âm")
    private Integer soLuongTon;
        
}
