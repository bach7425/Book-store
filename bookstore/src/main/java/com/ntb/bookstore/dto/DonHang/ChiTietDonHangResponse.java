package com.ntb.bookstore.dto.DonHang;

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
public class ChiTietDonHangResponse {
    private Long maSach;
    private String tenSach;
    private Integer soLuong;
    private BigDecimal donGia;
    private BigDecimal thanhTien;
}
