package com.ntb.bookstore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import com.ntb.bookstore.CauHinh.CauHinhUpload;
import com.ntb.bookstore.exception.HethongLoiException;

class UploadServiceTest {

    private Path tempDir;
    private UploadService service;

    @BeforeEach
    void setUp() throws Exception {
        tempDir = Paths.get("target", "test-uploads", UUID.randomUUID().toString())
                .toAbsolutePath()
                .normalize();
        Files.createDirectories(tempDir);
        CauHinhUpload cauHinhUpload = new CauHinhUpload();
        cauHinhUpload.setDir(tempDir.toString());
        cauHinhUpload.setPublicPath("/uploads");
        service = new UploadService(cauHinhUpload);
    }

    @Test
    void luuAnh_fileAnhHopLe_thiLuuFileVaTraPublicPath() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "cover.png",
                "image/png",
                new byte[] { 1, 2, 3 });

        String path = service.luuAnh(file, "sach");

        assertThat(path).isEqualTo("/uploads/sach/cover.png");
        assertThat(Files.exists(tempDir.resolve("sach").resolve("cover.png"))).isTrue();
    }

    @Test
    void luuAnh_fileRong_thiNemLoi() {
        MockMultipartFile file = new MockMultipartFile("file", "empty.png", "image/png", new byte[0]);

        assertThatThrownBy(() -> service.luuAnh(file, "sach"))
                .isInstanceOf(HethongLoiException.class)
                .hasMessageContaining("File ảnh không được để trống");
    }

    @Test
    void luuAnh_contentTypeKhongPhaiAnh_thiNemLoi() {
        MockMultipartFile file = new MockMultipartFile("file", "note.txt", "text/plain", "hello".getBytes());

        assertThatThrownBy(() -> service.luuAnh(file, "sach"))
                .isInstanceOf(HethongLoiException.class)
                .hasMessageContaining("File upload phải là ảnh");
    }

    @Test
    void luuAnh_duoiFileKhongHoTro_thiNemLoi() {
        MockMultipartFile file = new MockMultipartFile("file", "cover.gif", "image/png", new byte[] { 1 });

        assertThatThrownBy(() -> service.luuAnh(file, "sach"))
                .isInstanceOf(HethongLoiException.class)
                .hasMessageContaining("Chỉ hỗ trợ ảnh");
    }

    @Test
    void luuAnh_thuMucTraversal_thiNemLoi() {
        MockMultipartFile file = new MockMultipartFile("file", "cover.png", "image/png", new byte[] { 1 });

        assertThatThrownBy(() -> service.luuAnh(file, "../ngoai"))
                .isInstanceOf(HethongLoiException.class)
                .hasMessageContaining("Thư mục upload không hợp lệ");
    }
}
