package com.ntb.bookstore.dto.XacThuc;

import com.ntb.bookstore.dto.NguoiDung.NguoiDungResponse;

import lombok.Builder;

@Builder
public record XacThucRespone(
    String maTruyCap,
    String maLamMoi,
    String loaiMa,
    NguoiDungResponse nguoiDung
) {
    public XacThucRespone {
        if (loaiMa == null || loaiMa.isBlank()) {
            loaiMa = "Bearer";
        }
    }

}

