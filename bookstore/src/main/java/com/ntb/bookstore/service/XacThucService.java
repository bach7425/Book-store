package com.ntb.bookstore.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ntb.bookstore.dto.NguoiDung.NguoiDungResponse;
import com.ntb.bookstore.dto.XacThuc.DangKyRequest;
import com.ntb.bookstore.dto.XacThuc.DangNhapRequest;
import com.ntb.bookstore.dto.XacThuc.XacThucRespone;
import com.ntb.bookstore.entity.GioHang;
import com.ntb.bookstore.entity.NguoiDung;
import com.ntb.bookstore.entity.enums.VaiTro;
import com.ntb.bookstore.exception.HethongLoiException;
import com.ntb.bookstore.repository.GioHangRepository;
import com.ntb.bookstore.repository.NguoiDungRepository;
import com.ntb.bookstore.security.JwtService;

@Service
public class XacThucService {
    private static final Logger logger = LoggerFactory.getLogger(XacThucService.class);
    private final NguoiDungRepository nguoiDungRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager quanLyXacThuc;
    private final GioHangRepository gioHangRepository;

    public XacThucService(NguoiDungRepository nguoiDungRepository, PasswordEncoder passwordEncoder,
            JwtService jwtService, AuthenticationManager quanLyXacThuc, GioHangRepository gioHangRepository) {
        this.nguoiDungRepository = nguoiDungRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.quanLyXacThuc = quanLyXacThuc;
        this.gioHangRepository = gioHangRepository;
    }

    public NguoiDungResponse dangKy(DangKyRequest dangKyRequest) {
        if (nguoiDungRepository.existsByTenDangNhap(dangKyRequest.getTenDangNhap())) {
            throw new HethongLoiException("Tên đăng nhập đã tồn tại: " + dangKyRequest.getTenDangNhap());
        }
        if (nguoiDungRepository.existsByEmail(dangKyRequest.getEmail())) {
            throw new HethongLoiException("Email đã tồn tại: " + dangKyRequest.getEmail());
        }
        if (dangKyRequest.getSoDienThoai() != null
                && nguoiDungRepository.existsBySoDienThoai(dangKyRequest.getSoDienThoai())) {
            throw new HethongLoiException("Số điện thoại đã tồn tại: " + dangKyRequest.getSoDienThoai());
        }
        NguoiDung nguoiDung = NguoiDung.builder()
                .tenDangNhap(dangKyRequest.getTenDangNhap())
                .email(dangKyRequest.getEmail())
                .matKhauBam(passwordEncoder.encode(dangKyRequest.getMatKhau()))
                .hoVaTen(dangKyRequest.getHoTen())
                .soDienThoai(dangKyRequest.getSoDienThoai())
                .vaiTro(VaiTro.NGUOI_DUNG)
                .build();
        nguoiDung = nguoiDungRepository.save(nguoiDung);
        gioHangRepository.save(GioHang.builder().nguoiDung(nguoiDung).build());
        logger.info("Người dùng mới đã được tạo: {}", nguoiDung.getTenDangNhap());
        return NguoiDungResponse.builder()
                .maNguoiDung(nguoiDung.getMaNguoiDung())
                .hoVaTen(nguoiDung.getHoVaTen())
                .tenDangNhap(nguoiDung.getTenDangNhap())
                .email(nguoiDung.getEmail())
                .soDienThoai(nguoiDung.getSoDienThoai())
                .anhDaiDien(nguoiDung.getAnhDaiDien())
                .vaiTro(nguoiDung.getAuthorities().stream().findFirst().map(Object::toString).orElse(null))
                .build();
    }

    public XacThucRespone dangNhap(DangNhapRequest dangNhapRequest) {
        try {
            quanLyXacThuc.authenticate(
                    new UsernamePasswordAuthenticationToken(dangNhapRequest.getTenDangNhap(),
                            dangNhapRequest.getMatKhau()));
        } catch (Exception e) {
            throw new HethongLoiException("Tên đăng nhập hoặc mật khẩu không hợp lệ");
        }

        NguoiDung nguoiDung = nguoiDungRepository.findByTenDangNhap(dangNhapRequest.getTenDangNhap())
                .orElseThrow(
                        () -> new HethongLoiException("Người dùng không tồn tại: " + dangNhapRequest.getTenDangNhap()));

        String maTruyCap = jwtService.taoMaTruyCap(nguoiDung.getTenDangNhap(), nguoiDung.getMaNguoiDung(),
                nguoiDung.getAuthorities().toString());
        String maLamMoi = jwtService.taoMaLamMoi(nguoiDung.getTenDangNhap(), nguoiDung.getMaNguoiDung(),
                nguoiDung.getAuthorities().toString());

        NguoiDungResponse nguoiDungResponse = NguoiDungResponse.builder()
                .maNguoiDung(nguoiDung.getMaNguoiDung())
                .hoVaTen(nguoiDung.getHoVaTen())
                .tenDangNhap(nguoiDung.getTenDangNhap())
                .email(nguoiDung.getEmail())
                .soDienThoai(nguoiDung.getSoDienThoai())
                .anhDaiDien(nguoiDung.getAnhDaiDien())
                .vaiTro(nguoiDung.getAuthorities().stream().findFirst().map(Object::toString).orElse(null))
                .build();

        return XacThucRespone.builder()
                .maTruyCap(maTruyCap)
                .maLamMoi(maLamMoi)
                .loaiMa("Bearer")
                .nguoiDung(nguoiDungResponse)
                .build();
    }

    public String taoAccessTokenMoi(String maLamMoi) {
        try {
            String username = jwtService.layTenNguoiDungTuToken(maLamMoi);
            NguoiDung nguoiDung = nguoiDungRepository.findByTenDangNhap(username)
                    .orElseThrow(() -> new HethongLoiException("Không tìm thấy người dùng"));
            return jwtService.taoMaTruyCap(nguoiDung.getTenDangNhap(), nguoiDung.getMaNguoiDung(),
                    nguoiDung.getAuthorities().toString());
        } catch (Exception ex) {
            throw new HethongLoiException("Refresh token không hợp lệ");
        }
    }

    public NguoiDung layNguoiDungHienTai() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal() instanceof String) {
            throw new HethongLoiException("Bạn chưa đăng nhập hoặc token không hợp lệ");
        }
        return (NguoiDung) authentication.getPrincipal();
    }

    public NguoiDung layNguoiDungHienTaiNeuCo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal() instanceof String) {
            return null;
        }
        return (NguoiDung) authentication.getPrincipal();
    }

    public Long layMaNguoiDungHienTai() {
        return layNguoiDungHienTai().getMaNguoiDung();
    }
}
