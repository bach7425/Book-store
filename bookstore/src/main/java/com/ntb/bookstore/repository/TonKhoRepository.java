package com.ntb.bookstore.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ntb.bookstore.entity.TonKho;

public interface TonKhoRepository extends JpaRepository<TonKho, Long> {
    Optional<TonKho> findBySachMaSach(Long maSach);
}
