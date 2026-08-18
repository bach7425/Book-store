package com.ntb.bookstore.CauHinh;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class CauHinhJwt {
    private String biMat = "BiMatDungDeBiMat34567898765434567";
    private Long thoiGianHieuLuc = 900_000L;// 15 phut
    private Long thoiGianLamMoi = 604_800_000L;// 7 ngay
}
