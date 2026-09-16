package com.ntb.bookstore.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class GioHangIntegrationTest extends BaseIntegrationTest {

    @Test
    void themSachVaoGioVaThemLaiTangSoLuong() throws Exception {
        var user = taoNguoiDung();
        var sach = taoSach("Sách giỏ hàng", BigDecimal.valueOf(50_000), 10);

        mockMvc.perform(post("/api/gio-hang/san-pham")
                .header("Authorization", bearer(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of("maSach", sach.getMaSach(), "soLuong", 2))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.duLieu.soLuong").value(2));

        mockMvc.perform(post("/api/gio-hang/san-pham")
                .header("Authorization", bearer(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of("maSach", sach.getMaSach(), "soLuong", 3))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.duLieu.soLuong").value(5));
    }

    @Test
    void capNhatSoLuongHopLeVaKhongHopLe() throws Exception {
        var user = taoNguoiDung();
        var item = themVaoGioHang(user, taoSach("Sách cập nhật giỏ", BigDecimal.valueOf(70_000), 8), 2);

        mockMvc.perform(put("/api/gio-hang/san-pham/{sanPhamId}", item.getMaChiTietGioHang())
                .header("Authorization", bearer(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of("soLuong", 4))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.duLieu.soLuong").value(4));

        mockMvc.perform(put("/api/gio-hang/san-pham/{sanPhamId}", item.getMaChiTietGioHang())
                .header("Authorization", bearer(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of("soLuong", 0))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.thanhCong").value(false));
    }

    @Test
    void xoaSanPhamVaXemGioHangRong() throws Exception {
        var user = taoNguoiDung();
        var item = themVaoGioHang(user, taoSach("Sách xóa giỏ", BigDecimal.valueOf(60_000), 8), 1);

        mockMvc.perform(delete("/api/gio-hang/san-pham/{sanPhamId}", item.getMaChiTietGioHang())
                .header("Authorization", bearer(user)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/gio-hang")
                .header("Authorization", bearer(user)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.thanhCong").value(false));
    }
}
