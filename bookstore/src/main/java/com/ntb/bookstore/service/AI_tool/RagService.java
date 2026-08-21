package com.ntb.bookstore.service.AI_tool;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ntb.bookstore.entity.Sach;
import com.ntb.bookstore.entity.TheLoai;
import com.ntb.bookstore.repository.SachRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RagService {
    private final VectorStore vectorStore;
    private final SachRepository sachRepository;

    @Transactional(readOnly = true)
    public void napDuLieuRag() {
        List<Sach> danhSach = sachRepository.findAllForRag();
        List<Document> danhSachTaiLieu = danhSach.stream()
                .map(this::chuyenThanhDocument)
                .collect(Collectors.toList());
        vectorStore.add(danhSachTaiLieu);
    }

    public void themSachVaoVectorStore(Sach sach) {

        Document document = chuyenThanhDocument(sach);

        vectorStore.add(List.of(document));
    }

    private Document chuyenThanhDocument(Sach sach) {
        return new Document("""
                Tên sách: %s
                Tác giả: %s
                Thể loại: %s
                Mô tả: %s
                Giá : %s
                """.formatted(sach.getTenSach(), sach.getTacGia().getTen(),
                sach.getTheLoais().stream().map(TheLoai::getTen).collect(Collectors.joining(", ")),
                sach.getMoTa(), sach.getGia()),
                Map.of(
                        "maSach", sach.getMaSach(),
                        "tenSach", sach.getTenSach(),
                        "tacGia", sach.getTacGia().getTen()));
    }
}
