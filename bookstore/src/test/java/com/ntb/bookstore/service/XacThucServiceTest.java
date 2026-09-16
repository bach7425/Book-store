package com.ntb.bookstore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ntb.bookstore.dto.NguoiDung.NguoiDungResponse;
import com.ntb.bookstore.dto.XacThuc.DangKyRequest;
import com.ntb.bookstore.dto.XacThuc.DangNhapRequest;
import com.ntb.bookstore.dto.XacThuc.XacThucRespone;
import com.ntb.bookstore.entity.GioHang;
import com.ntb.bookstore.entity.NguoiDung;
import com.ntb.bookstore.entity.enums.VaiTro;
import com.ntb.bookstore.exception.HethongLoiException;
import com.ntb.bookstore.repository.GioHangRepository;
import com.ntb.bookstore.repository.NguoiDungRepository;
import com.ntb.bookstore.security.JwtService;

@ExtendWith(MockitoExtension.class)
class XacThucServiceTest {

    @Mock
    private NguoiDungRepository nguoiDungRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private GioHangRepository gioHangRepository;

    private XacThucService service;

    @BeforeEach
    void setUp() {
        service = new XacThucService(
                nguoiDungRepository,
                passwordEncoder,
                jwtService,
                authenticationManager,
                gioHangRepository);
    }

    @Test
    void dangKy_taoNguoiDungMoiVaGioHangMacDinh() {
        DangKyRequest request = new DangKyRequest();
        request.setHoTen("Nguyen Van A");
        request.setTenDangNhap("vana");
        request.setEmail("vana@example.com");
        request.setMatKhau("secret");
        request.setSoDienThoai("0909009009");

        when(nguoiDungRepository.existsByTenDangNhap("vana")).thenReturn(false);
        when(nguoiDungRepository.existsByEmail("vana@example.com")).thenReturn(false);
        when(nguoiDungRepository.existsBySoDienThoai("0909009009")).thenReturn(false);
        when(passwordEncoder.encode("secret")).thenReturn("hashed-secret");
        when(nguoiDungRepository.save(any(NguoiDung.class))).thenAnswer(invocation -> {
            NguoiDung nguoiDung = invocation.getArgument(0);
            nguoiDung.setMaNguoiDung(10L);
            return nguoiDung;
        });

        NguoiDungResponse response = service.dangKy(request);

        assertThat(response.getMaNguoiDung()).isEqualTo(10L);
        assertThat(response.getTenDangNhap()).isEqualTo("vana");
        assertThat(response.getEmail()).isEqualTo("vana@example.com");
        assertThat(response.getVaiTro()).isEqualTo("ROLE_NGUOI_DUNG");

        ArgumentCaptor<GioHang> gioHangCaptor = ArgumentCaptor.forClass(GioHang.class);
        verify(gioHangRepository).save(gioHangCaptor.capture());
        assertThat(gioHangCaptor.getValue().getNguoiDung().getTenDangNhap()).isEqualTo("vana");
    }

    @Test
    void dangKy_usernameDaTonTai_thiNemLoiVaKhongLuu() {
        DangKyRequest request = new DangKyRequest();
        request.setTenDangNhap("hoai.an");

        when(nguoiDungRepository.existsByTenDangNhap("hoai.an")).thenReturn(true);

        assertThatThrownBy(() -> service.dangKy(request))
                .isInstanceOf(HethongLoiException.class)
                .hasMessageContaining("Tên đăng nhập đã tồn tại");

        verify(nguoiDungRepository, never()).save(any());
        verify(gioHangRepository, never()).save(any());
    }

    @Test
    void dangNhap_thanhCong_thiTraTokenVaThongTinNguoiDung() {
        DangNhapRequest request = new DangNhapRequest();
        request.setTenDangNhap("admin");
        request.setMatKhau("admin@123");
        NguoiDung admin = NguoiDung.builder()
                .maNguoiDung(1L)
                .tenDangNhap("admin")
                .email("admin@bookstore.vn")
                .hoVaTen("Quan tri vien")
                .vaiTro(VaiTro.QUAN_TRI_VIEN)
                .build();

        when(nguoiDungRepository.findByTenDangNhap("admin")).thenReturn(Optional.of(admin));
        when(jwtService.taoMaTruyCap("admin", 1L, admin.getAuthorities().toString())).thenReturn("access-token");
        when(jwtService.taoMaLamMoi("admin", 1L, admin.getAuthorities().toString())).thenReturn("refresh-token");

        XacThucRespone response = service.dangNhap(request);

        assertThat(response.maTruyCap()).isEqualTo("access-token");
        assertThat(response.maLamMoi()).isEqualTo("refresh-token");
        assertThat(response.loaiMa()).isEqualTo("Bearer");
        assertThat(response.nguoiDung().getVaiTro()).isEqualTo("ROLE_QUAN_TRI_VIEN");
        verify(authenticationManager).authenticate(any());
    }

    @Test
    void taoAccessTokenMoi_refreshTokenHopLe_thiTaoAccessTokenMoi() {
        NguoiDung user = NguoiDung.builder()
                .maNguoiDung(2L)
                .tenDangNhap("hoai.an")
                .vaiTro(VaiTro.NGUOI_DUNG)
                .build();

        when(jwtService.layTenNguoiDungTuToken("refresh-token")).thenReturn("hoai.an");
        when(nguoiDungRepository.findByTenDangNhap("hoai.an")).thenReturn(Optional.of(user));
        when(jwtService.taoMaTruyCap("hoai.an", 2L, user.getAuthorities().toString())).thenReturn("new-access");

        assertThat(service.taoAccessTokenMoi("refresh-token")).isEqualTo("new-access");
    }
}
