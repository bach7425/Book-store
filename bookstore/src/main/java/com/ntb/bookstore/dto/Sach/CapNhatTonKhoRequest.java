package com.ntb.bookstore.dto.Sach;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CapNhatTonKhoRequest {
    @Min(value = 0, message = "Tồn kho không được âm")
    private Integer soLuong;
}
