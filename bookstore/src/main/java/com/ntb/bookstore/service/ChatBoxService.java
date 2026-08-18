package com.ntb.bookstore.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.ai.vectorstore.SearchRequest;

@Service
public class ChatBoxService {
        private final VectorStore vectorStore;
        private final ChatClient chatClient;

        public ChatBoxService(VectorStore vectorStore, ChatClient.Builder chatClientBuilder) {

                this.vectorStore = vectorStore;
                this.chatClient = chatClientBuilder.build();
        }

        public String hoi(String cauHoi) {
                List<Document> danhSachTaiLieu = vectorStore.similaritySearch(
                                SearchRequest.builder()
                                                .query(cauHoi)
                                                .topK(5)
                                                .build());
                danhSachTaiLieu.forEach(document -> {
                        System.out.println("==============");
                        System.out.println(document.getText());
                        System.out.println(document.getMetadata());
                });
                String noiDung = danhSachTaiLieu.stream()
                                .map(Document::getText)
                                .collect(Collectors.joining("\n\n"));
                return chatClient.prompt()
                                .system("""
                                                Bạn là trợ lý tư vấn sách.

                                                Ưu tiên sử dụng DỮ LIỆU NỘI BỘ được cung cấp.
                                                Nếu dữ liệu nội bộ không đủ để trả lời câu hỏi,
                                                hãy sử dụng Google Search để tìm thông tin trên Internet.
                                                Nếu sử dụng thông tin từ Internet, hãy nói rõ
                                                rằng thông tin đó đến từ nguồn bên ngoài.
                                                Có thể dựa trên những câu hỏi trước đó để đưa ra câu trả lời chính xác hơn.
                                                Trả lời ngắn gọn, chính xác.
                                                                                """)
                                .user("""
                                                DỮ LIỆU :

                                                %s

                                                CÂU HỎI:

                                                %s
                                                """.formatted(noiDung, cauHoi))
                                .call()
                                .content();
        }
}
