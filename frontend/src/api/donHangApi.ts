import type { DonHang, PhanTrang } from '../types';
import { goiApi } from './client';

export const donHangApi = {
  taoDonHang: (duLieu: { maDiaChi: number; phuongThucThanhToan: string; maGiamGia?: string }) =>
    goiApi<DonHang>({ url: '/api/don-hang', method: 'POST', data: duLieu }),
  layDanhSachDonHang: (params?: { status?: string; page?: number; size?: number }) =>
    goiApi<PhanTrang<DonHang>>({ url: '/api/don-hang', params: { size: 10, ...params } }),
  layChiTietDonHang: (maDonHang: number) => goiApi<DonHang>({ url: `/api/don-hang/${maDonHang}` }),
  huyDonHang: (maDonHang: number) => goiApi<DonHang>({ url: `/api/don-hang/${maDonHang}/huy`, method: 'POST' }),
  thanhToanDonHang: (maDonHang: number) =>
    goiApi<DonHang>({ url: `/api/don-hang/${maDonHang}/thanh-toan`, method: 'PUT' }),
};
