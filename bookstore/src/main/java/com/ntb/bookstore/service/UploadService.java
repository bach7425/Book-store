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
            throw new HethongLoiException("File anh khong hop le");
        }
        duoiFile = duoiFile.toLowerCase(Locale.ROOT);
        if (!duoi_file_cho_phep.contains(duoiFile)) {
            throw new HethongLoiException("Chi ho tro anh jpg, jpeg, png, webp");
        }

        String tenFile = UUID.randomUUID() + "." + duoiFile;
        Path thuMucGoc = Paths.get(cauHinhUpload.getDir()).toAbsolutePath().normalize();
        Path thuMucLuu = thuMucGoc.resolve(thuMuc).normalize();
        if (!thuMucLuu.startsWith(thuMucGoc)) {
            throw new HethongLoiException("Thu muc upload khong hop le");
        }

        try {
            Files.createDirectories(thuMucLuu);
            file.transferTo(thuMucLuu.resolve(tenFile));
        } catch (IOException ex) {
            throw new HethongLoiException("Khong the luu file anh");
        }

        String publicPath = cauHinhUpload.getPublicPath();
        if (!publicPath.startsWith("/")) {
            publicPath = "/" + publicPath;
        }
        return publicPath + "/" + thuMuc + "/" + tenFile;
    }

    private void kiemTraFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new HethongLoiException("File anh khong duoc de trong");
        }
        String contentType = file.getContentType();
        if (contentType == null || !loai_file_cho_phep.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new HethongLoiException("File upload phai la anh jpg, png hoac webp");
        }
    }
}
