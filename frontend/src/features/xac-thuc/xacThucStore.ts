import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import type { NguoiDung } from '../../types';
import { chuanHoaVaiTro } from '../../utils/vaiTro';

interface XacThucState {
  maTruyCap: string | null;
  maLamMoi: string | null;
  nguoiDung: NguoiDung | null;
  daDangNhap: boolean;
  dangNhapThanhCong: (maTruyCap: string, maLamMoi: string | null, nguoiDung: NguoiDung) => void;
  capNhatMaTruyCap: (maTruyCap: string) => void;
  capNhatNguoiDung: (nguoiDung: NguoiDung) => void;
  dangXuat: () => void;
}

function chuanHoaNguoiDung(nguoiDung: NguoiDung) {
  return { ...nguoiDung, vaiTro: chuanHoaVaiTro(nguoiDung.vaiTro) };
}

export const useXacThucStore = create<XacThucState>()(
  persist(
    (set) => ({
      maTruyCap: null,
      maLamMoi: null,
      nguoiDung: null,
      daDangNhap: false,
      dangNhapThanhCong: (maTruyCap, maLamMoi, nguoiDung) => set({ maTruyCap, maLamMoi, nguoiDung: chuanHoaNguoiDung(nguoiDung), daDangNhap: true }),
      capNhatMaTruyCap: (maTruyCap) => set({ maTruyCap, daDangNhap: true }),
      capNhatNguoiDung: (nguoiDung) => set({ nguoiDung: chuanHoaNguoiDung(nguoiDung) }),
      dangXuat: () => set({ maTruyCap: null, maLamMoi: null, nguoiDung: null, daDangNhap: false }),
    }),
    { name: 'bookstore-xac-thuc' },
  ),
);
