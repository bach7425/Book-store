package com.ntb.bookstore.dto.QuanTri;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BaoCaoDoanhThuResponse {
    private BigDecimal tongDoanhThu;
    private Long soDonDaThanhToan;
    private LocalDate tuNgay;
    private LocalDate denNgay;
}
