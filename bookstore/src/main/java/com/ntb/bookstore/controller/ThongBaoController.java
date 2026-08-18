package com.ntb.bookstore.controller;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.ntb.bookstore.dto.ApiResponse;
import com.ntb.bookstore.dto.PageResponse;
import com.ntb.bookstore.dto.ThongBao.ThongBaoResponse;
import com.ntb.bookstore.service.ThongBaoService;
import com.ntb.bookstore.service.XacThucService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
@PreAuthorize("hasAnyAuthority('ROLE_NGUOI_DUNG','ROLE_QUAN_TRI_VIEN')")
public class ThongBaoController {

    private final ThongBaoService thongBaoService;
    private final XacThucService xacThucService;

    @GetMapping("/thong-bao")
    public ResponseEntity<ApiResponse<PageResponse<ThongBaoResponse>>> danhSachThongBao(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long maNguoiDung = xacThucService.layMaNguoiDungHienTai();
        String baseUrl = ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString();
        return ResponseEntity.ok(ApiResponse.of(true, "Lay danh sach thong bao thanh cong", LocalDateTime.now(),
                thongBaoService.danhSachThongBao(maNguoiDung, page, size, baseUrl)));
    }

    @GetMapping("/thong-bao/chua-doc/so-luong")
    public ResponseEntity<ApiResponse<Long>> demThongBaoChuaDoc() {
        Long maNguoiDung = xacThucService.layMaNguoiDungHienTai();
        return ResponseEntity.ok(ApiResponse.of(true, "Lay so luong thong bao chua doc thanh cong",
                LocalDateTime.now(), thongBaoService.demThongBaoChuaDoc(maNguoiDung)));
    }

    @PatchMapping("/thong-bao/{maThongBao}/da-doc")
    public ResponseEntity<ApiResponse<ThongBaoResponse>> danhDauDaDoc(@PathVariable Long maThongBao) {
        Long maNguoiDung = xacThucService.layMaNguoiDungHienTai();
        return ResponseEntity.ok(ApiResponse.of(true, "Danh dau thong bao da doc thanh cong", LocalDateTime.now(),
                thongBaoService.danhDauDaDoc(maNguoiDung, maThongBao)));
    }

    @PatchMapping("/thong-bao/da-doc")
    public ResponseEntity<ApiResponse<Void>> danhDauTatCaDaDoc() {
        Long maNguoiDung = xacThucService.layMaNguoiDungHienTai();
        thongBaoService.danhDauTatCaDaDoc(maNguoiDung);
        return ResponseEntity.ok(ApiResponse.of(true, "Danh dau tat ca thong bao da doc thanh cong",
                LocalDateTime.now(), null));
    }

}
