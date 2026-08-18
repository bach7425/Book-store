package com.ntb.bookstore.dto.MaGiamGia;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CapNhatMaGiamGiaRequest {
    private String maCode;
    private String loaiGiam;
    private BigDecimal giaTri;
    private BigDecimal giamToiDa;
    private BigDecimal donToiThieu;
    private Integer soLuong;
    private LocalDateTime ngayBatDau;
    private LocalDateTime ngayKetThuc;
    private String trangThai;
}
