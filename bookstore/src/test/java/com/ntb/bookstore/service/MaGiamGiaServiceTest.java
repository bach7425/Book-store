package com.ntb.bookstore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.ntb.bookstore.dto.PageResponse;
import com.ntb.bookstore.dto.MaGiamGia.CapNhatMaGiamGiaRequest;
import com.ntb.bookstore.dto.MaGiamGia.MaGiamGiaResponse;
import com.ntb.bookstore.dto.MaGiamGia.TaoMaGiamGiaRequest;
import com.ntb.bookstore.entity.MaGiamGia;
import com.ntb.bookstore.entity.enums.LoaiGiamGia;
import com.ntb.bookstore.entity.enums.TrangThaiMaGiamGia;
import com.ntb.bookstore.exception.HethongLoiException;
import com.ntb.bookstore.repository.MaGiamGiaRepository;

@ExtendWith(MockitoExtension.class)
class MaGiamGiaServiceTest {

    @Mock
    private MaGiamGiaRepository maGiamGiaRepository;

    private MaGiamGiaService service;

    @BeforeEach
    void setUp() {
        service = new MaGiamGiaService(maGiamGiaRepository);
    }

    @Test
    void taoMaGiamGia_hopLe_thiTrimCodeVaMacDinhHoatDong() {
        TaoMaGiamGiaRequest request = taoRequest(" NEW10 ");
        when(maGiamGiaRepository.existsByMaCodeIgnoreCase("NEW10")).thenReturn(false);
        when(maGiamGiaRepository.save(any(MaGiamGia.class))).thenAnswer(invocation -> {
            MaGiamGia ma = invocation.getArgument(0);
            ma.setMaGiamGia(1L);
            return ma;
        });

        MaGiamGiaResponse response = service.taoMaGiamGia(request);

        assertThat(response.getMaCode()).isEqualTo("NEW10");
        assertThat(response.getTrangThai()).isEqualTo("HOAT_DONG");
        assertThat(response.getSoLuongDaDung()).isZero();
    }

    @Test
    void taoMaGiamGia_trungCode_thiNemLoi() {
        TaoMaGiamGiaRequest request = taoRequest("HELLO10");
        when(maGiamGiaRepository.existsByMaCodeIgnoreCase("HELLO10")).thenReturn(true);

        assertThatThrownBy(() -> service.taoMaGiamGia(request))
                .isInstanceOf(HethongLoiException.class)
                .hasMessageContaining("đã tồn tại");
    }

    @Test
    void taoMaGiamGia_ngayKetThucTruocNgayBatDau_thiNemLoi() {
        TaoMaGiamGiaRequest request = taoRequest("BADDATE");
        request.setNgayBatDau(LocalDateTime.now().plusDays(2));
        request.setNgayKetThuc(LocalDateTime.now().plusDays(1));
        when(maGiamGiaRepository.existsByMaCodeIgnoreCase("BADDATE")).thenReturn(false);

        assertThatThrownBy(() -> service.taoMaGiamGia(request))
                .isInstanceOf(HethongLoiException.class)
                .hasMessageContaining("Ngày kết thúc phải sau ngày bắt đầu");
    }

    @Test
    void capNhatMaGiamGia_doiCodeHopLe_thiLuuCodeMoi() {
        MaGiamGia maGiamGia = maGiamGia("OLD");
        CapNhatMaGiamGiaRequest request = new CapNhatMaGiamGiaRequest();
        request.setMaCode("NEW");
        request.setGiaTri(new BigDecimal("15"));

        when(maGiamGiaRepository.findById(1L)).thenReturn(Optional.of(maGiamGia));
        when(maGiamGiaRepository.existsByMaCodeIgnoreCase("NEW")).thenReturn(false);
        when(maGiamGiaRepository.save(maGiamGia)).thenReturn(maGiamGia);

        MaGiamGiaResponse response = service.capNhatMaGiamGia(1L, request);

        assertThat(response.getMaCode()).isEqualTo("NEW");
        assertThat(response.getGiaTri()).isEqualByComparingTo("15");
    }

    @Test
    void xoaMaGiamGia_dangHoatDong_thiChuyenSangNgung() {
        MaGiamGia maGiamGia = maGiamGia("HELLO10");
        when(maGiamGiaRepository.findById(1L)).thenReturn(Optional.of(maGiamGia));

        service.xoaMaGiamGia(1L);

        assertThat(maGiamGia.getTrangThai()).isEqualTo(TrangThaiMaGiamGia.NGUNG);
        verify(maGiamGiaRepository).save(maGiamGia);
    }

    @Test
    void danhSachMaGiamGia_capNhatMaHetHanTruocKhiTraDanhSach() {
        MaGiamGia hetHan = maGiamGia("OLD10");
        MaGiamGia dangCo = maGiamGia("NEW10");
        when(maGiamGiaRepository.findByTrangThaiAndNgayKetThucLessThanEqual(any(), any())).thenReturn(List.of(hetHan));
        when(maGiamGiaRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(dangCo)));

        PageResponse<MaGiamGiaResponse> response = service.danhSachMaGiamGia(null, 0, 10, "/api/quan-tri/ma-giam-gia");

        assertThat(hetHan.getTrangThai()).isEqualTo(TrangThaiMaGiamGia.HET_HAN);
        assertThat(response.getDuLieu()).hasSize(1);
        verify(maGiamGiaRepository).saveAll(List.of(hetHan));
    }

    private static TaoMaGiamGiaRequest taoRequest(String code) {
        TaoMaGiamGiaRequest request = new TaoMaGiamGiaRequest();
        request.setMaCode(code);
        request.setLoaiGiam("PHAN_TRAM");
        request.setGiaTri(new BigDecimal("10"));
        request.setGiamToiDa(new BigDecimal("50000"));
        request.setDonToiThieu(new BigDecimal("100000"));
        request.setSoLuong(10);
        request.setNgayBatDau(LocalDateTime.now().minusDays(1));
        request.setNgayKetThuc(LocalDateTime.now().plusDays(10));
        return request;
    }

    private static MaGiamGia maGiamGia(String code) {
        return MaGiamGia.builder()
                .maGiamGia(1L)
                .maCode(code)
                .loaiGiam(LoaiGiamGia.PHAN_TRAM)
                .giaTri(new BigDecimal("10"))
                .giamToiDa(new BigDecimal("50000"))
                .donToiThieu(new BigDecimal("100000"))
                .soLuong(10)
                .soLuongDaDung(0)
                .ngayBatDau(LocalDateTime.now().minusDays(1))
                .ngayKetThuc(LocalDateTime.now().plusDays(1))
                .trangThai(TrangThaiMaGiamGia.HOAT_DONG)
                .build();
    }
}
