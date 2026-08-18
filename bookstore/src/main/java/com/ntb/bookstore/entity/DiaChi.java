package com.ntb.bookstore.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "dia_chi")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiaChi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_dia_chi")
    private Long maDiaChi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_nguoi_dung", nullable = false)
    private NguoiDung nguoiDung;

    @Column(name = "nguoi_nhan", nullable = false, length = 100)
    private String nguoiNhan;

    @Column(name = "so_dien_thoai", nullable = false, length = 20)
    private String soDienThoai;

    @Column(name = "dia_chi_chi_tiet", nullable = false, columnDefinition = "TEXT")
    private String diaChiChiTiet;

    @Column(name = "mac_dinh", nullable = false)
    @Builder.Default
    private Boolean macDinh = false;
}