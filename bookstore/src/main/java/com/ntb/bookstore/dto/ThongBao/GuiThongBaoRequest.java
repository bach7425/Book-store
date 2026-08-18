package com.ntb.bookstore.dto.ThongBao;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GuiThongBaoRequest {
    private Long maNguoiDung;
    private boolean guiTatCa;
    @NotBlank(message = "Tiêu đề không được để trống")
    private String tieuDe;
    @NotBlank(message = "Nội dung thông báo không được để trống")
    private String noiDung;
    private String loai;
    private String duongDan;
}
