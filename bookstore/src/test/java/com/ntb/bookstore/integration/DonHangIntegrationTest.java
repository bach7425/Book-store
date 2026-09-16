package com.ntb.bookstore.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.ntb.bookstore.entity.enums.LoaiGiamGia;

class DonHangIntegrationTest extends BaseIntegrationTest {

    @Test
    void taoDonHangTuGioHangVaTinhTienDung() throws Exception {
        var user = taoNguoiDung();
        var diaChi = taoDiaChi(user);
        var sach = taoSach("Sách đặt hàng", BigDecimal.valueOf(100_000), 10);
        var maGiamGia = taoMaGiamGia("HELLO", LoaiGiamGia.PHAN_TRAM, BigDecimal.TEN);
        themVaoGioHang(user, sach, 2);

        mockMvc.perform(post("/api/don-hang")
                .header("Authorization", bearer(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of(
                        "maDiaChi", diaChi.getMaDiaChi(),
                        "phuongThucThanhToan", "TIEN_MAT",
                        "maGiamGia", maGiamGia.getMaCode()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.duLieu.tongTien").value(200000.00))
                .andExpect(jsonPath("$.duLieu.soTienGiam").value(20000.00))
                .andExpect(jsonPath("$.duLieu.phiVanChuyen").value(30000.00))
                .andExpect(jsonPath("$.duLieu.tongTienThanhToan").value(210000.00))
                .andExpect(jsonPath("$.duLieu.trangThai").value("CHO_XU_LY"));
    }

    @Test
    void taoDonHangKhiGioRongHoacDiaChiUserKhacTraBadRequest() throws Exception {
        var user = taoNguoiDung();
        var userKhac = taoNguoiDung();
        var diaChiKhac = taoDiaChi(userKhac);
        taoGioHang(user);

        mockMvc.perform(post("/api/don-hang")
                .header("Authorization", bearer(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of(
                        "maDiaChi", diaChiKhac.getMaDiaChi(),
                        "phuongThucThanhToan", "TIEN_MAT"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.thanhCong").value(false));
    }

    @Test
    void kiemTraMaGiamGiaHopLeVaKhongHopLe() throws Exception {
        var user = taoNguoiDung();
        var maGiamGia = taoMaGiamGia("SALE", LoaiGiamGia.SO_TIEN, BigDecimal.valueOf(15_000));

        mockMvc.perform(post("/api/don-hang/kiem-tra-ma-giam-gia")
                .header("Authorization", bearer(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of("maGiamGia", maGiamGia.getMaCode(), "tongTien", 100_000))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.duLieu.soTienGiam").value(15000.00));

        mockMvc.perform(post("/api/don-hang/kiem-tra-ma-giam-gia")
                .header("Authorization", bearer(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of("maGiamGia", "KHONGCO", "tongTien", 100_000))))
                .andExpect(status().isNotFound());
    }

    @Test
    void userXemDonCuaMinhVaKhongXemDonNguoiKhac() throws Exception {
        var user = taoNguoiDung();
        var userKhac = taoNguoiDung();
        var donHang = taoDonHangDaMua(user, taoSach("Sách riêng tư", BigDecimal.valueOf(80_000), 3));

        mockMvc.perform(get("/api/don-hang/{maDonHang}", donHang.getMaDonHang())
                .header("Authorization", bearer(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.duLieu.maDonHang").value(donHang.getMaDonHang()));

        mockMvc.perform(get("/api/don-hang/{maDonHang}", donHang.getMaDonHang())
                .header("Authorization", bearer(userKhac)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.thanhCong").value(false));
    }

    @Test
    void huyDonHangVaAdminCapNhatTrangThai() throws Exception {
        var user = taoNguoiDung();
        var admin = taoAdmin();
        var diaChi = taoDiaChi(user);
        var sach = taoSach("Sách trạng thái", BigDecimal.valueOf(90_000), 5);
        themVaoGioHang(user, sach, 1);

        var createResult = mockMvc.perform(post("/api/don-hang")
                .header("Authorization", bearer(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of("maDiaChi", diaChi.getMaDiaChi(), "phuongThucThanhToan", "TIEN_MAT"))))
                .andExpect(status().isOk())
                .andReturn();
        long maDonHang = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .at("/duLieu/maDonHang").asLong();

        mockMvc.perform(put("/api/don-hang/{maDonHang}/trang-thai", maDonHang)
                .header("Authorization", bearer(admin))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of("trangThai", "DA_XAC_NHAN"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.duLieu.trangThai").value("DA_XAC_NHAN"));

        mockMvc.perform(put("/api/don-hang/{maDonHang}/trang-thai", maDonHang)
                .header("Authorization", bearer(admin))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of("trangThai", "CHO_XU_LY"))))
                .andExpect(status().isBadRequest());

        assertThat(donHangRepository.findById(maDonHang)).isPresent();
    }
}
