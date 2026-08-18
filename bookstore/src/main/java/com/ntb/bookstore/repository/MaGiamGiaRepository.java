package com.ntb.bookstore.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ntb.bookstore.entity.MaGiamGia;
import com.ntb.bookstore.entity.enums.TrangThaiMaGiamGia;

public interface MaGiamGiaRepository extends JpaRepository<MaGiamGia, Long> {
    Optional<MaGiamGia> findByMaCodeIgnoreCase(String maCode);

    boolean existsByMaCodeIgnoreCase(String maCode);

    Page<MaGiamGia> findByTrangThai(TrangThaiMaGiamGia trangThai, Pageable pageable);

    List<MaGiamGia> findByTrangThaiAndNgayKetThucLessThanEqual(
            TrangThaiMaGiamGia trangThai,
            LocalDateTime ngayKetThuc);
}
