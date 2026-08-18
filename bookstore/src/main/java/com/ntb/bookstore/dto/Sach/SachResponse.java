package com.ntb.bookstore.dto.Sach;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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
public class SachResponse {
    private Long maSach;
    private String tenSach;
    private String moTa;
    private BigDecimal gia;
    private String anhBia;
    private String nhaXuatBan;
    private LocalDate ngayXuatBan;
    private TacGiaResponse tacGia;
    private List<TheLoaiResponse> theLoais;
    private Integer soLuongTon;
    private Double diemDanhGiaTrungBinh;
    private Long soLuongDanhGia;
}
