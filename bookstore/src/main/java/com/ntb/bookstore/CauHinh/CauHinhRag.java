package com.ntb.bookstore.CauHinh;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Configuration
public class CauHinhRag {
    @Bean
    @ConditionalOnProperty(prefix = "spring.ai.model", name = "embedding", havingValue = "google-genai")
    public VectorStore vectorStore(EmbeddingModel embeddingModel) {
        System.out.println("RAG: Đã tạo VectorStore bằng EmbeddingModel " + embeddingModel.getClass().getSimpleName());
        return SimpleVectorStore.builder(embeddingModel).build();
    }
}
