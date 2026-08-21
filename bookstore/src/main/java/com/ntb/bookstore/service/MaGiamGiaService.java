package com.ntb.bookstore.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.ntb.bookstore.dto.PageResponse;
import com.ntb.bookstore.dto.MaGiamGia.CapNhatMaGiamGiaRequest;
import com.ntb.bookstore.dto.MaGiamGia.MaGiamGiaResponse;
import com.ntb.bookstore.dto.MaGiamGia.TaoMaGiamGiaRequest;
import com.ntb.bookstore.entity.MaGiamGia;
import com.ntb.bookstore.entity.enums.LoaiGiamGia;
import com.ntb.bookstore.entity.enums.TrangThaiMaGiamGia;
import com.ntb.bookstore.exception.HethongLoiException;
import com.ntb.bookstore.exception.KhongCoDuLieuException;
import com.ntb.bookstore.repository.MaGiamGiaRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class MaGiamGiaService {

    private final MaGiamGiaRepository maGiamGiaRepository;

    public PageResponse<MaGiamGiaResponse> danhSachMaGiamGia(String trangThai, int page, int size, String baseUrl) {
        capNhatMaHetHan();
        Pageable pageable = PageRequest.of(page, size, Sort.by("ngayTao").descending());
        Page<MaGiamGia> maGiamGias;
        if (trangThai == null || trangThai.isBlank()) {
            maGiamGias = maGiamGiaRepository.findAll(pageable);
        } else {

            maGiamGias = maGiamGiaRepository.findByTrangThai(kiemTraTrangThai(trangThai), pageable);
        }
        return new PageResponse<>(maGiamGias.map(this::toResponse), baseUrl);
    }

    public MaGiamGiaResponse chiTietMaGiamGia(Long maGiamGiaId) {
        MaGiamGia maGiamGia = maGiamGiaRepository.findById(maGiamGiaId)
                .orElseThrow(() -> new KhongCoDuLieuException("Không tìm thấy mã giảm giá", maGiamGiaId));
        return toResponse(maGiamGia);
    }

    public MaGiamGiaResponse taoMaGiamGia(TaoMaGiamGiaRequest request) {
        String maCode = layMaCodeHopLe(request.getMaCode());
        if (maGiamGiaRepository.existsByMaCodeIgnoreCase(maCode)) {
            throw new HethongLoiException("Mã giảm giá " + maCode + " đã tồn tại");
        }

        MaGiamGia maGiamGia = MaGiamGia.builder()
                .maCode(maCode)
                .loaiGiam(kiemTraLoaiGiam(request.getLoaiGiam()))
                .giaTri(request.getGiaTri())
                .giamToiDa(request.getGiamToiDa())
                .donToiThieu(request.getDonToiThieu() == null ? BigDecimal.ZERO : request.getDonToiThieu())
                .soLuong(request.getSoLuong())
                .soLuongDaDung(0)
                .ngayBatDau(request.getNgayBatDau())
                .ngayKetThuc(request.getNgayKetThuc())
                .trangThai(request.getTrangThai() == null || request.getTrangThai().isBlank()
                        ? TrangThaiMaGiamGia.HOAT_DONG
                        : kiemTraTrangThaiQuanTri(request.getTrangThai()))
                .build();
        kiemTraMaGiamGia(maGiamGia);
        return toResponse(maGiamGiaRepository.save(maGiamGia));
    }

    public MaGiamGiaResponse capNhatMaGiamGia(Long maGiamGiaId, CapNhatMaGiamGiaRequest request) {
        MaGiamGia maGiamGia = maGiamGiaRepository.findById(maGiamGiaId)
                .orElseThrow(() -> new KhongCoDuLieuException("Không tìm thấy mã giảm giá", maGiamGiaId));
        if (request.getMaCode() != null) {
            String maCode = layMaCodeHopLe(request.getMaCode());
            if (!maGiamGia.getMaCode().equalsIgnoreCase(maCode)
                    && maGiamGiaRepository.existsByMaCodeIgnoreCase(maCode)) {
                throw new HethongLoiException("Mã giảm giá " + maCode + " đã tồn tại");
            }
            maGiamGia.setMaCode(maCode);
        }
        if (request.getLoaiGiam() != null) {
            maGiamGia.setLoaiGiam(kiemTraLoaiGiam(request.getLoaiGiam()));
        }
        if (request.getGiaTri() != null) {
            maGiamGia.setGiaTri(request.getGiaTri());
        }
        if (request.getGiamToiDa() != null) {
            maGiamGia.setGiamToiDa(request.getGiamToiDa());
        }
        if (request.getDonToiThieu() != null) {
            maGiamGia.setDonToiThieu(request.getDonToiThieu());
        }
        if (request.getSoLuong() != null) {
            maGiamGia.setSoLuong(request.getSoLuong());
        }
        if (request.getNgayBatDau() != null) {
            maGiamGia.setNgayBatDau(request.getNgayBatDau());
        }
        if (request.getNgayKetThuc() != null) {
            maGiamGia.setNgayKetThuc(request.getNgayKetThuc());
        }
        if (request.getTrangThai() != null) {
            maGiamGia.setTrangThai(kiemTraTrangThaiQuanTri(request.getTrangThai()));
        }

        kiemTraMaGiamGia(maGiamGia);
        return toResponse(maGiamGiaRepository.save(maGiamGia));
    }

    public void xoaMaGiamGia(Long maGiamGiaId) {
        MaGiamGia maGiamGia = maGiamGiaRepository.findById(maGiamGiaId)
                .orElseThrow(() -> new KhongCoDuLieuException("Không tìm thấy mã giảm giá", maGiamGiaId));
        if (maGiamGia.getTrangThai() == TrangThaiMaGiamGia.NGUNG) {
            throw new HethongLoiException("Mã giảm giá đã ngưng hoạt động");
        }
        maGiamGia.setTrangThai(TrangThaiMaGiamGia.NGUNG);
        maGiamGiaRepository.save(maGiamGia);
    }

    private String layMaCodeHopLe(String maCode) {
        if (maCode == null || maCode.isBlank()) {
            throw new HethongLoiException("Mã voucher không được để trống");
        }
        return maCode.trim();
    }

    private LoaiGiamGia kiemTraLoaiGiam(String loaiGiam) {
        if (loaiGiam == null || loaiGiam.isBlank()) {
            throw new HethongLoiException("Loại giảm giá không được để trống");
        }
        try {
            return LoaiGiamGia.valueOf(loaiGiam.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new HethongLoiException("Loại giảm giá không hợp lệ: " + loaiGiam);
        }
    }

    private TrangThaiMaGiamGia kiemTraTrangThai(String trangThai) {
        if (trangThai == null || trangThai.isBlank()) {
            throw new HethongLoiException("Trạng thái mã giảm giá không được để trống");
        }
        try {
            return TrangThaiMaGiamGia.valueOf(trangThai.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new HethongLoiException("Trạng thái mã giảm giá không hợp lệ: " + trangThai);
        }
    }

    private TrangThaiMaGiamGia kiemTraTrangThaiQuanTri(String trangThai) {
        TrangThaiMaGiamGia trangThaiHopLe = kiemTraTrangThai(trangThai);
        if (trangThaiHopLe == TrangThaiMaGiamGia.HET_HAN) {
            throw new HethongLoiException("Trạng thái hết hạn chỉ được hệ thống tự cập nhật");
        }
        return trangThaiHopLe;
    }

    private void capNhatMaHetHan() {
        List<MaGiamGia> maHetHans = maGiamGiaRepository.findByTrangThaiAndNgayKetThucLessThanEqual(
                TrangThaiMaGiamGia.HOAT_DONG,
                LocalDateTime.now());
        if (maHetHans.isEmpty()) {
            return;
        }
        maHetHans.forEach(maGiamGia -> maGiamGia.setTrangThai(TrangThaiMaGiamGia.HET_HAN));
        maGiamGiaRepository.saveAll(maHetHans);
    }

    private void kiemTraMaGiamGia(MaGiamGia maGiamGia) {
        if (maGiamGia.getGiaTri() == null || maGiamGia.getGiaTri().compareTo(BigDecimal.ZERO) <= 0) {
            throw new HethongLoiException("Giá trị giảm giá phải lớn hơn 0");
        }
        if (maGiamGia.getGiamToiDa() != null && maGiamGia.getGiamToiDa().compareTo(BigDecimal.ZERO) < 0) {
            throw new HethongLoiException("Giảm tối đa không được âm");
        }
        if (maGiamGia.getDonToiThieu() != null && maGiamGia.getDonToiThieu().compareTo(BigDecimal.ZERO) < 0) {
            throw new HethongLoiException("Đơn tối thiểu không được âm");
        }
        if (maGiamGia.getSoLuong() == null || maGiamGia.getSoLuong() < 0) {
            throw new HethongLoiException("Số lượng mã giảm giá không được âm");
        }
        if (maGiamGia.getNgayBatDau() == null || maGiamGia.getNgayKetThuc() == null) {
            throw new HethongLoiException("Ngày bắt đầu và ngày kết thúc là bắt buộc");
        }
        if (!maGiamGia.getNgayKetThuc().isAfter(maGiamGia.getNgayBatDau())) {
            throw new HethongLoiException("Ngày kết thúc phải sau ngày bắt đầu");
        }
    }

    private MaGiamGiaResponse toResponse(MaGiamGia maGiamGia) {
        return MaGiamGiaResponse.builder()
                .maGiamGia(maGiamGia.getMaGiamGia())
                .maCode(maGiamGia.getMaCode())
                .loaiGiam(maGiamGia.getLoaiGiam().name())
                .giaTri(maGiamGia.getGiaTri())
                .giamToiDa(maGiamGia.getGiamToiDa())
                .donToiThieu(maGiamGia.getDonToiThieu())
                .soLuong(maGiamGia.getSoLuong())
                .soLuongDaDung(maGiamGia.getSoLuongDaDung())
                .ngayBatDau(maGiamGia.getNgayBatDau())
                .ngayKetThuc(maGiamGia.getNgayKetThuc())
                .trangThai(maGiamGia.getTrangThai().name())
                .ngayTao(maGiamGia.getNgayTao())
                .build();
    }
}
