package com.ntb.bookstore.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class NguoiDungIntegrationTest extends BaseIntegrationTest {

    @Test
    void xemVaCapNhatThongTinCaNhan() throws Exception {
        var user = taoNguoiDung();

        mockMvc.perform(get("/api/nguoi-dung/thong-tin")
                .header("Authorization", bearer(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.duLieu.tenDangNhap").value(user.getTenDangNhap()));

        mockMvc.perform(put("/api/nguoi-dung/thong-tin")
                .header("Authorization", bearer(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of(
                        "hoVaTen", "Tên đã cập nhật",
                        "email", "updated" + testId + "@example.com",
                        "soDienThoai", "0911111111"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.duLieu.hoVaTen").value("Tên đã cập nhật"));
    }

    @Test
    void doiMatKhauDungVaSai() throws Exception {
        var user = taoNguoiDung();

        mockMvc.perform(put("/api/nguoi-dung/doi-mat-khau")
                .header("Authorization", bearer(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of("matKhauCu", "pass123", "matKhauMoi", "newpass123"))))
                .andExpect(status().isOk());

        mockMvc.perform(put("/api/nguoi-dung/doi-mat-khau")
                .header("Authorization", bearer(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of("matKhauCu", "sai", "matKhauMoi", "newpass456"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.thanhCong").value(false));
    }

    @Test
    void themCapNhatDiaChiVaChanSuaDiaChiUserKhac() throws Exception {
        var user = taoNguoiDung();
        var userKhac = taoNguoiDung();
        var diaChiKhac = taoDiaChi(userKhac);

        mockMvc.perform(post("/api/nguoi-dung/dia-chi")
                .header("Authorization", bearer(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of(
                        "nguoiNhan", "Người nhận",
                        "soDienThoai", "0912345678",
                        "diaChiChiTiet", "456 Đường Test",
                        "macDinh", true))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.duLieu.macDinh").value(true));

        mockMvc.perform(put("/api/nguoi-dung/dia-chi/{id}", diaChiKhac.getMaDiaChi())
                .header("Authorization", bearer(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of(
                        "nguoiNhan", "Không hợp lệ",
                        "soDienThoai", "0999999999",
                        "diaChiChiTiet", "Địa chỉ user khác",
                        "macDinh", false))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.thanhCong").value(false));
    }
}
