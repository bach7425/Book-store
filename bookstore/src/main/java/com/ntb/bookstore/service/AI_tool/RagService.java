package com.ntb.bookstore.service.AI_tool;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ntb.bookstore.entity.Sach;
import com.ntb.bookstore.entity.TheLoai;
import com.ntb.bookstore.repository.SachRepository;
import com.ntb.bookstore.repository.TonKhoRepository;

@Service
public class RagService {
    private final VectorStore vectorStore;
    private final SachRepository sachRepository;
    private final TonKhoRepository tonKhoRepository;

    public RagService(ObjectProvider<VectorStore> vectorStoreProvider, SachRepository sachRepository,
            TonKhoRepository tonKhoRepository) {
        this.vectorStore = vectorStoreProvider.getIfAvailable();
        this.sachRepository = sachRepository;
        this.tonKhoRepository = tonKhoRepository;
    }

    @Transactional(readOnly = true)
    public void napDuLieuRag() {
        if (vectorStore == null) {
            return;
        }
        List<Sach> danhSach = sachRepository.findAllForRag();
        List<Document> danhSachTaiLieu = danhSach.stream()
                .map(this::chuyenThanhDocument)
                .collect(Collectors.toList());
        vectorStore.add(danhSachTaiLieu);
    }

    public void themSachVaoVectorStore(Sach sach) {
        if (vectorStore == null) {
            return;
        }
        String documentId = taoDocumentId(sach.getMaSach());
        vectorStore.delete(List.of(documentId));

        Document document = chuyenThanhDocument(sach);

        vectorStore.add(List.of(document));
    }

    private String taoDocumentId(Long maSach) {
        return "sach-" + maSach;
    }

    private Document chuyenThanhDocument(Sach sach) {
        Integer soLuongConHang = tonKhoRepository.findBySachMaSach(sach.getMaSach())
                .map(tonKho -> tonKho.getSoLuong())
                .orElse(0);
        return new Document(taoDocumentId(sach.getMaSach()), """
                Tên sách: %s
                Thể loại: %s
                Mô tả: %s
                Giá : %s
                Số lượng còn : %s sách
                Tác giả: %s
                Thông tin tác giả: %s
                """.formatted(sach.getTenSach(),
                sach.getTheLoais().stream().map(TheLoai::getTen).collect(Collectors.joining(", ")),
                sach.getMoTa(), sach.getGia(), soLuongConHang, sach.getTacGia().getTen(), sach.getTacGia().getTieuSu()),
                Map.of(
                        "maSach", sach.getMaSach(),
                        "tenSach", sach.getTenSach(),
                        "theLoai", sach.getTheLoais().stream().map(TheLoai::getTen).collect(Collectors.joining(", ")),
                        "tacGia", sach.getTacGia().getTen(),
                        "soLuongConHang", soLuongConHang,
                        "gia", sach.getGia()));
    }
}
