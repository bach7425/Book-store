package com.ntb.bookstore.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;

class SecurityIntegrationTest extends BaseIntegrationTest {

    @Test
    void khachGoiApiCanDangNhapNhanUnauthorized() throws Exception {
        mockMvc.perform(get("/api/gio-hang"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void userThuongGoiApiQuanTriNhanForbidden() throws Exception {
        var user = taoNguoiDung();

        mockMvc.perform(get("/api/quan-tri/khach-hang")
                .header("Authorization", bearer(user)))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminGoiApiQuanTriThanhCong() throws Exception {
        var admin = taoAdmin();

        mockMvc.perform(get("/api/quan-tri/khach-hang")
                .header("Authorization", bearer(admin)))
                .andExpect(status().isOk());
    }

    @Test
    void tokenKhongHopLeKhongDuocXacThuc() throws Exception {
        mockMvc.perform(get("/api/nguoi-dung/thong-tin")
                .header("Authorization", "Bearer token-sai-dinh-dang"))
                .andExpect(status().isUnauthorized());
    }
}
