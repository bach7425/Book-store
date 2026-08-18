package com.ntb.bookstore.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ton_kho")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TonKho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_ton_kho")
    private Long maTonKho;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_sach", nullable = false, unique = true)
    private Sach sach;

    @Column(name = "so_luong", nullable = false)
    @Builder.Default
    private Integer soLuong = 0;
}
