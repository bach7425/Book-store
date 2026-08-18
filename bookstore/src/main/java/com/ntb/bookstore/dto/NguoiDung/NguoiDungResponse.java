package com.ntb.bookstore.dto.NguoiDung;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NguoiDungResponse {
    private Long maNguoiDung;
    private String hoVaTen;
    private String email;
    private String tenDangNhap;
    private String soDienThoai;
    private String anhDaiDien;
    private String vaiTro;
}
