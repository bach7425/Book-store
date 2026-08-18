package com.ntb.bookstore.dto.NguoiDung;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThemDiaChiRequest {
    @NotBlank(message = "Người nhận không được để trống")
    @Size(max = 100, message = "Người nhận không được vượt quá 100 ký tự")
    private String nguoiNhan;

    @NotBlank(message = "Số điện thoại không được để trống")
    private String soDienThoai;

    @NotBlank(message = "Địa chỉ chi tiết không được để trống")
    @Size(max = 255, message = "Địa chỉ không được vượt quá 255 ký tự")
    private String diaChiChiTiet;
    private Boolean macDinh;
}
