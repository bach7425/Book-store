package com.ntb.bookstore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ntb.bookstore.dto.Sach.SachResponse;
import com.ntb.bookstore.entity.NguoiDung;
import com.ntb.bookstore.entity.Sach;
import com.ntb.bookstore.exception.HethongLoiException;
import com.ntb.bookstore.repository.NguoiDungRepository;
import com.ntb.bookstore.repository.SachRepository;

@ExtendWith(MockitoExtension.class)
class SachYeuThichServiceTest {

    @Mock
    private NguoiDungRepository nguoiDungRepository;
    @Mock
    private SachRepository sachRepository;
    @Mock
    private SachService sachService;

    private SachYeuThichService service;

    @BeforeEach
    void setUp() {
        service = new SachYeuThichService(nguoiDungRepository, sachRepository, sachService);
    }

    @Test
    void themSachYeuThich_chuaTonTai_thiThemVaLuuUser() {
        NguoiDung user = nguoiDung(2L);
        Sach sach = sach(1L, "Mat Biec");
        SachResponse expected = SachResponse.builder().maSach(1L).tenSach("Mat Biec").build();
        when(nguoiDungRepository.findById(2L)).thenReturn(Optional.of(user));
        when(sachRepository.findById(1L)).thenReturn(Optional.of(sach));
        when(sachService.toResponse(sach)).thenReturn(expected);

        SachResponse response = service.themSachYeuThich(2L, 1L);

        assertThat(response).isSameAs(expected);
        assertThat(user.getSachYeuThichs()).containsExactly(sach);
        verify(nguoiDungRepository).save(user);
    }

    @Test
    void themSachYeuThich_daTonTai_thiNemLoi() {
        NguoiDung user = nguoiDung(2L);
        Sach sach = sach(1L, "Mat Biec");
        user.getSachYeuThichs().add(sach);
        when(nguoiDungRepository.findById(2L)).thenReturn(Optional.of(user));
        when(sachRepository.findById(1L)).thenReturn(Optional.of(sach));

        assertThatThrownBy(() -> service.themSachYeuThich(2L, 1L))
                .isInstanceOf(HethongLoiException.class)
                .hasMessageContaining("Sách đã có");
    }

    @Test
    void xoaSachYeuThich_dangTonTai_thiXoaVaLuuUser() {
        NguoiDung user = nguoiDung(2L);
        Sach sach = sach(1L, "Mat Biec");
        user.getSachYeuThichs().add(sach);
        when(nguoiDungRepository.findById(2L)).thenReturn(Optional.of(user));
        when(sachRepository.findById(1L)).thenReturn(Optional.of(sach));

        service.xoaSachYeuThich(2L, 1L);

        assertThat(user.getSachYeuThichs()).isEmpty();
        verify(nguoiDungRepository).save(user);
    }

    @Test
    void xoaSachYeuThich_khongNamTrongDanhSach_thiNemLoi() {
        NguoiDung user = nguoiDung(2L);
        Sach sach = sach(1L, "Mat Biec");
        when(nguoiDungRepository.findById(2L)).thenReturn(Optional.of(user));
        when(sachRepository.findById(1L)).thenReturn(Optional.of(sach));

        assertThatThrownBy(() -> service.xoaSachYeuThich(2L, 1L))
                .isInstanceOf(HethongLoiException.class)
                .hasMessageContaining("không còn nằm");
    }

    private static NguoiDung nguoiDung(Long id) {
        return NguoiDung.builder().maNguoiDung(id).tenDangNhap("user" + id).build();
    }

    private static Sach sach(Long id, String ten) {
        return Sach.builder().maSach(id).tenSach(ten).gia(new BigDecimal("100000")).build();
    }
}
