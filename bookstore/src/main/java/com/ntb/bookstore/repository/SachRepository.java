package com.ntb.bookstore.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ntb.bookstore.entity.Sach;

public interface SachRepository extends JpaRepository<Sach, Long> {

        @Query("select distinct s from Sach s " +
                        "left join fetch s.tacGia tg " +
                        "left join s.theLoais tl " +
                        "where (:tuKhoa is null or :tuKhoa = '' or lower(s.tenSach) like lower(concat('%', :tuKhoa, '%')) "
                        +
                        "or lower(s.moTa) like lower(concat('%', :tuKhoa, '%')) " +
                        "or lower(tg.ten) like lower(concat('%', :tuKhoa, '%'))) " +
                        "and (:tacGiaId is null or tg.maTacGia = :tacGiaId) " +
                        "and (:theLoaiId is null or tl.maTheLoai = :theLoaiId) " +
                        "and (:giaMin is null or s.gia >= :giaMin) " +
                        "and (:giaMax is null or s.gia <= :giaMax)")
        Page<Sach> timKiemSach(@Param("tuKhoa") String tuKhoa,
                        @Param("tacGiaId") Long tacGiaId,
                        @Param("theLoaiId") Long theLoaiId,
                        @Param("giaMin") BigDecimal giaMin,
                        @Param("giaMax") BigDecimal giaMax,
                        Pageable pageable);

        @Query("""
                            select distinct s
                            from Sach s
                            left join fetch s.tacGia
                            left join fetch s.theLoais
                        """)
        List<Sach> findAllForRag();

        @Query("select s from NguoiDung n join n.sachYeuThichs s where n.maNguoiDung = :maNguoiDung")
        Page<Sach> findSachYeuThichByMaNguoiDung(@Param("maNguoiDung") Long maNguoiDung, Pageable pageable);
}
