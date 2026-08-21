import type { DiaChi, NguoiDung } from '../types';
import { goiApi } from './client';

export const nguoiDungApi = {
  layThongTinNguoiDung: () => goiApi<NguoiDung>({ url: '/api/nguoi-dung/thong-tin' }),
  capNhatThongTin: (duLieu: { hoVaTen: string; email: string; soDienThoai?: string }) =>
    goiApi<NguoiDung>({ url: '/api/nguoi-dung/thong-tin', method: 'PUT', data: duLieu }),
  capNhatAnhDaiDien: async (file: File) => {
    const formData = new FormData();
    formData.append('file', file);
    return goiApi<NguoiDung>({
      url: '/api/nguoi-dung/anh-dai-dien',
      method: 'POST',
      data: formData,
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },
  doiMatKhau: (duLieu: { matKhauCu: string; matKhauMoi: string }) =>
    goiApi<void>({ url: '/api/nguoi-dung/doi-mat-khau', method: 'PUT', data: duLieu }),
  layDanhSachDiaChi: async () => {
    try {
      return await goiApi<DiaChi[]>({ url: '/api/nguoi-dung/dia-chi' });
    } catch (error) {
      if (error instanceof Error && error.message.toLowerCase().includes('địa chỉ')) return [];
      throw error;
    }
  },
  themDiaChi: (duLieu: Omit<DiaChi, 'maDiaChi'>) =>
    goiApi<DiaChi>({ url: '/api/nguoi-dung/dia-chi', method: 'POST', data: duLieu }),
  capNhatDiaChi: (maDiaChi: number, duLieu: Omit<DiaChi, 'maDiaChi'>) =>
    goiApi<DiaChi>({ url: `/api/nguoi-dung/dia-chi/${maDiaChi}`, method: 'PUT', data: duLieu }),
};
