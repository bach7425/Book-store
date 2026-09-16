package com.ntb.bookstore.CauHinh;

import org.springframework.context.annotation.Configuration;

import com.ntb.bookstore.service.AI_tool.RagService;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.ai.vectorstore.VectorStore;

@Configuration
@RequiredArgsConstructor
public class CauHinhNapDuLieuRag {
    private final RagService ragService;

    @Bean
    @ConditionalOnBean(VectorStore.class)
    CommandLineRunner nap() {
        return args -> ragService.napDuLieuRag();
    }
}
