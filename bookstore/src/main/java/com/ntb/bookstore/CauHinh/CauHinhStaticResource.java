package com.ntb.bookstore.CauHinh;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class CauHinhStaticResource implements WebMvcConfigurer {
    private final CauHinhUpload cauHinhUpload;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadDir = Paths.get(cauHinhUpload.getDir()).toAbsolutePath().normalize();
        String publicPath = cauHinhUpload.getPublicPath();
        if (!publicPath.startsWith("/")) {
            publicPath = "/" + publicPath;
        }
        String resourceLocation = uploadDir.toUri().toString();
        if (!resourceLocation.endsWith("/")) {
            resourceLocation = resourceLocation + "/";
        }
        registry.addResourceHandler(publicPath + "/**")
                .addResourceLocations(resourceLocation);
    }
}
