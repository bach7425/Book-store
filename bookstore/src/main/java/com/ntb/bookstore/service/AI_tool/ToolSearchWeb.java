package com.ntb.bookstore.service.AI_tool;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ToolSearchWeb {
        private RestClient restClient;

        public ToolSearchWeb(RestClient.Builder builder, @Value("${tavily.api-key}") String apiKey) {
                this.restClient = builder.baseUrl("https://api.tavily.com")
                                .defaultHeader("Authorization", "Bearer " + apiKey)
                                .build();
        }

        public String timKiemtrenWeb(String query) {
                Map<String, Object> request = Map.of(
                                "query", query,
                                "search_depth", "basic",
                                "max_results", 5);
                TavilyResponse response = restClient.post()
                                .uri("/search")
                                .body(request)
                                .retrieve()
                                .body(TavilyResponse.class);

                if (response == null || response.results() == null) {
                        return "Không tìm thấy kết quả.";
                }

                StringBuilder ketQua = new StringBuilder();

                for (TavilyResult result : response.results()) {

                        ketQua.append("Tiêu đề: ")
                                        .append(result.title())
                                        .append("\n");

                        ketQua.append("URL: ")
                                        .append(result.url())
                                        .append("\n");

                        ketQua.append("Nội dung: ")
                                        .append(result.content())
                                        .append("\n");
                        ketQua.append("=======================\n");
                }

                return ketQua.toString();
        }

        public record TavilyResponse(
                        List<TavilyResult> results) {
        }

        public record TavilyResult(
                        String title,
                        String url,
                        String content) {
        }
}
