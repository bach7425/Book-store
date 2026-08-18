package com.ntb.bookstore.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ntb.bookstore.entity.GioHang;


public interface GioHangRepository extends JpaRepository<GioHang, Long> {
    Optional<GioHang> findByNguoiDungMaNguoiDung(Long maNguoiDung);
}
