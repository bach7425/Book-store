package com.ntb.bookstore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.ntb.bookstore.dto.PageResponse;
import com.ntb.bookstore.dto.QuanTri.KhachHangResponse;
import com.ntb.bookstore.entity.NguoiDung;
import com.ntb.bookstore.entity.enums.TrangThaiThanhToan;
import com.ntb.bookstore.entity.enums.VaiTro;
import com.ntb.bookstore.repository.DonHangRepository;
import com.ntb.bookstore.repository.NguoiDungRepository;

@ExtendWith(MockitoExtension.class)
class QuanTriKhachHangServiceTest {

    @Mock
    private NguoiDungRepository nguoiDungRepository;
    @Mock
    private DonHangRepository donHangRepository;

    private QuanTriKhachHangService service;

    @BeforeEach
    void setUp() {
        service = new QuanTriKhachHangService(nguoiDungRepository, donHangRepository);
    }

    @Test
    void danhSachKhachHang_thiMapSoDonVaTongChiTieu() {
        NguoiDung user = NguoiDung.builder()
                .maNguoiDung(2L)
                .hoVaTen("Hoai An")
                .tenDangNhap("hoai.an")
                .email("an@example.com")
                .soDienThoai("0912345678")
                .vaiTro(VaiTro.NGUOI_DUNG)
                .build();
        when(nguoiDungRepository.timKiemTheoVaiTro(org.mockito.Mockito.eq(VaiTro.NGUOI_DUNG),
                org.mockito.Mockito.eq("an"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(user)));
        when(donHangRepository.countByNguoiDung(user)).thenReturn(3L);
        when(donHangRepository.tinhTongChiTieu(user, TrangThaiThanhToan.DA_THANH_TOAN))
                .thenReturn(new BigDecimal("750000"));

        PageResponse<KhachHangResponse> response = service.danhSachKhachHang("an", 0, 10, "/api/quan-tri/khach-hang");

        assertThat(response.getDuLieu()).hasSize(1);
        assertThat(response.getDuLieu().get(0).getSoDonHang()).isEqualTo(3L);
        assertThat(response.getDuLieu().get(0).getTongChiTieu()).isEqualByComparingTo("750000");
    }

    @Test
    void danhSachKhachHang_tongChiTieuNull_thiTraZero() {
        NguoiDung user = NguoiDung.builder()
                .maNguoiDung(2L)
                .tenDangNhap("hoai.an")
                .email("an@example.com")
                .vaiTro(VaiTro.NGUOI_DUNG)
                .build();
        when(nguoiDungRepository.timKiemTheoVaiTro(any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(user)));
        when(donHangRepository.countByNguoiDung(user)).thenReturn(0L);
        when(donHangRepository.tinhTongChiTieu(user, TrangThaiThanhToan.DA_THANH_TOAN)).thenReturn(null);

        PageResponse<KhachHangResponse> response = service.danhSachKhachHang(null, 0, 10, "/api/quan-tri/khach-hang");

        assertThat(response.getDuLieu().get(0).getTongChiTieu()).isEqualByComparingTo(BigDecimal.ZERO);
    }
}
