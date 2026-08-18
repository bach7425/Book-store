import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { ImagePlus, X } from 'lucide-react';
import { useEffect, useRef, useState } from 'react';
import { useForm } from 'react-hook-form';
import { quanTriApi } from '../../api/quanTriApi';
import { sachApi } from '../../api/sachApi';
import { Bang, OTrong } from '../../components/ui/Bang';
import { Button } from '../../components/ui/Button';
import { Input } from '../../components/ui/Input';
import { PhanTrang } from '../../components/ui/PhanTrang';
import { useToastStore } from '../../components/ui/toastStore';
import type { Sach, TheLoai } from '../../types';
import { dinhDangTien, duongDanAnh } from '../../utils/dinhDang';

type FormSach = { tenSach: string; moTa?: string; gia: number | ''; nhaXuatBan?: string; maTacGia?: number; soLuongTon?: number | '' };

const lopTagTheLoai = 'inline-flex items-center gap-2 rounded bg-[#d8efe1] px-3 py-1 text-sm font-bold text-[#14532d]';
const formSachRong: FormSach = { tenSach: '', moTa: '', gia: '', nhaXuatBan: '', maTacGia: undefined, soLuongTon: '' };

export function QuanTriSachPage() {
  const queryClient = useQueryClient();
  const baoTin = useToastStore((state) => state.baoTin);
  const baoLoi = useToastStore((state) => state.baoLoi);
  const [hienFormSach, setHienFormSach] = useState(false);
  const [dangSua, setDangSua] = useState<Sach | null>(null);
  const [anhBiaFile, setAnhBiaFile] = useState<File | null>(null);
  const [anhBiaPreview, setAnhBiaPreview] = useState<string | null>(null);
  const [tenTacGia, setTenTacGia] = useState('');
  const [moDanhSachTacGia, setMoDanhSachTacGia] = useState(false);
  const [theLoaiDaChon, setTheLoaiDaChon] = useState<TheLoai[]>([]);
  const [tuKhoaTheLoai, setTuKhoaTheLoai] = useState('');
  const [moDanhSachTheLoai, setMoDanhSachTheLoai] = useState(false);
  const [page, setPage] = useState(0);
  const anhBiaInputRef = useRef<HTMLInputElement | null>(null);
  const { register, handleSubmit, reset, setValue } = useForm<FormSach>({ defaultValues: formSachRong });
  const { data, error } = useQuery({ queryKey: ['quan-tri', 'sach', page], queryFn: () => sachApi.layDanhSachSach({ page, size: 10 }) });
  const { data: tacGia, isLoading: dangTaiTacGia } = useQuery({ queryKey: ['tac-gia'], queryFn: () => sachApi.layDanhSachTacGia() });
  const { data: theLoai, isLoading: dangTaiTheLoai } = useQuery({ queryKey: ['the-loai'], queryFn: sachApi.layDanhSachTheLoai });

  const timTacGiaTheoTen = (ten: string) => tacGia?.duLieu.find((item) => item.ten.trim().toLowerCase() === ten.trim().toLowerCase());
  const tacGiaGoiY = (tacGia?.duLieu ?? []).filter((item) => item.ten.toLowerCase().includes(tenTacGia.trim().toLowerCase()));
  const theLoaiGoiY = (theLoai ?? []).filter((item) => {
    const daChon = theLoaiDaChon.some((daChon) => daChon.maTheLoai === item.maTheLoai);
    return !daChon && item.ten.toLowerCase().includes(tuKhoaTheLoai.trim().toLowerCase());
  });

  const datAnhBiaPreview = (preview: string | null) => {
    setAnhBiaPreview((anhCu) => {
      if (anhCu?.startsWith('blob:')) URL.revokeObjectURL(anhCu);
      return preview;
    });
  };

  const xoaAnhBiaFile = () => {
    setAnhBiaFile(null);
    datAnhBiaPreview(null);
    if (anhBiaInputRef.current) anhBiaInputRef.current.value = '';
  };

  const xoaForm = () => {
    reset(formSachRong);
    setDangSua(null);
    setTenTacGia('');
    setMoDanhSachTacGia(false);
    setTheLoaiDaChon([]);
    setTuKhoaTheLoai('');
    setMoDanhSachTheLoai(false);
    setHienFormSach(false);
    xoaAnhBiaFile();
  };

  const themSachMoi = () => {
    xoaForm();
    setHienFormSach(true);
  };

  const chonAnhBiaFile = (file?: File) => {
    if (anhBiaInputRef.current) anhBiaInputRef.current.value = '';
    setAnhBiaFile(file ?? null);
    datAnhBiaPreview(file ? URL.createObjectURL(file) : null);
  };

  const suaSach = (sach: Sach) => {
    setDangSua(sach);
    setHienFormSach(true);
    setAnhBiaFile(null);
    setTenTacGia(sach.tacGia?.ten ?? '');
    setMoDanhSachTacGia(false);
    setTheLoaiDaChon(sach.theLoais ?? []);
    setTuKhoaTheLoai('');
    setMoDanhSachTheLoai(false);
    datAnhBiaPreview(sach.anhBia ? duongDanAnh(sach.anhBia) : null);
    if (anhBiaInputRef.current) anhBiaInputRef.current.value = '';
    reset({
      tenSach: sach.tenSach,
      moTa: sach.moTa,
      gia: sach.gia,
      nhaXuatBan: sach.nhaXuatBan,
      maTacGia: sach.tacGia?.maTacGia,
      soLuongTon: sach.soLuongTon,
    });
  };

  useEffect(() => () => {
    if (anhBiaPreview?.startsWith('blob:')) URL.revokeObjectURL(anhBiaPreview);
  }, [anhBiaPreview]);

  const luuSach = useMutation({
    mutationFn: async (form: FormSach) => {
      const duLieu = { ...form, gia: Number(form.gia), soLuongTon: Number(form.soLuongTon || 0), maTacGia: Number(form.maTacGia || 0), maTheLoai: theLoaiDaChon.map((item) => item.maTheLoai) };
      const sachDaLuu = dangSua ? await quanTriApi.capNhatSach(dangSua.maSach, duLieu) : await quanTriApi.themSach(duLieu);
      return anhBiaFile ? quanTriApi.capNhatAnhBia(sachDaLuu.maSach, anhBiaFile) : sachDaLuu;
    },
    onSuccess: () => {
      xoaForm();
      baoTin('Đã lưu sách');
    },
    onSettled: () => {
      queryClient.refetchQueries({ queryKey: ['quan-tri', 'sach'] });
      queryClient.invalidateQueries({ queryKey: ['sach'] });
    },
    onError: (err) => baoLoi(err.message),
  });

  const xuLyLuuSach = (form: FormSach) => {
    const gia = Number(form.gia);
    const soLuongTon = Number(form.soLuongTon || 0);
    if (gia < 0) return baoLoi('Giá sách không được âm');
    if (soLuongTon < 0) return baoLoi('Tồn kho không được âm');
    const tacGiaDaChon = timTacGiaTheoTen(tenTacGia);
    if (!tacGiaDaChon) return baoLoi('Vui lòng chọn tác giả trong danh sách gợi ý');
    luuSach.mutate({ ...form, maTacGia: tacGiaDaChon.maTacGia });
  };

  const themTheLoai = (item: TheLoai) => {
    setTheLoaiDaChon((danhSach) => danhSach.some((daChon) => daChon.maTheLoai === item.maTheLoai) ? danhSach : [...danhSach, item]);
    setTuKhoaTheLoai('');
    setMoDanhSachTheLoai(false);
  };

  const xoaTheLoai = (maTheLoai: number) => setTheLoaiDaChon((danhSach) => danhSach.filter((item) => item.maTheLoai !== maTheLoai));

  const hienThiTheLoai = (danhSach?: TheLoai[]) => {
    if (!danhSach?.length) return <span className="text-sm text-[#74777d]">Chưa có</span>;
    return <div className="flex max-w-xs flex-wrap gap-2">{danhSach.map((item) => <span key={item.maTheLoai} className={lopTagTheLoai}>{item.ten}</span>)}</div>;
  };

  return (
    <div className="grid gap-6">
      <section>
        <div className="mb-4 flex flex-wrap items-center justify-between gap-3">
          <div>
            <p className="archival-label text-[#7d562d]">Quản lý mặt hàng</p>
            <h1 className="font-serif-display mt-1 text-3xl font-bold text-[#03192e]">Danh mục sách</h1>
          </div>
          <Button type="button" onClick={themSachMoi}>Thêm sách</Button>
        </div>
      </section>

      {hienFormSach ? (
        <form onSubmit={handleSubmit(xuLyLuuSach)} className="paper-panel rounded p-5">
          <div className="flex flex-wrap items-start justify-between gap-4">
            <div>
              <p className="archival-label text-[#7d562d]">Kho sách</p>
              <h2 className="font-serif-display mt-1 text-3xl font-bold text-[#03192e]">{dangSua ? 'Sửa sách' : 'Thêm sách'}</h2>
            </div>
            <div className="flex gap-2">
              <Button disabled={luuSach.isPending}>Lưu sách</Button>
              <Button type="button" kieu="phu" onClick={xoaForm}>Hủy</Button>
            </div>
          </div>

          <div className="mt-5 grid gap-5 lg:grid-cols-[300px_1fr]">
            <div className="rounded border border-dashed border-[#c4c6cd] bg-[#fbf9f8] p-4">
              <div className="flex items-center justify-between gap-3">
                <span className="archival-label text-[#7d562d]">Ảnh bìa</span>
                {anhBiaFile ? <button type="button" onClick={xoaAnhBiaFile} className="inline-flex h-8 w-8 items-center justify-center rounded bg-white text-[#7d562d] ring-1 ring-[#d8c6b4] transition hover:bg-[#fff7ef]" aria-label="Bỏ ảnh đã chọn"><X size={16} /></button> : null}
              </div>
              {anhBiaPreview ? (
                <div className="mt-3 overflow-hidden rounded bg-white ring-1 ring-[#e4e2e2]">
                  <img src={anhBiaPreview} alt="Xem trước ảnh bìa" className="aspect-[2/3] w-full object-cover" />
                </div>
              ) : (
                <label className="mt-3 flex aspect-[2/3] cursor-pointer flex-col items-center justify-center rounded bg-white px-4 py-6 text-center text-sm text-[#74777d] ring-1 ring-[#e4e2e2] transition hover:bg-[#fff7ef]">
                  <ImagePlus className="mb-2 text-[#7d562d]" size={28} />
                  Chọn ảnh bìa từ máy
                  <input ref={anhBiaInputRef} type="file" accept="image/png,image/jpeg,image/webp" className="sr-only" onChange={(event) => chonAnhBiaFile(event.target.files?.[0])} />
                </label>
              )}
              {anhBiaPreview ? (
                <label className="mt-3 inline-flex min-h-10 w-full cursor-pointer items-center justify-center rounded bg-white px-4 py-2 text-sm font-bold text-[#7d562d] ring-1 ring-[#d8c6b4] transition hover:bg-[#fff7ef]">
                  Chọn ảnh khác
                  <input ref={anhBiaInputRef} type="file" accept="image/png,image/jpeg,image/webp" className="sr-only" onChange={(event) => chonAnhBiaFile(event.target.files?.[0])} />
                </label>
              ) : null}
              <p className="mt-2 text-xs text-[#74777d]">{anhBiaFile ? `File đã chọn: ${anhBiaFile.name}` : dangSua?.anhBia ? 'Ảnh bìa hiện tại của sách.' : 'Hỗ trợ PNG, JPG hoặc WEBP.'}</p>
            </div>

            <div className="grid content-start gap-3 md:grid-cols-2">
              <Input className="md:col-span-2" placeholder="Tên sách" {...register('tenSach', { required: true })} />
              <Input placeholder="Giá" type="number" min={0} {...register('gia', { required: true, min: 0 })} />
              <Input placeholder="Số lượng tồn" type="number" min={0} {...register('soLuongTon', { min: 0 })} />
              <Input placeholder="Nhà xuất bản" {...register('nhaXuatBan')} />
              <div className="relative">
                <Input
                  placeholder={dangTaiTacGia ? 'Đang tải tác giả' : 'Gõ tên tác giả'}
                  value={tenTacGia}
                  disabled={dangTaiTacGia}
                  autoComplete="off"
                  onFocus={() => setMoDanhSachTacGia(true)}
                  onChange={(event) => {
                    const ten = event.target.value;
                    const tacGiaDaChon = timTacGiaTheoTen(ten);
                    setTenTacGia(ten);
                    setMoDanhSachTacGia(true);
                    setValue('maTacGia', tacGiaDaChon?.maTacGia);
                  }}
                />
                <input type="hidden" {...register('maTacGia')} />
                {moDanhSachTacGia ? (
                  <div className="absolute left-0 right-0 top-[calc(100%+4px)] z-20 max-h-52 overflow-y-auto rounded border border-[#c4c6cd] bg-white shadow-[0_16px_32px_rgba(26,46,68,0.12)]">
                    {tacGiaGoiY.length === 0 ? <p className="px-3 py-2 text-sm text-[#74777d]">Không tìm thấy tác giả</p> : tacGiaGoiY.map((item) => (
                      <button key={item.maTacGia} type="button" className="block w-full px-3 py-2 text-left text-sm font-semibold text-[#03192e] transition hover:bg-[#fff7ef]" onClick={() => { setTenTacGia(item.ten); setValue('maTacGia', item.maTacGia); setMoDanhSachTacGia(false); }}>
                        {item.ten}
                      </button>
                    ))}
                  </div>
                ) : null}
              </div>
              <label className="grid gap-2 text-sm font-semibold text-[#03192e] md:col-span-2">
                Thể loại
                <div className="relative">
                  <Input
                    placeholder={dangTaiTheLoai ? 'Đang tải thể loại' : 'Gõ tên thể loại'}
                    value={tuKhoaTheLoai}
                    disabled={dangTaiTheLoai}
                    autoComplete="off"
                    onFocus={() => setMoDanhSachTheLoai(true)}
                    onChange={(event) => { setTuKhoaTheLoai(event.target.value); setMoDanhSachTheLoai(true); }}
                  />
                  {moDanhSachTheLoai ? (
                    <div className="absolute left-0 right-0 top-[calc(100%+4px)] z-10 max-h-52 overflow-y-auto rounded border border-[#c4c6cd] bg-white shadow-[0_16px_32px_rgba(26,46,68,0.12)]">
                      {theLoaiGoiY.length === 0 ? <p className="px-3 py-2 text-sm font-normal text-[#74777d]">Không tìm thấy thể loại</p> : theLoaiGoiY.map((item) => (
                        <button key={item.maTheLoai} type="button" className="block w-full px-3 py-2 text-left text-sm font-semibold text-[#03192e] transition hover:bg-[#fff7ef]" onClick={() => themTheLoai(item)}>
                          {item.ten}
                        </button>
                      ))}
                    </div>
                  ) : null}
                </div>
                <div className="flex min-h-10 flex-wrap gap-2 rounded border border-[#e4e2e2] bg-[#fbf9f8] p-2">
                  {theLoaiDaChon.length === 0 ? <span className="text-sm font-normal text-[#74777d]">Chưa chọn thể loại</span> : theLoaiDaChon.map((item) => (
                    <span key={item.maTheLoai} className={lopTagTheLoai}>
                      {item.ten}
                      <button type="button" onClick={() => xoaTheLoai(item.maTheLoai)} className="rounded text-[#7d562d] hover:text-[#93000a]" aria-label={`Bỏ thể loại ${item.ten}`}>
                        <X size={14} />
                      </button>
                    </span>
                  ))}
                </div>
              </label>
              <textarea className="textarea-paper min-h-32 px-3 py-2 text-sm md:col-span-2" placeholder="Mô tả" {...register('moTa')} />
            </div>
          </div>
        </form>
      ) : null}

      <section>
        {error ? <OTrong tieuDe="Không tải được sách" moTa={error.message} /> : (
          <Bang>
            <table className="w-full min-w-[980px] text-left text-sm">
              <thead className="du-lieu-heading">
                <tr><th className="px-4 py-3">Sách</th><th className="px-4 py-3">Thể loại</th><th className="px-4 py-3">Giá</th><th className="px-4 py-3">Tồn kho</th><th className="px-4 py-3"></th></tr>
              </thead>
              <tbody className="divide-y divide-[#ece6df]">
                {(data?.duLieu ?? []).map((sach) => (
                  <tr className="du-lieu-row" key={sach.maSach}>
                    <td className="px-4 py-3">
                      <div className="flex items-center gap-3">
                        <div className="book-shadow h-20 w-14 shrink-0 overflow-hidden rounded bg-[#03192e]">{sach.anhBia ? <img src={duongDanAnh(sach.anhBia)} alt={sach.tenSach} className="h-full w-full object-cover" /> : <div className="grid h-full w-full place-items-center p-2 text-center"><span className="font-serif-display text-xs font-bold leading-tight text-white">{sach.tenSach}</span></div>}</div>
                        <div className="min-w-0 font-semibold text-[#03192e]"><span className="block truncate">{sach.tenSach}</span><span className="block truncate font-normal text-[#43474d]">{sach.tacGia?.ten}</span></div>
                      </div>
                    </td>
                    <td className="px-4 py-3">{hienThiTheLoai(sach.theLoais)}</td>
                    <td className="font-mono-label px-4 py-3 text-[#7d562d]">{dinhDangTien(sach.gia)}</td>
                    <td className="font-mono-label px-4 py-3 text-[#03192e]">{sach.soLuongTon ?? 0}</td>
                    <td className="px-4 py-3 text-right"><Button kieu="phu" className="bg-[#fff7ef]" onClick={() => suaSach(sach)}>Sửa</Button></td>
                  </tr>
                ))}
              </tbody>
            </table>
          </Bang>
        )}
        <div className="mt-4">
          <PhanTrang data={data} page={page} onPageChange={setPage} />
        </div>
      </section>
    </div>
  );
}
