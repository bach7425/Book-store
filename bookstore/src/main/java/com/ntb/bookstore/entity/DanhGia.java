package com.ntb.bookstore.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import com.ntb.bookstore.entity.enums.TrangThaiDanhGia;

@Entity
@Table(name = "danh_gia", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "ma_nguoi_dung", "ma_sach" })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DanhGia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_danh_gia")
    private Long maDanhGia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_nguoi_dung", nullable = false)
    private NguoiDung nguoiDung;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_sach", nullable = false)
    private Sach sach;

    @Column(name = "so_sao", nullable = false)
    private Integer soSao;

    @Column(columnDefinition = "TEXT")
    private String noiDung;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private TrangThaiDanhGia trangThai = TrangThaiDanhGia.CHO_DUYET;

    @Column(name = "phan_hoi", columnDefinition = "TEXT")
    private String phanHoi;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @PrePersist
    protected void khiTao() {
        ngayTao = LocalDateTime.now();
    }

    @PreUpdate
    protected void khiCapNhat() {
        ngayCapNhat = LocalDateTime.now();
    }
}
