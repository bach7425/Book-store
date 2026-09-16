package com.ntb.bookstore.CauHinh;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.google.genai.embedding.GoogleGenAiEmbeddingConnectionDetails;
import org.springframework.ai.google.genai.text.GoogleGenAiTextEmbeddingModel;
import org.springframework.ai.google.genai.text.GoogleGenAiTextEmbeddingOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
public class CauHinhGoogleEmbedding {

        @Bean
        @ConditionalOnProperty(prefix = "spring.ai.model", name = "embedding", havingValue = "google-genai")
        public EmbeddingModel googleGenAiTextEmbeddingModel(
                        @Value("${spring.ai.google.genai.embedding.api-key:}") String apiKey,
                        @Value("${spring.ai.google.genai.embedding.text.model:gemini-embedding-001}") String model) {

                if (!StringUtils.hasText(apiKey)) {
                        throw new IllegalStateException(
                                        "GEMINI_API_KEY là bắt buộc khi AI_EMBEDDING_MODEL=google-genai. "
                                                        + "cài đặt AI_EMBEDDING_MODEL=none để chạy mà không có embedding.");
                }

                GoogleGenAiEmbeddingConnectionDetails connectionDetails = GoogleGenAiEmbeddingConnectionDetails
                                .builder()
                                .apiKey(apiKey)
                                .build();
                GoogleGenAiTextEmbeddingOptions options = GoogleGenAiTextEmbeddingOptions.builder()
                                .model(model)
                                .build();

                return new GoogleGenAiTextEmbeddingModel(connectionDetails, options);
        }
}
