package com.ntb.bookstore.dto.NguoiDung;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NguoiDungProfileResponse {
    private Long maNguoiDung;
    private String hoVaTen;
    private String tenDangNhap;
    private String email;
    private String soDienThoai;
    private String anhDaiDien;
    private String vaiTro;
}
