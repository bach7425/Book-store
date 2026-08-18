package com.ntb.bookstore.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.ntb.bookstore.dto.ApiResponse;
import com.ntb.bookstore.dto.PageResponse;
import com.ntb.bookstore.dto.Sach.SachResponse;
import com.ntb.bookstore.service.SachService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/sach")
@AllArgsConstructor
public class SachController {

    private final SachService sachService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<SachResponse>>> danhSachSach(
            @RequestParam(required = false) String tuKhoa,
            @RequestParam(required = false) Long tacGiaId,
            @RequestParam(required = false) Long theLoaiId,
            @RequestParam(required = false) BigDecimal giaMin,
            @RequestParam(required = false) BigDecimal giaMax,
            @RequestParam(required = false, defaultValue = "tenSach") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String baseUrl = ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString();
        return ResponseEntity.ok(ApiResponse.of(true, "Lay danh sach sach thanh cong", LocalDateTime.now(),
                sachService.danhSachSach(tuKhoa, tacGiaId, theLoaiId, giaMin, giaMax, sort, page, size, baseUrl)));
    }

    @GetMapping("/{maSach}")
    public ResponseEntity<ApiResponse<SachResponse>> chiTietSach(@PathVariable Long maSach) {
        return ResponseEntity.ok(ApiResponse.of(true, "Lay chi tiet sach thanh cong", LocalDateTime.now(),
                sachService.chiTietSach(maSach)));
    }
}
