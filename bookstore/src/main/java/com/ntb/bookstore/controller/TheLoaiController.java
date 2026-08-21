package com.ntb.bookstore.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ntb.bookstore.dto.ApiResponse;
import com.ntb.bookstore.dto.Sach.TheLoaiResponse;
import com.ntb.bookstore.service.SachService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/the-loai")
@AllArgsConstructor
public class TheLoaiController {

    private final SachService sachService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TheLoaiResponse>>> danhSachTheLoai() {
        return ResponseEntity.ok(ApiResponse.of(true, "Lấy danh sách thể loại thành công", LocalDateTime.now(),
                sachService.danhSachTheLoai()));
    }
}
