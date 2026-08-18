package com.ntb.bookstore.dto.QuanTri;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SachBanChayResponse {
    private Long maSach;
    private String tenSach;
    private String anhBia;
    private String tacGia;
    private Long soLuongBan;
    private BigDecimal doanhThu;
}
