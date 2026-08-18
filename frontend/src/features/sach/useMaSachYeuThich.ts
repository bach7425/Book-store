import { useMemo } from 'react';
import { useQuery } from '@tanstack/react-query';
import { sachApi } from '../../api/sachApi';
import { chuanHoaVaiTro } from '../../utils/vaiTro';
import { useXacThucStore } from '../xac-thuc/xacThucStore';

export function useMaSachYeuThich() {
  const daDangNhap = useXacThucStore((state) => state.daDangNhap);
  const nguoiDung = useXacThucStore((state) => state.nguoiDung);
  const laNguoiDungMuaHang = chuanHoaVaiTro(nguoiDung?.vaiTro) === 'ROLE_NGUOI_DUNG';
  const { data } = useQuery({
    queryKey: ['sach-yeu-thich', 'tat-ca'],
    queryFn: () => sachApi.laySachYeuThich(0, 1000),
    enabled: daDangNhap && laNguoiDungMuaHang,
  });

  return useMemo(() => new Set((data?.duLieu ?? []).map((sach) => sach.maSach)), [data]);
}
