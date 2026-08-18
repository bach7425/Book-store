import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { ImagePlus, X } from 'lucide-react';
import { useRef, useState } from 'react';
import { useForm } from 'react-hook-form';
import { quanTriApi } from '../../api/quanTriApi';
import { sachApi } from '../../api/sachApi';
import { Bang, OTrong } from '../../components/ui/Bang';
import { Button } from '../../components/ui/Button';
import { Input } from '../../components/ui/Input';
import { PhanTrang } from '../../components/ui/PhanTrang';
import { useToastStore } from '../../components/ui/toastStore';
import type { TacGia } from '../../types';
import { duongDanAnh } from '../../utils/dinhDang';

type FormTacGia = {
  ten: string;
  tieuSu?: string;
};

const formTacGiaRong: FormTacGia = { ten: '', tieuSu: '' };

export function QuanTriTacGiaPage() {
  const [page, setPage] = useState(0);
  const [hienForm, setHienForm] = useState(false);
  const [dangSua, setDangSua] = useState<TacGia | null>(null);
  const [anhFile, setAnhFile] = useState<File | null>(null);
  const [anhPreview, setAnhPreview] = useState<string | null>(null);
  const inputAnhRef = useRef<HTMLInputElement | null>(null);
  const queryClient = useQueryClient();
  const baoTin = useToastStore((state) => state.baoTin);
  const baoLoi = useToastStore((state) => state.baoLoi);
  const { register, handleSubmit, reset } = useForm<FormTacGia>({ defaultValues: formTacGiaRong });
  const { data, error } = useQuery({
    queryKey: ['quan-tri', 'tac-gia', page],
    queryFn: () => sachApi.layDanhSachTacGia({ page, size: 10 }),
  });

  const datAnhPreview = (preview: string | null) => {
    setAnhPreview((anhCu) => {
      if (anhCu?.startsWith('blob:')) URL.revokeObjectURL(anhCu);
      return preview;
    });
  };

  const dongForm = () => {
    setHienForm(false);
    setDangSua(null);
    setAnhFile(null);
    datAnhPreview(null);
    reset(formTacGiaRong);
    if (inputAnhRef.current) inputAnhRef.current.value = '';
  };

  const themTacGiaMoi = () => {
    dongForm();
    setHienForm(true);
  };

  const suaTacGia = (tacGia: TacGia) => {
    setDangSua(tacGia);
    setHienForm(true);
    setAnhFile(null);
    datAnhPreview(tacGia.anhDaiDien ? duongDanAnh(tacGia.anhDaiDien) : null);
    reset({ ten: tacGia.ten, tieuSu: tacGia.tieuSu ?? '' });
    if (inputAnhRef.current) inputAnhRef.current.value = '';
  };

  const chonAnh = (file?: File) => {
    setAnhFile(file ?? null);
    datAnhPreview(file ? URL.createObjectURL(file) : dangSua?.anhDaiDien ? duongDanAnh(dangSua.anhDaiDien) : null);
    if (inputAnhRef.current) inputAnhRef.current.value = '';
  };

  const luu = useMutation({
    mutationFn: async (form: FormTacGia) => {
      const payload = { ten: form.ten.trim(), tieuSu: form.tieuSu?.trim() || undefined };
      const tacGiaDaLuu = dangSua
        ? await quanTriApi.capNhatTacGia(dangSua.maTacGia, payload)
        : await quanTriApi.themTacGia(payload);
      return anhFile ? quanTriApi.capNhatAnhDaiDienTacGia(tacGiaDaLuu.maTacGia, anhFile) : tacGiaDaLuu;
    },
    onSuccess: () => {
      dongForm();
      queryClient.invalidateQueries({ queryKey: ['quan-tri', 'tac-gia'] });
      queryClient.invalidateQueries({ queryKey: ['tac-gia'] });
      baoTin(dangSua ? 'Đã cập nhật tác giả' : 'Đã thêm tác giả');
    },
    onError: (err) => baoLoi(err.message),
  });

  return (
    <div className="grid gap-6">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <p className="archival-label text-[#7d562d]">Hồ sơ tác giả</p>
          <h1 className="font-serif-display mt-1 text-3xl font-bold text-[#03192e]">Quản lý tác giả</h1>
        </div>
        <Button type="button" onClick={themTacGiaMoi}>Thêm tác giả</Button>
      </div>

      {hienForm ? (
        <form onSubmit={handleSubmit((form) => luu.mutate(form))} className="paper-panel rounded p-5">
          <div className="flex flex-wrap items-start justify-between gap-4">
            <div>
              <p className="archival-label text-[#7d562d]">Thông tin tác giả</p>
              <h2 className="font-serif-display mt-1 text-3xl font-bold text-[#03192e]">{dangSua ? 'Sửa tác giả' : 'Thêm tác giả'}</h2>
            </div>
            <div className="flex gap-2">
              <Button disabled={luu.isPending}>{dangSua ? 'Cập nhật' : 'Thêm tác giả'}</Button>
              <Button type="button" kieu="phu" onClick={dongForm}>Hủy</Button>
            </div>
          </div>

          <div className="mt-5 grid gap-5 lg:grid-cols-[240px_1fr]">
            <div className="rounded border border-dashed border-[#c4c6cd] bg-[#fbf9f8] p-4">
              <div className="flex items-center justify-between gap-3">
                <span className="archival-label text-[#7d562d]">Ảnh đại diện</span>
                {anhFile ? <button type="button" onClick={() => chonAnh(undefined)} className="inline-flex h-8 w-8 items-center justify-center rounded bg-white text-[#7d562d] ring-1 ring-[#d8c6b4]" aria-label="Bỏ ảnh đã chọn"><X size={16} /></button> : null}
              </div>
              {anhPreview ? (
                <img src={anhPreview} alt="Ảnh tác giả" className="mt-3 aspect-square w-full rounded object-cover ring-1 ring-[#e4e2e2]" />
              ) : (
                <label className="mt-3 flex aspect-square cursor-pointer flex-col items-center justify-center rounded bg-white px-4 py-6 text-center text-sm text-[#74777d] ring-1 ring-[#e4e2e2] transition hover:bg-[#fff7ef]">
                  <ImagePlus className="mb-2 text-[#7d562d]" size={28} />
                  Chọn ảnh từ máy
                  <input ref={inputAnhRef} type="file" accept="image/png,image/jpeg,image/webp" className="sr-only" onChange={(event) => chonAnh(event.target.files?.[0])} />
                </label>
              )}
              {anhPreview ? (
                <label className="mt-3 inline-flex min-h-10 w-full cursor-pointer items-center justify-center rounded bg-white px-4 py-2 text-sm font-bold text-[#7d562d] ring-1 ring-[#d8c6b4] transition hover:bg-[#fff7ef]">
                  Chọn ảnh khác
                  <input ref={inputAnhRef} type="file" accept="image/png,image/jpeg,image/webp" className="sr-only" onChange={(event) => chonAnh(event.target.files?.[0])} />
                </label>
              ) : null}
            </div>

            <div className="grid content-start gap-3">
              <Input placeholder="Tên tác giả" {...register('ten', { required: true })} />
              <textarea className="textarea-paper min-h-40 px-3 py-2 text-sm" placeholder="Tiểu sử" {...register('tieuSu')} />
            </div>
          </div>
        </form>
      ) : null}

      {error ? <OTrong tieuDe="Không tải được tác giả" moTa={error.message} /> : (
        <>
          <Bang>
            <table className="w-full min-w-[820px] text-left text-sm">
              <thead className="du-lieu-heading">
                <tr>
                  <th className="px-4 py-3">Tác giả</th>
                  <th className="px-4 py-3">Tiểu sử</th>
                  <th className="px-4 py-3">Thao tác</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-[#ece6df]">
                {(data?.duLieu ?? []).map((tacGia) => (
                  <tr className="du-lieu-row align-top" key={tacGia.maTacGia}>
                    <td className="px-4 py-3">
                      <div className="flex items-center gap-3">
                        <div className="size-14 shrink-0 overflow-hidden rounded bg-[#f1e8df]">
                          {tacGia.anhDaiDien ? <img src={duongDanAnh(tacGia.anhDaiDien)} alt={tacGia.ten} className="h-full w-full object-cover" /> : null}
                        </div>
                        <p className="font-semibold text-[#03192e]">{tacGia.ten}</p>
                      </div>
                    </td>
                    <td className="max-w-xl px-4 py-3 text-[#43474d]">{tacGia.tieuSu || 'Chưa có'}</td>
                    <td className="px-4 py-3 text-right"><Button kieu="phu" onClick={() => suaTacGia(tacGia)}>Sửa</Button></td>
                  </tr>
                ))}
              </tbody>
            </table>
          </Bang>
          <div className="mt-4">
            <PhanTrang data={data} page={page} onPageChange={setPage} />
          </div>
        </>
      )}
    </div>
  );
}
