package com.ntb.bookstore.dto.GioHang;

import java.math.BigDecimal;

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
public class ChiTietGioHangResponse {
    private Long maChiTietGioHang;
    private Long maSach;
    private String tenSach;
    private String anhBia;
    private Integer soLuong;
    private BigDecimal donGia;
}
