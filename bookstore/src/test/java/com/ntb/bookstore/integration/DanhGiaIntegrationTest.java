package com.ntb.bookstore.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.ntb.bookstore.entity.enums.TrangThaiDanhGia;

class DanhGiaIntegrationTest extends BaseIntegrationTest {

    @Test
    void userDaMuaDuocDanhGiaVaKhongDuocDanhGiaTrung() throws Exception {
        var user = taoNguoiDung();
        var sach = taoSach("Sách review", BigDecimal.valueOf(100_000), 5);
        taoDonHangDaMua(user, sach);

        mockMvc.perform(post("/api/sach/{maSach}/danh-gia", sach.getMaSach())
                .header("Authorization", bearer(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of("soSao", 5, "noiDung", "Rất hay"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.duLieu.trangThai").value("CHO_DUYET"));

        mockMvc.perform(post("/api/sach/{maSach}/danh-gia", sach.getMaSach())
                .header("Authorization", bearer(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of("soSao", 4, "noiDung", "Đánh giá lại"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void userChuaMuaHoacSoSaoKhongHopLeKhongDuocDanhGia() throws Exception {
        var user = taoNguoiDung();
        var sach = taoSach("Sách chưa mua", BigDecimal.valueOf(100_000), 5);

        mockMvc.perform(post("/api/sach/{maSach}/danh-gia", sach.getMaSach())
                .header("Authorization", bearer(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of("soSao", 5, "noiDung", "Chưa mua"))))
                .andExpect(status().isBadRequest());

        taoDonHangDaMua(user, sach);
        mockMvc.perform(post("/api/sach/{maSach}/danh-gia", sach.getMaSach())
                .header("Authorization", bearer(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of("soSao", 6, "noiDung", "Sai số sao"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void capNhatXoaVaChanSuaDanhGiaUserKhac() throws Exception {
        var user = taoNguoiDung();
        var userKhac = taoNguoiDung();
        var sach = taoSach("Sách quyền review", BigDecimal.valueOf(100_000), 5);
        var danhGia = taoDanhGia(user, sach, TrangThaiDanhGia.DA_DUYET);

        mockMvc.perform(put("/api/danh-gia/{maDanhGia}", danhGia.getMaDanhGia())
                .header("Authorization", bearer(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of("soSao", 4, "noiDung", "Cập nhật"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.duLieu.trangThai").value("CHO_DUYET"));

        mockMvc.perform(put("/api/danh-gia/{maDanhGia}", danhGia.getMaDanhGia())
                .header("Authorization", bearer(userKhac))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of("soSao", 3, "noiDung", "Không được phép"))))
                .andExpect(status().isBadRequest());

        mockMvc.perform(delete("/api/danh-gia/{maDanhGia}", danhGia.getMaDanhGia())
                .header("Authorization", bearer(user)))
                .andExpect(status().isOk());
    }

    @Test
    void adminDuyetTuChoiVaPublicChiThayDanhGiaDaDuyet() throws Exception {
        var admin = taoAdmin();
        var sach = taoSach("Sách duyệt review", BigDecimal.valueOf(100_000), 5);
        var daDuyet = taoDanhGia(taoNguoiDung(), sach, TrangThaiDanhGia.CHO_DUYET);
        var tuChoi = taoDanhGia(taoNguoiDung(), sach, TrangThaiDanhGia.CHO_DUYET);

        mockMvc.perform(patch("/api/quan-tri/danh-gia/{maDanhGia}/duyet", daDuyet.getMaDanhGia())
                .header("Authorization", bearer(admin))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of("phanHoi", "OK"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.duLieu.trangThai").value("DA_DUYET"));

        mockMvc.perform(patch("/api/quan-tri/danh-gia/{maDanhGia}/tu-choi", tuChoi.getMaDanhGia())
                .header("Authorization", bearer(admin))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of("phanHoi", "Cần bổ sung"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.duLieu.trangThai").value("TU_CHOI"));

        mockMvc.perform(get("/api/sach/{maSach}/danh-gia", sach.getMaSach()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.duLieu.tongSoPhanTu").value(1))
                .andExpect(jsonPath("$.duLieu.duLieu[0].trangThai").value("DA_DUYET"));
    }
}
