package com.ntb.bookstore.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ntb.bookstore.dto.ApiResponse;
import com.ntb.bookstore.dto.NguoiDung.CapNhatProfileRequest;
import com.ntb.bookstore.dto.NguoiDung.DiaChiResponse;
import com.ntb.bookstore.dto.NguoiDung.DoiMatKhauRequest;
import com.ntb.bookstore.dto.NguoiDung.NguoiDungProfileResponse;
import com.ntb.bookstore.dto.NguoiDung.ThemDiaChiRequest;
import com.ntb.bookstore.service.NguoiDungService;
import com.ntb.bookstore.service.XacThucService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/nguoi-dung")
@RequiredArgsConstructor
public class NguoiDungController {

        private final NguoiDungService nguoiDungService;
        private final XacThucService xacThucService;

        @GetMapping("/thong-tin")
        @PreAuthorize("hasAnyAuthority('ROLE_NGUOI_DUNG','ROLE_QUAN_TRI_VIEN')")
        public ResponseEntity<ApiResponse<NguoiDungProfileResponse>> layThongTinProfile() {
                Long maNguoiDung = xacThucService.layMaNguoiDungHienTai();
                return ResponseEntity.ok(ApiResponse.of(true,
                                "Lấy thông tin profile thành công",
                                LocalDateTime.now(),
                                nguoiDungService.layThongTinProfile(maNguoiDung)));
        }

        @PutMapping("/thong-tin")
        @PreAuthorize("hasAnyAuthority('ROLE_NGUOI_DUNG','ROLE_QUAN_TRI_VIEN')")
        public ResponseEntity<ApiResponse<NguoiDungProfileResponse>> capNhatProfile(
                        @RequestBody CapNhatProfileRequest request) {
                Long maNguoiDung = xacThucService.layMaNguoiDungHienTai();
                return ResponseEntity.ok(ApiResponse.of(true,
                                "Cập nhật profile thành công",
                                LocalDateTime.now(),
                                nguoiDungService.capNhatProfile(maNguoiDung, request.getHoVaTen(),
                                                request.getEmail(), request.getSoDienThoai())));
        }

        @PostMapping("/anh-dai-dien")
        @PreAuthorize("hasAnyAuthority('ROLE_NGUOI_DUNG','ROLE_QUAN_TRI_VIEN')")
        public ResponseEntity<ApiResponse<NguoiDungProfileResponse>> capNhatAnhDaiDien(
                        @RequestParam("file") MultipartFile file) {
                Long maNguoiDung = xacThucService.layMaNguoiDungHienTai();
                return ResponseEntity.ok(ApiResponse.of(true,
                                "Cập nhật ảnh đại diện thành công",
                                LocalDateTime.now(),
                                nguoiDungService.capNhatAnhDaiDien(maNguoiDung, file)));
        }

        @PutMapping("/doi-mat-khau")
        @PreAuthorize("hasAnyAuthority('ROLE_NGUOI_DUNG','ROLE_QUAN_TRI_VIEN')")
        public ResponseEntity<ApiResponse<Void>> doiMatKhau(@RequestBody @Valid DoiMatKhauRequest request) {
                Long maNguoiDung = xacThucService.layMaNguoiDungHienTai();
                nguoiDungService.doiMatKhau(maNguoiDung, request.getMatKhauCu(), request.getMatKhauMoi());
                return ResponseEntity.ok(ApiResponse.of(true, "Đổi mật khẩu thành công", LocalDateTime.now(), null));
        }

        @GetMapping("/dia-chi")
        @PreAuthorize("hasAnyAuthority('ROLE_NGUOI_DUNG')")
        public ResponseEntity<ApiResponse<List<DiaChiResponse>>> layDanhSachDiaChi() {
                Long maNguoiDung = xacThucService.layMaNguoiDungHienTai();
                return ResponseEntity.ok(ApiResponse.of(true,
                                "Lấy danh sách địa chỉ thành công",
                                LocalDateTime.now(),
                                nguoiDungService.layDanhSachDiaChi(maNguoiDung)));
        }

        @PostMapping("/dia-chi")
        @PreAuthorize("hasAnyAuthority('ROLE_NGUOI_DUNG')")
        public ResponseEntity<ApiResponse<DiaChiResponse>> themDiaChi(@RequestBody @Valid ThemDiaChiRequest request) {
                Long maNguoiDung = xacThucService.layMaNguoiDungHienTai();
                return ResponseEntity.ok(ApiResponse.of(true,
                                "Thêm địa chỉ thành công",
                                LocalDateTime.now(),
                                nguoiDungService.themDiaChi(maNguoiDung, request.getNguoiNhan(),
                                                request.getSoDienThoai(),
                                                request.getDiaChiChiTiet(), request.getMacDinh())));
        }

        @PutMapping("/dia-chi/{id}")
        @PreAuthorize("hasAnyAuthority('ROLE_NGUOI_DUNG')")
        public ResponseEntity<ApiResponse<DiaChiResponse>> capNhatDiaChi(@PathVariable Long id,
                        @RequestBody @Valid ThemDiaChiRequest request) {
                Long maNguoiDung = xacThucService.layMaNguoiDungHienTai();
                return ResponseEntity.ok(ApiResponse.of(true,
                                "Cập nhật địa chỉ thành công",
                                LocalDateTime.now(),
                                nguoiDungService.capNhatDiaChi(maNguoiDung, id, request.getNguoiNhan(),
                                                request.getSoDienThoai(),
                                                request.getDiaChiChiTiet(), request.getMacDinh())));
        }

}
