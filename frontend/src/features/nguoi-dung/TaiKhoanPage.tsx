import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { Camera, CheckCircle2, Edit2, MapPin } from 'lucide-react';
import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { nguoiDungApi } from '../../api/nguoiDungApi';
import { AvatarNguoiDung } from '../../components/ui/AvatarNguoiDung';
import { Bang, OTrong } from '../../components/ui/Bang';
import { Button } from '../../components/ui/Button';
import { Input } from '../../components/ui/Input';
import { useToastStore } from '../../components/ui/toastStore';
import type { DiaChi } from '../../types';
import { chuanHoaVaiTro } from '../../utils/vaiTro';
import { useXacThucStore } from '../xac-thuc/xacThucStore';

type FormDiaChi = Omit<DiaChi, 'maDiaChi'>;

const diaChiMacDinh: FormDiaChi = {
  nguoiNhan: '',
  soDienThoai: '',
  diaChiChiTiet: '',
  macDinh: false,
};

function duLieuDiaChi(item: DiaChi): FormDiaChi {
  return {
    nguoiNhan: item.nguoiNhan,
    soDienThoai: item.soDienThoai,
    diaChiChiTiet: item.diaChiChiTiet,
    macDinh: item.macDinh,
  };
}

export function TaiKhoanPage() {
  const queryClient = useQueryClient();
  const baoTin = useToastStore((state) => state.baoTin);
  const baoLoi = useToastStore((state) => state.baoLoi);
  const capNhatNguoiDung = useXacThucStore((state) => state.capNhatNguoiDung);
  const nguoiDung = useXacThucStore((state) => state.nguoiDung);
  const laNguoiDungMuaHang = chuanHoaVaiTro(nguoiDung?.vaiTro) === 'ROLE_NGUOI_DUNG';
  const [diaChiDangSua, setDiaChiDangSua] = useState<DiaChi | null>(null);
  const { data: profile } = useQuery({ queryKey: ['nguoi-dung'], queryFn: nguoiDungApi.layThongTinNguoiDung });
  const { data: diaChi = [] } = useQuery({ queryKey: ['dia-chi'], queryFn: nguoiDungApi.layDanhSachDiaChi, enabled: laNguoiDungMuaHang });
  const { register, handleSubmit } = useForm({ values: { hoVaTen: profile?.hoVaTen ?? '', soDienThoai: profile?.soDienThoai ?? '' } });
  const { register: registerDiaChi, handleSubmit: handleSubmitDiaChi, reset: resetDiaChi } = useForm<FormDiaChi>({ defaultValues: diaChiMacDinh });
  const daDuBaDiaChi = diaChi.length >= 3;
  const capNhat = useMutation({
    mutationFn: nguoiDungApi.capNhatThongTin,
    onSuccess: (duLieu) => {
      capNhatNguoiDung(duLieu);
      queryClient.invalidateQueries({ queryKey: ['nguoi-dung'] });
      baoTin('Đã cập nhật tài khoản');
    },
    onError: (err) => baoLoi(err.message),
  });
  const themDiaChi = useMutation({
    mutationFn: (duLieu: FormDiaChi) => nguoiDungApi.themDiaChi({ ...duLieu, macDinh: diaChi.length === 0 || duLieu.macDinh }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['dia-chi'] });
      resetDiaChi(diaChiMacDinh);
      baoTin('Đã thêm địa chỉ');
    },
    onError: (err) => baoLoi(err.message),
  });
  const capNhatDiaChi = useMutation({
    mutationFn: ({ maDiaChi, duLieu }: { maDiaChi: number; duLieu: FormDiaChi }) => nguoiDungApi.capNhatDiaChi(maDiaChi, duLieu),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['dia-chi'] });
      setDiaChiDangSua(null);
      resetDiaChi(diaChiMacDinh);
      baoTin('Đã cập nhật địa chỉ');
    },
    onError: (err) => baoLoi(err.message),
  });
  const capNhatAnh = useMutation({
    mutationFn: nguoiDungApi.capNhatAnhDaiDien,
    onSuccess: (duLieu) => {
      capNhatNguoiDung(duLieu);
      queryClient.invalidateQueries({ queryKey: ['nguoi-dung'] });
      baoTin('Đã cập nhật ảnh đại diện');
    },
    onError: (err) => baoLoi(err.message),
  });

  const luuDiaChi = (duLieu: FormDiaChi) => {
    if (!diaChiDangSua && daDuBaDiaChi) {
      baoLoi('Bạn chỉ có thể thêm tối đa 3 địa chỉ');
      return;
    }
    if (diaChiDangSua) capNhatDiaChi.mutate({ maDiaChi: diaChiDangSua.maDiaChi, duLieu });
    else themDiaChi.mutate(duLieu);
  };

  const batDauSuaDiaChi = (item: DiaChi) => {
    setDiaChiDangSua(item);
    resetDiaChi(duLieuDiaChi(item));
  };

  const huySuaDiaChi = () => {
    setDiaChiDangSua(null);
    resetDiaChi(diaChiMacDinh);
  };

  const datLamMacDinh = (item: DiaChi) => {
    capNhatDiaChi.mutate({ maDiaChi: item.maDiaChi, duLieu: { ...duLieuDiaChi(item), macDinh: true } });
  };

  return (
    <div className="mx-auto max-w-7xl px-4 py-8">
      <div className="grid gap-6 lg:grid-cols-[420px_1fr]">
        <form onSubmit={handleSubmit((duLieu) => capNhat.mutate(duLieu))} className="paper-panel h-fit rounded p-5">
        <div className="mb-6 flex items-center gap-4 rounded border border-[#c4c6cd] bg-[#fbf9f8] p-4">
          <AvatarNguoiDung ten={profile?.hoVaTen} anhDaiDien={profile?.anhDaiDien} kichThuoc="xl" />
          <div className="min-w-0">
            <p className="truncate font-serif-display text-2xl font-bold text-[#03192e]">{profile?.hoVaTen ?? 'Độc giả'}</p>
            <p className="truncate text-sm text-[#43474d]">{profile?.tenDangNhap}</p>
            <label className="mt-3 inline-flex cursor-pointer items-center gap-2 rounded border border-[#c4c6cd] bg-white px-3 py-2 text-sm font-bold text-[#03192e] hover:border-[#7d562d] hover:text-[#7d562d]">
              <Camera size={16} /> Đổi ảnh
              <input
                type="file"
                accept="image/*"
                className="sr-only"
                disabled={capNhatAnh.isPending}
                onChange={(event) => {
                  const file = event.target.files?.[0];
                  if (file) capNhatAnh.mutate(file);
                  event.currentTarget.value = '';
                }}
              />
            </label>
          </div>
        </div>
        <p className="archival-label text-[#7d562d]">Hồ sơ cá nhân</p>
        <h1 className="font-serif-display mt-1 text-3xl font-bold text-[#03192e]">Tài khoản</h1>
        <label className="mt-5 block text-sm font-semibold text-[#03192e]">Họ và tên<Input className="mt-2" {...register('hoVaTen')} /></label>
        <label className="mt-4 block text-sm font-semibold text-[#03192e]">Số điện thoại<Input className="mt-2" {...register('soDienThoai')} /></label>
        <p className="mt-4 rounded border border-[#c4c6cd] bg-[#fbf9f8] px-3 py-2 text-sm text-[#43474d]">Email: {profile?.email}</p>
        <Button className="mt-5" disabled={capNhat.isPending}>Lưu thông tin</Button>
        </form>
        {laNguoiDungMuaHang ? <section className="space-y-5">
        <form onSubmit={handleSubmitDiaChi(luuDiaChi)} className="paper-panel rounded p-5">
          <div className="flex flex-wrap items-start justify-between gap-3">
            <div>
              <p className="archival-label text-[#7d562d]">Sổ địa chỉ</p>
              <h2 className="font-serif-display mt-1 text-2xl font-bold text-[#03192e]">{diaChiDangSua ? 'Cập nhật địa chỉ' : 'Thêm địa chỉ'}</h2>
              <p className="mt-1 text-sm text-[#43474d]">{diaChi.length}/3 địa chỉ đã lưu</p>
            </div>
            {diaChiDangSua ? <Button type="button" kieu="rong" onClick={huySuaDiaChi}>Hủy sửa</Button> : null}
          </div>
          {daDuBaDiaChi && !diaChiDangSua ? <p className="mt-4 rounded border border-[#ffb4ab] bg-[#ffdad6] px-3 py-2 text-sm font-semibold text-[#93000a]">Bạn chỉ có thể thêm tối đa 3 địa chỉ.</p> : null}
          <div className="mt-4 grid gap-3 md:grid-cols-2">
            <Input placeholder="Người nhận" {...registerDiaChi('nguoiNhan', { required: true })} />
            <Input placeholder="Số điện thoại" {...registerDiaChi('soDienThoai', { required: true })} />
            <Input className="md:col-span-2" placeholder="Địa chỉ chi tiết" {...registerDiaChi('diaChiChiTiet', { required: true })} />
          </div>
          <label className="mt-4 flex items-center gap-2 text-sm font-bold text-[#03192e]">
            <input type="checkbox" className="h-4 w-4 accent-[#03192e]" {...registerDiaChi('macDinh')} />
            Đặt làm địa chỉ mặc định
          </label>
          <Button className="mt-4" disabled={(!diaChiDangSua && daDuBaDiaChi) || themDiaChi.isPending || capNhatDiaChi.isPending}>
            {diaChiDangSua ? 'Lưu địa chỉ' : 'Thêm địa chỉ'}
          </Button>
        </form>
        {diaChi.length === 0 ? <OTrong tieuDe="Chưa có địa chỉ" moTa="Hãy thêm địa chỉ nhận hàng đầu tiên." /> : (
          <Bang><table className="w-full min-w-[760px] text-left text-sm"><thead className="du-lieu-heading"><tr><th className="px-4 py-3">Người nhận</th><th className="px-4 py-3">Địa chỉ</th><th className="px-4 py-3">Mặc định</th><th className="px-4 py-3"></th></tr></thead><tbody className="divide-y divide-[#ece6df]">{diaChi.map((item) => <tr className="du-lieu-row" key={item.maDiaChi}><td className="px-4 py-3 font-semibold text-[#03192e]">{item.nguoiNhan}<br /><span className="font-normal text-[#43474d]">{item.soDienThoai}</span></td><td className="px-4 py-3 text-[#43474d]"><span className="inline-flex items-start gap-2"><MapPin className="mt-0.5 shrink-0 text-[#7d562d]" size={16} />{item.diaChiChiTiet}</span></td><td className="px-4 py-3">{item.macDinh ? <span className="inline-flex items-center gap-1 rounded bg-[#e8f5e9] px-2 py-1 text-xs font-bold text-[#246b32]"><CheckCircle2 size={14} /> Mặc định</span> : <Button type="button" kieu="rong" onClick={() => datLamMacDinh(item)} disabled={capNhatDiaChi.isPending}>Đặt mặc định</Button>}</td><td className="px-4 py-3 text-right"><Button type="button" kieu="phu" onClick={() => batDauSuaDiaChi(item)}><Edit2 size={15} /> Sửa</Button></td></tr>)}</tbody></table></Bang>
        )}
        </section> : (
          <section className="paper-panel h-fit rounded p-5">
            <p className="archival-label text-[#7d562d]">Tài khoản quản trị</p>
            <h2 className="font-serif-display mt-1 text-2xl font-bold text-[#03192e]">Không có sổ địa chỉ</h2>
            <p className="mt-2 text-sm leading-6 text-[#43474d]">Tài khoản quản trị dùng để vận hành hệ thống, không tham gia luồng mua hàng và thanh toán.</p>
          </section>
        )}
      </div>
    </div>
  );
}
