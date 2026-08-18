package com.ntb.bookstore.dto.XacThuc;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LamMoiTokenRequest {
    @NotBlank(message = "Refresh token không được để trống")
    private String maLamMoi;
}
