package com.ntb.bookstore.security;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.*;
import jakarta.servlet.http.*;

public class JwtBoLocXacThuc extends OncePerRequestFilter{
    private static final Logger logger = LoggerFactory.getLogger(JwtBoLocXacThuc.class);
    private final JwtService jwtService;
    private final UserDetailsService nguoiDungChiTietDichVu;
    public JwtBoLocXacThuc(JwtService jwtService, UserDetailsService nguoiDungChiTietDichVu) {
        this.jwtService = jwtService;
        this.nguoiDungChiTietDichVu = nguoiDungChiTietDichVu;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,FilterChain filterChain) throws IOException, ServletException {
        try {
            String jwt = layTokenTuYeuCau(request);
            if (jwt != null) {
                String tenNguoiDung = jwtService.layTenNguoiDungTuToken(jwt);
                if (tenNguoiDung != null) {
                    var chiTietNguoiDung = nguoiDungChiTietDichVu.loadUserByUsername(tenNguoiDung);
                    if (jwtService.kiemTraToken(jwt, chiTietNguoiDung)) {
                        UsernamePasswordAuthenticationToken xacThuc = new UsernamePasswordAuthenticationToken(chiTietNguoiDung, null, chiTietNguoiDung.getAuthorities());
                        xacThuc.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(xacThuc);
                    }
                }
            }
        } catch (Exception e) {
            logger.debug("Không thể thiết lập xác thực từ JWT: {}", e.getMessage());
        }
        filterChain.doFilter(request, response);
    }

    private String layTokenTuYeuCau(HttpServletRequest request) {
        String maBearer  = request.getHeader("Authorization");
        if(StringUtils.hasText(maBearer) && maBearer.startsWith("Bearer ")) {
            return maBearer.substring(7);
        }
        return null;
    }
}
