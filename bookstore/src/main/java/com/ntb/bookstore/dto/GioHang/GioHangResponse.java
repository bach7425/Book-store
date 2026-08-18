package com.ntb.bookstore.dto.GioHang;

import java.math.BigDecimal;
import java.util.List;

import com.ntb.bookstore.dto.PageResponse;

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
public class GioHangResponse {
    private Long maGioHang;
    private Integer tongSoLuong;
    private BigDecimal tongTien;
    private List<String> thongBao;
    private PageResponse<ChiTietGioHangResponse> items;
}
