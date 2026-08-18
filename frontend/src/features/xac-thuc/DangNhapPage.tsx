import { useMutation } from '@tanstack/react-query';
import { BookOpen, LogIn, ShieldCheck } from 'lucide-react';
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
    <div className="mx-auto grid max-w-6xl gap-8 px-4 py-12 md:grid-cols-[1fr_430px]">
      <section className="relative overflow-hidden rounded border border-[#c4c6cd] bg-[#03192e] p-8 text-white">
        <div className="absolute right-8 top-8 h-44 w-32 rotate-6 rounded bg-[#f4d3a3] book-shadow" />
        <div className="absolute right-24 top-24 h-44 w-32 -rotate-6 rounded bg-[#7d562d] book-shadow" />
        <div className="relative max-w-lg">
          <p className="archival-label text-[#f4d3a3]">Book Store</p>
          <h1 className="font-serif-display mt-4 text-5xl font-bold leading-tight">Đăng nhập để tiếp tục hành trình đọc sách</h1>
          <p className="mt-5 text-base leading-7 text-white/75">Đồng bộ giỏ hàng, theo dõi đơn hàng và truy cập không gian quản trị nếu tài khoản của bạn có quyền.</p>
        </div>
        <div className="relative mt-10 grid gap-3 text-sm text-white/82 sm:grid-cols-2">
          <div className="rounded border border-white/15 bg-white/8 p-4"><BookOpen size={18} /><p className="mt-3 font-semibold">Giữ lại lịch sử mua hàng</p></div>
          <div className="rounded border border-white/15 bg-white/8 p-4"><ShieldCheck size={18} /><p className="mt-3 font-semibold">Bảo vệ tài khoản bằng JWT</p></div>
        </div>
      </section>
      <form onSubmit={handleSubmit((duLieu) => mutation.mutate(duLieu))} className="paper-panel h-fit rounded p-6">
        <p className="archival-label text-[#7d562d]">Tài khoản</p>
        <h2 className="font-serif-display mt-2 text-3xl font-bold text-[#03192e]">Đăng nhập</h2>
        <label className="mt-6 block text-sm font-semibold text-[#03192e]">Tên đăng nhập<Input className="mt-2" {...register('tenDangNhap', { required: true })} /></label>
        <label className="mt-4 block text-sm font-semibold text-[#03192e]">Mật khẩu<Input className="mt-2" type="password" {...register('matKhau', { required: true })} /></label>
        <Button className="mt-6 w-full" disabled={mutation.isPending}><LogIn size={18} /> Đăng nhập</Button>
        <p className="mt-4 text-center text-sm text-[#43474d]">Chưa có tài khoản? <Link className="font-semibold text-[#7d562d]" to="/dang-ky">Đăng ký</Link></p>
      </form>
    </div>
  );
}
