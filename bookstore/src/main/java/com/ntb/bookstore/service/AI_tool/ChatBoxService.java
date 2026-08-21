package com.ntb.bookstore.service.AI_tool;

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
   private final ToolSearchWeb toolSearchWeb;

   public ChatBoxService(VectorStore vectorStore, ChatClient.Builder chatClientBuilder, ToolSearchWeb toolSearchWeb) {

      this.vectorStore = vectorStore;
      this.chatClient = chatClientBuilder.build();
      this.toolSearchWeb = toolSearchWeb;
   }

   public String hoi(String cauHoi, Boolean isSearchWeb) {
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
      String noiDungWeb = isSearchWeb ? toolSearchWeb.timKiemtrenWeb(cauHoi) : "Không tìm kiếm trên web.";
      return chatClient.prompt()
            .system(
                  """
                        Bạn là trợ lý AI tư vấn sách.
                        Quy tắc:

                        - Nếu câu hỏi có thể trả lời từ dữ liệu nhà sách, hãy ưu tiên dữ liệu nhà sách.
                        - Nếu có dữ liệu web, chỉ sử dụng thông tin thực sự có trong dữ liệu web được cung cấp.
                        - Không tự bịa, suy đoán hoặc thêm thông tin không có trong dữ liệu được cung cấp.
                        - Trả lời ngắn gọn, trực tiếp vào câu hỏi.
                        - Với câu hỏi đơn giản, chỉ trả lời tối đa 10 câu.
                        - Không tạo bảng nếu không cần thiết.
                        - Không giải thích thêm những thông tin người dùng không hỏi.
                        - Khi sử dụng dữ liệu web, đặt URL của nguồn đã sử dụng ở cuối câu trả lời.
                        - Dữ liệu nhà sách có sách liên quan đến câu trả lời, có thể đề xuất các sách đó ở cuối câu trả lời.

                        Dữ liệu nhà sách:
                           %s
                        Dữ liệu web:
                           %s
                           """
                        .formatted(noiDung, noiDungWeb))

            .user(cauHoi)

            .call()

            .content();
   }
}
