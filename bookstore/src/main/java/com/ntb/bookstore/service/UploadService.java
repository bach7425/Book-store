package com.ntb.bookstore.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ntb.bookstore.CauHinh.CauHinhUpload;
import com.ntb.bookstore.exception.HethongLoiException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UploadService {
    private static final Set<String> duoi_file_cho_phep = Set.of("jpg", "jpeg", "png", "webp");
    private static final Set<String> loai_file_cho_phep = Set.of("image/jpeg", "image/png", "image/webp");

    private final CauHinhUpload cauHinhUpload;

    public String luuAnh(MultipartFile file, String thuMuc) {
        kiemTraFile(file);

        String duoiFile = StringUtils.getFilenameExtension(file.getOriginalFilename());
        if (duoiFile == null) {
            throw new HethongLoiException("File ảnh không hợp lệ");
        }
        duoiFile = duoiFile.toLowerCase(Locale.ROOT);
        if (!duoi_file_cho_phep.contains(duoiFile)) {
            throw new HethongLoiException("Chỉ hỗ trợ ảnh jpg, jpeg, png, webp");
        }

        String tenFile = file.getOriginalFilename();
        Path thuMucGoc = Paths.get(cauHinhUpload.getDir()).toAbsolutePath().normalize();
        Path thuMucLuu = thuMucGoc.resolve(thuMuc).normalize();
        if (!thuMucLuu.startsWith(thuMucGoc)) {
            throw new HethongLoiException("Thư mục upload không hợp lệ");
        }

        try {
            Files.createDirectories(thuMucLuu);
            file.transferTo(thuMucLuu.resolve(tenFile));
        } catch (IOException ex) {
            throw new HethongLoiException("Không thể lưu file ảnh");
        }

        String publicPath = cauHinhUpload.getPublicPath();
        if (!publicPath.startsWith("/")) {
            publicPath = "/" + publicPath;
        }
        return publicPath + "/" + thuMuc + "/" + tenFile;
    }

    private void kiemTraFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new HethongLoiException("File ảnh không được để trống");
        }
        String contentType = file.getContentType();
        if (contentType == null || !loai_file_cho_phep.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new HethongLoiException("File upload phải là ảnh jpg, png hoặc webp");
        }
    }
}
