package com.ntb.bookstore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ntb.bookstore.dto.DonHang.DonHangResponse;
import com.ntb.bookstore.dto.MaGiamGia.KiemTraMaGiamGiaResponse;
import com.ntb.bookstore.entity.ChiTietDonHang;
import com.ntb.bookstore.entity.ChiTietGioHang;
import com.ntb.bookstore.entity.DiaChi;
import com.ntb.bookstore.entity.DonHang;
import com.ntb.bookstore.entity.GioHang;
import com.ntb.bookstore.entity.MaGiamGia;
import com.ntb.bookstore.entity.NguoiDung;
import com.ntb.bookstore.entity.Sach;
import com.ntb.bookstore.entity.ThanhToan;
import com.ntb.bookstore.entity.TonKho;
import com.ntb.bookstore.entity.enums.LoaiGiamGia;
import com.ntb.bookstore.entity.enums.LoaiThongBao;
import com.ntb.bookstore.entity.enums.PhuongThucThanhToan;
import com.ntb.bookstore.entity.enums.TrangThaiDonHang;
import com.ntb.bookstore.entity.enums.TrangThaiMaGiamGia;
import com.ntb.bookstore.entity.enums.TrangThaiThanhToan;
import com.ntb.bookstore.exception.HethongLoiException;
import com.ntb.bookstore.repository.ChiTietGioHangRepository;
import com.ntb.bookstore.repository.DiaChiRepository;
import com.ntb.bookstore.repository.DonHangRepository;
import com.ntb.bookstore.repository.GioHangRepository;
import com.ntb.bookstore.repository.MaGiamGiaRepository;
import com.ntb.bookstore.repository.NguoiDungRepository;
import com.ntb.bookstore.repository.TonKhoRepository;

@ExtendWith(MockitoExtension.class)
class DonHangServiceTest {

    @Mock
    private DonHangRepository donHangRepository;
    @Mock
    private GioHangRepository gioHangRepository;
    @Mock
    private ChiTietGioHangRepository chiTietGioHangRepository;
    @Mock
    private DiaChiRepository diaChiRepository;
    @Mock
    private NguoiDungRepository nguoiDungRepository;
    @Mock
    private TonKhoRepository tonKhoRepository;
    @Mock
    private MaGiamGiaRepository maGiamGiaRepository;
    @Mock
    private ThongBaoService thongBaoService;

    private DonHangService service;

    @BeforeEach
    void setUp() {
        service = new DonHangService(
                donHangRepository,
                gioHangRepository,
                chiTietGioHangRepository,
                diaChiRepository,
                nguoiDungRepository,
                tonKhoRepository,
                maGiamGiaRepository,
                thongBaoService);
    }

    @Test
    void kiemTraMaGiamGia_phanTramHopLe_thiTinhDungSoTienGiam() {
        MaGiamGia maGiamGia = maGiamGiaPhanTram("HELLO10", "10", "50000", "150000");
        when(maGiamGiaRepository.findByMaCodeIgnoreCase("HELLO10")).thenReturn(Optional.of(maGiamGia));

        KiemTraMaGiamGiaResponse response = service.kiemTraMaGiamGia("HELLO10", new BigDecimal("300000"));

        assertThat(response.getMaGiamGia()).isEqualTo("HELLO10");
        assertThat(response.getSoTienGiam()).isEqualByComparingTo("30000.00");
        assertThat(response.getPhiVanChuyen()).isEqualByComparingTo("30000");
        assertThat(response.getTongTienThanhToan()).isEqualByComparingTo("300000.00");
    }

    @Test
    void kiemTraMaGiamGia_vuotGiamToiDa_thiLayMucToiDa() {
        MaGiamGia maGiamGia = maGiamGiaPhanTram("HELLO10", "10", "50000", "150000");
        when(maGiamGiaRepository.findByMaCodeIgnoreCase("HELLO10")).thenReturn(Optional.of(maGiamGia));

        KiemTraMaGiamGiaResponse response = service.kiemTraMaGiamGia("HELLO10", new BigDecimal("900000"));

        assertThat(response.getSoTienGiam()).isEqualByComparingTo("50000.00");
        assertThat(response.getTongTienThanhToan()).isEqualByComparingTo("880000.00");
    }

    @Test
    void kiemTraMaGiamGia_chuaDatDonToiThieu_thiNemLoi() {
        MaGiamGia maGiamGia = maGiamGiaPhanTram("HELLO10", "10", "50000", "150000");
        when(maGiamGiaRepository.findByMaCodeIgnoreCase("HELLO10")).thenReturn(Optional.of(maGiamGia));

        assertThatThrownBy(() -> service.kiemTraMaGiamGia("HELLO10", new BigDecimal("100000")))
                .isInstanceOf(HethongLoiException.class)
                .hasMessageContaining("Đơn hàng tối thiểu");
    }

    @Test
    void taoDonHang_hopLeKhongMaGiamGia_thiTaoDonHangVaXoaGioHang() {
        NguoiDung user = nguoiDung(2L);
        DiaChi diaChi = diaChi(user);
        Sach sach = sach(1L, "Mat Biec", "89000");
        GioHang gioHang = gioHang(user, sach, 2);
        TonKho tonKho = tonKho(sach, 10);

        when(nguoiDungRepository.findById(2L)).thenReturn(Optional.of(user));
        when(diaChiRepository.findById(1L)).thenReturn(Optional.of(diaChi));
        when(gioHangRepository.findByNguoiDungMaNguoiDung(2L)).thenReturn(Optional.of(gioHang));
        when(tonKhoRepository.findBySachMaSach(1L)).thenReturn(Optional.of(tonKho));
        when(donHangRepository.save(any(DonHang.class))).thenAnswer(invocation -> {
            DonHang donHang = invocation.getArgument(0);
            if (donHang.getMaDonHang() == null) {
                donHang.setMaDonHang(50L);
            }
            return donHang;
        });

        DonHangResponse response = service.taoDonHang(2L, 1L, "TIEN_MAT", null);

        assertThat(response.getMaDonHang()).isEqualTo(50L);
        assertThat(response.getTongTien()).isEqualByComparingTo("178000");
        assertThat(response.getPhiVanChuyen()).isEqualByComparingTo("30000");
        assertThat(response.getSoTienGiam()).isEqualByComparingTo("0");
        assertThat(response.getTongTienThanhToan()).isEqualByComparingTo("208000");
        assertThat(response.getTrangThai()).isEqualTo("CHO_XU_LY");
        assertThat(response.getTrangThaiThanhToan()).isEqualTo("CHO_THANH_TOAN");
        assertThat(response.getItems()).hasSize(1);
        assertThat(tonKho.getSoLuong()).isEqualTo(8);
        assertThat(gioHang.getChiTietGioHangs()).isEmpty();
        verify(chiTietGioHangRepository).deleteByGioHangMaGioHang(30L);
    }

    @Test
    void taoDonHang_diaChiKhongThuocNguoiDung_thiNemLoi() {
        NguoiDung user = nguoiDung(2L);
        DiaChi diaChiNguoiKhac = diaChi(nguoiDung(3L));
        when(nguoiDungRepository.findById(2L)).thenReturn(Optional.of(user));
        when(diaChiRepository.findById(1L)).thenReturn(Optional.of(diaChiNguoiKhac));

        assertThatThrownBy(() -> service.taoDonHang(2L, 1L, "TIEN_MAT", null))
                .isInstanceOf(HethongLoiException.class)
                .hasMessageContaining("Địa chỉ không thuộc");
    }

    @Test
    void capNhatTrangThaiDonHang_chuyenSangDaGiao_thiDanhDauDaThanhToanVaGuiThongBao() {
        NguoiDung user = nguoiDung(2L);
        DonHang donHang = DonHang.builder()
                .maDonHang(99L)
                .nguoiDung(user)
                .diaChi(diaChi(user))
                .trangThai(TrangThaiDonHang.DANG_GIAO)
                .tongTien(new BigDecimal("100000"))
                .phiVanChuyen(new BigDecimal("30000"))
                .soTienGiam(BigDecimal.ZERO)
                .tongTienThanhToan(new BigDecimal("130000"))
                .thanhToan(ThanhToan.builder()
                        .phuongThuc(PhuongThucThanhToan.TIEN_MAT)
                        .trangThai(TrangThaiThanhToan.CHO_THANH_TOAN)
                        .soTien(new BigDecimal("130000"))
                        .build())
                .build();
        when(donHangRepository.findById(99L)).thenReturn(Optional.of(donHang));
        when(donHangRepository.save(donHang)).thenReturn(donHang);

        DonHangResponse response = service.capNhatTrangThaiDonHang(99L, TrangThaiDonHang.DA_GIAO);

        assertThat(response.getTrangThai()).isEqualTo("DA_GIAO");
        assertThat(response.getTrangThaiThanhToan()).isEqualTo("DA_THANH_TOAN");
        assertThat(donHang.getThanhToan().getThoiGianThanhToan()).isNotNull();
        verify(thongBaoService).guiChoNguoiDung(
                user,
                "Cập nhật trạng thái đơn hàng",
                "Đơn hàng #99 đã được cập nhật sang trạng thái DA_GIAO",
                LoaiThongBao.DON_HANG,
                "/don-hang/99");
    }

    @Test
    void huyDonHang_hopLe_thiHoanTonKhoVaLuotMaGiamGia() {
        NguoiDung user = nguoiDung(2L);
        Sach sach = sach(1L, "Mat Biec", "89000");
        MaGiamGia maGiamGia = maGiamGiaPhanTram("HELLO10", "10", "50000", "0");
        maGiamGia.setSoLuongDaDung(1);
        TonKho tonKho = tonKho(sach, 5);
        DonHang donHang = DonHang.builder()
                .maDonHang(77L)
                .nguoiDung(user)
                .diaChi(diaChi(user))
                .maGiamGia(maGiamGia)
                .trangThai(TrangThaiDonHang.CHO_XU_LY)
                .tongTien(new BigDecimal("178000"))
                .phiVanChuyen(new BigDecimal("30000"))
                .soTienGiam(new BigDecimal("17800"))
                .tongTienThanhToan(new BigDecimal("190200"))
                .build();
        donHang.getChiTietDonHangs().add(ChiTietDonHang.builder()
                .donHang(donHang)
                .sach(sach)
                .soLuong(2)
                .donGia(sach.getGia())
                .thanhTien(new BigDecimal("178000"))
                .build());

        when(donHangRepository.findById(77L)).thenReturn(Optional.of(donHang));
        when(tonKhoRepository.findBySachMaSach(1L)).thenReturn(Optional.of(tonKho));
        when(donHangRepository.save(donHang)).thenReturn(donHang);

        DonHangResponse response = service.huyDonHang(2L, 77L);

        assertThat(response.getTrangThai()).isEqualTo("DA_HUY");
        assertThat(tonKho.getSoLuong()).isEqualTo(7);
        assertThat(maGiamGia.getSoLuongDaDung()).isZero();
        verify(maGiamGiaRepository).save(maGiamGia);
    }

    private static NguoiDung nguoiDung(Long id) {
        return NguoiDung.builder()
                .maNguoiDung(id)
                .tenDangNhap("user" + id)
                .hoVaTen("User " + id)
                .email("user" + id + "@example.com")
                .soDienThoai("090000000" + id)
                .build();
    }

    private static DiaChi diaChi(NguoiDung nguoiDung) {
        return DiaChi.builder()
                .maDiaChi(1L)
                .nguoiDung(nguoiDung)
                .nguoiNhan(nguoiDung.getHoVaTen())
                .soDienThoai("0900000000")
                .diaChiChiTiet("Dia chi test")
                .build();
    }

    private static Sach sach(Long id, String ten, String gia) {
        return Sach.builder()
                .maSach(id)
                .tenSach(ten)
                .gia(new BigDecimal(gia))
                .build();
    }

    private static GioHang gioHang(NguoiDung user, Sach sach, int soLuong) {
        GioHang gioHang = GioHang.builder()
                .maGioHang(30L)
                .nguoiDung(user)
                .build();
        gioHang.getChiTietGioHangs().add(ChiTietGioHang.builder()
                .maChiTietGioHang(31L)
                .gioHang(gioHang)
                .sach(sach)
                .soLuong(soLuong)
                .build());
        return gioHang;
    }

    private static TonKho tonKho(Sach sach, int soLuong) {
        return TonKho.builder()
                .sach(sach)
                .soLuong(soLuong)
                .build();
    }

    private static MaGiamGia maGiamGiaPhanTram(String code, String giaTri, String giamToiDa, String donToiThieu) {
        return MaGiamGia.builder()
                .maGiamGia(1L)
                .maCode(code)
                .loaiGiam(LoaiGiamGia.PHAN_TRAM)
                .giaTri(new BigDecimal(giaTri))
                .giamToiDa(new BigDecimal(giamToiDa))
                .donToiThieu(new BigDecimal(donToiThieu))
                .soLuong(10)
                .soLuongDaDung(0)
                .ngayBatDau(LocalDateTime.now().minusDays(1))
                .ngayKetThuc(LocalDateTime.now().plusDays(1))
                .trangThai(TrangThaiMaGiamGia.HOAT_DONG)
                .build();
    }
}
