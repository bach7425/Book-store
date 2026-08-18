package com.ntb.bookstore.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ntb.bookstore.dto.NguoiDung.DiaChiResponse;
import com.ntb.bookstore.dto.NguoiDung.NguoiDungProfileResponse;
import com.ntb.bookstore.entity.DiaChi;
import com.ntb.bookstore.entity.NguoiDung;
import com.ntb.bookstore.exception.HethongLoiException;
import com.ntb.bookstore.exception.KhongCoDuLieuException;
import com.ntb.bookstore.repository.DiaChiRepository;
import com.ntb.bookstore.repository.NguoiDungRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class NguoiDungService {
    private final NguoiDungRepository nguoiDungRepository;
    private final DiaChiRepository diaChiRepository;
    private final PasswordEncoder passwordEncoder;
    private final UploadService uploadService;

    public NguoiDungProfileResponse layThongTinProfile(Long maNguoiDung) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(maNguoiDung)
                .orElseThrow(() -> new KhongCoDuLieuException("Không tìm thấy người dùng", maNguoiDung));
        return NguoiDungProfileResponse.builder()
                .maNguoiDung(nguoiDung.getMaNguoiDung())
                .hoVaTen(nguoiDung.getHoVaTen())
                .tenDangNhap(nguoiDung.getTenDangNhap())
                .email(nguoiDung.getEmail())
                .soDienThoai(nguoiDung.getSoDienThoai())
                .anhDaiDien(nguoiDung.getAnhDaiDien())
                .vaiTro(nguoiDung.getVaiTro().name())
                .build();
    }

    public NguoiDungProfileResponse capNhatProfile(Long maNguoiDung, String hoVaTen, String soDienThoai) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(maNguoiDung)
                .orElseThrow(() -> new KhongCoDuLieuException("Không tìm thấy người dùng", maNguoiDung));
        if (hoVaTen != null && !hoVaTen.isBlank()) {
            nguoiDung.setHoVaTen(hoVaTen);
        }
        if (soDienThoai != null && !soDienThoai.isBlank()) {
            nguoiDung.setSoDienThoai(soDienThoai);
        }
        nguoiDung = nguoiDungRepository.save(nguoiDung);
        return layThongTinProfile(nguoiDung.getMaNguoiDung());
    }

    public void doiMatKhau(Long maNguoiDung, String matKhauCu, String matKhauMoi) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(maNguoiDung)
                .orElseThrow(() -> new KhongCoDuLieuException("Không tìm thấy người dùng", maNguoiDung));
        if (!passwordEncoder.matches(matKhauCu, nguoiDung.getMatKhauBam())) {
            throw new HethongLoiException("Mật khẩu cũ không đúng");
        }
        if (matKhauCu.equals(matKhauMoi)) {
            throw new HethongLoiException("Mật khẩu mới không được trùng với mật khẩu cũ");
        }
        nguoiDung.setMatKhauBam(passwordEncoder.encode(matKhauMoi));
        nguoiDungRepository.save(nguoiDung);
    }

    public NguoiDungProfileResponse capNhatAnhDaiDien(Long maNguoiDung, MultipartFile file) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(maNguoiDung)
                .orElseThrow(() -> new KhongCoDuLieuException("Không tìm thấy người dùng", maNguoiDung));
        String duongDanAnh = uploadService.luuAnh(file, "nguoi-dung");
        nguoiDung.setAnhDaiDien(duongDanAnh);
        nguoiDung = nguoiDungRepository.save(nguoiDung);
        return layThongTinProfile(nguoiDung.getMaNguoiDung());
    }

    public List<DiaChiResponse> layDanhSachDiaChi(Long maNguoiDung) {
        List<DiaChi> diaChiList = diaChiRepository.findByNguoiDungMaNguoiDung(maNguoiDung);
        if (diaChiList.isEmpty()) {
            throw new KhongCoDuLieuException("Không tìm thấy địa chỉ nào ");
        }
        return diaChiList.stream()
                .map(this::toDiaChiResponse)
                .collect(Collectors.toList());
    }

    public DiaChiResponse themDiaChi(Long maNguoiDung, String nguoiNhan, String soDienThoai, String diaChiChiTiet,
            Boolean macDinh) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(maNguoiDung)
                .orElseThrow(() -> new KhongCoDuLieuException("Không tìm thấy người dùng", maNguoiDung));
        List<DiaChi> diaChiList = diaChiRepository.findByNguoiDungMaNguoiDung(maNguoiDung);
        if (diaChiList.size() >= 3) {
            throw new HethongLoiException("Bạn chỉ có thể thêm tối đa 3 địa chỉ");
        }
        if (Boolean.TRUE.equals(macDinh)) {
            diaChiList.forEach(item -> item.setMacDinh(false));
            diaChiRepository.saveAll(diaChiList);
        }
        DiaChi diaChi = DiaChi.builder()
                .nguoiDung(nguoiDung)
                .nguoiNhan(nguoiNhan)
                .soDienThoai(soDienThoai)
                .diaChiChiTiet(diaChiChiTiet)
                .macDinh(Boolean.TRUE.equals(macDinh))
                .build();

        DiaChi saved = diaChiRepository.save(diaChi);
        return toDiaChiResponse(saved);
    }

    public DiaChiResponse capNhatDiaChi(Long maNguoiDung, Long maDiaChi, String nguoiNhan, String soDienThoai,
            String diaChiChiTiet, Boolean macDinh) {
        DiaChi diaChi = diaChiRepository.findById(maDiaChi)
                .orElseThrow(() -> new KhongCoDuLieuException("Không tìm thấy địa chỉ", maDiaChi));
        List<DiaChi> diaChiList = diaChiRepository.findByNguoiDungMaNguoiDung(maNguoiDung);

        if (!diaChi.getNguoiDung().getMaNguoiDung().equals(maNguoiDung)) {
            throw new HethongLoiException("Bạn không có quyền cập nhật địa chỉ này");
        }
        diaChi.setNguoiNhan(nguoiNhan);
        diaChi.setSoDienThoai(soDienThoai);
        diaChi.setDiaChiChiTiet(diaChiChiTiet);

        if (Boolean.TRUE.equals(macDinh)) {
            diaChiList.forEach(item -> item.setMacDinh(false));
            diaChiRepository.saveAll(diaChiList);
            diaChi.setMacDinh(Boolean.TRUE.equals(macDinh));
        }
        diaChi = diaChiRepository.save(diaChi);
        return toDiaChiResponse(diaChi);
    }

    private DiaChiResponse toDiaChiResponse(DiaChi diaChi) {
        return DiaChiResponse.builder()
                .maDiaChi(diaChi.getMaDiaChi())
                .nguoiNhan(diaChi.getNguoiNhan())
                .soDienThoai(diaChi.getSoDienThoai())
                .diaChiChiTiet(diaChi.getDiaChiChiTiet())
                .macDinh(diaChi.getMacDinh())
                .build();
    }
}
