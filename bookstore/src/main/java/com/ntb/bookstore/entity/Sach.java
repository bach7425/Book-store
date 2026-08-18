package com.ntb.bookstore.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sach")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sach {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_sach")
    private Long maSach;

    @Column(name = "ten_sach", nullable = false, length = 255)
    private String tenSach;

    @Column(columnDefinition = "TEXT")
    private String moTa;

    @Column(nullable = false, precision = 15)
    private BigDecimal gia;

    @Column(name = "anh_bia", length = 500)
    private String anhBia;

    @Column(name = "nha_xuat_ban", length = 150)
    private String nhaXuatBan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_tac_gia", nullable = false)
    private TacGia tacGia;

    @Column(name = "ngay_xuat_ban")
    private LocalDate ngayXuatBan;

    @ManyToMany
    @JoinTable(name = "sach_the_loai", joinColumns = @JoinColumn(name = "ma_sach"), inverseJoinColumns = @JoinColumn(name = "ma_the_loai"))
    @Builder.Default
    private List<TheLoai> theLoais = new ArrayList<>();

    @OneToOne(mappedBy = "sach", cascade = CascadeType.ALL)
    private TonKho tonKho;

    @OneToMany(mappedBy = "sach")
    @Builder.Default
    private List<ChiTietGioHang> chiTietGioHangs = new ArrayList<>();

    @OneToMany(mappedBy = "sach")
    @Builder.Default
    private List<DanhGia> danhGias = new ArrayList<>();

    @ManyToMany(mappedBy = "sachYeuThichs")
    @Builder.Default
    private List<NguoiDung> nguoiDungYeuThichs = new ArrayList<>();

}
