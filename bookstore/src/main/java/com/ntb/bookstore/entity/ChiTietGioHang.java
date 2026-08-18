package com.ntb.bookstore.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "chi_tiet_gio_hang",
    uniqueConstraints = {
        @UniqueConstraint(
            columnNames = {"ma_gio_hang", "ma_sach"}
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChiTietGioHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_chi_tiet_gio_hang")
    private Long maChiTietGioHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_gio_hang", nullable = false)
    private GioHang gioHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_sach", nullable = false)
    private Sach sach;

    @Column(nullable = false)
    private Integer soLuong;
}