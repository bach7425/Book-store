package com.ntb.bookstore.controller.QuanTriVien;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ntb.bookstore.dto.ApiResponse;
import com.ntb.bookstore.dto.ThongBao.GuiThongBaoRequest;
import com.ntb.bookstore.service.ThongBaoService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/quan-tri/thong-bao")
@PreAuthorize("hasAnyAuthority('ROLE_QUAN_TRI_VIEN')")
@AllArgsConstructor
public class QuanTriThongBaoController {

    private final ThongBaoService thongBaoService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> guiThongBaoQuanTri(@RequestBody @Valid GuiThongBaoRequest request) {
        thongBaoService.guiThongBaoQuanTri(request.getMaNguoiDung(), request.isGuiTatCa(), request.getTieuDe(),
                request.getNoiDung(), request.getLoai(), request.getDuongDan());
        return ResponseEntity.ok(ApiResponse.of(true, "Gui thong bao thanh cong", LocalDateTime.now(), null));
    }
}
