package com.ntb.bookstore.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.ntb.bookstore.entity.enums.TrangThaiDonHang;

@Entity
@Table(name = "don_hang")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DonHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_don_hang")
    private Long maDonHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_nguoi_dung", nullable = false)
    private NguoiDung nguoiDung;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_dia_chi", nullable = false)
    private DiaChi diaChi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_giam_gia")
    private MaGiamGia maGiamGia;

    @Column(name = "tong_tien", nullable = false, precision = 15, scale = 2)
    private BigDecimal tongTien;

    @Column(name = "phi_van_chuyen", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal phiVanChuyen = BigDecimal.ZERO;

    @Column(name = "so_tien_giam", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal soTienGiam = BigDecimal.ZERO;

    @Column(name = "tong_tien_thanh_toan", nullable = false, precision = 15, scale = 2)
    private BigDecimal tongTienThanhToan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private TrangThaiDonHang trangThai = TrangThaiDonHang.CHO_XU_LY;

    @OneToMany(mappedBy = "donHang", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ChiTietDonHang> chiTietDonHangs = new ArrayList<>();

    @OneToOne(mappedBy = "donHang", cascade = CascadeType.ALL)
    private ThanhToan thanhToan;

    @OneToMany(mappedBy = "donHang")
    @Builder.Default
    private List<LichSuDonHang> lichSuDonHangs = new ArrayList<>();

}
