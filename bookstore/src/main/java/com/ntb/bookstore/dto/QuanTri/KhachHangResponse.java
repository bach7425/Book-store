package com.ntb.bookstore.dto.QuanTri;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class KhachHangResponse {
    private Long maNguoiDung;
    private String hoVaTen;
    private String email;
    private String tenDangNhap;
    private String soDienThoai;
    private String anhDaiDien;
    private String vaiTro;
    private Long soDonHang;
    private BigDecimal tongChiTieu;
}
