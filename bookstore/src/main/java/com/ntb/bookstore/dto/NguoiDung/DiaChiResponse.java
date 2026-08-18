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
public class DiaChiResponse {
    private Long maDiaChi;
    private String nguoiNhan;
    private String soDienThoai;
    private String diaChiChiTiet;
    private Boolean macDinh;
}
