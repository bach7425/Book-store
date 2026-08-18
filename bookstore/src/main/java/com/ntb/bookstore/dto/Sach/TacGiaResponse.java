package com.ntb.bookstore.dto.Sach;

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
public class TacGiaResponse {
    private Long maTacGia;
    private String ten;
    private String tieuSu;
    private String anhDaiDien;
}
