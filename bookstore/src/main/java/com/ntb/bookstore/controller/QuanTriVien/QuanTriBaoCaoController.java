package com.ntb.bookstore.controller.QuanTriVien;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.ntb.bookstore.dto.ApiResponse;
import com.ntb.bookstore.dto.PageResponse;
import com.ntb.bookstore.dto.QuanTri.BaoCaoDoanhThuResponse;
import com.ntb.bookstore.dto.QuanTri.SachBanChayResponse;
import com.ntb.bookstore.service.BaoCaoService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/quan-tri")
@PreAuthorize("hasAnyAuthority('ROLE_QUAN_TRI_VIEN')")
@AllArgsConstructor
public class QuanTriBaoCaoController {

    private final BaoCaoService baoCaoService;

    @GetMapping("/bao-cao/doanh-thu")
    public ResponseEntity<ApiResponse<BaoCaoDoanhThuResponse>> baoCaoDoanhThu(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay) {
        return ResponseEntity.ok(ApiResponse.of(true, "Lấy báo cáo doanh thu thành công", LocalDateTime.now(),
                baoCaoService.baoCaoDoanhThu(tuNgay, denNgay)));
    }

    @GetMapping("/thong-ke/sach-ban-chay")
    public ResponseEntity<ApiResponse<PageResponse<SachBanChayResponse>>> thongKeSachBanChay(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String baseUrl = ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString();
        return ResponseEntity.ok(ApiResponse.of(true, "Lấy thống kê sách bán chạy thành công", LocalDateTime.now(),
                baoCaoService.thongKeSachBanChay(tuNgay, denNgay, page, size, baseUrl)));
    }
}
