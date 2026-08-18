import { useMutation } from '@tanstack/react-query';
import { UserPlus } from 'lucide-react';
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
    <div className="mx-auto grid max-w-6xl gap-8 px-4 py-12 lg:grid-cols-[420px_1fr]">
      <aside className="paper-panel h-fit rounded p-6">
        <p className="archival-label text-[#7d562d]">Thành viên mới</p>
        <h1 className="font-serif-display mt-2 text-4xl font-bold leading-tight text-[#03192e]">Mở một tài khoản đọc sách riêng của bạn</h1>
        <p className="mt-4 text-sm leading-6 text-[#43474d]">Tài khoản giúp lưu giỏ hàng, địa chỉ giao hàng, lịch sử đơn và danh sách sách yêu thích.</p>
        <div className="mt-6 rounded border border-[#c4c6cd] bg-[#fbf9f8] p-4">
          <p className="archival-label text-[#03192e]">Dữ liệu đăng ký</p>
          <p className="mt-2 text-sm text-[#43474d]">Tên đăng nhập, email, số điện thoại và mật khẩu được gửi đúng DTO tiếng Việt của backend.</p>
        </div>
      </aside>
      <form onSubmit={handleSubmit((duLieu) => mutation.mutate(duLieu))} className="paper-panel rounded p-6">
        <div className="flex items-center gap-3">
          <span className="grid h-11 w-11 place-items-center rounded bg-[#03192e] text-white"><UserPlus size={20} /></span>
          <div>
            <p className="archival-label text-[#7d562d]">Tài khoản</p>
            <h2 className="font-serif-display text-3xl font-bold text-[#03192e]">Tạo tài khoản</h2>
          </div>
        </div>
        <div className="mt-6 grid gap-4 md:grid-cols-2">
          <label className="block text-sm font-semibold text-[#03192e]">Họ và tên<Input className="mt-2" {...register('hoTen', { required: true })} /></label>
          <label className="block text-sm font-semibold text-[#03192e]">Tên đăng nhập<Input className="mt-2" {...register('tenDangNhap', { required: true })} /></label>
          <label className="block text-sm font-semibold text-[#03192e]">Email<Input className="mt-2" type="email" {...register('email', { required: true })} /></label>
          <label className="block text-sm font-semibold text-[#03192e]">Số điện thoại<Input className="mt-2" {...register('soDienThoai')} /></label>
          <label className="block text-sm font-semibold text-[#03192e] md:col-span-2">Mật khẩu<Input className="mt-2" type="password" {...register('matKhau', { required: true })} /></label>
        </div>
        <Button className="mt-6 w-full" disabled={mutation.isPending}>Đăng ký</Button>
        <p className="mt-4 text-center text-sm text-[#43474d]">Đã có tài khoản? <Link className="font-semibold text-[#7d562d]" to="/dang-nhap">Đăng nhập</Link></p>
      </form>
    </div>
  );
}
