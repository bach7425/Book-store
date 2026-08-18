package com.ntb.bookstore.entity;

import java.util.*;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "the_loai")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TheLoai {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_the_loai")
    private Long maTheLoai;

    @Column(nullable = false, length = 100)
    private String ten;

    @Column(columnDefinition = "TEXT")
    private String moTa;

    @ManyToMany(mappedBy = "theLoais")
    @Builder.Default
    private List<Sach> sachs = new ArrayList<>();
}