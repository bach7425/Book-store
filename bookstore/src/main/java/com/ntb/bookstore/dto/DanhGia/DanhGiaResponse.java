package com.ntb.bookstore.dto.DanhGia;

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
public class DanhGiaResponse {
    private Long maDanhGia;
    private Long maNguoiDung;
    private Long maSach;
    private String tenSach;
    private String tenNguoiDung;
    private String anhDaiDienNguoiDung;
    private Integer soSao;
    private String noiDung;
    private String trangThai;
    private String phanHoi;
}
