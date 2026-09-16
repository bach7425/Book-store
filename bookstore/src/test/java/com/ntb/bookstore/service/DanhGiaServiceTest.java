package com.ntb.bookstore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ntb.bookstore.dto.DanhGia.DanhGiaResponse;
import com.ntb.bookstore.entity.ChiTietDonHang;
import com.ntb.bookstore.entity.DanhGia;
import com.ntb.bookstore.entity.DonHang;
import com.ntb.bookstore.entity.NguoiDung;
import com.ntb.bookstore.entity.Sach;
import com.ntb.bookstore.entity.enums.LoaiThongBao;
import com.ntb.bookstore.entity.enums.TrangThaiDanhGia;
import com.ntb.bookstore.exception.HethongLoiException;
import com.ntb.bookstore.repository.DanhGiaRepository;
import com.ntb.bookstore.repository.DonHangRepository;
import com.ntb.bookstore.repository.NguoiDungRepository;
import com.ntb.bookstore.repository.SachRepository;

@ExtendWith(MockitoExtension.class)
class DanhGiaServiceTest {

    @Mock
    private DanhGiaRepository danhGiaRepository;
    @Mock
    private SachRepository sachRepository;
    @Mock
    private NguoiDungRepository nguoiDungRepository;
    @Mock
    private DonHangRepository donHangRepository;
    @Mock
    private ThongBaoService thongBaoService;

    private DanhGiaService service;

    @BeforeEach
    void setUp() {
        service = new DanhGiaService(
                danhGiaRepository,
                sachRepository,
                nguoiDungRepository,
                donHangRepository,
                thongBaoService);
    }

    @Test
    void themDanhGia_daMuaSach_thiTaoDanhGiaChoDuyetVaThongBaoAdmin() {
        NguoiDung user = nguoiDung(2L);
        Sach sach = sach(1L, "Mat Biec");
        DonHang donHang = DonHang.builder().nguoiDung(user).build();
        donHang.getChiTietDonHangs().add(ChiTietDonHang.builder()
                .donHang(donHang)
                .sach(sach)
                .soLuong(1)
                .donGia(sach.getGia())
                .thanhTien(sach.getGia())
                .build());

        when(nguoiDungRepository.findById(2L)).thenReturn(Optional.of(user));
        when(sachRepository.findById(1L)).thenReturn(Optional.of(sach));
        when(donHangRepository.findByNguoiDung(user)).thenReturn(List.of(donHang));
        when(danhGiaRepository.findByNguoiDungAndSach(user, sach)).thenReturn(Optional.empty());
        when(danhGiaRepository.save(any(DanhGia.class))).thenAnswer(invocation -> {
            DanhGia danhGia = invocation.getArgument(0);
            danhGia.setMaDanhGia(100L);
            return danhGia;
        });

        DanhGiaResponse response = service.themDanhGia(2L, 1L, 5, "Sach rat hay");

        assertThat(response.getMaDanhGia()).isEqualTo(100L);
        assertThat(response.getMaNguoiDung()).isEqualTo(2L);
        assertThat(response.getMaSach()).isEqualTo(1L);
        assertThat(response.getSoSao()).isEqualTo(5);
        assertThat(response.getTrangThai()).isEqualTo("CHO_DUYET");
        verify(thongBaoService).guiChoTatCaQuanTriVien(
                "Có đánh giá chờ duyệt",
                "Sách Mat Biec có đánh giá mới cần duyệt",
                LoaiThongBao.DANH_GIA,
                "/quan-tri/danh-gia");
    }

    @Test
    void themDanhGia_chuaMuaSach_thiNemLoi() {
        NguoiDung user = nguoiDung(2L);
        Sach sach = sach(1L, "Mat Biec");

        when(nguoiDungRepository.findById(2L)).thenReturn(Optional.of(user));
        when(sachRepository.findById(1L)).thenReturn(Optional.of(sach));
        when(donHangRepository.findByNguoiDung(user)).thenReturn(List.of());

        assertThatThrownBy(() -> service.themDanhGia(2L, 1L, 5, "Hay"))
                .isInstanceOf(HethongLoiException.class)
                .hasMessageContaining("Chỉ người dùng đã mua sách");
    }

    @Test
    void themDanhGia_soSaoNgoaiKhoang_thiNemLoi() {
        assertThatThrownBy(() -> service.themDanhGia(2L, 1L, 6, "Hay"))
                .isInstanceOf(HethongLoiException.class)
                .hasMessageContaining("Số sao phải nằm trong khoảng");
    }

    @Test
    void capNhatDanhGia_khongPhaiChuSoHuu_thiNemLoi() {
        NguoiDung chuSoHuu = nguoiDung(2L);
        NguoiDung nguoiKhac = nguoiDung(3L);
        DanhGia danhGia = danhGia(10L, chuSoHuu, sach(1L, "Mat Biec"));

        when(danhGiaRepository.findById(10L)).thenReturn(Optional.of(danhGia));
        when(nguoiDungRepository.findById(3L)).thenReturn(Optional.of(nguoiKhac));

        assertThatThrownBy(() -> service.capNhatDanhGia(3L, 10L, 4, "Sua"))
                .isInstanceOf(HethongLoiException.class)
                .hasMessageContaining("không có quyền cập nhật");
    }

    @Test
    void duyetDanhGia_thiCapNhatTrangThaiVaThongBaoNguoiDung() {
        NguoiDung user = nguoiDung(2L);
        Sach sach = sach(1L, "Mat Biec");
        DanhGia danhGia = danhGia(10L, user, sach);

        when(danhGiaRepository.findById(10L)).thenReturn(Optional.of(danhGia));
        when(danhGiaRepository.save(danhGia)).thenReturn(danhGia);

        DanhGiaResponse response = service.duyetDanhGia(10L, "Cam on ban");

        assertThat(response.getTrangThai()).isEqualTo("DA_DUYET");
        assertThat(response.getPhanHoi()).isEqualTo("Cam on ban");
        verify(thongBaoService).guiChoNguoiDung(
                user,
                "Cập nhật đánh giá",
                "Đánh giá của bạn cho sách Mat Biec đã được duyệt. Phản hồi: Cam on ban",
                LoaiThongBao.DANH_GIA,
                "/sach/1");
    }

    private static NguoiDung nguoiDung(Long id) {
        return NguoiDung.builder()
                .maNguoiDung(id)
                .tenDangNhap("user" + id)
                .hoVaTen("User " + id)
                .build();
    }

    private static Sach sach(Long id, String ten) {
        return Sach.builder()
                .maSach(id)
                .tenSach(ten)
                .gia(new BigDecimal("100000"))
                .build();
    }

    private static DanhGia danhGia(Long id, NguoiDung nguoiDung, Sach sach) {
        return DanhGia.builder()
                .maDanhGia(id)
                .nguoiDung(nguoiDung)
                .sach(sach)
                .soSao(5)
                .noiDung("Hay")
                .trangThai(TrangThaiDanhGia.CHO_DUYET)
                .build();
    }
}
