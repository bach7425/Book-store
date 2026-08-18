package com.ntb.bookstore.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.Getter;


@Getter

public class PageResponse<T> {
    private List<T> duLieu;
    private int trang;
    private int kichThuocTrang;
    private long tongSoPhanTu;
    private int tongSoTrang;
    private String trangTruoc;
    private String tiepTheo;

    public PageResponse(Page<T> page, String baseUrl) {
        this.duLieu = page.getContent();
        this.trang = page.getNumber();
        this.kichThuocTrang = page.getSize();
        this.tongSoPhanTu = page.getTotalElements();
        this.tongSoTrang = page.getTotalPages();
        this.trangTruoc = page.hasPrevious()
            ? baseUrl + "?page=" + (page.getNumber() - 1) + "&size=" + page.getSize()
            : null;

    this.tiepTheo = page.hasNext()
            ? baseUrl + "?page=" + (page.getNumber() + 1) + "&size=" + page.getSize()
            : null;
}
}