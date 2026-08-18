package com.ntb.bookstore.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ntb.bookstore.dto.PageResponse;
import com.ntb.bookstore.dto.GioHang.ChiTietGioHangResponse;
import com.ntb.bookstore.dto.GioHang.GioHangResponse;
import com.ntb.bookstore.entity.ChiTietGioHang;
import com.ntb.bookstore.entity.GioHang;
import com.ntb.bookstore.entity.NguoiDung;
import com.ntb.bookstore.entity.Sach;
import com.ntb.bookstore.entity.TonKho;
import com.ntb.bookstore.exception.HethongLoiException;
import com.ntb.bookstore.exception.KhongCoDuLieuException;
import com.ntb.bookstore.repository.ChiTietGioHangRepository;
import com.ntb.bookstore.repository.GioHangRepository;
import com.ntb.bookstore.repository.NguoiDungRepository;
import com.ntb.bookstore.repository.SachRepository;
import com.ntb.bookstore.repository.TonKhoRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class GioHangService {

    private final GioHangRepository gioHangRepository;
    private final ChiTietGioHangRepository chiTietGioHangRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final SachRepository sachRepository;
    private final TonKhoRepository tonKhoRepository;

    public GioHangResponse layGioHang(Long maNguoiDung, int page, int size, String baseUrl) {
        GioHang gioHang = layHoacTaoGioHang(maNguoiDung);
        if (gioHang.getChiTietGioHangs().isEmpty()) {
            throw new KhongCoDuLieuException("Giỏ hàng trống ", gioHang.getMaGioHang());
        }
        List<String> thongBao = dongBoGioHangTheoTonKho(gioHang);
        if (!thongBao.isEmpty()) {
            chiTietGioHangRepository.flush();
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<ChiTietGioHangResponse> items = chiTietGioHangRepository
                .findByGioHangMaGioHang(gioHang.getMaGioHang(), pageable)
                .map(this::toChiTietResponse);
        return toGioHangResponse(gioHang, new PageResponse<>(items, baseUrl), thongBao);
    }

    public ChiTietGioHangResponse themSanPham(Long maNguoiDung, Long maSach, Integer soLuong) {
        if (soLuong == null || soLuong <= 0) {
            throw new HethongLoiException("Số lượng phải lớn hơn 0");
        }
        GioHang gioHang = layHoacTaoGioHang(maNguoiDung);
        Sach sach = sachRepository.findById(maSach)
                .orElseThrow(() -> new KhongCoDuLieuException("Không tìm thấy sách", maSach));

        ChiTietGioHang item = chiTietGioHangRepository
                .findByGioHangMaGioHangAndSachMaSach(gioHang.getMaGioHang(), maSach)
                .orElse(null);
        int soLuongMoi = soLuong + (item == null ? 0 : item.getSoLuong());
        kiemTraTonKho(sach, soLuongMoi);

        if (item == null) {
            item = ChiTietGioHang.builder()
                    .gioHang(gioHang)
                    .sach(sach)
                    .soLuong(soLuong)
                    .build();
            gioHang.getChiTietGioHangs().add(item);
        } else {
            item.setSoLuong(soLuongMoi);
        }
        ChiTietGioHang luu = chiTietGioHangRepository.save(item);
        return toChiTietResponse(luu);
    }

    public ChiTietGioHangResponse capNhatSoLuong(Long maNguoiDung, Long itemId, Integer soLuong) {
        if (soLuong == null || soLuong <= 0) {
            throw new HethongLoiException("Số lượng phải lớn hơn 0");
        }
        ChiTietGioHang item = laySanPhamCuaNguoiDung(maNguoiDung, itemId);
        kiemTraTonKho(item.getSach(), soLuong);
        item.setSoLuong(soLuong);
        ChiTietGioHang luu = chiTietGioHangRepository.save(item);
        return toChiTietResponse(luu);
    }

    public void xoaSanPham(Long maNguoiDung, Long itemId) {
        ChiTietGioHang item = laySanPhamCuaNguoiDung(maNguoiDung, itemId);
        GioHang gioHang = item.getGioHang();
        gioHang.getChiTietGioHangs().remove(item);
        chiTietGioHangRepository.delete(item);
    }

    private GioHang layHoacTaoGioHang(Long maNguoiDung) {
        return gioHangRepository.findByNguoiDungMaNguoiDung(maNguoiDung)
                .orElseGet(() -> {
                    NguoiDung nguoiDung = nguoiDungRepository.findById(maNguoiDung)
                            .orElseThrow(() -> new KhongCoDuLieuException("Không tìm thấy người dùng", maNguoiDung));
                    return gioHangRepository.save(GioHang.builder().nguoiDung(nguoiDung).build());
                });
    }

    private ChiTietGioHang laySanPhamCuaNguoiDung(Long maNguoiDung, Long itemId) {
        return chiTietGioHangRepository
                .findByMaChiTietGioHangAndGioHangNguoiDungMaNguoiDung(itemId, maNguoiDung)
                .orElseThrow(() -> new KhongCoDuLieuException("Không tìm thấy sản phẩm trong giỏ hàng", itemId));
    }

    private void kiemTraTonKho(Sach sach, Integer soLuong) {
        TonKho tonKho = tonKhoRepository.findBySachMaSach(sach.getMaSach())
                .orElse(TonKho.builder().sach(sach).soLuong(0).build());
        if (tonKho.getSoLuong() < soLuong) {
            throw new HethongLoiException("Số lượng không đủ cho sách " + sach.getTenSach());
        }
        if (tonKho.getSoLuong() <= 0) {
            throw new HethongLoiException("Sách hết hàng: " + sach.getTenSach());
        }
    }

    private List<String> dongBoGioHangTheoTonKho(GioHang gioHang) {
        List<String> thongBao = new ArrayList<>();
        List<ChiTietGioHang> items = new ArrayList<>(gioHang.getChiTietGioHangs());
        for (ChiTietGioHang item : items) {
            TonKho tonKho = tonKhoRepository.findBySachMaSach(item.getSach().getMaSach())
                    .orElse(TonKho.builder().sach(item.getSach()).soLuong(0).build());
            int soLuongTon = tonKho.getSoLuong() == null ? 0 : tonKho.getSoLuong();
            String tenSach = item.getSach().getTenSach();

            if (soLuongTon <= 0) {
                gioHang.getChiTietGioHangs().remove(item);
                chiTietGioHangRepository.delete(item);
                thongBao.add("Sách " + tenSach + " đã hết hàng nên đã bị xóa khỏi giỏ hàng");
            } else if (item.getSoLuong() > soLuongTon) {
                item.setSoLuong(soLuongTon);
                chiTietGioHangRepository.save(item);
                thongBao.add("Sách " + tenSach + " chỉ còn " + soLuongTon
                        + " sản phẩm, số lượng trong giỏ đã được cập nhật");
            }
        }
        return thongBao;
    }

    private GioHangResponse toGioHangResponse(GioHang gioHang, PageResponse<ChiTietGioHangResponse> items,
            List<String> thongBao) {
        int tongSoLuong = gioHang.getChiTietGioHangs().stream()
                .mapToInt(ChiTietGioHang::getSoLuong)
                .sum();
        BigDecimal tongTien = gioHang.getChiTietGioHangs().stream()
                .map(item -> item.getSach().getGia().multiply(BigDecimal.valueOf(item.getSoLuong())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return GioHangResponse.builder()
                .maGioHang(gioHang.getMaGioHang())
                .tongSoLuong(tongSoLuong)
                .tongTien(tongTien)
                .thongBao(thongBao)
                .items(items)
                .build();
    }

    private ChiTietGioHangResponse toChiTietResponse(ChiTietGioHang item) {
        return ChiTietGioHangResponse.builder()
                .maChiTietGioHang(item.getMaChiTietGioHang())
                .maSach(item.getSach().getMaSach())
                .tenSach(item.getSach().getTenSach())
                .anhBia(item.getSach().getAnhBia())
                .soLuong(item.getSoLuong())
                .donGia(item.getSach().getGia())
                .build();
    }
}
