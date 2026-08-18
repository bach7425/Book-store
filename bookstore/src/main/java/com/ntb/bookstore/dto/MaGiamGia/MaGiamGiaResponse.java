package com.ntb.bookstore.dto.MaGiamGia;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MaGiamGiaResponse {
    private Long maGiamGia;
    private String maCode;
    private String loaiGiam;
    private BigDecimal giaTri;
    private BigDecimal giamToiDa;
    private BigDecimal donToiThieu;
    private Integer soLuong;
    private Integer soLuongDaDung;
    private LocalDateTime ngayBatDau;
    private LocalDateTime ngayKetThuc;
    private String trangThai;
    private LocalDateTime ngayTao;
}
