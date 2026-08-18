export interface PhanHoiApi<T> {
  thanhCong: boolean;
  thongBao: string;
  thoiGian: string;
  duLieu: T;
}

export interface PhanTrang<T> {
  duLieu: T[];
  trang: number;
  kichThuocTrang: number;
  tongSoPhanTu: number;
  tongSoTrang: number;
  trangTruoc: string | null;
  tiepTheo: string | null;
}

export interface TacGia {
  maTacGia: number;
  ten: string;
  tieuSu?: string;
  anhDaiDien?: string;
}

export interface TheLoai {
  maTheLoai: number;
  ten: string;
  moTa?: string;
}

export interface Sach {
  maSach: number;
  tenSach: string;
  moTa?: string;
  gia: number;
  anhBia?: string;
  nhaXuatBan?: string;
  ngayXuatBan?: string;
  tacGia?: TacGia;
  theLoais?: TheLoai[];
  soLuongTon?: number;
  diemDanhGiaTrungBinh?: number;
  soLuongDanhGia?: number;
}

export interface NguoiDung {
  maNguoiDung: number;
  hoVaTen: string;
  tenDangNhap: string;
  email: string;
  soDienThoai?: string;
  anhDaiDien?: string;
  vaiTro: string;
  soDonHang?: number;
  tongChiTieu?: number;
}

export interface XacThucResponse {
  maTruyCap: string;
  maLamMoi?: string;
  loaiMa: string;
  nguoiDung: NguoiDung;
}

export interface LamMoiTokenResponse {
  maTruyCap: string;
  loaiMa: string;
}

export interface DiaChi {
  maDiaChi: number;
  nguoiNhan: string;
  soDienThoai: string;
  diaChiChiTiet: string;
  macDinh: boolean;
}

export interface ChiTietGioHang {
  maChiTietGioHang: number;
  maSach: number;
  tenSach: string;
  anhBia?: string;
  soLuong: number;
  donGia: number;
}

export interface GioHang {
  maGioHang: number;
  tongSoLuong: number;
  tongTien: number;
  thongBao?: string[];
  items: PhanTrang<ChiTietGioHang>;
}

export interface ChiTietDonHang {
  maSach: number;
  tenSach: string;
  soLuong: number;
  donGia: number;
  thanhTien: number;
}

export interface DonHang {
  maDonHang: number;
  maNguoiDung?: number;
  tenKhachHang?: string;
  emailKhachHang?: string;
  soDienThoaiKhachHang?: string;
  nguoiNhan?: string;
  soDienThoaiNhan?: string;
  diaChiGiaoHang?: string;
  trangThai: string;
  tongTien: number;
  phiVanChuyen: number;
  soTienGiam: number;
  tongTienThanhToan: number;
  maGiamGia?: string;
  phuongThucThanhToan?: string;
  trangThaiThanhToan?: string;
  soTienThanhToan?: number;
  ngayTao?: string;
  thoiGianThanhToan?: string;
  sanPhams?: ChiTietDonHang[];
  items?: ChiTietDonHang[];
}

export interface DanhGia {
  maDanhGia: number;
  maNguoiDung?: number;
  maSach?: number;
  tenSach?: string;
  tenNguoiDung?: string;
  anhDaiDienNguoiDung?: string;
  soSao: number;
  noiDung: string;
  trangThai?: string;
  phanHoi?: string;
  ngayTao?: string;
}

export interface BaoCaoDoanhThu {
  tongDoanhThu: number;
  soDonDaThanhToan: number;
  tuNgay?: string;
  denNgay?: string;
}

export interface SachBanChay {
  maSach: number;
  tenSach: string;
  anhBia?: string;
  tacGia?: string;
  soLuongBan: number;
  doanhThu: number;
}

export interface MaGiamGia {
  maGiamGia?: number;
  maCode?: string;
  code?: string;
  tenMa?: string;
  loaiGiam?: string;
  loaiGiamGia?: string;
  giaTri?: number;
  giaTriGiam?: number;
  giamToiDa?: number;
  donToiThieu?: number;
  donHangToiThieu?: number;
  soLuong?: number;
  soLuongDaDung?: number;
  trangThai?: string;
  ngayBatDau?: string;
  ngayKetThuc?: string;
  ngayTao?: string;
}

export interface KiemTraMaGiamGiaRequest {
  maGiamGia: string;
  tongTien: number;
}

export interface KiemTraMaGiamGiaResponse {
  maGiamGia: string;
  loaiGiam: string;
  giaTri: number;
  soTienGiam: number;
  phiVanChuyen: number;
  tongTien: number;
  tongTienThanhToan: number;
  thongBao: string;
}

export interface ThongBao {
  maThongBao: number;
  tieuDe: string;
  noiDung: string;
  loai: 'DON_HANG' | 'DANH_GIA' | 'MA_GIAM_GIA' | 'QUAN_TRI' | string;
  daDoc: boolean;
  ngayTao: string;
  duongDan?: string;
}

export interface GuiThongBaoRequest {
  maNguoiDung?: number;
  guiTatCa: boolean;
  tieuDe: string;
  noiDung: string;
  loai?: string;
  duongDan?: string;
}
