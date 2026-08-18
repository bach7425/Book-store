import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { quanTriApi } from '../../api/quanTriApi';
import { Bang } from '../../components/ui/Bang';
import { Button } from '../../components/ui/Button';
import { Input } from '../../components/ui/Input';
import { PhanTrang } from '../../components/ui/PhanTrang';
import { TrangThai } from '../../components/ui/TrangThai';
import { useToastStore } from '../../components/ui/toastStore';
import type { MaGiamGia } from '../../types';
import { dinhDangNgay, dinhDangTien } from '../../utils/dinhDang';

type FormMaGiamGia = {
  maCode: string;
  loaiGiam: 'PHAN_TRAM' | 'SO_TIEN';
  giaTri: number;
  giamToiDa?: number;
  donToiThieu: number;
  soLuong: number;
  ngayBatDau: string;
  ngayKetThuc: string;
};

const giaTriMacDinh: FormMaGiamGia = {
  maCode: '',
  loaiGiam: 'PHAN_TRAM',
  giaTri: 0,
  giamToiDa: undefined,
  donToiThieu: 0,
  soLuong: 1,
  ngayBatDau: '',
  ngayKetThuc: '',
};

const trangThaiLocOptions = ['HOAT_DONG', 'HET_HAN', 'NGUNG'];

function toDatetimeLocal(giaTri?: string) {
  if (!giaTri) return '';
  return giaTri.slice(0, 16);
}

function taoPayload(form: FormMaGiamGia): Partial<MaGiamGia> {
  return {
    maCode: form.maCode.trim(),
    loaiGiam: form.loaiGiam,
    giaTri: form.giaTri,
    giamToiDa: form.giamToiDa || undefined,
    donToiThieu: form.donToiThieu,
    soLuong: form.soLuong,
    ngayBatDau: form.ngayBatDau,
    ngayKetThuc: form.ngayKetThuc,
  };
}

function ngayKetThucHopLe(form: FormMaGiamGia) {
  return Boolean(
    form.ngayBatDau &&
    form.ngayKetThuc &&
    new Date(form.ngayKetThuc).getTime() > new Date(form.ngayBatDau).getTime(),
  );
}

function hienThiLoaiGiam(item: MaGiamGia) {
  const loaiGiam = item.loaiGiam ?? item.loaiGiamGia;
  const giaTri = item.giaTri ?? item.giaTriGiam ?? 0;
  if (loaiGiam === 'PHAN_TRAM') return `${giaTri}%`;
  return dinhDangTien(giaTri);
}

function tenTrangThaiMa(trangThai: string) {
  if (trangThai === 'NGUNG') return 'Ngưng';
  if (trangThai === 'HET_HAN') return 'Hết hạn';
  return 'Hoạt động';
}

export function QuanTriMaGiamGiaPage() {
  const [hienFormMaGiamGia, setHienFormMaGiamGia] = useState(false);
  const [maDangSua, setMaDangSua] = useState<number | null>(null);
  const [trangThai, setTrangThai] = useState('');
  const [page, setPage] = useState(0);
  const queryClient = useQueryClient();
  const baoTin = useToastStore((state) => state.baoTin);
  const baoLoi = useToastStore((state) => state.baoLoi);
  const { register, handleSubmit, reset, watch } = useForm<FormMaGiamGia>({ defaultValues: giaTriMacDinh });
  const loaiGiam = watch('loaiGiam');
  const { data } = useQuery({
    queryKey: ['quan-tri', 'ma-giam-gia', trangThai, page],
    queryFn: () => quanTriApi.layMaGiamGia({ trangThai: trangThai || undefined, page }),
  });

  const lamMoi = () => queryClient.invalidateQueries({ queryKey: ['quan-tri', 'ma-giam-gia'] });

  const dongForm = () => {
    setMaDangSua(null);
    setHienFormMaGiamGia(false);
    reset(giaTriMacDinh);
  };

  const themMaMoi = () => {
    setMaDangSua(null);
    reset(giaTriMacDinh);
    setHienFormMaGiamGia(true);
  };

  const luu = useMutation({
    mutationFn: (form: FormMaGiamGia) => {
      if (!ngayKetThucHopLe(form)) {
        throw new Error('Ngày kết thúc phải sau ngày bắt đầu');
      }
      const payload = taoPayload(form);
      return maDangSua ? quanTriApi.capNhatMaGiamGia(maDangSua, payload) : quanTriApi.taoMaGiamGia(payload);
    },
    onSuccess: () => {
      lamMoi();
      const dangCapNhat = Boolean(maDangSua);
      dongForm();
      baoTin(dangCapNhat ? 'Đã cập nhật mã giảm giá' : 'Đã tạo mã giảm giá');
    },
    onError: (err) => baoLoi(err.message),
  });

  const ngungHoatDong = useMutation({
    mutationFn: (maGiamGia: number) => quanTriApi.xoaMaGiamGia(maGiamGia),
    onSuccess: () => {
      lamMoi();
      baoTin('Mã giảm giá đã ngưng hoạt động');
    },
    onError: (err) => baoLoi(err.message),
  });

  const kichHoat = useMutation({
    mutationFn: (maGiamGia: number) => quanTriApi.capNhatMaGiamGia(maGiamGia, { trangThai: 'HOAT_DONG' }),
    onSuccess: () => {
      lamMoi();
      baoTin('Đã kích hoạt mã giảm giá');
    },
    onError: (err) => baoLoi(err.message),
  });

  const suaMa = (item: MaGiamGia) => {
    setMaDangSua(item.maGiamGia ?? null);
    setHienFormMaGiamGia(true);
    reset({
      maCode: item.maCode ?? item.code ?? '',
      loaiGiam: (item.loaiGiam ?? item.loaiGiamGia ?? 'PHAN_TRAM') as FormMaGiamGia['loaiGiam'],
      giaTri: item.giaTri ?? item.giaTriGiam ?? 0,
      giamToiDa: item.giamToiDa,
      donToiThieu: item.donToiThieu ?? item.donHangToiThieu ?? 0,
      soLuong: item.soLuong ?? 1,
      ngayBatDau: toDatetimeLocal(item.ngayBatDau),
      ngayKetThuc: toDatetimeLocal(item.ngayKetThuc),
    });
  };

  return (
    <div className="grid gap-6">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <p className="archival-label text-[#7d562d]">Khuyến mãi</p>
          <h1 className="font-serif-display mt-1 text-3xl font-bold text-[#03192e]">Mã khuyến mãi</h1>
        </div>
        <div className="flex flex-wrap gap-2">
          <select className="select-paper px-3 py-2" value={trangThai} onChange={(event) => { setTrangThai(event.target.value); setPage(0); }}>
            <option value="">Tất cả</option>
            {trangThaiLocOptions.map((item) => <option key={item} value={item}>{tenTrangThaiMa(item)}</option>)}
          </select>
          <Button type="button" onClick={themMaMoi}>Thêm mã</Button>
        </div>
      </div>

      {hienFormMaGiamGia ? (
        <form onSubmit={handleSubmit((form) => luu.mutate(form))} className="paper-panel rounded p-5">
          <div className="flex flex-wrap items-start justify-between gap-4">
            <div>
              <p className="archival-label text-[#7d562d]">Thiết lập khuyến mãi</p>
              <h2 className="font-serif-display mt-1 text-3xl font-bold text-[#03192e]">{maDangSua ? 'Sửa mã giảm giá' : 'Tạo mã giảm giá'}</h2>
            </div>
            <div className="flex gap-2">
              <Button disabled={luu.isPending}>{maDangSua ? 'Cập nhật mã' : 'Tạo mã'}</Button>
              <Button type="button" kieu="phu" onClick={dongForm}>Hủy</Button>
            </div>
          </div>
          <div className="mt-4 grid gap-3 md:grid-cols-2 xl:grid-cols-3">
            <label className="text-sm font-semibold text-[#03192e]">Mã code<Input className="mt-2" placeholder="VD: SUMMER25" {...register('maCode', { required: true })} /></label>
            <label className="text-sm font-semibold text-[#03192e]">Loại giảm
              <select className="select-paper mt-2 w-full px-3 py-2" {...register('loaiGiam', { required: true })}>
                <option value="PHAN_TRAM">Giảm theo phần trăm</option>
                <option value="SO_TIEN">Giảm số tiền</option>
              </select>
            </label>
            <label className="text-sm font-semibold text-[#03192e]">Giá trị giảm<Input className="mt-2" type="number" min={1} placeholder={loaiGiam === 'PHAN_TRAM' ? 'VD: 10' : 'VD: 50000'} {...register('giaTri', { required: true, valueAsNumber: true })} /></label>
            <label className="text-sm font-semibold text-[#03192e]">Giảm tối đa<Input className="mt-2" type="number" min={0} placeholder="Chỉ cần cho mã phần trăm" {...register('giamToiDa', { valueAsNumber: true })} /></label>
            <label className="text-sm font-semibold text-[#03192e]">Đơn tối thiểu<Input className="mt-2" type="number" min={0} {...register('donToiThieu', { required: true, valueAsNumber: true })} /></label>
            <label className="text-sm font-semibold text-[#03192e]">Số lượng<Input className="mt-2" type="number" min={0} {...register('soLuong', { required: true, valueAsNumber: true })} /></label>
            <label className="text-sm font-semibold text-[#03192e]">Ngày bắt đầu<Input className="mt-2" type="datetime-local" {...register('ngayBatDau', { required: true })} /></label>
            <label className="text-sm font-semibold text-[#03192e]">Ngày kết thúc<Input className="mt-2" type="datetime-local" {...register('ngayKetThuc', { required: true })} /></label>
          </div>
        </form>
      ) : null}

      <Bang>
        <table className="w-full min-w-[1120px] text-left text-sm">
          <thead className="du-lieu-heading">
            <tr>
              <th className="px-4 py-3">Mã</th>
              <th className="px-4 py-3">Giảm</th>
              <th className="px-4 py-3">Điều kiện</th>
              <th className="px-4 py-3">Số lượng</th>
              <th className="px-4 py-3">Thời hạn</th>
              <th className="px-4 py-3">Trạng thái</th>
              <th className="px-4 py-3">Thao tác</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-[#ece6df]">
            {(data?.duLieu ?? []).map((item, index) => (
              <tr className="du-lieu-row align-top" key={item.maGiamGia ?? index}>
                <td className="px-4 py-3"><span className="font-mono-label font-semibold text-[#03192e]">{item.maCode ?? item.code}</span></td>
                <td className="px-4 py-3 text-[#43474d]">
                  <p className="font-semibold text-[#03192e]">{hienThiLoaiGiam(item)}</p>
                  {item.giamToiDa ? <p className="mt-1 text-xs text-[#74777d]">Tối đa {dinhDangTien(item.giamToiDa)}</p> : null}
                </td>
                <td className="font-mono-label px-4 py-3 text-[#7d562d]">{dinhDangTien(item.donToiThieu ?? item.donHangToiThieu)}</td>
                <td className="px-4 py-3 text-[#43474d]">{item.soLuongDaDung ?? 0}/{item.soLuong ?? 0}</td>
                <td className="px-4 py-3 text-xs font-semibold text-[#43474d]"><p>{dinhDangNgay(item.ngayBatDau)}</p><p className="mt-1">{dinhDangNgay(item.ngayKetThuc)}</p></td>
                <td className="px-4 py-3"><TrangThai giaTri={item.trangThai} /></td>
                <td className="px-4 py-3">
                  <div className="flex flex-wrap justify-end gap-2">
                    <Button kieu="phu" onClick={() => suaMa(item)}>Sửa</Button>
                    {item.trangThai === 'HOAT_DONG' && item.maGiamGia ? (
                      <Button kieu="canh-bao" disabled={ngungHoatDong.isPending} onClick={() => ngungHoatDong.mutate(item.maGiamGia!)}>
                        Ngưng
                      </Button>
                    ) : item.maGiamGia ? (
                      <Button disabled={kichHoat.isPending} onClick={() => kichHoat.mutate(item.maGiamGia!)}>
                        Kích hoạt
                      </Button>
                    ) : (
                      <span className="text-sm font-semibold text-[#74777d]">Không còn thao tác</span>
                    )}
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </Bang>
      <div className="mt-4">
        <PhanTrang data={data} page={page} onPageChange={setPage} />
      </div>
    </div>
  );
}
