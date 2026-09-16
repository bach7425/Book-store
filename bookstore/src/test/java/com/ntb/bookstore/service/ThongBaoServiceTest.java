package com.ntb.bookstore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ntb.bookstore.dto.ThongBao.ThongBaoResponse;
import com.ntb.bookstore.entity.NguoiDung;
import com.ntb.bookstore.entity.ThongBao;
import com.ntb.bookstore.entity.enums.LoaiThongBao;
import com.ntb.bookstore.entity.enums.VaiTro;
import com.ntb.bookstore.exception.HethongLoiException;
import com.ntb.bookstore.repository.NguoiDungRepository;
import com.ntb.bookstore.repository.ThongBaoRepository;

@ExtendWith(MockitoExtension.class)
class ThongBaoServiceTest {

    @Mock
    private ThongBaoRepository thongBaoRepository;
    @Mock
    private NguoiDungRepository nguoiDungRepository;

    private ThongBaoService service;

    @BeforeEach
    void setUp() {
        service = new ThongBaoService(thongBaoRepository, nguoiDungRepository);
    }

    @Test
    void guiChoTatCaNguoiDung_thiTaoThongBaoChoMoiUser() {
        when(nguoiDungRepository.findByVaiTro(VaiTro.NGUOI_DUNG)).thenReturn(List.of(nguoiDung(2L), nguoiDung(3L)));
        when(thongBaoRepository.save(any(ThongBao.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.guiChoTatCaNguoiDung("Tieu de", "Noi dung", LoaiThongBao.QUAN_TRI, "/");

        verify(thongBaoRepository, org.mockito.Mockito.times(2)).save(any(ThongBao.class));
    }

    @Test
    void guiThongBaoQuanTri_khongGuiTatCaVaThieuUser_thiNemLoi() {
        assertThatThrownBy(() -> service.guiThongBaoQuanTri(null, false, "Tieu de", "Noi dung", "QUAN_TRI", "/"))
                .isInstanceOf(HethongLoiException.class)
                .hasMessageContaining("Mã người dùng là bắt buộc");
    }

    @Test
    void guiThongBaoQuanTri_loaiKhongHopLe_thiNemLoi() {
        assertThatThrownBy(() -> service.guiThongBaoQuanTri(2L, false, "Tieu de", "Noi dung", "SAI", "/"))
                .isInstanceOf(HethongLoiException.class)
                .hasMessageContaining("Loại thông báo không hợp lệ");
    }

    @Test
    void demThongBaoChuaDoc_thiTraSoLuongTuRepository() {
        NguoiDung user = nguoiDung(2L);
        when(nguoiDungRepository.findById(2L)).thenReturn(Optional.of(user));
        when(thongBaoRepository.countByNguoiDungAndDaDocFalse(user)).thenReturn(3L);

        assertThat(service.demThongBaoChuaDoc(2L)).isEqualTo(3L);
    }

    @Test
    void danhDauDaDoc_thongBaoThuocUser_thiCapNhatDaDoc() {
        NguoiDung user = nguoiDung(2L);
        ThongBao thongBao = thongBao(10L, user, false);
        when(nguoiDungRepository.findById(2L)).thenReturn(Optional.of(user));
        when(thongBaoRepository.findById(10L)).thenReturn(Optional.of(thongBao));
        when(thongBaoRepository.save(thongBao)).thenReturn(thongBao);

        ThongBaoResponse response = service.danhDauDaDoc(2L, 10L);

        assertThat(response.getDaDoc()).isTrue();
    }

    @Test
    void danhDauDaDoc_thongBaoNguoiKhac_thiNemLoi() {
        when(nguoiDungRepository.findById(2L)).thenReturn(Optional.of(nguoiDung(2L)));
        when(thongBaoRepository.findById(10L)).thenReturn(Optional.of(thongBao(10L, nguoiDung(3L), false)));

        assertThatThrownBy(() -> service.danhDauDaDoc(2L, 10L))
                .isInstanceOf(HethongLoiException.class)
                .hasMessageContaining("không có quyền cập nhật");
    }

    @Test
    void danhDauTatCaDaDoc_thiCapNhatTatCaThongBaoChuaDoc() {
        NguoiDung user = nguoiDung(2L);
        ThongBao mot = thongBao(1L, user, false);
        ThongBao hai = thongBao(2L, user, false);
        when(nguoiDungRepository.findById(2L)).thenReturn(Optional.of(user));
        when(thongBaoRepository.findByNguoiDungAndDaDocFalse(user)).thenReturn(List.of(mot, hai));

        service.danhDauTatCaDaDoc(2L);

        assertThat(mot.getDaDoc()).isTrue();
        assertThat(hai.getDaDoc()).isTrue();
        verify(thongBaoRepository).save(mot);
        verify(thongBaoRepository).save(hai);
    }

    private static NguoiDung nguoiDung(Long id) {
        return NguoiDung.builder()
                .maNguoiDung(id)
                .tenDangNhap("user" + id)
                .hoVaTen("User " + id)
                .vaiTro(VaiTro.NGUOI_DUNG)
                .build();
    }

    private static ThongBao thongBao(Long id, NguoiDung user, boolean daDoc) {
        return ThongBao.builder()
                .maThongBao(id)
                .nguoiDung(user)
                .tieuDe("Tieu de")
                .noiDung("Noi dung")
                .loai(LoaiThongBao.QUAN_TRI)
                .daDoc(daDoc)
                .duongDan("/")
                .build();
    }
}
