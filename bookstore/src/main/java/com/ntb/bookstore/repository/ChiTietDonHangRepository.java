package com.ntb.bookstore.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ntb.bookstore.entity.ChiTietDonHang;
import com.ntb.bookstore.entity.enums.TrangThaiDonHang;
import com.ntb.bookstore.entity.enums.TrangThaiThanhToan;
import com.ntb.bookstore.repository.projection.SachBanChayProjection;

public interface ChiTietDonHangRepository extends JpaRepository<ChiTietDonHang, Long> {

    @Query(value = "select ct.sach.maSach as maSach, ct.sach.tenSach as tenSach, ct.sach.anhBia as anhBia, " +
            "ct.sach.tacGia.ten as tacGia, sum(ct.soLuong) as soLuongBan, coalesce(sum(ct.thanhTien), 0) as doanhThu " +
            "from ChiTietDonHang ct join ct.donHang d join d.thanhToan t " +
            "where t.trangThai = :trangThaiThanhToan " +
            "and d.trangThai = :trangThaiDonHang " +
            "and t.thoiGianThanhToan >= :tuNgay " +
            "and t.thoiGianThanhToan < :denNgay " +
            "group by ct.sach.maSach, ct.sach.tenSach, ct.sach.anhBia, ct.sach.tacGia.ten " +
            "order by sum(ct.soLuong) desc",
            countQuery = "select count(distinct ct.sach.maSach) " +
                    "from ChiTietDonHang ct join ct.donHang d join d.thanhToan t " +
                    "where t.trangThai = :trangThaiThanhToan " +
                    "and d.trangThai = :trangThaiDonHang " +
                    "and t.thoiGianThanhToan >= :tuNgay " +
                    "and t.thoiGianThanhToan < :denNgay")
    Page<SachBanChayProjection> thongKeSachBanChay(
            @Param("trangThaiThanhToan") TrangThaiThanhToan trangThaiThanhToan,
            @Param("trangThaiDonHang") TrangThaiDonHang trangThaiDonHang,
            @Param("tuNgay") LocalDateTime tuNgay,
            @Param("denNgay") LocalDateTime denNgay,
            Pageable pageable);
}
