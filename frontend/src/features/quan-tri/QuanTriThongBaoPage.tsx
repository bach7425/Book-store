import { useMutation } from '@tanstack/react-query';
import { Send } from 'lucide-react';
import { useForm } from 'react-hook-form';
import { thongBaoApi } from '../../api/thongBaoApi';
import { Button } from '../../components/ui/Button';
import { Input } from '../../components/ui/Input';
import { useToastStore } from '../../components/ui/toastStore';
import type { GuiThongBaoRequest } from '../../types';

type FormThongBao = GuiThongBaoRequest & { maNguoiDungText?: string };

const loaiThongBao = [
  { value: 'QUAN_TRI', label: 'Quản trị' },
  { value: 'DON_HANG', label: 'Đơn hàng' },
  { value: 'DANH_GIA', label: 'Đánh giá' },
  { value: 'MA_GIAM_GIA', label: 'Khuyến mãi' },
];

export function QuanTriThongBaoPage() {
  const baoTin = useToastStore((state) => state.baoTin);
  const baoLoi = useToastStore((state) => state.baoLoi);
  const { register, handleSubmit, reset, watch } = useForm<FormThongBao>({
    defaultValues: { guiTatCa: true, loai: 'QUAN_TRI' },
  });
  const guiTatCa = watch('guiTatCa');
  const guiThongBao = useMutation({
    mutationFn: (form: FormThongBao) =>
      thongBaoApi.guiThongBaoQuanTri({
        guiTatCa: form.guiTatCa,
        maNguoiDung: form.guiTatCa ? undefined : Number(form.maNguoiDungText || form.maNguoiDung),
        tieuDe: form.tieuDe,
        noiDung: form.noiDung,
        loai: form.loai,
        duongDan: form.duongDan || undefined,
      }),
    onSuccess: () => {
      reset({ guiTatCa: true, loai: 'QUAN_TRI' });
      baoTin('Đã gửi thông báo');
    },
    onError: (err) => baoLoi(err.message),
  });

  return (
    <div className="mx-auto max-w-[860px]">
      <form onSubmit={handleSubmit((form) => guiThongBao.mutate(form))} className="paper-panel rounded p-6">
        <p className="archival-label text-[#7d562d]">Trung tâm thông báo</p>
        <h1 className="font-serif-display mt-1 text-3xl font-bold text-[#03192e]">Gửi thông báo</h1>
        <p className="mt-1 text-sm text-[#43474d]">Gửi thông báo cho một khách hàng hoặc toàn bộ người dùng.</p>

        <div className="mt-6 grid gap-4">
          <label className="flex items-center gap-3 rounded border border-[#c4c6cd] bg-white px-4 py-3 text-sm font-bold text-[#03192e]">
            <input type="checkbox" className="h-4 w-4 accent-[#03192e]" {...register('guiTatCa')} />
            Gửi cho tất cả người dùng
          </label>

          {!guiTatCa ? (
            <label className="block text-sm font-bold text-[#43474d]">
              Mã người dùng
              <Input className="mt-2" type="number" min={1} placeholder="Nhập mã người dùng" {...register('maNguoiDungText')} />
            </label>
          ) : null}

          <label className="block text-sm font-bold text-[#43474d]">
            Loại thông báo
            <select className="mt-2 min-h-10 w-full rounded border border-[#c4c6cd] bg-[#fbf9f8] px-3 text-sm" {...register('loai')}>
              {loaiThongBao.map((item) => <option key={item.value} value={item.value}>{item.label}</option>)}
            </select>
          </label>

          <Input placeholder="Tiêu đề" {...register('tieuDe', { required: true })} />
          <textarea
            className="min-h-36 rounded border border-[#c4c6cd] bg-[#fbf9f8] px-3 py-3 text-sm outline-none transition focus:border-[#7d562d] focus:ring-2 focus:ring-[#ffdcbd]"
            placeholder="Nội dung thông báo"
            {...register('noiDung', { required: true })}
          />
          <Input placeholder="Đường dẫn khi bấm vào thông báo, ví dụ /don-hang" {...register('duongDan')} />
        </div>

        <div className="mt-6 flex justify-end">
          <Button disabled={guiThongBao.isPending}>
            <Send size={16} /> Gửi thông báo
          </Button>
        </div>
      </form>
    </div>
  );
}
