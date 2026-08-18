package com.ntb.bookstore.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ntb.bookstore.dto.PageResponse;
import com.ntb.bookstore.dto.Sach.SachResponse;
import com.ntb.bookstore.entity.NguoiDung;
import com.ntb.bookstore.entity.Sach;
import com.ntb.bookstore.exception.HethongLoiException;
import com.ntb.bookstore.exception.KhongCoDuLieuException;
import com.ntb.bookstore.repository.NguoiDungRepository;
import com.ntb.bookstore.repository.SachRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class SachYeuThichService {

    private final NguoiDungRepository nguoiDungRepository;
    private final SachRepository sachRepository;
    private final SachService sachService;

    public SachResponse themSachYeuThich(Long maNguoiDung, Long maSach) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(maNguoiDung)
                .orElseThrow(() -> new KhongCoDuLieuException("Không tìm thấy người dùng", maNguoiDung));
        Sach sach = sachRepository.findById(maSach)
                .orElseThrow(() -> new KhongCoDuLieuException("Không tìm thấy sách", maSach));
        boolean daTonTai = nguoiDung.getSachYeuThichs().stream()
                .anyMatch(item -> item.getMaSach().equals(sach.getMaSach()));
        if (daTonTai) {
            throw new HethongLoiException("Sách đã có trong danh sách yêu thích");
        }
        nguoiDung.getSachYeuThichs().add(sach);
        nguoiDungRepository.save(nguoiDung);
        return sachService.toResponse(sach);
    }

    public PageResponse<SachResponse> danhSachSachYeuThich(Long maNguoiDung, int page, int size, String baseUrl) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(maNguoiDung)
                .orElseThrow(() -> new KhongCoDuLieuException("Không tìm thấy người dùng", maNguoiDung));
        Pageable pageable = PageRequest.of(page, size);
        Page<Sach> sachPage = sachRepository.findSachYeuThichByMaNguoiDung(nguoiDung.getMaNguoiDung(), pageable);
        return new PageResponse<>(sachPage.map(sachService::toResponse), baseUrl);
    }

    public void xoaSachYeuThich(Long maNguoiDung, Long maSach) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(maNguoiDung)
                .orElseThrow(() -> new KhongCoDuLieuException("Không tìm thấy người dùng", maNguoiDung));
        Sach sach = sachRepository.findById(maSach)
                .orElseThrow(() -> new KhongCoDuLieuException("Không tìm thấy sách", maSach));
        boolean daXoa = nguoiDung.getSachYeuThichs().removeIf(item -> item.getMaSach().equals(sach.getMaSach()));
        if (!daXoa) {
            throw new HethongLoiException("Sách không còn nằm trong danh sách yêu thích");
        }
        nguoiDungRepository.save(nguoiDung);
    }

}
