import type { BaoCaoDoanhThu, DanhGia, DonHang, MaGiamGia, NguoiDung, PhanTrang, Sach, SachBanChay, TacGia, TheLoai } from '../types';
import { apiClient, goiApi } from './client';

export const quanTriApi = {
  layBaoCaoDoanhThu: (params?: { tuNgay?: string; denNgay?: string }) =>
    goiApi<BaoCaoDoanhThu>({ url: '/api/quan-tri/bao-cao/doanh-thu', params }),
  laySachBanChay: (params?: { tuNgay?: string; denNgay?: string; page?: number; size?: number }) =>
    goiApi<PhanTrang<SachBanChay>>({ url: '/api/quan-tri/thong-ke/sach-ban-chay', params: { size: 8, ...params } }),
  themSach: (duLieu: Partial<Sach> & { maTacGia?: number; maTheLoai?: number[] }) =>
    goiApi<Sach>({ url: '/api/quan-tri/sach', method: 'POST', data: duLieu }),
  capNhatSach: (maSach: number, duLieu: Partial<Sach> & { maTacGia?: number; maTheLoai?: number[] }) =>
    goiApi<Sach>({ url: `/api/quan-tri/sach/${maSach}`, method: 'PUT', data: duLieu }),
  capNhatTonKho: (maSach: number, soLuong: number) =>
    goiApi<Sach>({ url: `/api/quan-tri/sach/${maSach}/ton-kho`, method: 'PUT', data: { soLuong } }),
  capNhatAnhBia: async (maSach: number, file: File) => {
    const formData = new FormData();
    formData.append('file', file);
    const response = await apiClient.post(`/api/quan-tri/sach/${maSach}/anh-bia`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    return response.data.duLieu as Sach;
  },
  layDonHangQuanTri: (params?: { trangThai?: string; page?: number; size?: number }) =>
    goiApi<PhanTrang<DonHang>>({ url: '/api/quan-tri/don-hang', params: { size: 10, ...params } }),
  capNhatTrangThaiDonHang: (maDonHang: number, trangThai: string) =>
    goiApi<DonHang>({ url: `/api/don-hang/${maDonHang}/trang-thai`, method: 'PUT', data: { trangThai } }),
  layKhachHang: (params?: { tuKhoa?: string; page?: number; size?: number }) =>
    goiApi<PhanTrang<NguoiDung>>({ url: '/api/quan-tri/khach-hang', params: { size: 10, ...params } }),
  layDanhGiaQuanTri: (params?: { trangThai?: string; page?: number; size?: number }) =>
    goiApi<PhanTrang<DanhGia>>({ url: '/api/quan-tri/danh-gia', params: { size: 10, ...params } }),
  duyetDanhGia: (maDanhGia: number) =>
    goiApi<DanhGia>({ url: `/api/quan-tri/danh-gia/${maDanhGia}/duyet`, method: 'PATCH' }),
  tuChoiDanhGia: (maDanhGia: number) =>
    goiApi<DanhGia>({ url: `/api/quan-tri/danh-gia/${maDanhGia}/tu-choi`, method: 'PATCH' }),
  layMaGiamGia: (params?: { trangThai?: string; page?: number; size?: number }) =>
    goiApi<PhanTrang<MaGiamGia>>({ url: '/api/quan-tri/ma-giam-gia', params: { size: 10, ...params } }),
  taoMaGiamGia: (duLieu: Partial<MaGiamGia>) =>
    goiApi<MaGiamGia>({ url: '/api/quan-tri/ma-giam-gia', method: 'POST', data: duLieu }),
  capNhatMaGiamGia: (maGiamGia: number, duLieu: Partial<MaGiamGia>) =>
    goiApi<MaGiamGia>({ url: `/api/quan-tri/ma-giam-gia/${maGiamGia}`, method: 'PUT', data: duLieu }),
  xoaMaGiamGia: (maGiamGia: number) =>
    goiApi<void>({ url: `/api/quan-tri/ma-giam-gia/${maGiamGia}`, method: 'DELETE' }),
  themTacGia: (duLieu: Partial<TacGia>) =>
    goiApi<TacGia>({ url: '/api/quan-tri/tac-gia', method: 'POST', data: duLieu }),
  capNhatTacGia: (maTacGia: number, duLieu: Partial<TacGia>) =>
    goiApi<TacGia>({ url: `/api/quan-tri/tac-gia/${maTacGia}`, method: 'PUT', data: duLieu }),
  capNhatAnhDaiDienTacGia: async (maTacGia: number, file: File) => {
    const formData = new FormData();
    formData.append('file', file);
    const response = await apiClient.post(`/api/quan-tri/tac-gia/${maTacGia}/anh-dai-dien`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    return response.data.duLieu as TacGia;
  },
  themTheLoai: (duLieu: Partial<TheLoai>) =>
    goiApi<TheLoai>({ url: '/api/quan-tri/the-loai', method: 'POST', data: duLieu }),
  capNhatTheLoai: (maTheLoai: number, duLieu: Partial<TheLoai>) =>
    goiApi<TheLoai>({ url: `/api/quan-tri/the-loai/${maTheLoai}`, method: 'PUT', data: duLieu }),
};
