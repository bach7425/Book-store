package com.ntb.bookstore.integration;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.ntb.bookstore.entity.enums.TrangThaiDanhGia;

class PublicCatalogIntegrationTest extends BaseIntegrationTest {

    @Test
    void layDanhSachSachVaLocTheoTuKhoaTacGiaTheLoaiGia() throws Exception {
        var sach = taoSach("Clean Code", BigDecimal.valueOf(120_000), 8);
        var theLoai = sach.getTheLoais().getFirst();

        mockMvc.perform(get("/api/sach")
                .param("tuKhoa", "Clean")
                .param("tacGiaId", sach.getTacGia().getMaTacGia().toString())
                .param("theLoaiId", theLoai.getMaTheLoai().toString())
                .param("giaMin", "100000")
                .param("giaMax", "150000")
                .param("page", "0")
                .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.duLieu.duLieu[0].maSach").value(sach.getMaSach()));
    }

    @Test
    void xemChiTietVaSachKhongTonTai() throws Exception {
        var sach = taoSach("Mắt Biếc", BigDecimal.valueOf(90_000), 4);

        mockMvc.perform(get("/api/sach/{maSach}", sach.getMaSach()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.duLieu.tenSach").value(sach.getTenSach()));

        mockMvc.perform(get("/api/sach/{maSach}", 999_999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.thanhCong").value(false));
    }

    @Test
    void publicChiThayDanhGiaDaDuyet() throws Exception {
        var sach = taoSach("Sách đánh giá", BigDecimal.valueOf(80_000), 5);
        taoDanhGia(taoNguoiDung(), sach, TrangThaiDanhGia.DA_DUYET);
        taoDanhGia(taoNguoiDung(), sach, TrangThaiDanhGia.CHO_DUYET);

        mockMvc.perform(get("/api/sach/{maSach}/danh-gia", sach.getMaSach()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.duLieu.tongSoPhanTu").value(1))
                .andExpect(jsonPath("$.duLieu.duLieu", hasSize(1)))
                .andExpect(jsonPath("$.duLieu.duLieu[0].trangThai").value("DA_DUYET"));
    }
}
