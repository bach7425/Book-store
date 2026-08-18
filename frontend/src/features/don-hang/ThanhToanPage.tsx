import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { MapPin, Ticket, X } from 'lucide-react';
import { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import { useNavigate } from 'react-router-dom';
import { donHangApi } from '../../api/donHangApi';
import { gioHangApi } from '../../api/gioHangApi';
import { maGiamGiaApi } from '../../api/maGiamGiaApi';
import { nguoiDungApi } from '../../api/nguoiDungApi';
import { OTrong } from '../../components/ui/Bang';
import { Button } from '../../components/ui/Button';
import { Input } from '../../components/ui/Input';
import { NutQuayLai } from '../../components/ui/NutQuayLai';
import { useToastStore } from '../../components/ui/toastStore';
import type { DiaChi, KiemTraMaGiamGiaResponse } from '../../types';
import { dinhDangTien } from '../../utils/dinhDang';

type FormDiaChi = Omit<DiaChi, 'maDiaChi'>;
const PHI_VAN_CHUYEN_MAC_DINH = 30000;

const diaChiMacDinh: FormDiaChi = {
  nguoiNhan: '',
  soDienThoai: '',
  diaChiChiTiet: '',
  macDinh: false,
};

export function ThanhToanPage() {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const baoTin = useToastStore((state) => state.baoTin);
  const baoLoi = useToastStore((state) => state.baoLoi);
  const { data: gioHang } = useQuery({ queryKey: ['gio-hang'], queryFn: gioHangApi.layGioHang });
  const { data: diaChi = [] } = useQuery({ queryKey: ['dia-chi'], queryFn: nguoiDungApi.layDanhSachDiaChi });
  const [maDiaChi, setMaDiaChi] = useState<number | undefined>();
  const [maGiamGia, setMaGiamGia] = useState('');
  const [maGiamGiaDaApDung, setMaGiamGiaDaApDung] = useState('');
  const [ketQuaMaGiamGia, setKetQuaMaGiamGia] = useState<KiemTraMaGiamGiaResponse | null>(null);
  const [phuongThucThanhToan, setPhuongThucThanhToan] = useState('TIEN_MAT');
  const { register, handleSubmit, reset } = useForm<FormDiaChi>({ defaultValues: diaChiMacDinh });
  const daDuBaDiaChi = diaChi.length >= 3;
  const maDiaChiDangChon = maDiaChi ?? diaChi.find((item) => item.macDinh)?.maDiaChi ?? diaChi[0]?.maDiaChi;
  const tamTinh = gioHang?.tongTien ?? 0;
  const soTienGiam = ketQuaMaGiamGia?.soTienGiam ?? 0;
  const phiVanChuyen = ketQuaMaGiamGia?.phiVanChuyen ?? PHI_VAN_CHUYEN_MAC_DINH;
  const tongThanhToan = ketQuaMaGiamGia?.tongTienThanhToan ?? Math.max(tamTinh - soTienGiam, 0) + phiVanChuyen;

  useEffect(() => {
    if (diaChi.length === 0) {
      setMaDiaChi(undefined);
      return;
    }
    if (!maDiaChi || !diaChi.some((item) => item.maDiaChi === maDiaChi)) {
      setMaDiaChi(diaChi.find((item) => item.macDinh)?.maDiaChi ?? diaChi[0].maDiaChi);
    }
  }, [diaChi, maDiaChi]);

  const themDiaChi = useMutation({
    mutationFn: (duLieu: FormDiaChi) => nguoiDungApi.themDiaChi({ ...duLieu, macDinh: diaChi.length === 0 || duLieu.macDinh }),
    onSuccess: (duLieu) => {
      queryClient.invalidateQueries({ queryKey: ['dia-chi'] });
      setMaDiaChi(duLieu.maDiaChi);
      reset(diaChiMacDinh);
      baoTin('Đã thêm địa chỉ nhận hàng');
    },
    onError: (err) => baoLoi(err.message),
  });
  const datDiaChiMacDinh = useMutation({
    mutationFn: (item: DiaChi) => nguoiDungApi.capNhatDiaChi(item.maDiaChi, { nguoiNhan: item.nguoiNhan, soDienThoai: item.soDienThoai, diaChiChiTiet: item.diaChiChiTiet, macDinh: true }),
    onSuccess: (duLieu) => {
      queryClient.invalidateQueries({ queryKey: ['dia-chi'] });
      setMaDiaChi(duLieu.maDiaChi);
      baoTin('Đã chọn và đặt địa chỉ mặc định');
    },
    onError: (err) => baoLoi(err.message),
  });
  const kiemTraMaGiamGia = useMutation({
    mutationFn: () => {
      const ma = maGiamGia.trim();
      if (!ma) throw new Error('Vui lòng nhập mã giảm giá');
      if (tamTinh <= 0) throw new Error('Giỏ hàng chưa có tổng tiền hợp lệ');
      return maGiamGiaApi.kiemTraMaGiamGia({ maGiamGia: ma, tongTien: tamTinh });
    },
    onSuccess: (duLieu) => {
      setKetQuaMaGiamGia(duLieu);
      setMaGiamGiaDaApDung(duLieu.maGiamGia);
      setMaGiamGia(duLieu.maGiamGia);
      baoTin(duLieu.thongBao || 'Đã áp dụng mã giảm giá');
    },
    onError: (err) => {
      setKetQuaMaGiamGia(null);
      setMaGiamGiaDaApDung('');
      baoLoi(err.message);
    },
  });
  const taoDon = useMutation({
    mutationFn: () => {
      if (!maDiaChiDangChon) throw new Error('Vui lòng thêm hoặc chọn địa chỉ nhận hàng');
      return donHangApi.taoDonHang({ maDiaChi: maDiaChiDangChon, phuongThucThanhToan, maGiamGia: maGiamGiaDaApDung || undefined });
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['gio-hang'] });
      queryClient.invalidateQueries({ queryKey: ['don-hang'] });
      baoTin('Tạo đơn hàng thành công');
      navigate('/don-hang');
    },
    onError: (err) => baoLoi(err.message),
  });

  const luuDiaChi = (duLieu: FormDiaChi) => {
    if (daDuBaDiaChi) {
      baoLoi('Bạn chỉ có thể thêm tối đa 3 địa chỉ');
      return;
    }
    themDiaChi.mutate(duLieu);
  };

  const chonDiaChiThanhToan = (item: DiaChi) => {
    setMaDiaChi(item.maDiaChi);
    if (!item.macDinh && !datDiaChiMacDinh.isPending) {
      datDiaChiMacDinh.mutate(item);
    }
  };

  const capNhatMaGiamGia = (giaTri: string) => {
    setMaGiamGia(giaTri);
    setKetQuaMaGiamGia(null);
    setMaGiamGiaDaApDung('');
  };

  const boMaGiamGia = () => {
    setMaGiamGia('');
    setKetQuaMaGiamGia(null);
    setMaGiamGiaDaApDung('');
  };

  useEffect(() => {
    if (ketQuaMaGiamGia && ketQuaMaGiamGia.tongTien !== tamTinh) {
      setKetQuaMaGiamGia(null);
      setMaGiamGiaDaApDung('');
    }
  }, [ketQuaMaGiamGia, tamTinh]);

  if ((gioHang?.items?.duLieu.length ?? 0) === 0) {
    return (
      <div className="mx-auto max-w-4xl px-4 py-8">
        <NutQuayLai veDau="/gio-hang" className="mb-6" />
        <OTrong tieuDe="Không có sản phẩm để thanh toán" />
      </div>
    );
  }

  return (
    <div className="mx-auto max-w-[1180px] px-4 py-10 md:px-10">
      <NutQuayLai veDau="/gio-hang" className="mb-6" />
      <div className="grid gap-8 lg:grid-cols-[1fr_380px]">
        <section className="space-y-6">
          <div className="paper-panel rounded p-6">
            <p className="archival-label text-[#7d562d]">Quầy thanh toán</p>
            <h1 className="font-serif-display mt-1 text-4xl font-bold text-[#03192e]">Thanh toán</h1>
            <div className="mt-5">
              <div className="flex items-center justify-between gap-3">
                <h2 className="font-serif-display text-2xl font-bold text-[#03192e]">Địa chỉ nhận hàng</h2>
                <span className="text-sm font-semibold text-[#43474d]">{diaChi.length}/3 địa chỉ</span>
              </div>
              {diaChi.length === 0 ? (
                <OTrong tieuDe="Chưa có địa chỉ nhận hàng" moTa="Thêm địa chỉ bên dưới để tiếp tục đặt hàng." />
              ) : (
                <div className="mt-4 grid gap-3">
                  {diaChi.map((item) => {
                    const dangChon = maDiaChiDangChon === item.maDiaChi;
                    return (
                      <div
                        key={item.maDiaChi}
                        role="button"
                        tabIndex={0}
                        onClick={() => chonDiaChiThanhToan(item)}
                        onKeyDown={(event) => {
                          if (event.key === 'Enter' || event.key === ' ') {
                            event.preventDefault();
                            chonDiaChiThanhToan(item);
                          }
                        }}
                        className={`rounded border p-4 text-left transition ${dangChon ? 'border-[#03192e] bg-[#f5f3f3] ring-2 ring-[#ffdcbd]' : 'border-[#c4c6cd] bg-white hover:border-[#7d562d]'}`}
                      >
                        <div className="flex items-start justify-between gap-4">
                          <div className="min-w-0">
                            <p className="font-bold text-[#03192e]">{item.nguoiNhan}</p>
                            <p className="mt-1 text-sm font-semibold text-[#43474d]">{item.soDienThoai}</p>
                            <p className="mt-2 flex items-start gap-2 text-sm leading-6 text-[#43474d]"><MapPin className="mt-1 shrink-0 text-[#7d562d]" size={16} /> {item.diaChiChiTiet}</p>
                          </div>
                          <div className="grid justify-items-end gap-2">
                            {item.macDinh ? <span className="rounded bg-[#e8f5e9] px-2 py-1 text-xs font-bold text-[#246b32]">Mặc định</span> : null}
                            <input
                              type="radio"
                              name="diaChiThanhToan"
                              value={item.maDiaChi}
                              checked={dangChon}
                              onClick={(event) => event.stopPropagation()}
                              onChange={() => chonDiaChiThanhToan(item)}
                              className="h-5 w-5 accent-[#03192e]"
                              aria-label={`Chọn địa chỉ của ${item.nguoiNhan}`}
                            />
                          </div>
                        </div>
                      </div>
                    );
                  })}
                </div>
              )}
            </div>
            <label className="mt-5 block text-sm font-semibold">
              Phương thức thanh toán
              <select className="mt-2 min-h-10 w-full rounded border border-[#c4c6cd] bg-[#fbf9f8] px-3" value={phuongThucThanhToan} onChange={(event) => setPhuongThucThanhToan(event.target.value)}>
              <option value="TIEN_MAT">Thanh toán khi nhận hàng</option>
                <option value="CHUYEN_KHOAN">Chuyển khoản</option>
              </select>
            </label>
            <label className="mt-4 block text-sm font-semibold">
              Mã giảm giá
              <div className="mt-2 grid gap-2 sm:grid-cols-[1fr_116px]">
                <div className="relative">
                  <Ticket className="absolute left-3 top-1/2 -translate-y-1/2 text-[#7d562d]" size={17} />
                  <Input className="pl-10 pr-10 uppercase" value={maGiamGia} onChange={(event) => capNhatMaGiamGia(event.target.value)} placeholder="Nhập mã nếu có" />
                  {maGiamGia ? (
                    <button type="button" onClick={boMaGiamGia} className="absolute right-2 top-1/2 -translate-y-1/2 rounded p-1 text-[#74777d] hover:bg-[#efeded] hover:text-[#03192e]" aria-label="Bỏ mã giảm giá">
                      <X size={15} />
                    </button>
                  ) : null}
                </div>
                <Button type="button" kieu="phu" className="w-full px-3" onClick={() => kiemTraMaGiamGia.mutate()} disabled={kiemTraMaGiamGia.isPending || tamTinh <= 0 || !maGiamGia.trim()}>
                  {kiemTraMaGiamGia.isPending ? 'Đang áp...' : 'Áp dụng'}
                </Button>
              </div>
              {maGiamGiaDaApDung ? <span className="mt-2 block text-xs font-bold text-[#246b32]">Đã áp dụng mã {maGiamGiaDaApDung}</span> : null}
            </label>
          </div>

          {!daDuBaDiaChi ? <form onSubmit={handleSubmit(luuDiaChi)} className="paper-panel rounded p-6">
            <h2 className="font-serif-display text-2xl font-bold text-[#03192e]">Thêm địa chỉ mới</h2>
            <div className="mt-4 grid gap-3 md:grid-cols-2">
              <Input placeholder="Người nhận" {...register('nguoiNhan', { required: true })} />
              <Input placeholder="Số điện thoại" {...register('soDienThoai', { required: true })} />
              <Input className="md:col-span-2" placeholder="Địa chỉ chi tiết" {...register('diaChiChiTiet', { required: true })} />
            </div>
            <label className="mt-4 flex items-center gap-2 text-sm font-bold text-[#03192e]">
              <input type="checkbox" className="h-4 w-4 accent-[#03192e]" {...register('macDinh')} />
              Đặt làm địa chỉ mặc định
            </label>
            <Button className="mt-4" disabled={daDuBaDiaChi || themDiaChi.isPending}>Thêm địa chỉ</Button>
          </form> : null}
        </section>

        <aside className="paper-panel h-fit rounded p-5">
          <h2 className="font-serif-display text-2xl font-bold text-[#03192e]">Đơn hàng</h2>
          <div className="mt-4 divide-y divide-stone-100">
            {gioHang?.items.duLieu.map((item) => <div key={item.maChiTietGioHang} className="flex justify-between gap-4 py-3 text-sm"><span>{item.tenSach} x {item.soLuong}</span><strong>{dinhDangTien(item.donGia * item.soLuong)}</strong></div>)}
          </div>
          <div className="mt-4 space-y-3 border-t border-[#e4e2e2] pt-4 text-sm">
            <div className="flex justify-between"><span>Tạm tính</span><strong>{dinhDangTien(tamTinh)}</strong></div>
            <div className="flex justify-between"><span>Phí vận chuyển</span><strong>{dinhDangTien(phiVanChuyen)}</strong></div>
            {soTienGiam > 0 ? <div className="flex justify-between"><span>Giảm giá</span><strong className="text-[#246b32]">-{dinhDangTien(soTienGiam)}</strong></div> : null}
          </div>
          <div className="mt-4 flex justify-between text-lg"><span>Tổng thanh toán</span><strong className="font-mono-label text-[#7d562d]">{dinhDangTien(tongThanhToan)}</strong></div>
          {!maDiaChiDangChon ? <p className="mt-4 rounded border border-[#ffb4ab] bg-[#ffdad6] px-3 py-2 text-sm font-semibold text-[#93000a]">Vui lòng thêm hoặc chọn địa chỉ nhận hàng.</p> : null}
          <Button className="mt-5 w-full" disabled={!maDiaChiDangChon || taoDon.isPending} onClick={() => taoDon.mutate()}>Đặt hàng</Button>
        </aside>
      </div>
    </div>
  );
}
