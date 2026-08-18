package com.ntb.bookstore.dto.XacThuc;

import lombok.Builder;

@Builder
public record LamMoiTokenResponse(
        String maTruyCap,
        String loaiMa) {
    public LamMoiTokenResponse {
        if (loaiMa == null || loaiMa.isBlank()) {
            loaiMa = "Bearer";
        }
    }
}
