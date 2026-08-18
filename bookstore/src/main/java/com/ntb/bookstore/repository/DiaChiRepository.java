package com.ntb.bookstore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ntb.bookstore.entity.DiaChi;

public interface DiaChiRepository extends JpaRepository<DiaChi, Long> {
    List<DiaChi> findByNguoiDungMaNguoiDung(Long maNguoiDung);
}
