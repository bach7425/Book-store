package com.ntb.bookstore.CauHinh;

import org.springframework.context.annotation.Configuration;

import com.ntb.bookstore.service.AI_tool.RagService;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

@Configuration
@RequiredArgsConstructor
public class CauHinhNapDuLieuRag {
    private final RagService ragService;

    @Bean
    CommandLineRunner nap() {
        return args -> ragService.napDuLieuRag();
    }
}
