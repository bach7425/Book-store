import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { Heart, LibraryBig, ShoppingBag, Star } from 'lucide-react';
import { useEffect, useMemo, useState } from 'react';
import type { FormEvent } from 'react';
import { useParams } from 'react-router-dom';
import { gioHangApi } from '../../api/gioHangApi';
import { sachApi } from '../../api/sachApi';
import { DangTai, OTrong } from '../../components/ui/Bang';
import { AvatarNguoiDung } from '../../components/ui/AvatarNguoiDung';
import { Button } from '../../components/ui/Button';
import { NutQuayLai } from '../../components/ui/NutQuayLai';
import { TrangThai } from '../../components/ui/TrangThai';
import { useToastStore } from '../../components/ui/toastStore';
import type { DanhGia } from '../../types';
import { dinhDangTien, duongDanAnh } from '../../utils/dinhDang';
import { chuanHoaVaiTro } from '../../utils/vaiTro';
import { useXacThucStore } from '../xac-thuc/xacThucStore';
import { useMaSachYeuThich } from './useMaSachYeuThich';

function DanhGiaSao({ soSao, onChange, disabled = false }: { soSao: number; onChange?: (soSao: number) => void; disabled?: boolean }) {
  return (
    <div className="flex items-center gap-1">
      {[1, 2, 3, 4, 5].map((sao) => {
        const dangChon = sao <= soSao;
        return (
          <button
            key={sao}
            type="button"
            aria-label={`${sao} sao`}
            className={`grid h-9 w-9 place-items-center rounded transition ${dangChon ? 'text-[#f59e0b]' : 'text-[#c4c6cd]'} ${disabled ? 'cursor-default' : 'hover:bg-[#fff7ef] hover:text-[#f59e0b]'}`}
            disabled={disabled}
            onClick={() => onChange?.(sao)}
          >
            <Star size={20} fill={dangChon ? 'currentColor' : 'none'} />
          </button>
        );
      })}
    </div>
  );
}

function noiDungTrangThaiDanhGia(danhGia: DanhGia) {
  if (danhGia.trangThai === 'CHO_DUYET') return 'Đánh giá của bạn đang chờ quản trị viên duyệt.';
  if (danhGia.trangThai === 'TU_CHOI') return 'Đánh giá của bạn đã bị từ chối. Bạn có thể sửa và gửi lại.';
  return null;
}

export function ChiTietSachPage() {
  const { maSach } = useParams();
  const id = Number(maSach);
  const queryClient = useQueryClient();
  const baoTin = useToastStore((state) => state.baoTin);
  const baoLoi = useToastStore((state) => state.baoLoi);
  const nguoiDung = useXacThucStore((state) => state.nguoiDung);
  const laNguoiDungMuaHang = chuanHoaVaiTro(nguoiDung?.vaiTro) === 'ROLE_NGUOI_DUNG';
  const laQuanTriVien = chuanHoaVaiTro(nguoiDung?.vaiTro) === 'ROLE_QUAN_TRI_VIEN';
  const maSachYeuThich = useMaSachYeuThich();
  const daYeuThich = maSachYeuThich.has(id);
  const [soSao, setSoSao] = useState(0);
  const [noiDung, setNoiDung] = useState('');
  const [dangThemDanhGia, setDangThemDanhGia] = useState(false);
  const [dangSuaDanhGia, setDangSuaDanhGia] = useState(false);

  const { data: sach, isLoading, error } = useQuery({ queryKey: ['sach', id], queryFn: () => sachApi.layChiTietSach(id), enabled: Number.isFinite(id) });
  const { data: danhGia } = useQuery({ queryKey: ['danh-gia', id], queryFn: () => sachApi.layDanhGiaSach(id), enabled: Number.isFinite(id) });
  const danhGiaCuaToi = useMemo(() => danhGia?.duLieu.find((item) => item.maNguoiDung === nguoiDung?.maNguoiDung), [danhGia?.duLieu, nguoiDung?.maNguoiDung]);
  const danhGiaHienThi = useMemo(
    () => (danhGia?.duLieu ?? []).filter((item) => item.maDanhGia !== danhGiaCuaToi?.maDanhGia && (laQuanTriVien || item.trangThai === 'DA_DUYET')),
    [danhGia?.duLieu, danhGiaCuaToi?.maDanhGia, laQuanTriVien],
  );

  useEffect(() => {
    if (!danhGiaCuaToi || dangSuaDanhGia) return;
    setSoSao(danhGiaCuaToi.soSao);
    setNoiDung(danhGiaCuaToi.noiDung);
  }, [danhGiaCuaToi, dangSuaDanhGia]);

  const lamMoiDanhGia = () => {
    queryClient.invalidateQueries({ queryKey: ['danh-gia', id] });
    queryClient.invalidateQueries({ queryKey: ['sach', id] });
  };

  const themGio = useMutation({
    mutationFn: () => gioHangApi.themVaoGioHang(id, 1),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['gio-hang'] });
      baoTin('Đã thêm vào giỏ hàng');
    },
    onError: (err) => baoLoi(err.message),
  });
  const chuyenTrangThaiYeuThich = useMutation({
    mutationFn: async () => {
      if (daYeuThich) await sachApi.xoaSachYeuThich(id);
      else await sachApi.themSachYeuThich(id);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['sach-yeu-thich'] });
      baoTin(daYeuThich ? 'Đã bỏ sách khỏi danh sách yêu thích' : 'Đã thêm vào yêu thích');
    },
    onError: (err) => baoLoi(err.message),
  });
  const guiDanhGia = useMutation({
    mutationFn: () => {
      const duLieu = { soSao, noiDung: noiDung.trim() };
      return danhGiaCuaToi ? sachApi.capNhatDanhGia(danhGiaCuaToi.maDanhGia, duLieu) : sachApi.themDanhGia(id, duLieu);
    },
    onSuccess: () => {
      lamMoiDanhGia();
      setDangThemDanhGia(false);
      setDangSuaDanhGia(false);
      baoTin(danhGiaCuaToi ? 'Đã gửi lại đánh giá, vui lòng chờ duyệt' : 'Đã gửi đánh giá, vui lòng chờ duyệt');
    },
    onError: (err) => baoLoi(err.message),
  });
  const xoaDanhGia = useMutation({
    mutationFn: (maDanhGia: number) => sachApi.xoaDanhGia(maDanhGia),
    onSuccess: () => {
      setSoSao(0);
      setNoiDung('');
      setDangThemDanhGia(false);
      setDangSuaDanhGia(false);
      lamMoiDanhGia();
      baoTin('Đã xóa đánh giá');
    },
    onError: (err) => baoLoi(err.message),
  });

  const dangGuiDanhGia = guiDanhGia.isPending || xoaDanhGia.isPending;
  const hienFormDanhGia = laNguoiDungMuaHang && ((!danhGiaCuaToi && dangThemDanhGia) || (danhGiaCuaToi && dangSuaDanhGia));

  const submitDanhGia = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (soSao < 1) {
      baoLoi('Vui lòng chọn số sao đánh giá');
      return;
    }
    if (!noiDung.trim()) {
      baoLoi('Vui lòng nhập nội dung đánh giá');
      return;
    }
    guiDanhGia.mutate();
  };

  if (isLoading) return <div className="mx-auto max-w-[1280px] px-4 py-10 md:px-10"><DangTai /></div>;
  if (error || !sach) return <div className="mx-auto max-w-[1280px] px-4 py-10 md:px-10"><OTrong tieuDe="Không tìm thấy sách" moTa={error?.message} /></div>;

  return (
    <div className="mx-auto max-w-[1280px] px-4 py-10 md:px-10">
      <NutQuayLai veDau="/sach" className="mb-6" />
      <div className="grid gap-10 lg:grid-cols-[430px_1fr]">
        <div className="paper-panel rounded p-5">
          <div className="book-shadow overflow-hidden rounded bg-[#efeded]">
            {sach.anhBia ? <img src={duongDanAnh(sach.anhBia)} alt={sach.tenSach} className="aspect-[2/3] w-full object-cover" /> : <div className="grid aspect-[2/3] place-items-center bg-[#03192e] p-10 text-center"><span className="font-serif-display text-4xl font-bold text-white">{sach.tenSach}</span></div>}
          </div>
        </div>
        <section>
          <div className="flex flex-wrap gap-2">
            {sach.theLoais?.map((item) => <span key={item.maTheLoai} className="archival-label rounded bg-[#d8efe1] px-3 py-2 text-[#14532d]">{item.ten}</span>)}
          </div>
          <h1 className="font-serif-display mt-5 max-w-3xl text-5xl font-bold leading-tight text-[#03192e]">{sach.tenSach}</h1>
          <p className="mt-3 text-[#43474d]">Tác giả: <strong className="text-[#1b1c1c]">{sach.tacGia?.ten ?? 'Chưa rõ'}</strong> - NXB: {sach.nhaXuatBan ?? 'Chưa rõ'}</p>
          <div className="mt-5 flex flex-wrap items-center gap-4">
            <span className="font-mono-label text-3xl font-bold text-[#7d562d]">{dinhDangTien(sach.gia)}</span>
            <span className="flex items-center gap-1 rounded bg-white px-3 py-2 text-[#f59e0b] ring-1 ring-[#e4e2e2]"><Star size={18} fill="currentColor" /> {(sach.diemDanhGiaTrungBinh ?? 0).toFixed(1)} <span className="text-[#43474d]">({sach.soLuongDanhGia ?? 0})</span></span>
            <span className="flex items-center gap-2 text-sm font-semibold text-[#43474d]"><LibraryBig size={18} /> Tồn kho: {sach.soLuongTon ?? 0}</span>
          </div>
          {laNguoiDungMuaHang ? <div className="paper-panel mt-8 flex flex-wrap items-center gap-3 rounded p-4">
            <Button onClick={() => themGio.mutate()} disabled={themGio.isPending || (sach.soLuongTon ?? 1) <= 0}><ShoppingBag size={18} /> Thêm vào giỏ</Button>
            <Button kieu="phu" onClick={() => chuyenTrangThaiYeuThich.mutate()} disabled={chuyenTrangThaiYeuThich.isPending}><Heart className={daYeuThich ? 'text-[#c62828]' : undefined} size={18} fill={daYeuThich ? 'currentColor' : 'none'} /> Yêu thích</Button>
          </div> : null}
        </section>
      </div>
      <section className="paper-panel mt-12 rounded p-6">
        <h2 className="font-serif-display text-2xl font-bold text-[#03192e]">Mô tả sách</h2>
        <p className="mt-4 max-w-5xl text-lg leading-8 text-[#43474d]">{sach.moTa ?? 'Chưa có mô tả cho sách này.'}</p>
      </section>
      <section className="paper-panel mt-6 rounded p-6">
        <div className="flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between">
          <h2 className="font-serif-display text-2xl font-bold text-[#03192e]">Đánh giá sách</h2>
          {laNguoiDungMuaHang && !danhGiaCuaToi && !dangThemDanhGia ? <Button kieu="phu" onClick={() => { setSoSao(0); setNoiDung(''); setDangThemDanhGia(true); }}>Thêm đánh giá</Button> : null}
          {danhGiaCuaToi && !dangSuaDanhGia ? <Button kieu="phu" onClick={() => setDangSuaDanhGia(true)}>Sửa đánh giá</Button> : null}
        </div>

        {hienFormDanhGia ? (
          <form className="mt-5 rounded border border-[#ece6df] bg-[#fbf9f8] p-4" onSubmit={submitDanhGia}>
            <div className="flex flex-wrap items-center justify-between gap-3">
              <div>
                <p className="font-semibold text-[#03192e]">{danhGiaCuaToi ? 'Sửa đánh giá của bạn' : 'Viết đánh giá của bạn'}</p>
                <p className="mt-1 text-xs font-semibold text-[#74777d]">Đánh giá sẽ được hiển thị sau khi quản trị viên duyệt.</p>
              </div>
              <DanhGiaSao soSao={soSao} onChange={setSoSao} />
            </div>
            <textarea
              className="mt-4 min-h-32 w-full rounded border border-[#c4c6cd] bg-white px-3 py-3 text-sm outline-none transition focus:border-[#7d562d] focus:ring-2 focus:ring-[#ffdcbd]"
              placeholder="Chia sẻ cảm nhận của bạn về cuốn sách..."
              value={noiDung}
              onChange={(event) => setNoiDung(event.target.value)}
            />
            <div className="mt-4 flex flex-wrap justify-end gap-2">
              {danhGiaCuaToi ? <Button type="button" kieu="rong" onClick={() => { setDangSuaDanhGia(false); setSoSao(danhGiaCuaToi.soSao); setNoiDung(danhGiaCuaToi.noiDung); }}>Hủy sửa</Button> : null}
              {!danhGiaCuaToi ? <Button type="button" kieu="rong" onClick={() => { setDangThemDanhGia(false); setSoSao(0); setNoiDung(''); }}>Hủy</Button> : null}
              <Button type="submit" disabled={dangGuiDanhGia}>{danhGiaCuaToi ? 'Gửi lại đánh giá' : 'Gửi đánh giá'}</Button>
            </div>
          </form>
        ) : null}

        {danhGiaCuaToi && !dangSuaDanhGia ? (
          <div className="mt-5 rounded border border-[#d8c6b4] bg-[#fff7ef] p-4">
            <div className="flex flex-wrap items-start justify-between gap-3">
              <div className="flex min-w-0 items-start gap-3">
                <AvatarNguoiDung ten={danhGiaCuaToi.tenNguoiDung} anhDaiDien={danhGiaCuaToi.anhDaiDienNguoiDung} />
                <div className="min-w-0">
                <div className="flex flex-wrap items-center gap-3">
                  <span className="font-bold text-[#03192e]">Đánh giá của bạn</span>
                  <TrangThai giaTri={danhGiaCuaToi.trangThai} />
                </div>
                <div className="mt-2"><DanhGiaSao soSao={danhGiaCuaToi.soSao} disabled /></div>
                </div>
              </div>
              <Button kieu="canh-bao" disabled={dangGuiDanhGia} onClick={() => xoaDanhGia.mutate(danhGiaCuaToi.maDanhGia)}>Xóa</Button>
            </div>
            {noiDungTrangThaiDanhGia(danhGiaCuaToi) ? <p className="mt-3 text-sm font-semibold text-[#7d562d]">{noiDungTrangThaiDanhGia(danhGiaCuaToi)}</p> : null}
            {danhGiaCuaToi.phanHoi ? <p className="mt-2 rounded bg-white px-3 py-2 text-sm text-[#43474d]">Phản hồi: {danhGiaCuaToi.phanHoi}</p> : null}
            <p className="mt-3 text-sm text-[#43474d]">{danhGiaCuaToi.noiDung}</p>
          </div>
        ) : null}

        <div className="mt-5 grid gap-3">
          {danhGiaHienThi.length === 0 ? (
            <p className="text-sm text-[#43474d]">{danhGiaCuaToi ? 'Chưa có đánh giá công khai khác.' : 'Chưa có đánh giá.'}</p>
          ) : (
            danhGiaHienThi.map((item) => (
              <div key={item.maDanhGia} className="rounded border border-[#e4e2e2] bg-[#fbf9f8] p-4">
                <div className="flex flex-wrap items-center justify-between gap-3">
                  <div className="flex min-w-0 items-center gap-3">
                    <AvatarNguoiDung ten={item.tenNguoiDung} anhDaiDien={item.anhDaiDienNguoiDung} />
                    <span className="truncate font-bold">{item.tenNguoiDung ?? 'Độc giả'}</span>
                  </div>
                  <div className="flex items-center gap-3">
                    <DanhGiaSao soSao={item.soSao} disabled />
                    {item.trangThai && item.trangThai !== 'DA_DUYET' ? <TrangThai giaTri={item.trangThai} /> : null}
                  </div>
                </div>
                <p className="mt-2 text-sm text-[#43474d]">{item.noiDung}</p>
              </div>
            ))
          )}
        </div>
      </section>
    </div>
  );
}
