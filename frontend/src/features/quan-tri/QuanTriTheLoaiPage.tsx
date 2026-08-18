import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { quanTriApi } from '../../api/quanTriApi';
import { sachApi } from '../../api/sachApi';
import { Bang, OTrong } from '../../components/ui/Bang';
import { Button } from '../../components/ui/Button';
import { Input } from '../../components/ui/Input';
import { useToastStore } from '../../components/ui/toastStore';
import type { TheLoai } from '../../types';

type FormTheLoai = {
  ten: string;
  moTa?: string;
};

const formTheLoaiRong: FormTheLoai = { ten: '', moTa: '' };

export function QuanTriTheLoaiPage() {
  const [hienForm, setHienForm] = useState(false);
  const [dangSua, setDangSua] = useState<TheLoai | null>(null);
  const queryClient = useQueryClient();
  const baoTin = useToastStore((state) => state.baoTin);
  const baoLoi = useToastStore((state) => state.baoLoi);
  const { register, handleSubmit, reset } = useForm<FormTheLoai>({ defaultValues: formTheLoaiRong });
  const { data, error } = useQuery({ queryKey: ['the-loai'], queryFn: sachApi.layDanhSachTheLoai });

  const dongForm = () => {
    setHienForm(false);
    setDangSua(null);
    reset(formTheLoaiRong);
  };

  const themTheLoaiMoi = () => {
    dongForm();
    setHienForm(true);
  };

  const suaTheLoai = (theLoai: TheLoai) => {
    setDangSua(theLoai);
    setHienForm(true);
    reset({ ten: theLoai.ten, moTa: theLoai.moTa ?? '' });
  };

  const luu = useMutation({
    mutationFn: (form: FormTheLoai) => {
      const payload = { ten: form.ten.trim(), moTa: form.moTa?.trim() || undefined };
      return dangSua ? quanTriApi.capNhatTheLoai(dangSua.maTheLoai, payload) : quanTriApi.themTheLoai(payload);
    },
    onSuccess: () => {
      dongForm();
      queryClient.invalidateQueries({ queryKey: ['the-loai'] });
      baoTin(dangSua ? 'Đã cập nhật thể loại' : 'Đã thêm thể loại');
    },
    onError: (err) => baoLoi(err.message),
  });

  return (
    <div className="grid gap-6">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <p className="archival-label text-[#7d562d]">Phân loại sách</p>
          <h1 className="font-serif-display mt-1 text-3xl font-bold text-[#03192e]">Quản lý thể loại</h1>
        </div>
        <Button type="button" onClick={themTheLoaiMoi}>Thêm thể loại</Button>
      </div>

      {hienForm ? (
        <form onSubmit={handleSubmit((form) => luu.mutate(form))} className="paper-panel rounded p-5">
          <div className="flex flex-wrap items-start justify-between gap-4">
            <div>
              <p className="archival-label text-[#7d562d]">Thông tin thể loại</p>
              <h2 className="font-serif-display mt-1 text-3xl font-bold text-[#03192e]">{dangSua ? 'Sửa thể loại' : 'Thêm thể loại'}</h2>
            </div>
            <div className="flex gap-2">
              <Button disabled={luu.isPending}>{dangSua ? 'Cập nhật' : 'Thêm thể loại'}</Button>
              <Button type="button" kieu="phu" onClick={dongForm}>Hủy</Button>
            </div>
          </div>
          <div className="mt-4 grid gap-3 md:grid-cols-2">
            <Input placeholder="Tên thể loại" {...register('ten', { required: true })} />
            <textarea className="textarea-paper min-h-28 px-3 py-2 text-sm md:col-span-2" placeholder="Mô tả" {...register('moTa')} />
          </div>
        </form>
      ) : null}

      {error ? <OTrong tieuDe="Không tải được thể loại" moTa={error.message} /> : (
        <Bang>
          <table className="w-full min-w-[720px] text-left text-sm">
            <thead className="du-lieu-heading">
              <tr>
                <th className="px-4 py-3">Thể loại</th>
                <th className="px-4 py-3">Mô tả</th>
                <th className="px-4 py-3">Thao tác</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-[#ece6df]">
              {(data ?? []).map((theLoai) => (
                <tr className="du-lieu-row align-top" key={theLoai.maTheLoai}>
                  <td className="px-4 py-3 font-semibold text-[#03192e]">{theLoai.ten}</td>
                  <td className="px-4 py-3 text-[#43474d]">{theLoai.moTa || 'Chưa có'}</td>
                  <td className="px-4 py-3 text-right"><Button kieu="phu" onClick={() => suaTheLoai(theLoai)}>Sửa</Button></td>
                </tr>
              ))}
            </tbody>
          </table>
        </Bang>
      )}
    </div>
  );
}
