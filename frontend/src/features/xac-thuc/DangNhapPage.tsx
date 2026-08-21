import { useMutation } from '@tanstack/react-query';
import { Eye, EyeOff, LogIn, Lock, UserRound } from 'lucide-react';
import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { xacThucApi, type DangNhapRequest } from '../../api/xacThucApi';
import { Button } from '../../components/ui/Button';
import { Input } from '../../components/ui/Input';
import { useToastStore } from '../../components/ui/toastStore';
import { chuanHoaVaiTro } from '../../utils/vaiTro';
import { useXacThucStore } from './xacThucStore';

export function DangNhapPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const baoTin = useToastStore((state) => state.baoTin);
  const baoLoi = useToastStore((state) => state.baoLoi);
  const dangNhapThanhCong = useXacThucStore((state) => state.dangNhapThanhCong);
  const [hienMatKhau, setHienMatKhau] = useState(false);
  const { register, handleSubmit } = useForm<DangNhapRequest>();
  const mutation = useMutation({
    mutationFn: xacThucApi.dangNhap,
    onSuccess: (duLieu) => {
      dangNhapThanhCong(duLieu.maTruyCap, duLieu.maLamMoi ?? null, duLieu.nguoiDung);
      baoTin('Đăng nhập thành công');
      const from = (location.state as { from?: { pathname?: string } } | null)?.from?.pathname;
      navigate(from || (chuanHoaVaiTro(duLieu.nguoiDung.vaiTro) === 'ROLE_QUAN_TRI_VIEN' ? '/quan-tri' : '/'));
    },
    onError: (error) => baoLoi(error.message),
  });

  return (
    <div className="mx-auto flex min-h-[calc(100vh-4rem)] max-w-6xl items-center justify-center px-4 py-12">
      <form onSubmit={handleSubmit((duLieu) => mutation.mutate(duLieu))} className="paper-panel w-full max-w-md rounded p-8">
        <div className="mb-8 text-center">
          <h1 className="font-serif-display text-4xl font-bold leading-tight text-[#03192e]">Chào mừng trở lại</h1>
          <p className="mt-2 text-sm text-[#43474d]">Vui lòng đăng nhập để tiếp tục</p>
        </div>
        <label className="block text-sm font-semibold text-[#03192e]">
          Tên đăng nhập
          <span className="relative mt-2 block">
            <UserRound className="pointer-events-none absolute left-3 top-1/2 -translate-y-1/2 text-[#74777d]" size={18} />
            <Input className="pl-10" placeholder="Nhập tên đăng nhập của bạn" {...register('tenDangNhap', { required: true })} />
          </span>
        </label>
        <label className="mt-5 block text-sm font-semibold text-[#03192e]">
          Mật khẩu
          <span className="relative mt-2 block">
            <Lock className="pointer-events-none absolute left-3 top-1/2 -translate-y-1/2 text-[#74777d]" size={18} />
            <Input className="pl-10 pr-11" type={hienMatKhau ? 'text' : 'password'} placeholder="••••••••" {...register('matKhau', { required: true })} />
            <button
              type="button"
              className="absolute right-2 top-1/2 grid h-8 w-8 -translate-y-1/2 place-items-center rounded text-[#74777d] transition hover:bg-[#efeded] hover:text-[#03192e]"
              onClick={() => setHienMatKhau((value) => !value)}
              aria-label={hienMatKhau ? 'Ẩn mật khẩu' : 'Hiện mật khẩu'}
            >
              {hienMatKhau ? <EyeOff size={17} /> : <Eye size={17} />}
            </button>
          </span>
        </label>
        <Button className="mt-6 w-full" disabled={mutation.isPending}><LogIn size={18} /> Đăng nhập</Button>
        <div className="mt-8 border-t border-[#e4e2e2] pt-6 text-center">
          <p className="text-sm text-[#43474d]">Chưa có tài khoản? <Link className="font-bold text-[#7d562d] hover:text-[#03192e]" to="/dang-ky">Đăng ký ngay</Link></p>
        </div>
      </form>
    </div>
  );
}
