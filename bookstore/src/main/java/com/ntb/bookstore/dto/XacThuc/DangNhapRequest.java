package com.ntb.bookstore.dto.XacThuc;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DangNhapRequest {
    @NotBlank(message = "Tên đăng nhập không được để trống")
    private String tenDangNhap;
    @NotBlank(message = "Mật khẩu không được để trống")
    private String matKhau;
}
