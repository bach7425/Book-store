package com.ntb.bookstore.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.ntb.bookstore.entity.ChiTietDonHang;
import com.ntb.bookstore.entity.ChiTietGioHang;
import com.ntb.bookstore.entity.DanhGia;
import com.ntb.bookstore.entity.DiaChi;
import com.ntb.bookstore.entity.DonHang;
import com.ntb.bookstore.entity.GioHang;
import com.ntb.bookstore.entity.MaGiamGia;
import com.ntb.bookstore.entity.NguoiDung;
import com.ntb.bookstore.entity.Sach;
import com.ntb.bookstore.entity.TacGia;
import com.ntb.bookstore.entity.ThanhToan;
import com.ntb.bookstore.entity.TheLoai;
import com.ntb.bookstore.entity.ThongBao;
import com.ntb.bookstore.entity.TonKho;
import com.ntb.bookstore.entity.enums.LoaiGiamGia;
import com.ntb.bookstore.entity.enums.LoaiThongBao;
import com.ntb.bookstore.entity.enums.PhuongThucThanhToan;
import com.ntb.bookstore.entity.enums.TrangThaiDanhGia;
import com.ntb.bookstore.entity.enums.TrangThaiDonHang;
import com.ntb.bookstore.entity.enums.TrangThaiMaGiamGia;
import com.ntb.bookstore.entity.enums.TrangThaiThanhToan;
import com.ntb.bookstore.entity.enums.VaiTro;
import com.ntb.bookstore.repository.ChiTietDonHangRepository;
import com.ntb.bookstore.repository.ChiTietGioHangRepository;
import com.ntb.bookstore.repository.DanhGiaRepository;
import com.ntb.bookstore.repository.DiaChiRepository;
import com.ntb.bookstore.repository.DonHangRepository;
import com.ntb.bookstore.repository.GioHangRepository;
import com.ntb.bookstore.repository.MaGiamGiaRepository;
import com.ntb.bookstore.repository.NguoiDungRepository;
import com.ntb.bookstore.repository.SachRepository;
import com.ntb.bookstore.repository.TacGiaRepository;
import com.ntb.bookstore.repository.ThanhToanRepository;
import com.ntb.bookstore.repository.TheLoaiRepository;
import com.ntb.bookstore.repository.ThongBaoRepository;
import com.ntb.bookstore.repository.TonKhoRepository;
import com.ntb.bookstore.security.JwtService;
import com.ntb.bookstore.service.AI_tool.ChatBoxService;
import com.ntb.bookstore.service.AI_tool.RagService;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
abstract class BaseIntegrationTest {
    private static final AtomicInteger SEQUENCE = new AtomicInteger();

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected JwtService jwtService;

    @Autowired
    protected NguoiDungRepository nguoiDungRepository;

    @Autowired
    protected TacGiaRepository tacGiaRepository;

    @Autowired
    protected TheLoaiRepository theLoaiRepository;

    @Autowired
    protected SachRepository sachRepository;

    @Autowired
    protected TonKhoRepository tonKhoRepository;

    @Autowired
    protected GioHangRepository gioHangRepository;

    @Autowired
    protected ChiTietGioHangRepository chiTietGioHangRepository;

    @Autowired
    protected DiaChiRepository diaChiRepository;

    @Autowired
    protected DonHangRepository donHangRepository;

    @Autowired
    protected ChiTietDonHangRepository chiTietDonHangRepository;

    @Autowired
    protected ThanhToanRepository thanhToanRepository;

    @Autowired
    protected MaGiamGiaRepository maGiamGiaRepository;

    @Autowired
    protected DanhGiaRepository danhGiaRepository;

    @Autowired
    protected ThongBaoRepository thongBaoRepository;

    @MockitoBean
    protected RagService ragService;

    @MockitoBean
    protected ChatBoxService chatBoxService;

    @MockitoBean(name = "googleGenAiTextEmbedding")
    protected EmbeddingModel embeddingModel;

    protected int testId;

    @BeforeEach
    void setUpBase() {
        testId = SEQUENCE.incrementAndGet();
    }

    protected String json(Object value) throws Exception {
        return objectMapper.writeValueAsString(value);
    }

    protected String bearer(NguoiDung nguoiDung) {
        return "Bearer " + jwtService.taoMaTruyCap(
                nguoiDung.getTenDangNhap(),
                nguoiDung.getMaNguoiDung(),
                nguoiDung.getVaiTro().name());
    }

    protected NguoiDung taoNguoiDung() {
        return taoNguoiDung(VaiTro.NGUOI_DUNG, "user");
    }

    protected NguoiDung taoAdmin() {
        return taoNguoiDung(VaiTro.QUAN_TRI_VIEN, "admin");
    }

    protected NguoiDung taoNguoiDung(VaiTro vaiTro, String prefix) {
        String suffix = prefix + testId + "_" + SEQUENCE.incrementAndGet();
        NguoiDung nguoiDung = NguoiDung.builder()
                .hoVaTen("Người dùng " + suffix)
                .tenDangNhap(suffix)
                .email(suffix + "@example.com")
                .soDienThoai("090" + String.format("%07d", SEQUENCE.incrementAndGet()))
                .matKhauBam(passwordEncoder.encode("pass123"))
                .vaiTro(vaiTro)
                .build();
        return nguoiDungRepository.saveAndFlush(nguoiDung);
    }

    protected TacGia taoTacGia(String ten) {
        return tacGiaRepository.saveAndFlush(TacGia.builder()
                .ten(ten + " " + testId + " " + SEQUENCE.incrementAndGet())
                .tieuSu("Tiểu sử")
                .build());
    }

    protected TheLoai taoTheLoai(String ten) {
        return theLoaiRepository.saveAndFlush(TheLoai.builder()
                .ten(ten + " " + testId + " " + SEQUENCE.incrementAndGet())
                .moTa("Mô tả thể loại")
                .build());
    }

    protected Sach taoSach(String ten, BigDecimal gia, int ton) {
        TacGia tacGia = taoTacGia("Tác giả");
        TheLoai theLoai = taoTheLoai("Thể loại");
        Sach sach = Sach.builder()
                .tenSach(ten + " " + testId + " " + SEQUENCE.incrementAndGet())
                .moTa("Mô tả sách kiểm thử")
                .gia(gia)
                .anhBia("/uploads/sach/test.jpg")
                .nhaXuatBan("NXB Test")
                .tacGia(tacGia)
                .theLoais(List.of(theLoai))
                .build();
        sach = sachRepository.saveAndFlush(sach);
        TonKho tonKho = tonKhoRepository.saveAndFlush(TonKho.builder()
                .sach(sach)
                .soLuong(ton)
                .build());
        sach.setTonKho(tonKho);
        return sachRepository.saveAndFlush(sach);
    }

    protected DiaChi taoDiaChi(NguoiDung nguoiDung) {
        return diaChiRepository.saveAndFlush(DiaChi.builder()
                .nguoiDung(nguoiDung)
                .nguoiNhan(nguoiDung.getHoVaTen())
                .soDienThoai("0912345678")
                .diaChiChiTiet("123 Đường Test, Quận 1")
                .macDinh(true)
                .build());
    }

    protected GioHang taoGioHang(NguoiDung nguoiDung) {
        return gioHangRepository.findByNguoiDungMaNguoiDung(nguoiDung.getMaNguoiDung())
                .orElseGet(() -> gioHangRepository.saveAndFlush(GioHang.builder()
                        .nguoiDung(nguoiDung)
                        .build()));
    }

    protected ChiTietGioHang themVaoGioHang(NguoiDung nguoiDung, Sach sach, int soLuong) {
        GioHang gioHang = taoGioHang(nguoiDung);
        ChiTietGioHang item = chiTietGioHangRepository.saveAndFlush(ChiTietGioHang.builder()
                .gioHang(gioHang)
                .sach(sach)
                .soLuong(soLuong)
                .build());
        gioHang.getChiTietGioHangs().add(item);
        return item;
    }

    protected MaGiamGia taoMaGiamGia(String code, LoaiGiamGia loai, BigDecimal giaTri) {
        return maGiamGiaRepository.saveAndFlush(MaGiamGia.builder()
                .maCode(code + testId + SEQUENCE.incrementAndGet())
                .loaiGiam(loai)
                .giaTri(giaTri)
                .giamToiDa(BigDecimal.valueOf(50_000))
                .donToiThieu(BigDecimal.ZERO)
                .soLuong(10)
                .soLuongDaDung(0)
                .ngayBatDau(LocalDateTime.now().minusDays(1))
                .ngayKetThuc(LocalDateTime.now().plusDays(7))
                .trangThai(TrangThaiMaGiamGia.HOAT_DONG)
                .build());
    }

    protected DonHang taoDonHangDaMua(NguoiDung nguoiDung, Sach sach) {
        DiaChi diaChi = taoDiaChi(nguoiDung);
        DonHang donHang = donHangRepository.saveAndFlush(DonHang.builder()
                .nguoiDung(nguoiDung)
                .diaChi(diaChi)
                .tongTien(sach.getGia())
                .phiVanChuyen(BigDecimal.valueOf(30_000))
                .soTienGiam(BigDecimal.ZERO)
                .tongTienThanhToan(sach.getGia().add(BigDecimal.valueOf(30_000)))
                .trangThai(TrangThaiDonHang.DA_GIAO)
                .build());
        ChiTietDonHang chiTiet = chiTietDonHangRepository.saveAndFlush(ChiTietDonHang.builder()
                .donHang(donHang)
                .sach(sach)
                .soLuong(1)
                .donGia(sach.getGia())
                .thanhTien(sach.getGia())
                .build());
        donHang.getChiTietDonHangs().add(chiTiet);
        ThanhToan thanhToan = thanhToanRepository.saveAndFlush(ThanhToan.builder()
                .donHang(donHang)
                .phuongThuc(PhuongThucThanhToan.TIEN_MAT)
                .trangThai(TrangThaiThanhToan.DA_THANH_TOAN)
                .soTien(donHang.getTongTienThanhToan())
                .ngayTao(LocalDateTime.now())
                .thoiGianThanhToan(LocalDateTime.now())
                .build());
        donHang.setThanhToan(thanhToan);
        return donHangRepository.saveAndFlush(donHang);
    }

    protected DanhGia taoDanhGia(NguoiDung nguoiDung, Sach sach, TrangThaiDanhGia trangThai) {
        return danhGiaRepository.saveAndFlush(DanhGia.builder()
                .nguoiDung(nguoiDung)
                .sach(sach)
                .soSao(5)
                .noiDung("Nội dung đánh giá")
                .trangThai(trangThai)
                .build());
    }

    protected ThongBao taoThongBao(NguoiDung nguoiDung) {
        return thongBaoRepository.saveAndFlush(ThongBao.builder()
                .nguoiDung(nguoiDung)
                .tieuDe("Thông báo test")
                .noiDung("Nội dung test")
                .loai(LoaiThongBao.QUAN_TRI)
                .daDoc(false)
                .duongDan("/test")
                .build());
    }

    protected JsonNode dangNhap(String tenDangNhap, String matKhau) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/xac-thuc/dang-nhap")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of("tenDangNhap", tenDangNhap, "matKhau", matKhau))))
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }
}
