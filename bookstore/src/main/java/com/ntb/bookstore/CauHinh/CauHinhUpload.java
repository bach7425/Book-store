package com.ntb.bookstore.CauHinh;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.upload")
public class CauHinhUpload {
    private String dir = "uploads";
    private String publicPath = "/uploads";
}
