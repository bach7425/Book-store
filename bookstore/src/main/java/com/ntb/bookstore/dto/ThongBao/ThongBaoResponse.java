package com.ntb.bookstore.dto.ThongBao;

import java.time.LocalDateTime;

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
public class ThongBaoResponse {
    private Long maThongBao;
    private String tieuDe;
    private String noiDung;
    private String loai;
    private Boolean daDoc;
    private LocalDateTime ngayTao;
    private String duongDan;
}
