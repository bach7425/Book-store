package com.ntb.bookstore.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ntb.bookstore.entity.ThanhToan;
import com.ntb.bookstore.entity.enums.TrangThaiDonHang;
import com.ntb.bookstore.entity.enums.TrangThaiThanhToan;

public interface ThanhToanRepository extends JpaRepository<ThanhToan, Long> {

    Long countByTrangThaiAndThoiGianThanhToanGreaterThanEqualAndThoiGianThanhToanLessThan(
            TrangThaiThanhToan trangThai, LocalDateTime tuNgay, LocalDateTime denNgay);

    Long countByTrangThaiAndDonHangTrangThaiAndThoiGianThanhToanGreaterThanEqualAndThoiGianThanhToanLessThan(
            TrangThaiThanhToan trangThaiThanhToan,
            TrangThaiDonHang trangThaiDonHang,
            LocalDateTime tuNgay,
            LocalDateTime denNgay);

    @Query("select coalesce(sum(t.soTien), 0) from ThanhToan t " +
            "where t.trangThai = :trangThai " +
            "and t.thoiGianThanhToan >= :tuNgay " +
            "and t.thoiGianThanhToan < :denNgay")
    BigDecimal tinhTongDoanhThu(@Param("trangThai") TrangThaiThanhToan trangThai,
            @Param("tuNgay") LocalDateTime tuNgay,
            @Param("denNgay") LocalDateTime denNgay);

    @Query("select coalesce(sum(t.soTien), 0) from ThanhToan t " +
            "where t.trangThai = :trangThaiThanhToan " +
            "and t.donHang.trangThai = :trangThaiDonHang " +
            "and t.thoiGianThanhToan >= :tuNgay " +
            "and t.thoiGianThanhToan < :denNgay")
    BigDecimal tinhTongDoanhThuDonDaGiao(@Param("trangThaiThanhToan") TrangThaiThanhToan trangThaiThanhToan,
            @Param("trangThaiDonHang") TrangThaiDonHang trangThaiDonHang,
            @Param("tuNgay") LocalDateTime tuNgay,
            @Param("denNgay") LocalDateTime denNgay);
}
