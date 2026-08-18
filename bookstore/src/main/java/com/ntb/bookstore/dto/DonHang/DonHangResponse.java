package com.ntb.bookstore.dto.DonHang;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
public class DonHangResponse {
    private Long maDonHang;
    private Long maNguoiDung;
    private String tenKhachHang;
    private String emailKhachHang;
    private String soDienThoaiKhachHang;
    private String nguoiNhan;
    private String soDienThoaiNhan;
    private String diaChiGiaoHang;
    private String trangThai;
    private BigDecimal tongTien;
    private BigDecimal phiVanChuyen;
    private BigDecimal soTienGiam;
    private BigDecimal tongTienThanhToan;
    private String maGiamGia;
    private String phuongThucThanhToan;
    private String trangThaiThanhToan;
    private BigDecimal soTienThanhToan;
    private LocalDateTime ngayTao;
    private LocalDateTime thoiGianThanhToan;
    private List<ChiTietDonHangResponse> items;
}
