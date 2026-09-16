package com.ntb.bookstore.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class ThongBaoIntegrationTest extends BaseIntegrationTest {

    @Test
    void adminGuiThongBaoVaUserDocDanhDauDaDoc() throws Exception {
        var admin = taoAdmin();
        var user = taoNguoiDung();

        mockMvc.perform(post("/api/quan-tri/thong-bao")
                .header("Authorization", bearer(admin))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of(
                        "maNguoiDung", user.getMaNguoiDung(),
                        "guiTatCa", false,
                        "tieuDe", "Xin chào",
                        "noiDung", "Nội dung thông báo",
                        "loai", "QUAN_TRI",
                        "duongDan", "/thong-bao"))))
                .andExpect(status().isOk());

        var listResult = mockMvc.perform(get("/api/thong-bao")
                .header("Authorization", bearer(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.duLieu.tongSoPhanTu").value(1))
                .andReturn();
        long maThongBao = objectMapper.readTree(listResult.getResponse().getContentAsString())
                .at("/duLieu/duLieu/0/maThongBao").asLong();

        mockMvc.perform(get("/api/thong-bao/chua-doc/so-luong")
                .header("Authorization", bearer(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.duLieu").value(1));

        mockMvc.perform(patch("/api/thong-bao/{maThongBao}/da-doc", maThongBao)
                .header("Authorization", bearer(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.duLieu.daDoc").value(true));

        mockMvc.perform(patch("/api/thong-bao/da-doc")
                .header("Authorization", bearer(user)))
                .andExpect(status().isOk());
    }

    @Test
    void khongDanhDauDuocThongBaoCuaUserKhac() throws Exception {
        var user = taoNguoiDung();
        var userKhac = taoNguoiDung();
        var thongBaoKhac = taoThongBao(userKhac);

        mockMvc.perform(patch("/api/thong-bao/{maThongBao}/da-doc", thongBaoKhac.getMaThongBao())
                .header("Authorization", bearer(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.thanhCong").value(false));
    }

    @Test
    void guiThongBaoThieuTieuDeTraBadRequest() throws Exception {
        var admin = taoAdmin();
        var user = taoNguoiDung();

        mockMvc.perform(post("/api/quan-tri/thong-bao")
                .header("Authorization", bearer(admin))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of(
                        "maNguoiDung", user.getMaNguoiDung(),
                        "guiTatCa", false,
                        "tieuDe", "",
                        "noiDung", "Nội dung",
                        "loai", "QUAN_TRI"))))
                .andExpect(status().isBadRequest());
    }
}
