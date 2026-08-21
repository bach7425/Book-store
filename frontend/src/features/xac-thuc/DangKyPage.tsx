import { useMutation } from '@tanstack/react-query';
import { ArrowRight, Badge, Eye, EyeOff, Lock, Mail, Phone, UserRound } from 'lucide-react';
import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { Link, useNavigate } from 'react-router-dom';
import { xacThucApi, type DangKyRequest } from '../../api/xacThucApi';
import { Button } from '../../components/ui/Button';
import { Input } from '../../components/ui/Input';
import { useToastStore } from '../../components/ui/toastStore';

export function DangKyPage() {
  const navigate = useNavigate();
  const baoTin = useToastStore((state) => state.baoTin);
  const baoLoi = useToastStore((state) => state.baoLoi);
  const [hienMatKhau, setHienMatKhau] = useState(false);
  const { register, handleSubmit } = useForm<DangKyRequest>();
  const mutation = useMutation({
    mutationFn: xacThucApi.dangKy,
    onSuccess: () => {
      baoTin('Đăng ký thành công, hãy đăng nhập');
      navigate('/dang-nhap');
    },
    onError: (error) => baoLoi(error.message),
  });

  return (
    <div className="mx-auto flex min-h-[calc(100vh-4rem)] max-w-6xl items-center justify-center px-4 py-12">
      <form onSubmit={handleSubmit((duLieu) => mutation.mutate(duLieu))} className="paper-panel w-full max-w-lg rounded p-8 md:p-10">
        <div className="mb-8 text-center">
          <h1 className="font-serif-display text-3xl font-bold leading-tight text-[#03192e]">Gia nhập cộng đồng yêu sách</h1>
          <p className="mt-2 text-sm text-[#43474d]">Book Store - Nơi hội tụ những tâm hồn đồng điệu.</p>
        </div>
        <div className="grid gap-5">
          <label className="block text-sm font-semibold text-[#03192e]">
            Họ và tên
            <span className="relative mt-2 block">
              <UserRound className="pointer-events-none absolute left-3 top-1/2 -translate-y-1/2 text-[#74777d]" size={18} />
              <Input className="pl-10" placeholder="Nguyễn Văn A" {...register('hoTen', { required: true })} />
            </span>
          </label>
          <label className="block text-sm font-semibold text-[#03192e]">
            Tên đăng nhập
            <span className="relative mt-2 block">
              <Badge className="pointer-events-none absolute left-3 top-1/2 -translate-y-1/2 text-[#74777d]" size={18} />
              <Input className="pl-10" placeholder="nguyenvana123" {...register('tenDangNhap', { required: true })} />
            </span>
          </label>
          <label className="block text-sm font-semibold text-[#03192e]">
            Email
            <span className="relative mt-2 block">
              <Mail className="pointer-events-none absolute left-3 top-1/2 -translate-y-1/2 text-[#74777d]" size={18} />
              <Input className="pl-10" type="email" placeholder="nguyenvana@example.com" {...register('email', { required: true })} />
            </span>
          </label>
          <label className="block text-sm font-semibold text-[#03192e]">
            Số điện thoại
            <span className="relative mt-2 block">
              <Phone className="pointer-events-none absolute left-3 top-1/2 -translate-y-1/2 text-[#74777d]" size={18} />
              <Input className="pl-10" placeholder="0123 456 789" {...register('soDienThoai')} />
            </span>
          </label>
          <label className="block text-sm font-semibold text-[#03192e]">
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
        </div>
        <Button className="mt-8 w-full" disabled={mutation.isPending}>Đăng ký <ArrowRight size={18} /></Button>
        <div className="mt-8 border-t border-[#e4e2e2] pt-6 text-center">
          <p className="text-sm text-[#43474d]">Đã có tài khoản? <Link className="font-bold text-[#7d562d] hover:text-[#03192e]" to="/dang-nhap">Đăng nhập</Link></p>
        </div>
      </form>
    </div>
  );
}
