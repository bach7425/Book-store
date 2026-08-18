import type { ChiTietGioHang, GioHang } from '../types';
import { goiApi } from './client';

export const gioHangApi = {
  layGioHang: () => goiApi<GioHang>({ url: '/api/gio-hang' }),
  themVaoGioHang: (maSach: number, soLuong: number) =>
    goiApi<ChiTietGioHang>({ url: '/api/gio-hang/san-pham', method: 'POST', data: { maSach, soLuong } }),
  capNhatSoLuong: (sanPhamId: number, soLuong: number) =>
    goiApi<ChiTietGioHang>({ url: `/api/gio-hang/san-pham/${sanPhamId}`, method: 'PUT', data: { soLuong } }),
  xoaSanPham: (sanPhamId: number) => goiApi<void>({ url: `/api/gio-hang/san-pham/${sanPhamId}`, method: 'DELETE' }),
};
