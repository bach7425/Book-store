package com.ntb.bookstore.dto.NguoiDung;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CapNhatProfileRequest {
    private String hoVaTen;
    private String email;
    private String soDienThoai;
}
