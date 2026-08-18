import type { DanhGia, PhanTrang, Sach, TacGia, TheLoai } from '../types';
import { goiApi } from './client';

export interface ThamSoSach {
  tuKhoa?: string;
  tacGiaId?: string;
  theLoaiId?: string;
  giaMin?: string;
  giaMax?: string;
  sort?: string;
  page?: number;
  size?: number;
}

export const sachApi = {
  layDanhSachSach: (params: ThamSoSach) => goiApi<PhanTrang<Sach>>({ url: '/api/sach', params }),
  layChiTietSach: (maSach: number) => goiApi<Sach>({ url: `/api/sach/${maSach}` }),
  layDanhSachTheLoai: () => goiApi<TheLoai[]>({ url: '/api/the-loai' }),
  layDanhSachTacGia: (params?: { page?: number; size?: number; sort?: string }) =>
    goiApi<PhanTrang<TacGia>>({ url: '/api/tac-gia', params: { size: 100, ...params } }),
  layDanhGiaSach: (maSach: number, page = 0) =>
    goiApi<PhanTrang<DanhGia>>({ url: `/api/sach/${maSach}/danh-gia`, params: { page, size: 10 } }),
  themDanhGia: (maSach: number, duLieu: { soSao: number; noiDung: string }) =>
    goiApi<DanhGia>({ url: `/api/sach/${maSach}/danh-gia`, method: 'POST', data: duLieu }),
  capNhatDanhGia: (maDanhGia: number, duLieu: { soSao: number; noiDung: string }) =>
    goiApi<DanhGia>({ url: `/api/danh-gia/${maDanhGia}`, method: 'PUT', data: duLieu }),
  xoaDanhGia: (maDanhGia: number) => goiApi<void>({ url: `/api/danh-gia/${maDanhGia}`, method: 'DELETE' }),
  themSachYeuThich: (maSach: number) => goiApi<Sach>({ url: `/api/sach-yeu-thich/${maSach}`, method: 'POST' }),
  xoaSachYeuThich: (maSach: number) => goiApi<void>({ url: `/api/sach-yeu-thich/${maSach}`, method: 'DELETE' }),
  laySachYeuThich: (page = 0, size = 12) => goiApi<PhanTrang<Sach>>({ url: '/api/sach-yeu-thich', params: { page, size } }),
};
