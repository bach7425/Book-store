package com.ntb.bookstore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ntb.bookstore.dto.NguoiDung.DiaChiResponse;
import com.ntb.bookstore.dto.NguoiDung.NguoiDungProfileResponse;
import com.ntb.bookstore.entity.DiaChi;
import com.ntb.bookstore.entity.NguoiDung;
import com.ntb.bookstore.entity.enums.VaiTro;
import com.ntb.bookstore.exception.HethongLoiException;
import com.ntb.bookstore.exception.KhongCoDuLieuException;
import com.ntb.bookstore.repository.DiaChiRepository;
import com.ntb.bookstore.repository.NguoiDungRepository;

@ExtendWith(MockitoExtension.class)
class NguoiDungServiceTest {

    @Mock
    private NguoiDungRepository nguoiDungRepository;
    @Mock
    private DiaChiRepository diaChiRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UploadService uploadService;

    private NguoiDungService service;

    @BeforeEach
    void setUp() {
        service = new NguoiDungService(nguoiDungRepository, diaChiRepository, passwordEncoder, uploadService);
    }

    @Test
    void layThongTinProfile_userTonTai_thiTraProfile() {
        when(nguoiDungRepository.findById(2L)).thenReturn(Optional.of(nguoiDung(2L)));

        NguoiDungProfileResponse response = service.layThongTinProfile(2L);

        assertThat(response.getMaNguoiDung()).isEqualTo(2L);
        assertThat(response.getVaiTro()).isEqualTo("NGUOI_DUNG");
    }

    @Test
    void capNhatProfile_emailMoiBiTrung_thiNemLoi() {
        NguoiDung user = nguoiDung(2L);
        when(nguoiDungRepository.findById(2L)).thenReturn(Optional.of(user));
        when(nguoiDungRepository.existsByEmail("taken@example.com")).thenReturn(true);

        assertThatThrownBy(() -> service.capNhatProfile(2L, "Ten moi", "taken@example.com", "0911111111"))
                .isInstanceOf(HethongLoiException.class)
                .hasMessageContaining("Email đã tồn tại");
    }

    @Test
    void doiMatKhau_hopLe_thiLuuMatKhauBamMoi() {
        NguoiDung user = nguoiDung(2L);
        user.setMatKhauBam("old-hash");
        when(nguoiDungRepository.findById(2L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old", "old-hash")).thenReturn(true);
        when(passwordEncoder.encode("new")).thenReturn("new-hash");

        service.doiMatKhau(2L, "old", "new");

        assertThat(user.getMatKhauBam()).isEqualTo("new-hash");
        verify(nguoiDungRepository).save(user);
    }

    @Test
    void doiMatKhau_matKhauCuSai_thiNemLoi() {
        NguoiDung user = nguoiDung(2L);
        user.setMatKhauBam("old-hash");
        when(nguoiDungRepository.findById(2L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "old-hash")).thenReturn(false);

        assertThatThrownBy(() -> service.doiMatKhau(2L, "wrong", "new"))
                .isInstanceOf(HethongLoiException.class)
                .hasMessageContaining("Mật khẩu cũ không đúng");
    }

    @Test
    void themDiaChi_macDinhMoi_thiBoMacDinhDiaChiCu() {
        NguoiDung user = nguoiDung(2L);
        DiaChi diaChiCu = diaChi(1L, user, true);
        when(nguoiDungRepository.findById(2L)).thenReturn(Optional.of(user));
        when(diaChiRepository.findByNguoiDungMaNguoiDung(2L)).thenReturn(List.of(diaChiCu));
        when(diaChiRepository.save(org.mockito.ArgumentMatchers.any(DiaChi.class))).thenAnswer(invocation -> {
            DiaChi diaChi = invocation.getArgument(0);
            diaChi.setMaDiaChi(2L);
            return diaChi;
        });

        DiaChiResponse response = service.themDiaChi(2L, "Nguoi nhan", "0900000000", "Dia chi moi", true);

        assertThat(diaChiCu.getMacDinh()).isFalse();
        assertThat(response.getMaDiaChi()).isEqualTo(2L);
        assertThat(response.getMacDinh()).isTrue();
        verify(diaChiRepository).saveAll(List.of(diaChiCu));
    }

    @Test
    void themDiaChi_quaBaDiaChi_thiNemLoi() {
        NguoiDung user = nguoiDung(2L);
        when(nguoiDungRepository.findById(2L)).thenReturn(Optional.of(user));
        when(diaChiRepository.findByNguoiDungMaNguoiDung(2L)).thenReturn(List.of(
                diaChi(1L, user, false),
                diaChi(2L, user, false),
                diaChi(3L, user, false)));

        assertThatThrownBy(() -> service.themDiaChi(2L, "A", "090", "Dia chi", false))
                .isInstanceOf(HethongLoiException.class)
                .hasMessageContaining("tối đa 3 địa chỉ");
    }

    @Test
    void capNhatDiaChi_khongThuocUser_thiNemLoi() {
        DiaChi diaChiNguoiKhac = diaChi(1L, nguoiDung(3L), false);
        when(diaChiRepository.findById(1L)).thenReturn(Optional.of(diaChiNguoiKhac));
        when(diaChiRepository.findByNguoiDungMaNguoiDung(2L)).thenReturn(List.of());

        assertThatThrownBy(() -> service.capNhatDiaChi(2L, 1L, "A", "090", "Dia chi", false))
                .isInstanceOf(HethongLoiException.class)
                .hasMessageContaining("không có quyền cập nhật");
    }

    @Test
    void layDanhSachDiaChi_rong_thiNemLoi() {
        when(diaChiRepository.findByNguoiDungMaNguoiDung(2L)).thenReturn(List.of());

        assertThatThrownBy(() -> service.layDanhSachDiaChi(2L))
                .isInstanceOf(KhongCoDuLieuException.class)
                .hasMessageContaining("Không tìm thấy địa chỉ");
    }

    private static NguoiDung nguoiDung(Long id) {
        return NguoiDung.builder()
                .maNguoiDung(id)
                .hoVaTen("User " + id)
                .tenDangNhap("user" + id)
                .email("user" + id + "@example.com")
                .vaiTro(VaiTro.NGUOI_DUNG)
                .build();
    }

    private static DiaChi diaChi(Long id, NguoiDung user, boolean macDinh) {
        return DiaChi.builder()
                .maDiaChi(id)
                .nguoiDung(user)
                .nguoiNhan("User")
                .soDienThoai("0900000000")
                .diaChiChiTiet("Dia chi")
                .macDinh(macDinh)
                .build();
    }
}
