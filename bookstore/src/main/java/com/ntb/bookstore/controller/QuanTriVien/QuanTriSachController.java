package com.ntb.bookstore.controller.QuanTriVien;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ntb.bookstore.dto.ApiResponse;
import com.ntb.bookstore.dto.Sach.CapNhatSachRequest;
import com.ntb.bookstore.dto.Sach.CapNhatTonKhoRequest;
import com.ntb.bookstore.dto.Sach.SachResponse;
import com.ntb.bookstore.dto.Sach.ThemSachRequest;
import com.ntb.bookstore.service.SachService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/quan-tri/sach")
@PreAuthorize("hasAnyAuthority('ROLE_QUAN_TRI_VIEN')")
@AllArgsConstructor
public class QuanTriSachController {

        private final SachService sachService;

        @PostMapping
        public ResponseEntity<ApiResponse<SachResponse>> themSach(@RequestBody @Valid ThemSachRequest request) {
                return ResponseEntity.ok(ApiResponse.of(true, "Thêm sách thành công", LocalDateTime.now(),
                                sachService.themSach(request.getTenSach(), request.getMoTa(), request.getGia(),
                                                request.getNhaXuatBan(), request.getMaTacGia(), request.getMaTheLoai(),
                                                request.getSoLuongTon())));
        }

        @PutMapping("/{maSach}")
        public ResponseEntity<ApiResponse<SachResponse>> capNhatSach(@PathVariable Long maSach,
                        @RequestBody @Valid CapNhatSachRequest request) {
                return ResponseEntity.ok(ApiResponse.of(true, "Cập nhật sách thành công", LocalDateTime.now(),
                                sachService.capNhatSach(maSach, request.getTenSach(), request.getMoTa(),
                                                request.getGia(),
                                                request.getNhaXuatBan(), request.getMaTacGia(),
                                                request.getMaTheLoai(),
                                                request.getSoLuongTon())));
        }

        @PutMapping("/{maSach}/ton-kho")
        public ResponseEntity<ApiResponse<SachResponse>> capNhatTonKho(@PathVariable Long maSach,
                        @RequestBody @Valid CapNhatTonKhoRequest request) {
                return ResponseEntity.ok(ApiResponse.of(true, "Cập nhật tồn kho thành công", LocalDateTime.now(),
                                sachService.capNhatTonKho(maSach, request.getSoLuong())));
        }

        @PostMapping("/{maSach}/anh-bia")
        public ResponseEntity<ApiResponse<SachResponse>> capNhatAnhBia(@PathVariable Long maSach,
                        @RequestParam("file") MultipartFile file) {
                return ResponseEntity.ok(ApiResponse.of(true, "Cập nhật ảnh bìa thành công", LocalDateTime.now(),
                                sachService.capNhatAnhBia(maSach, file)));
        }
}
