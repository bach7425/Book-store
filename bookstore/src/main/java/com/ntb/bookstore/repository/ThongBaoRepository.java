package com.ntb.bookstore.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ntb.bookstore.entity.NguoiDung;
import com.ntb.bookstore.entity.ThongBao;

public interface ThongBaoRepository extends JpaRepository<ThongBao, Long> {
    Page<ThongBao> findByNguoiDungOrderByNgayTaoDesc(NguoiDung nguoiDung, Pageable pageable);

    Long countByNguoiDungAndDaDocFalse(NguoiDung nguoiDung);

    List<ThongBao> findByNguoiDungAndDaDocFalse(NguoiDung nguoiDung);
}
