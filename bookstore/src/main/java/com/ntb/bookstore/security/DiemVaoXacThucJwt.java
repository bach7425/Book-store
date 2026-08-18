package com.ntb.bookstore.security;

import java.io.IOException;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.ntb.bookstore.dto.LoiApi;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

//Trả 401 khi chưa đăng nhập hoặc token ko hợp lệ
public class DiemVaoXacThucJwt implements AuthenticationEntryPoint {
    private static final Logger logger = LoggerFactory.getLogger(DiemVaoXacThucJwt.class);
    
    private final ObjectMapper objectMapper;
    public DiemVaoXacThucJwt(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
         AuthenticationException authException) throws IOException, ServletException {
        logger.debug("Xác thực thất bại: {}",request.getRequestURI(), authException.getMessage());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding("UTF-8");
        LoiApi loiApi = new LoiApi("Unauthorized",
         "Token không hợp lệ hoặc đã hết hạn, Vui lòng đăng nhập lại",
          null,
          request.getRequestURI(),
          Instant.now()
        );

        objectMapper.writeValue(response.getOutputStream(), loiApi);
    }
}
