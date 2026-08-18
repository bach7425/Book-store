package com.ntb.bookstore.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ntb.bookstore.entity.NguoiDung;
import com.ntb.bookstore.entity.enums.VaiTro;

public interface NguoiDungRepository extends JpaRepository<NguoiDung, Long> {
    public Optional<NguoiDung> findByTenDangNhap(String tenDangNhap);
    public boolean existsByTenDangNhap(String tenDangNhap);
    public boolean existsByEmail(String email);
    public boolean existsBySoDienThoai(String soDienThoai);
    public List<NguoiDung> findByVaiTro(VaiTro vaiTro);

    @Query("select n from NguoiDung n " +
            "where n.vaiTro = :vaiTro " +
            "and (:tuKhoa is null or :tuKhoa = '' " +
            "or lower(n.hoVaTen) like lower(concat('%', :tuKhoa, '%')) " +
            "or lower(n.email) like lower(concat('%', :tuKhoa, '%')) " +
            "or lower(n.tenDangNhap) like lower(concat('%', :tuKhoa, '%')) " +
            "or lower(n.soDienThoai) like lower(concat('%', :tuKhoa, '%')))")
    Page<NguoiDung> timKiemTheoVaiTro(@Param("vaiTro") VaiTro vaiTro, @Param("tuKhoa") String tuKhoa,
            Pageable pageable);

    Optional<NguoiDung> findByMaNguoiDungAndVaiTro(Long maNguoiDung, VaiTro vaiTro);
}
