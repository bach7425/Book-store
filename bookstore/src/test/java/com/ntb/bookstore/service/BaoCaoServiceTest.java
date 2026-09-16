package com.ntb.bookstore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.ntb.bookstore.dto.PageResponse;
import com.ntb.bookstore.dto.QuanTri.BaoCaoDoanhThuResponse;
import com.ntb.bookstore.dto.QuanTri.SachBanChayResponse;
import com.ntb.bookstore.entity.enums.TrangThaiDonHang;
import com.ntb.bookstore.entity.enums.TrangThaiThanhToan;
import com.ntb.bookstore.repository.ChiTietDonHangRepository;
import com.ntb.bookstore.repository.ThanhToanRepository;
import com.ntb.bookstore.repository.projection.SachBanChayProjection;

@ExtendWith(MockitoExtension.class)
class BaoCaoServiceTest {

    @Mock
    private ThanhToanRepository thanhToanRepository;
    @Mock
    private ChiTietDonHangRepository chiTietDonHangRepository;

    private BaoCaoService service;

    @BeforeEach
    void setUp() {
        service = new BaoCaoService(thanhToanRepository, chiTietDonHangRepository);
    }

    @Test
    void baoCaoDoanhThu_coDuLieu_thiTraTongVaSoDon() {
        LocalDate tuNgay = LocalDate.of(2026, 9, 1);
        LocalDate denNgay = LocalDate.of(2026, 9, 12);
        when(thanhToanRepository.tinhTongDoanhThuDonDaGiao(
                eq(TrangThaiThanhToan.DA_THANH_TOAN),
                eq(TrangThaiDonHang.DA_GIAO),
                eq(LocalDateTime.of(2026, 9, 1, 0, 0)),
                eq(LocalDateTime.of(2026, 9, 13, 0, 0))))
                .thenReturn(new BigDecimal("500000"));
        when(thanhToanRepository
                .countByTrangThaiAndDonHangTrangThaiAndThoiGianThanhToanGreaterThanEqualAndThoiGianThanhToanLessThan(
                        eq(TrangThaiThanhToan.DA_THANH_TOAN),
                        eq(TrangThaiDonHang.DA_GIAO),
                        any(LocalDateTime.class),
                        any(LocalDateTime.class)))
                .thenReturn(2L);

        BaoCaoDoanhThuResponse response = service.baoCaoDoanhThu(tuNgay, denNgay);

        assertThat(response.getTongDoanhThu()).isEqualByComparingTo("500000");
        assertThat(response.getSoDonDaThanhToan()).isEqualTo(2L);
        assertThat(response.getTuNgay()).isEqualTo(tuNgay);
        assertThat(response.getDenNgay()).isEqualTo(denNgay);
    }

    @Test
    void baoCaoDoanhThu_repositoryTraNull_thiMacDinhZero() {
        when(thanhToanRepository.tinhTongDoanhThuDonDaGiao(any(), any(), any(), any())).thenReturn(null);
        when(thanhToanRepository
                .countByTrangThaiAndDonHangTrangThaiAndThoiGianThanhToanGreaterThanEqualAndThoiGianThanhToanLessThan(
                        any(), any(), any(), any()))
                .thenReturn(null);

        BaoCaoDoanhThuResponse response = service.baoCaoDoanhThu(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 12));

        assertThat(response.getTongDoanhThu()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(response.getSoDonDaThanhToan()).isZero();
    }

    @Test
    void thongKeSachBanChay_projectionNullMetric_thiMacDinhZero() {
        SachBanChayProjection projection = new SachBanChayProjection() {
            @Override
            public Long getMaSach() {
                return 1L;
            }

            @Override
            public String getTenSach() {
                return "Mat Biec";
            }

            @Override
            public String getAnhBia() {
                return "/uploads/sach/mat-biec.jpg";
            }

            @Override
            public String getTacGia() {
                return "Nguyen Nhat Anh";
            }

            @Override
            public Long getSoLuongBan() {
                return null;
            }

            @Override
            public BigDecimal getDoanhThu() {
                return null;
            }
        };
        when(chiTietDonHangRepository.thongKeSachBanChay(any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(projection)));

        PageResponse<SachBanChayResponse> response = service.thongKeSachBanChay(
                LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 12), 0, 10, "/api/quan-tri");

        assertThat(response.getDuLieu()).hasSize(1);
        assertThat(response.getDuLieu().get(0).getSoLuongBan()).isZero();
        assertThat(response.getDuLieu().get(0).getDoanhThu()).isEqualByComparingTo(BigDecimal.ZERO);
    }
}
