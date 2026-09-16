package com.ntb.bookstore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ntb.bookstore.dto.GioHang.ChiTietGioHangResponse;
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

@ExtendWith(MockitoExtension.class)
class GioHangServiceTest {

    @Mock
    private GioHangRepository gioHangRepository;
    @Mock
    private ChiTietGioHangRepository chiTietGioHangRepository;
    @Mock
    private NguoiDungRepository nguoiDungRepository;
    @Mock
    private SachRepository sachRepository;
    @Mock
    private TonKhoRepository tonKhoRepository;

    private GioHangService service;

    @BeforeEach
    void setUp() {
        service = new GioHangService(
                gioHangRepository,
                chiTietGioHangRepository,
                nguoiDungRepository,
                sachRepository,
                tonKhoRepository);
    }

    @Test
    void themSanPham_sachMoi_thiThemVaoGioHang() {
        NguoiDung user = nguoiDung(2L);
        GioHang gioHang = GioHang.builder().maGioHang(20L).nguoiDung(user).build();
        Sach sach = sach(3L, "Clean Code", "420000");

        when(gioHangRepository.findByNguoiDungMaNguoiDung(2L)).thenReturn(Optional.of(gioHang));
        when(sachRepository.findById(3L)).thenReturn(Optional.of(sach));
        when(chiTietGioHangRepository.findByGioHangMaGioHangAndSachMaSach(20L, 3L)).thenReturn(Optional.empty());
        when(tonKhoRepository.findBySachMaSach(3L)).thenReturn(Optional.of(tonKho(sach, 5)));
        when(chiTietGioHangRepository.save(any(ChiTietGioHang.class))).thenAnswer(invocation -> {
            ChiTietGioHang item = invocation.getArgument(0);
            item.setMaChiTietGioHang(100L);
            return item;
        });

        ChiTietGioHangResponse response = service.themSanPham(2L, 3L, 2);

        assertThat(response.getMaChiTietGioHang()).isEqualTo(100L);
        assertThat(response.getMaSach()).isEqualTo(3L);
        assertThat(response.getSoLuong()).isEqualTo(2);
        assertThat(gioHang.getChiTietGioHangs()).hasSize(1);
    }

    @Test
    void themSanPham_sachDaCo_thiCongDonSoLuong() {
        NguoiDung user = nguoiDung(2L);
        GioHang gioHang = GioHang.builder().maGioHang(20L).nguoiDung(user).build();
        Sach sach = sach(1L, "Mat Biec", "89000");
        ChiTietGioHang item = ChiTietGioHang.builder()
                .maChiTietGioHang(10L)
                .gioHang(gioHang)
                .sach(sach)
                .soLuong(1)
                .build();

        when(gioHangRepository.findByNguoiDungMaNguoiDung(2L)).thenReturn(Optional.of(gioHang));
        when(sachRepository.findById(1L)).thenReturn(Optional.of(sach));
        when(chiTietGioHangRepository.findByGioHangMaGioHangAndSachMaSach(20L, 1L)).thenReturn(Optional.of(item));
        when(tonKhoRepository.findBySachMaSach(1L)).thenReturn(Optional.of(tonKho(sach, 10)));
        when(chiTietGioHangRepository.save(item)).thenReturn(item);

        ChiTietGioHangResponse response = service.themSanPham(2L, 1L, 3);

        assertThat(response.getSoLuong()).isEqualTo(4);
        assertThat(item.getSoLuong()).isEqualTo(4);
    }

    @Test
    void themSanPham_soLuongKhongHopLe_thiNemLoi() {
        assertThatThrownBy(() -> service.themSanPham(2L, 1L, 0))
                .isInstanceOf(HethongLoiException.class)
                .hasMessageContaining("Số lượng phải lớn hơn 0");
    }

    @Test
    void themSanPham_vuotTonKho_thiNemLoi() {
        NguoiDung user = nguoiDung(2L);
        GioHang gioHang = GioHang.builder().maGioHang(20L).nguoiDung(user).build();
        Sach sach = sach(6L, "Clean Architecture", "510000");

        when(gioHangRepository.findByNguoiDungMaNguoiDung(2L)).thenReturn(Optional.of(gioHang));
        when(sachRepository.findById(6L)).thenReturn(Optional.of(sach));
        when(chiTietGioHangRepository.findByGioHangMaGioHangAndSachMaSach(20L, 6L)).thenReturn(Optional.empty());
        when(tonKhoRepository.findBySachMaSach(6L)).thenReturn(Optional.of(tonKho(sach, 1)));

        assertThatThrownBy(() -> service.themSanPham(2L, 6L, 2))
                .isInstanceOf(HethongLoiException.class)
                .hasMessageContaining("Số lượng không đủ");
    }

    @Test
    void capNhatSoLuong_itemKhongThuocUser_thiNemLoi() {
        when(chiTietGioHangRepository.findByMaChiTietGioHangAndGioHangNguoiDungMaNguoiDung(99L, 2L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.capNhatSoLuong(2L, 99L, 1))
                .isInstanceOf(KhongCoDuLieuException.class)
                .hasMessageContaining("Không tìm thấy sản phẩm trong giỏ hàng");
    }

    @Test
    void xoaSanPham_itemThuocUser_thiXoaKhoiGioHang() {
        NguoiDung user = nguoiDung(2L);
        GioHang gioHang = GioHang.builder().maGioHang(20L).nguoiDung(user).build();
        ChiTietGioHang item = ChiTietGioHang.builder()
                .maChiTietGioHang(10L)
                .gioHang(gioHang)
                .sach(sach(1L, "Mat Biec", "89000"))
                .soLuong(1)
                .build();
        gioHang.getChiTietGioHangs().add(item);

        when(chiTietGioHangRepository.findByMaChiTietGioHangAndGioHangNguoiDungMaNguoiDung(10L, 2L))
                .thenReturn(Optional.of(item));

        service.xoaSanPham(2L, 10L);

        assertThat(gioHang.getChiTietGioHangs()).isEmpty();
        verify(chiTietGioHangRepository).delete(item);
    }

    private static NguoiDung nguoiDung(Long id) {
        return NguoiDung.builder()
                .maNguoiDung(id)
                .tenDangNhap("user" + id)
                .hoVaTen("User " + id)
                .build();
    }

    private static Sach sach(Long id, String ten, String gia) {
        return Sach.builder()
                .maSach(id)
                .tenSach(ten)
                .gia(new BigDecimal(gia))
                .anhBia("/uploads/sach/" + id + ".jpg")
                .build();
    }

    private static TonKho tonKho(Sach sach, int soLuong) {
        return TonKho.builder()
                .sach(sach)
                .soLuong(soLuong)
                .build();
    }
}
