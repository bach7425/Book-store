package com.ntb.bookstore.controller;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.ntb.bookstore.dto.ApiResponse;
import com.ntb.bookstore.dto.PageResponse;
import com.ntb.bookstore.dto.Sach.TacGiaResponse;
import com.ntb.bookstore.service.SachService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/tac-gia")
@AllArgsConstructor
public class TacGiaController {

    private final SachService sachService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<TacGiaResponse>>> danhSachTacGia(
            @RequestParam(required = false, defaultValue = "ten") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String baseUrl = ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString();
        return ResponseEntity.ok(ApiResponse.of(true, "Lay danh sach tac gia thanh cong", LocalDateTime.now(),
                sachService.danhSachTacGia(sort, page, size, baseUrl)));
    }
}
