import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { chuanHoaVaiTro } from '../../utils/vaiTro';
import { useXacThucStore } from './xacThucStore';

export function BaoVeRoute({ vaiTro }: { vaiTro: string[] }) {
  const location = useLocation();
  const { daDangNhap, nguoiDung } = useXacThucStore();

  if (!daDangNhap) return <Navigate to="/dang-nhap" state={{ from: location }} replace />;
  if (nguoiDung && !vaiTro.includes(chuanHoaVaiTro(nguoiDung.vaiTro))) return <Navigate to="/" replace />;
  return <Outlet />;
}
