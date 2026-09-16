package com.ntb.bookstore.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class SachYeuThichIntegrationTest extends BaseIntegrationTest {

    @Test
    void themXemVaXoaSachYeuThich() throws Exception {
        var user = taoNguoiDung();
        var sach = taoSach("Sách yêu thích", BigDecimal.valueOf(85_000), 5);

        mockMvc.perform(post("/api/sach-yeu-thich/{maSach}", sach.getMaSach())
                .header("Authorization", bearer(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.duLieu.maSach").value(sach.getMaSach()));

        mockMvc.perform(get("/api/sach-yeu-thich")
                .header("Authorization", bearer(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.duLieu.tongSoPhanTu").value(1));

        mockMvc.perform(delete("/api/sach-yeu-thich/{maSach}", sach.getMaSach())
                .header("Authorization", bearer(user)))
                .andExpect(status().isOk());
    }

    @Test
    void themTrungSachYeuThichTraBadRequest() throws Exception {
        var user = taoNguoiDung();
        var sach = taoSach("Sách yêu thích trùng", BigDecimal.valueOf(85_000), 5);

        mockMvc.perform(post("/api/sach-yeu-thich/{maSach}", sach.getMaSach())
                .header("Authorization", bearer(user)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/sach-yeu-thich/{maSach}", sach.getMaSach())
                .header("Authorization", bearer(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.thanhCong").value(false));
    }
}
