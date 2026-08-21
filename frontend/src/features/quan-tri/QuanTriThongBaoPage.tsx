import { useMutation, useQuery } from '@tanstack/react-query';
import { CheckCircle2, Search, Send, UserRound } from 'lucide-react';
import { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import { quanTriApi } from '../../api/quanTriApi';
import { thongBaoApi } from '../../api/thongBaoApi';
import { Button } from '../../components/ui/Button';
import { Input } from '../../components/ui/Input';
import { useToastStore } from '../../components/ui/toastStore';
import type { GuiThongBaoRequest } from '../../types';

type FormThongBao = Omit<GuiThongBaoRequest, 'maNguoiDung'>;

const loaiThongBao = [
  { value: 'QUAN_TRI', label: 'Quản trị' },
  { value: 'DON_HANG', label: 'Đơn hàng' },
  { value: 'DANH_GIA', label: 'Đánh giá' },
  { value: 'MA_GIAM_GIA', label: 'Khuyến mãi' },
];

export function QuanTriThongBaoPage() {
  const baoTin = useToastStore((state) => state.baoTin);
  const baoLoi = useToastStore((state) => state.baoLoi);
  const [tuKhoaKhachHang, setTuKhoaKhachHang] = useState('');
  const [maNguoiDungDaChon, setMaNguoiDungDaChon] = useState<number | null>(null);
  const { register, handleSubmit, reset, watch } = useForm<FormThongBao>({
    defaultValues: { guiTatCa: true, loai: 'QUAN_TRI' },
  });
  const guiTatCa = watch('guiTatCa');

  const { data: khachHang, isLoading: dangTaiKhachHang } = useQuery({
    queryKey: ['quan-tri', 'khach-hang', 'thong-bao', tuKhoaKhachHang],
    queryFn: () => quanTriApi.layKhachHang({ tuKhoa: tuKhoaKhachHang || undefined, size: 6 }),
    enabled: !guiTatCa,
  });

  const guiThongBao = useMutation({
    mutationFn: (duLieu: GuiThongBaoRequest) => thongBaoApi.guiThongBaoQuanTri(duLieu),
    onSuccess: () => {
      reset({ guiTatCa: true, loai: 'QUAN_TRI' });
      setMaNguoiDungDaChon(null);
      setTuKhoaKhachHang('');
      baoTin('Đã gửi thông báo');
    },
    onError: (err) => baoLoi(err.message),
  });

  useEffect(() => {
    if (guiTatCa) {
      setMaNguoiDungDaChon(null);
      setTuKhoaKhachHang('');
    }
  }, [guiTatCa]);

  const guiForm = (form: FormThongBao) => {
    if (!form.guiTatCa && !maNguoiDungDaChon) {
      baoLoi('Vui lòng chọn khách hàng nhận thông báo');
      return;
    }

    guiThongBao.mutate({
      guiTatCa: form.guiTatCa,
      maNguoiDung: form.guiTatCa ? undefined : maNguoiDungDaChon ?? undefined,
      tieuDe: form.tieuDe,
      noiDung: form.noiDung,
      loai: form.loai,
      duongDan: form.duongDan || undefined,
    });
  };

  const danhSachKhachHang = khachHang?.duLieu ?? [];

  return (
    <div className="mx-auto max-w-[860px]">
      <form onSubmit={handleSubmit(guiForm)} className="paper-panel rounded p-6">
        <p className="archival-label text-[#7d562d]">Trung tâm thông báo</p>
        <h1 className="font-serif-display mt-1 text-3xl font-bold text-[#03192e]">Gửi thông báo</h1>
        <p className="mt-1 text-sm text-[#43474d]">Gửi thông báo cho một khách hàng hoặc toàn bộ người dùng.</p>

        <div className="mt-6 grid gap-4">
          <label className="flex items-center gap-3 rounded border border-[#c4c6cd] bg-white px-4 py-3 text-sm font-bold text-[#03192e]">
            <input type="checkbox" className="h-4 w-4 accent-[#03192e]" {...register('guiTatCa')} />
            Gửi cho tất cả người dùng
          </label>

          {!guiTatCa ? (
            <div className="rounded border border-[#c4c6cd] bg-white p-4">
              <label className="block text-sm font-bold text-[#43474d]">
                Chọn khách hàng
                <span className="relative mt-2 block">
                  <Search className="pointer-events-none absolute left-3 top-1/2 -translate-y-1/2 text-[#7d562d]" size={16} />
                  <Input
                    className="pl-9"
                    placeholder="Tìm theo tên, email hoặc tên đăng nhập"
                    value={tuKhoaKhachHang}
                    onChange={(event) => {
                      setTuKhoaKhachHang(event.target.value);
                      setMaNguoiDungDaChon(null);
                    }}
                  />
                </span>
              </label>

              <div className="mt-3 grid gap-2">
                {dangTaiKhachHang ? (
                  <p className="rounded border border-dashed border-[#c4c6cd] bg-[#fbf9f8] px-3 py-3 text-sm text-[#43474d]">
                    Đang tải danh sách khách hàng...
                  </p>
                ) : null}

                {!dangTaiKhachHang && danhSachKhachHang.length === 0 ? (
                  <p className="rounded border border-dashed border-[#c4c6cd] bg-[#fbf9f8] px-3 py-3 text-sm text-[#43474d]">
                    Không tìm thấy khách hàng phù hợp.
                  </p>
                ) : null}

                {danhSachKhachHang.map((khach) => {
                  const daChon = maNguoiDungDaChon === khach.maNguoiDung;
                  return (
                    <button
                      key={khach.maNguoiDung}
                      type="button"
                      className={`flex w-full items-center justify-between gap-3 rounded border px-3 py-3 text-left transition ${
                        daChon
                          ? 'border-[#7d562d] bg-[#fff7ef] ring-2 ring-[#ffdcbd]'
                          : 'border-[#ece6df] bg-[#fbf9f8] hover:border-[#7d562d]'
                      }`}
                      onClick={() => setMaNguoiDungDaChon(khach.maNguoiDung)}
                    >
                      <span className="flex min-w-0 items-center gap-3">
                        <span className="flex h-9 w-9 shrink-0 items-center justify-center rounded-full border border-[#c4c6cd] bg-white text-[#03192e]">
                          <UserRound size={18} />
                        </span>
                        <span className="min-w-0">
                          <span className="block truncate text-sm font-bold text-[#03192e]">{khach.hoVaTen}</span>
                          <span className="block truncate text-xs text-[#43474d]">
                            {khach.tenDangNhap} · {khach.email}
                          </span>
                        </span>
                      </span>
                      {daChon ? <CheckCircle2 className="shrink-0 text-[#7d562d]" size={18} /> : null}
                    </button>
                  );
                })}
              </div>
            </div>
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
