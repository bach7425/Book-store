package com.ntb.bookstore.dto.Sach;

import jakarta.validation.constraints.NotBlank;
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
public  class ThemTacGiaRequest {
    @NotBlank(message = "Tên tác giả không được để trống")
    private String ten;
    private String tieuSu;
    private String anhDaiDien;
}
