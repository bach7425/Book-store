package com.ntb.bookstore.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.ntb.bookstore.entity.enums.*;

@Entity
@Table(name = "ma_giam_gia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaGiamGia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_giam_gia", nullable = false)
    private Long maGiamGia;

    @Column(nullable = false, unique = true, length = 50)
    private String maCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_giam", nullable = false, length = 20)
    private LoaiGiamGia loaiGiam;

    @Column(name = "gia_tri", nullable = false, precision = 15, scale = 2)
    private BigDecimal giaTri;

    @Column(name = "giam_toi_da", precision = 15, scale = 2)
    private BigDecimal giamToiDa;

    @Column(name = "don_toi_thieu", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal donToiThieu = BigDecimal.ZERO;

    @Column(name = "so_luong", nullable = false)
    private Integer soLuong;

    @Column(name = "so_luong_da_dung", nullable = false)
    @Builder.Default
    private Integer soLuongDaDung = 0;

    @Column(name = "ngay_bat_dau", nullable = false)
    private LocalDateTime ngayBatDau;

    @Column(name = "ngay_ket_thuc", nullable = false)
    private LocalDateTime ngayKetThuc;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private TrangThaiMaGiamGia trangThai =
            TrangThaiMaGiamGia.HOAT_DONG;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @OneToMany(mappedBy = "maGiamGia")
    @Builder.Default
    private List<DonHang> donHangs =
            new ArrayList<>();

    @PrePersist
    protected void khiTao() {
        ngayTao = LocalDateTime.now();
    }
}
