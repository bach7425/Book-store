package com.ntb.bookstore.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.ntb.bookstore.CauHinh.CauHinhJwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
    private final CauHinhJwt cauHinhJwt;

    public JwtService(CauHinhJwt cauHinhJwt) {
        this.cauHinhJwt = cauHinhJwt;
    }
    private SecretKey getKhoaDangNhap() {
        return Keys.hmacShaKeyFor(cauHinhJwt.getBiMat().getBytes(StandardCharsets.UTF_8));
    }
    public String taoMaTruyCap(String tenNguoiDung, Long maNguoiDung, String vaiTro) {
        return xayDungToken(tenNguoiDung, maNguoiDung, vaiTro, cauHinhJwt.getThoiGianHieuLuc());
    }
    public String taoMaLamMoi(String tenNguoiDung, Long maNguoiDung, String vaiTro) {
        return xayDungToken(tenNguoiDung, maNguoiDung, vaiTro, cauHinhJwt.getThoiGianLamMoi());
    }
    private String xayDungToken(String tenNguoiDung,Long maNguoiDung,String vaiTro,Long thoiGianHieuLuc) {
        return Jwts.builder()
                .subject(tenNguoiDung)
                .claim("maNguoiDung", maNguoiDung)
                .claim("vaiTro", vaiTro)
                .signWith(getKhoaDangNhap())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + thoiGianHieuLuc))
                .compact();
    }
    public String layTenNguoiDungTuToken(String token) {
        return layThongTinTuToken(token, Claims::getSubject);
    }
    public Long layMaNguoiDungTuToken(String token) {
        return layThongTinTuToken(token, claims -> claims.get("maNguoiDung", Long.class));
    }
    public String layVaiTroTuToken(String token) {
        return layThongTinTuToken(token, claims -> claims.get("vaiTro", String.class));
    }
    public Date layThoiGianHetHanTuToken(String token) {
        return layThongTinTuToken(token, Claims::getExpiration);
    }
    private <T> T layThongTinTuToken(String token,Function<Claims, T> claimsResolver) {
        Claims claims = layTatCaThongTinTuToken(token);
        return claimsResolver.apply(claims);
    }
    public Claims layTatCaThongTinTuToken(String token) {
        return Jwts.parser()
                .verifyWith(getKhoaDangNhap())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    public boolean kiemTraToken(String token, UserDetails chiTietNguoiDung) {
        final String tenNguoiDung = layTenNguoiDungTuToken(token);
        return (tenNguoiDung.equals(chiTietNguoiDung.getUsername()) && !kiemTraTokenHetHan(token));
    }
    public boolean kiemTraTokenHetHan(String token) {
        return layThoiGianHetHanTuToken(token).before(new Date());
    }
}
