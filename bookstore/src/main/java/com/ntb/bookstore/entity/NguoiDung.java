package com.ntb.bookstore.entity;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.ntb.bookstore.entity.enums.VaiTro;

import java.util.ArrayList;
import java.util.Collection;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "nguoi_dung")
public class NguoiDung implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_nguoi_dung")
    private Long maNguoiDung;
    @Column(name = "ho_va_ten", length = 100)
    private String hoVaTen;
    @Column(nullable = false, unique = true, length = 100)
    private String email;
    @Column(name = "ten_dang_nhap", unique = true, nullable = false, length = 100)
    private String tenDangNhap;
    @Column(name = "mat_khau_bam", nullable = false, length = 255)
    private String matKhauBam;
    @Enumerated(EnumType.STRING)
    @Column(name = "vai_tro", nullable = false, length = 20)
    @Builder.Default
    private VaiTro vaiTro = VaiTro.NGUOI_DUNG;
    @Column(name = "so_dien_thoai", length = 20)
    private String soDienThoai;
    @Column(name = "anh_dai_dien", length = 500)
    private String anhDaiDien;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + vaiTro.name()));
    }

    @Override
    public String getPassword() {
        return matKhauBam;
    }

    @Override
    public String getUsername() {
        return tenDangNhap;
    }

    @OneToMany(mappedBy = "nguoiDung", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DiaChi> diaChis = new ArrayList<>();

    @OneToOne(mappedBy = "nguoiDung", cascade = CascadeType.ALL, orphanRemoval = true)
    private GioHang gioHang;

    @OneToMany(mappedBy = "nguoiDung")
    @Builder.Default
    private List<DonHang> donHangs = new ArrayList<>();

    @OneToMany(mappedBy = "nguoiDung")
    @Builder.Default
    private List<DanhGia> danhGias = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "sach_yeu_thich", joinColumns = @JoinColumn(name = "ma_nguoi_dung"), inverseJoinColumns = @JoinColumn(name = "ma_sach"))
    @Builder.Default
    private List<Sach> sachYeuThichs = new ArrayList<>();

    @OneToMany(mappedBy = "nguoiCapNhat")
    @Builder.Default
    private List<LichSuDonHang> lichSuDonHangs = new ArrayList<>();
}
