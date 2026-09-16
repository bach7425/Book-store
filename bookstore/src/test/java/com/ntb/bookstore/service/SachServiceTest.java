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

import com.ntb.bookstore.dto.Sach.SachResponse;
import com.ntb.bookstore.dto.Sach.TacGiaResponse;
import com.ntb.bookstore.dto.Sach.TheLoaiResponse;
import com.ntb.bookstore.entity.Sach;
import com.ntb.bookstore.entity.TacGia;
import com.ntb.bookstore.entity.TheLoai;
import com.ntb.bookstore.entity.TonKho;
import com.ntb.bookstore.entity.enums.TrangThaiDanhGia;
import com.ntb.bookstore.exception.HethongLoiException;
import com.ntb.bookstore.exception.KhongCoDuLieuException;
import com.ntb.bookstore.repository.DanhGiaRepository;
import com.ntb.bookstore.repository.SachRepository;
import com.ntb.bookstore.repository.TacGiaRepository;
import com.ntb.bookstore.repository.TheLoaiRepository;
import com.ntb.bookstore.repository.TonKhoRepository;
import com.ntb.bookstore.service.AI_tool.RagService;

@ExtendWith(MockitoExtension.class)
class SachServiceTest {

    @Mock
    private SachRepository sachRepository;
    @Mock
    private TacGiaRepository tacGiaRepository;
    @Mock
    private TheLoaiRepository theLoaiRepository;
    @Mock
    private TonKhoRepository tonKhoRepository;
    @Mock
    private DanhGiaRepository danhGiaRepository;
    @Mock
    private UploadService uploadService;
    @Mock
    private RagService ragService;

    private SachService service;

    @BeforeEach
    void setUp() {
        service = new SachService(
                sachRepository,
                tacGiaRepository,
                theLoaiRepository,
                tonKhoRepository,
                danhGiaRepository,
                uploadService,
                ragService);
    }

    @Test
    void chiTietSach_sachTonTai_thiTraDayDuThongTin() {
        TacGia tacGia = tacGia(1L, "Robert C. Martin");
        TheLoai theLoai = theLoai(3L, "Cong nghe thong tin");
        Sach sach = sach(3L, "Clean Code", "420000", tacGia, List.of(theLoai));

        when(sachRepository.findById(3L)).thenReturn(Optional.of(sach));
        when(tonKhoRepository.findBySachMaSach(3L)).thenReturn(Optional.of(TonKho.builder().sach(sach).soLuong(7).build()));
        when(danhGiaRepository.tinhDiemDanhGiaTrungBinh(3L, TrangThaiDanhGia.DA_DUYET)).thenReturn(4.5);
        when(danhGiaRepository.countBySachMaSachAndTrangThai(3L, TrangThaiDanhGia.DA_DUYET)).thenReturn(12L);

        SachResponse response = service.chiTietSach(3L);

        assertThat(response.getMaSach()).isEqualTo(3L);
        assertThat(response.getTenSach()).isEqualTo("Clean Code");
        assertThat(response.getSoLuongTon()).isEqualTo(7);
        assertThat(response.getDiemDanhGiaTrungBinh()).isEqualTo(4.5);
        assertThat(response.getSoLuongDanhGia()).isEqualTo(12L);
        assertThat(response.getTacGia().getTen()).isEqualTo("Robert C. Martin");
        assertThat(response.getTheLoais()).extracting(TheLoaiResponse::getTen).containsExactly("Cong nghe thong tin");
    }

    @Test
    void chiTietSach_khongTonTai_thiNemLoi() {
        when(sachRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.chiTietSach(999L))
                .isInstanceOf(KhongCoDuLieuException.class)
                .hasMessageContaining("Không tìm thấy sách");
    }

    @Test
    void themSach_hopLe_thiTaoSachTonKhoVaNapRag() {
        TacGia tacGia = tacGia(1L, "Nguyen Nhat Anh");
        TheLoai theLoai = theLoai(1L, "Van hoc Viet Nam");
        when(tacGiaRepository.findById(1L)).thenReturn(Optional.of(tacGia));
        when(theLoaiRepository.findAllById(List.of(1L))).thenReturn(List.of(theLoai));
        when(sachRepository.save(any(Sach.class))).thenAnswer(invocation -> {
            Sach sach = invocation.getArgument(0);
            sach.setMaSach(50L);
            return sach;
        });
        when(tonKhoRepository.findBySachMaSach(50L)).thenReturn(Optional.of(TonKho.builder().soLuong(20).build()));
        when(danhGiaRepository.tinhDiemDanhGiaTrungBinh(50L, TrangThaiDanhGia.DA_DUYET)).thenReturn(null);
        when(danhGiaRepository.countBySachMaSachAndTrangThai(50L, TrangThaiDanhGia.DA_DUYET)).thenReturn(0L);

        SachResponse response = service.themSach(
                "Sach moi",
                "Mo ta",
                new BigDecimal("100000"),
                "NXB",
                "12+",
                "NCC",
                null,
                "Tieng Viet",
                200,
                "20 x 13 cm",
                250,
                "Bia mem",
                1L,
                List.of(1L),
                20);

        assertThat(response.getMaSach()).isEqualTo(50L);
        assertThat(response.getSoLuongTon()).isEqualTo(20);
        verify(tonKhoRepository).save(any(TonKho.class));
        verify(ragService).themSachVaoVectorStore(any(Sach.class));
    }

    @Test
    void themSach_giaAm_thiNemLoi() {
        assertThatThrownBy(() -> service.themSach(
                "Sach loi", null, new BigDecimal("-1"), null, null, null, null,
                null, null, null, null, null, 1L, List.of(1L), 1))
                .isInstanceOf(HethongLoiException.class)
                .hasMessageContaining("Giá sách không được âm");
    }

    @Test
    void themSach_theLoaiKhongTonTaiDayDu_thiNemLoi() {
        when(tacGiaRepository.findById(1L)).thenReturn(Optional.of(tacGia(1L, "Tac gia")));
        when(theLoaiRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(theLoai(1L, "A")));

        assertThatThrownBy(() -> service.themSach(
                "Sach", null, BigDecimal.TEN, null, null, null, null, null,
                null, null, null, null, 1L, List.of(1L, 2L), 1))
                .isInstanceOf(HethongLoiException.class)
                .hasMessageContaining("Một số thể loại không tồn tại");
    }

    @Test
    void capNhatSach_hopLe_thiCapNhatThongTinVaTonKho() {
        TacGia tacGiaCu = tacGia(1L, "Tac gia cu");
        TacGia tacGiaMoi = tacGia(2L, "Tac gia moi");
        TheLoai theLoaiMoi = theLoai(2L, "Moi");
        Sach sach = sach(10L, "Cu", "50000", tacGiaCu, List.of(theLoai(1L, "Cu")));
        TonKho tonKho = TonKho.builder().sach(sach).soLuong(3).build();

        when(sachRepository.findById(10L)).thenReturn(Optional.of(sach));
        when(tacGiaRepository.findById(2L)).thenReturn(Optional.of(tacGiaMoi));
        when(theLoaiRepository.findAllById(List.of(2L))).thenReturn(List.of(theLoaiMoi));
        when(tonKhoRepository.findBySachMaSach(10L)).thenReturn(Optional.of(tonKho));
        when(sachRepository.save(sach)).thenReturn(sach);
        when(danhGiaRepository.tinhDiemDanhGiaTrungBinh(10L, TrangThaiDanhGia.DA_DUYET)).thenReturn(null);
        when(danhGiaRepository.countBySachMaSachAndTrangThai(10L, TrangThaiDanhGia.DA_DUYET)).thenReturn(0L);

        SachResponse response = service.capNhatSach(
                10L, "Moi", null, new BigDecimal("75000"), null, null, null, null,
                null, null, null, null, null, 2L, List.of(2L), 9);

        assertThat(response.getTenSach()).isEqualTo("Moi");
        assertThat(response.getGia()).isEqualByComparingTo("75000");
        assertThat(response.getTacGia().getTen()).isEqualTo("Tac gia moi");
        assertThat(response.getSoLuongTon()).isEqualTo(9);
        verify(ragService).themSachVaoVectorStore(sach);
    }

    @Test
    void themTacGia_tenRong_thiNemLoi() {
        assertThatThrownBy(() -> service.themTacGia(" ", "Tieu su"))
                .isInstanceOf(HethongLoiException.class)
                .hasMessageContaining("Tên tác giả không được để trống");
    }

    @Test
    void capNhatTheLoai_hopLe_thiTraResponseMoi() {
        TheLoai theLoai = theLoai(1L, "Cu");
        when(theLoaiRepository.findById(1L)).thenReturn(Optional.of(theLoai));
        when(theLoaiRepository.save(theLoai)).thenReturn(theLoai);

        TheLoaiResponse response = service.capNhatTheLoai(1L, "Moi", "Mo ta moi");

        assertThat(response.getTen()).isEqualTo("Moi");
        assertThat(response.getMoTa()).isEqualTo("Mo ta moi");
    }

    @Test
    void capNhatTacGia_hopLe_thiTraResponseMoi() {
        TacGia tacGia = tacGia(1L, "Cu");
        when(tacGiaRepository.findById(1L)).thenReturn(Optional.of(tacGia));
        when(tacGiaRepository.save(tacGia)).thenReturn(tacGia);

        TacGiaResponse response = service.capNhatTacGia(1L, "Moi", "Tieu su moi");

        assertThat(response.getTen()).isEqualTo("Moi");
        assertThat(response.getTieuSu()).isEqualTo("Tieu su moi");
    }

    private static Sach sach(Long id, String ten, String gia, TacGia tacGia, List<TheLoai> theLoais) {
        return Sach.builder()
                .maSach(id)
                .tenSach(ten)
                .moTa("Mo ta")
                .gia(new BigDecimal(gia))
                .tacGia(tacGia)
                .theLoais(theLoais)
                .build();
    }

    private static TacGia tacGia(Long id, String ten) {
        return TacGia.builder().maTacGia(id).ten(ten).tieuSu("Tieu su").build();
    }

    private static TheLoai theLoai(Long id, String ten) {
        return TheLoai.builder().maTheLoai(id).ten(ten).moTa("Mo ta").build();
    }
}
