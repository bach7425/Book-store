package com.ntb.bookstore.CauHinh;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;

@Configuration
public class CauHinhRag {
    @Bean
    @ConditionalOnBean(name = "googleGenAiTextEmbedding")
    public VectorStore vectorStore(@Qualifier("googleGenAiTextEmbedding") EmbeddingModel embeddingModel) {
        return SimpleVectorStore.builder(embeddingModel).build();
    }
}
