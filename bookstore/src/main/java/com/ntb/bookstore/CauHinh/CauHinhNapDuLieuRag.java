package com.ntb.bookstore.CauHinh;

import org.springframework.context.annotation.Configuration;
import lombok.RequiredArgsConstructor;
import com.ntb.bookstore.service.RagService;
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
