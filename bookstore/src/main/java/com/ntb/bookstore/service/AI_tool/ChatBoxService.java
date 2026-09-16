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
      List<Document> danhSachTaiLieuLienQuan = vectorStore.similaritySearch(
            SearchRequest.builder()
                  .query(cauHoi)
                  .topK(5)
                  .build());
      danhSachTaiLieuLienQuan.forEach(document -> {
         System.out.println("==============");
         System.out.println(document.getText());
         System.out.println(document.getMetadata());
      });
      String noiDung = danhSachTaiLieuLienQuan.stream()
            .map(Document::getText)
            .collect(Collectors.joining("\n\n"));
      String noiDungWeb = isSearchWeb ? toolSearchWeb.timKiemtrenWeb(cauHoi) : "Không tìm kiếm trên web.";
      return chatClient.prompt()
            .system(
                  """
                           Bạn là trợ lý AI tư vấn sách.

                           Quy tắc:

                        - Chỉ được trả lời bằng thông tin có trong Dữ liệu nhà sách hoặc Dữ liệu web được cung cấp.
                        - Nếu câu hỏi có thể trả lời từ Dữ liệu nhà sách, chỉ dùng Dữ liệu nhà sách.
                        - Chỉ dùng Dữ liệu web khi người dùng bật tìm kiếm web và Dữ liệu web có thông tin phù hợp.
                        - Không dùng kiến thức nền của mô hình để thêm tình tiết, nhân vật, bối cảnh hoặc nhận xét không có trong dữ liệu được cung cấp.
                        - Nếu dữ liệu được cung cấp không đủ để trả lời, hãy nói: "Hiện hệ thống chưa có thông tin này."
                        - Trả lời ngắn gọn, trực tiếp vào câu hỏi, tối đa 10 câu.
                        - Không tạo bảng nếu không cần thiết.
                        - Không giải thích thêm những thông tin người dùng không hỏi.
                        - Khi sử dụng dữ liệu web, đặt URL của nguồn đã sử dụng ở cuối câu trả lời.
                        - Chỉ đề xuất sách ở cuối nếu người dùng hỏi gợi ý/tư vấn/tìm sách.

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
