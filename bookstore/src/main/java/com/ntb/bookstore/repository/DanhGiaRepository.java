package com.ntb.bookstore.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ntb.bookstore.entity.DanhGia;
import com.ntb.bookstore.entity.NguoiDung;
import com.ntb.bookstore.entity.Sach;
import com.ntb.bookstore.entity.enums.TrangThaiDanhGia;

public interface DanhGiaRepository extends JpaRepository<DanhGia, Long> {
    Page<DanhGia> findBySach(Sach sach, Pageable pageable);

    Page<DanhGia> findBySachAndTrangThai(Sach sach, TrangThaiDanhGia trangThai, Pageable pageable);

    Page<DanhGia> findByTrangThai(TrangThaiDanhGia trangThai, Pageable pageable);

    @Query("select d from DanhGia d where d.sach = :sach and (d.trangThai = :trangThai or d.nguoiDung = :nguoiDung)")
    Page<DanhGia> findDanhGiaHienThiChoNguoiDung(@Param("sach") Sach sach,
            @Param("trangThai") TrangThaiDanhGia trangThai,
            @Param("nguoiDung") NguoiDung nguoiDung,
            Pageable pageable);

    Optional<DanhGia> findByNguoiDungAndSach(NguoiDung nguoiDung, Sach sach);

    @Query("select avg(d.soSao) from DanhGia d where d.sach.maSach = :maSach and d.trangThai = :trangThai")
    Double tinhDiemDanhGiaTrungBinh(@Param("maSach") Long maSach,
            @Param("trangThai") TrangThaiDanhGia trangThai);

    Long countBySachMaSachAndTrangThai(Long maSach, TrangThaiDanhGia trangThai);
}
