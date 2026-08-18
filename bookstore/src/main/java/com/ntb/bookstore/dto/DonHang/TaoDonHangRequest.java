package com.ntb.bookstore.dto.DonHang;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaoDonHangRequest {
    @NotNull(message = "Mã địa chỉ không được để trống")
    private Long maDiaChi;
    @NotBlank(message = "Phương thức thanh toán không được để trống")
    private String phuongThucThanhToan;
    private String maGiamGia;
}
