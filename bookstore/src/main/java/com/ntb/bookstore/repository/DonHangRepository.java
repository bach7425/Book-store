package com.ntb.bookstore.repository;

import java.util.List;
import java.util.Optional;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ntb.bookstore.entity.DonHang;
import com.ntb.bookstore.entity.NguoiDung;
import com.ntb.bookstore.entity.enums.TrangThaiDonHang;
import com.ntb.bookstore.entity.enums.TrangThaiThanhToan;

public interface DonHangRepository extends JpaRepository<DonHang, Long> {
    Page<DonHang> findByNguoiDung(NguoiDung nguoiDung, Pageable pageable);
    Page<DonHang> findByTrangThai(TrangThaiDonHang trangThai, Pageable pageable);
    Page<DonHang> findByNguoiDungAndTrangThai(NguoiDung nguoiDung, TrangThaiDonHang trangThai, Pageable pageable);
    List<DonHang> findByNguoiDung(NguoiDung nguoiDung);

    @Query(value = "select d from DonHang d " +
            "left join fetch d.nguoiDung " +
            "left join fetch d.diaChi " +
            "left join fetch d.thanhToan " +
            "left join fetch d.maGiamGia",
            countQuery = "select count(d) from DonHang d")
    Page<DonHang> findAllForQuanTri(Pageable pageable);

    @Query(value = "select d from DonHang d " +
            "left join fetch d.nguoiDung " +
            "left join fetch d.diaChi " +
            "left join fetch d.thanhToan " +
            "left join fetch d.maGiamGia " +
            "where d.trangThai = :trangThai",
            countQuery = "select count(d) from DonHang d where d.trangThai = :trangThai")
    Page<DonHang> findByTrangThaiForQuanTri(@Param("trangThai") TrangThaiDonHang trangThai, Pageable pageable);

    @Query("select distinct d from DonHang d " +
            "left join fetch d.chiTietDonHangs ct " +
            "left join fetch ct.sach " +
            "left join fetch d.nguoiDung " +
            "left join fetch d.diaChi " +
            "left join fetch d.thanhToan " +
            "left join fetch d.maGiamGia " +
            "where d.maDonHang = :maDonHang")
    Optional<DonHang> findByIdWithChiTiet(@Param("maDonHang") Long maDonHang);

    Long countByNguoiDung(NguoiDung nguoiDung);

    @Query("select coalesce(sum(t.soTien), 0) from DonHang d join d.thanhToan t " +
            "where d.nguoiDung = :nguoiDung and t.trangThai = :trangThai")
    BigDecimal tinhTongChiTieu(@Param("nguoiDung") NguoiDung nguoiDung,
            @Param("trangThai") TrangThaiThanhToan trangThai);
}
