package com.ntb.bookstore.entity;

import java.util.*;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tac_gia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TacGia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_tac_gia")
    private Long maTacGia;

    @Column(nullable = false, length = 150)
    private String ten;

    @Column(columnDefinition = "TEXT")
    private String tieuSu;

    @Column(columnDefinition = "TEXT")
    private String anhDaiDien;

    @OneToMany(mappedBy = "tacGia")
    @Builder.Default
    private List<Sach> sachs = new ArrayList<>();
}