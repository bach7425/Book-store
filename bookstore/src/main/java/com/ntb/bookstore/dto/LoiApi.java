package com.ntb.bookstore.dto;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record LoiApi(
    String loi,
    String thongBao, 
    List<ChiTietLoiTruong> chiTiets,
    String duongDan,
    Instant thoiGian
) {

    public record ChiTietLoiTruong(String tenTruong, String thongBao) {
    }
}

