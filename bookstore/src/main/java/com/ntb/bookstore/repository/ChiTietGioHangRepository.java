package com.ntb.bookstore.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ntb.bookstore.entity.ChiTietGioHang;

public interface ChiTietGioHangRepository extends JpaRepository<ChiTietGioHang, Long> {
    void deleteByGioHangMaGioHang(Long maGioHang);
    Page<ChiTietGioHang> findByGioHangMaGioHang(Long maGioHang, Pageable pageable);
    Optional<ChiTietGioHang> findByGioHangMaGioHangAndSachMaSach(Long maGioHang, Long maSach);
    Optional<ChiTietGioHang> findByMaChiTietGioHangAndGioHangNguoiDungMaNguoiDung(Long maChiTietGioHang, Long maNguoiDung);
}
