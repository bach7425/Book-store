package com.ntb.bookstore.dto.MaGiamGia;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class KiemTraMaGiamGiaResponse {
    private String maGiamGia;
    private String loaiGiam;
    private BigDecimal giaTri;
    private BigDecimal soTienGiam;
    private BigDecimal phiVanChuyen;
    private BigDecimal tongTien;
    private BigDecimal tongTienThanhToan;
    private String thongBao;
}
