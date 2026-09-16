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

    @Column(name = "do_tuoi", length = 50)
    private String doTuoi;

    @Column(name = "ten_nha_cung_cap", length = 255)
    private String tenNhaCungCap;

    @Column(name = "nguoi_dich", length = 255)
    private String nguoiDich;

    @Column(name = "ngon_ngu", length = 100)
    private String ngonNgu;

    @Column(name = "trong_luong_gram")
    private Integer trongLuongGram;

    @Column(name = "kich_thuoc_bao_bi", length = 100)
    private String kichThuocBaoBi;

    @Column(name = "so_trang")
    private Integer soTrang;

    @Column(name = "hinh_thuc", length = 100)
    private String hinhThuc;

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
