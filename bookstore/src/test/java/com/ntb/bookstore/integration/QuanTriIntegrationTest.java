package com.ntb.bookstore.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

class QuanTriIntegrationTest extends BaseIntegrationTest {

    @Test
    void adminThemCapNhatSachTacGiaTheLoai() throws Exception {
        var admin = taoAdmin();
        var tacGia = taoTacGia("Tác giả quản trị");
        var theLoai = taoTheLoai("Thể loại quản trị");

        var createResult = mockMvc.perform(post("/api/quan-tri/sach")
                .header("Authorization", bearer(admin))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of(
                        "tenSach", "Sách quản trị",
                        "gia", 120_000,
                        "maTacGia", tacGia.getMaTacGia(),
                        "maTheLoai", List.of(theLoai.getMaTheLoai()),
                        "soLuongTon", 12))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.duLieu.tenSach").value("Sách quản trị"))
                .andReturn();
        long maSach = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .at("/duLieu/maSach").asLong();

        mockMvc.perform(put("/api/quan-tri/sach/{maSach}", maSach)
                .header("Authorization", bearer(admin))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of(
                        "tenSach", "Sách quản trị cập nhật",
                        "gia", 130_000,
                        "maTacGia", tacGia.getMaTacGia(),
                        "maTheLoai", List.of(theLoai.getMaTheLoai()),
                        "soLuongTon", 9))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.duLieu.soLuongTon").value(9));

        mockMvc.perform(post("/api/quan-tri/tac-gia")
                .header("Authorization", bearer(admin))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of("ten", "Tác giả mới", "tieuSu", "Tiểu sử"))))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/quan-tri/the-loai")
                .header("Authorization", bearer(admin))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of("ten", "Thể loại mới", "moTa", "Mô tả"))))
                .andExpect(status().isOk());
    }

    @Test
    void duLieuSachKhongHopLeTraBadRequest() throws Exception {
        var admin = taoAdmin();

        mockMvc.perform(post("/api/quan-tri/sach")
                .header("Authorization", bearer(admin))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of("tenSach", "", "gia", -1, "soLuongTon", -1))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.thanhCong").value(false));
    }

    @Test
    void uploadAnhBiaHopLeVaChanFileKhongPhaiAnh() throws Exception {
        var admin = taoAdmin();
        var sach = taoSach("Sách upload", BigDecimal.valueOf(110_000), 5);
        var image = new MockMultipartFile("file", "cover.png", "image/png", new byte[] { 1, 2, 3 });
        var text = new MockMultipartFile("file", "cover.txt", "text/plain", "not-image".getBytes());

        mockMvc.perform(multipart("/api/quan-tri/sach/{maSach}/anh-bia", sach.getMaSach())
                .file(image)
                .header("Authorization", bearer(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.duLieu.anhBia").isNotEmpty());

        mockMvc.perform(multipart("/api/quan-tri/sach/{maSach}/anh-bia", sach.getMaSach())
                .file(text)
                .header("Authorization", bearer(admin)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void quanLyMaGiamGiaVaBaoCao() throws Exception {
        var admin = taoAdmin();

        var createResult = mockMvc.perform(post("/api/quan-tri/ma-giam-gia")
                .header("Authorization", bearer(admin))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of(
                        "maCode", "ADMINSALE" + testId,
                        "loaiGiam", "PHAN_TRAM",
                        "giaTri", 10,
                        "giamToiDa", 50_000,
                        "donToiThieu", 0,
                        "soLuong", 5,
                        "ngayBatDau", LocalDateTime.now().minusDays(1).toString(),
                        "ngayKetThuc", LocalDateTime.now().plusDays(1).toString(),
                        "trangThai", "HOAT_DONG"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.duLieu.maCode").value("ADMINSALE" + testId))
                .andReturn();
        long maGiamGia = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .at("/duLieu/maGiamGia").asLong();

        mockMvc.perform(put("/api/quan-tri/ma-giam-gia/{maGiamGia}", maGiamGia)
                .header("Authorization", bearer(admin))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of("soLuong", 9))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.duLieu.soLuong").value(9));

        mockMvc.perform(delete("/api/quan-tri/ma-giam-gia/{maGiamGia}", maGiamGia)
                .header("Authorization", bearer(admin)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/quan-tri/bao-cao/doanh-thu")
                .header("Authorization", bearer(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.duLieu").exists());

        mockMvc.perform(get("/api/quan-tri/thong-ke/sach-ban-chay")
                .header("Authorization", bearer(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.duLieu.duLieu").isArray());
    }
}
