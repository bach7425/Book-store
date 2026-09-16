package com.ntb.bookstore.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class XacThucIntegrationTest extends BaseIntegrationTest {

    @Test
    void dangKyThanhCong() throws Exception {
        mockMvc.perform(post("/api/xac-thuc/dang-ky")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of(
                        "hoTen", "Nguyễn Văn Test",
                        "tenDangNhap", "dangky" + testId,
                        "email", "dangky" + testId + "@example.com",
                        "matKhau", "pass123",
                        "soDienThoai", "0987654321"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.thanhCong").value(true))
                .andExpect(jsonPath("$.duLieu.tenDangNhap").value("dangky" + testId));
    }

    @Test
    void dangKyThieuDuLieuHoacTrungDuLieuTraBadRequest() throws Exception {
        var user = taoNguoiDung();

        mockMvc.perform(post("/api/xac-thuc/dang-ky")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of(
                        "hoTen", "",
                        "tenDangNhap", "missing" + testId,
                        "email", "missing" + testId + "@example.com",
                        "matKhau", "pass123"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.thanhCong").value(false));

        mockMvc.perform(post("/api/xac-thuc/dang-ky")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of(
                        "hoTen", "Nguyễn Văn Test",
                        "tenDangNhap", user.getTenDangNhap(),
                        "email", "unique" + testId + "@example.com",
                        "matKhau", "pass123"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.thanhCong").value(false));
    }

    @Test
    void dangNhapVaLamMoiTokenThanhCong() throws Exception {
        var user = taoNguoiDung();

        var response = dangNhap(user.getTenDangNhap(), "pass123");

        assertThat(response.at("/duLieu/maTruyCap").asText()).isNotBlank();
        assertThat(response.at("/duLieu/maLamMoi").asText()).isNotBlank();

        mockMvc.perform(post("/api/xac-thuc/lam-moi-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of("maLamMoi", response.at("/duLieu/maLamMoi").asText()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.duLieu.maTruyCap").isNotEmpty());
    }

    @Test
    void dangNhapSaiTraBadRequest() throws Exception {
        var user = taoNguoiDung();

        mockMvc.perform(post("/api/xac-thuc/dang-nhap")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of("tenDangNhap", user.getTenDangNhap(), "matKhau", "sai-mat-khau"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.thanhCong").value(false));
    }
}
