import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { Heart, LibraryBig, Minus, Plus, ShoppingBag, Star, Zap } from 'lucide-react';
import { useEffect, useMemo, useState } from 'react';
import type { FormEvent } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { gioHangApi } from '../../api/gioHangApi';
import { sachApi } from '../../api/sachApi';
import { DangTai, OTrong } from '../../components/ui/Bang';
import { AvatarNguoiDung } from '../../components/ui/AvatarNguoiDung';
import { Button } from '../../components/ui/Button';
import { NutQuayLai } from '../../components/ui/NutQuayLai';
import { PhanTrang } from '../../components/ui/PhanTrang';
import { TrangThai } from '../../components/ui/TrangThai';
import { useToastStore } from '../../components/ui/toastStore';
import type { DanhGia } from '../../types';
import { dinhDangNgay, dinhDangTien, duongDanAnh } from '../../utils/dinhDang';
import { chuanHoaVaiTro } from '../../utils/vaiTro';
import { useXacThucStore } from '../xac-thuc/xacThucStore';
import { SachCard } from './SachCard';
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
  const navigate = useNavigate();
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
  const [soLuong, setSoLuong] = useState(1);
  const [trangDanhGia, setTrangDanhGia] = useState(0);
  const [tabDangChon, setTabDangChon] = useState<'mo-ta' | 'danh-gia'>('mo-ta');

  const { data: sach, isLoading, error } = useQuery({ queryKey: ['sach', id], queryFn: () => sachApi.layChiTietSach(id), enabled: Number.isFinite(id) });
  const { data: danhGia } = useQuery({ queryKey: ['danh-gia', id, trangDanhGia], queryFn: () => sachApi.layDanhGiaSach(id, trangDanhGia), enabled: Number.isFinite(id) });
  const { data: tongQuanDanhGia } = useQuery({ queryKey: ['danh-gia', id, 'tong-quan'], queryFn: () => sachApi.layDanhGiaSach(id, 0, 100), enabled: Number.isFinite(id) });
  const { data: sachLienQuan = [] } = useQuery({ queryKey: ['sach-lien-quan', id], queryFn: () => sachApi.laySachLienQuan(id), enabled: Number.isFinite(id) });
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

  useEffect(() => {
    const soLuongTon = sach?.soLuongTon ?? 0;
    setSoLuong((giaTri) => Math.min(Math.max(giaTri, 1), Math.max(soLuongTon, 1)));
  }, [sach?.soLuongTon]);

  const lamMoiDanhGia = () => {
    queryClient.invalidateQueries({ queryKey: ['danh-gia', id] });
    queryClient.invalidateQueries({ queryKey: ['sach', id] });
  };

  const themGio = useMutation({
    mutationFn: () => gioHangApi.themVaoGioHang(id, soLuong),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['gio-hang'] });
      baoTin('Đã thêm vào giỏ hàng');
    },
    onError: (err) => baoLoi(err.message),
  });
  const muaNgay = useMutation({
    mutationFn: () => gioHangApi.themVaoGioHang(id, soLuong),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['gio-hang'] });
      navigate('/thanh-toan');
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
  const soLuongTon = sach?.soLuongTon ?? 0;
  const conHang = soLuongTon > 0;
  const tongSoDanhGia = sach?.soLuongDanhGia ?? 0;
  const danhGiaDaDuyet = (tongQuanDanhGia?.duLieu ?? []).filter((item) => item.trangThai === 'DA_DUYET');
  const thongKeSao = [5, 4, 3, 2, 1].map((sao) => {
    const soLuongDanhGia = danhGiaDaDuyet.filter((item) => item.soSao === sao).length;
    const phanTram = tongSoDanhGia > 0 ? Math.round((soLuongDanhGia / tongSoDanhGia) * 100) : 0;
    return { sao, soLuongDanhGia, phanTram };
  });
  const thongTinChiTiet = ([
    ['Độ tuổi', sach?.doTuoi],
    ['Tên nhà cung cấp', sach?.tenNhaCungCap],
    ['Tác giả', sach?.tacGia?.ten],
    ['Người dịch', sach?.nguoiDich],
    ['NXB', sach?.nhaXuatBan],
    ['Năm XB', sach?.ngayXuatBan ? new Date(sach.ngayXuatBan).getFullYear().toString() : undefined],
    ['Ngôn ngữ', sach?.ngonNgu],
    ['Trọng lượng (gr)', sach?.trongLuongGram],
    ['Kích thước bao bì', sach?.kichThuocBaoBi],
    ['Số trang', sach?.soTrang],
    ['Hình thức', sach?.hinhThuc],
  ] as Array<[string, string | number | undefined]>).filter(([, giaTri]) => giaTri !== undefined && giaTri !== null && giaTri !== '');

  const doiSoLuong = (giaTri: number) => {
    setSoLuong(Math.min(Math.max(giaTri, 1), Math.max(soLuongTon, 1)));
  };

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
      <section className="grid gap-8 md:grid-cols-12">
        <div className="md:col-span-5 lg:col-span-4">
          <div className="sticky top-24 mx-auto max-w-[420px]">
            <div className="book-shadow overflow-hidden rounded border border-[#c4c6cd] bg-[#efeded] transition duration-300 hover:-translate-y-1">
              {sach.anhBia ? (
                <img src={duongDanAnh(sach.anhBia)} alt={sach.tenSach} className="aspect-[2/3] w-full object-cover" />
              ) : (
                <div className="grid aspect-[2/3] place-items-center bg-[#03192e] p-10 text-center">
                  <span className="font-serif-display text-4xl font-bold leading-tight text-white">{sach.tenSach}</span>
                </div>
              )}
            </div>
          </div>
        </div>

        <div className="md:col-span-7 lg:col-span-8">
          <div className="border-b border-[#c4c6cd] pb-6">
            <div className="flex flex-wrap gap-2">
              {sach.theLoais?.map((item) => <span key={item.maTheLoai} className="archival-label rounded bg-[#d8efe1] px-3 py-2 text-[#14532d]">{item.ten}</span>)}
            </div>
            <h1 className="font-serif-display mt-5 max-w-4xl text-4xl font-bold leading-tight text-[#03192e] md:text-5xl">{sach.tenSach}</h1>
            <div className="mt-4 flex flex-wrap items-center gap-3 text-lg text-[#43474d]">
              <span>Tác giả: <strong className="text-[#1b1c1c]">{sach.tacGia?.ten ?? 'Chưa rõ'}</strong></span>
              <span className="h-1 w-1 rounded-full bg-[#c4c6cd]" />
              <span>{sach.nhaXuatBan ?? 'Chưa rõ NXB'}</span>
              {sach.ngayXuatBan ? (
                <>
                  <span className="h-1 w-1 rounded-full bg-[#c4c6cd]" />
                  <span>{new Date(sach.ngayXuatBan).getFullYear()}</span>
                </>
              ) : null}
            </div>
            <div className="mt-5 flex flex-wrap items-center gap-3">
              <span className="flex items-center gap-1 text-[#f59e0b]">
                {[1, 2, 3, 4, 5].map((sao) => <Star key={sao} size={20} fill={sao <= Math.round(sach.diemDanhGiaTrungBinh ?? 0) ? 'currentColor' : 'none'} />)}
              </span>
              <span className="font-mono-label text-sm font-semibold text-[#43474d]">{(sach.diemDanhGiaTrungBinh ?? 0).toFixed(1)} ({sach.soLuongDanhGia ?? 0} đánh giá)</span>
            </div>
            <div className="mt-5">
              <span className="archival-label block text-[#74777d]">Giá bán</span>
              <span className="font-mono-label mt-1 block text-4xl font-bold text-[#03192e]">{dinhDangTien(sach.gia)}</span>
            </div>
          </div>

          <div className="mt-6 rounded border border-[#c4c6cd] bg-[#f5f3f3] p-5">
            <div className="mb-5 flex flex-wrap items-center gap-3">
              <span className={`archival-label rounded px-3 py-2 ${conHang ? 'bg-[#d8efe1] text-[#14532d]' : 'bg-[#ffdad6] text-[#93000a]'}`}>{conHang ? 'Còn hàng' : 'Hết hàng'}</span>
              <span className="flex items-center gap-2 text-sm font-semibold text-[#43474d]"><LibraryBig size={18} /> {soLuongTon} sản phẩm có sẵn</span>
            </div>
            {laNguoiDungMuaHang ? (
              <div className="flex flex-wrap items-end gap-3">
                <label className="grid gap-2 text-sm font-bold text-[#43474d]">
                  Số lượng
                  <div className="flex min-h-11 items-center overflow-hidden rounded border border-[#c4c6cd] bg-white">
                    <button type="button" className="grid h-11 w-11 place-items-center text-[#43474d] transition hover:bg-[#fff7ef] disabled:opacity-50" onClick={() => doiSoLuong(soLuong - 1)} disabled={!conHang || soLuong <= 1} aria-label="Giảm số lượng">
                      <Minus size={16} />
                    </button>
                    <input
                      className="h-11 w-16 border-x border-[#e4e2e2] text-center font-mono-label font-bold outline-none"
                      type="number"
                      min={1}
                      max={Math.max(soLuongTon, 1)}
                      value={soLuong}
                      disabled={!conHang}
                      onChange={(event) => doiSoLuong(Number(event.target.value) || 1)}
                      aria-label="Số lượng"
                    />
                    <button type="button" className="grid h-11 w-11 place-items-center text-[#43474d] transition hover:bg-[#fff7ef] disabled:opacity-50" onClick={() => doiSoLuong(soLuong + 1)} disabled={!conHang || soLuong >= soLuongTon} aria-label="Tăng số lượng">
                      <Plus size={16} />
                    </button>
                  </div>
                </label>
                <Button className="min-h-12 px-5" kieu="phu" onClick={() => themGio.mutate()} disabled={themGio.isPending || !conHang}><ShoppingBag size={18} /> Thêm vào giỏ</Button>
                <Button className="min-h-12 px-7" onClick={() => muaNgay.mutate()} disabled={muaNgay.isPending || !conHang}><Zap size={18} /> Mua ngay</Button>
                <Button className="min-h-12" kieu="rong" onClick={() => chuyenTrangThaiYeuThich.mutate()} disabled={chuyenTrangThaiYeuThich.isPending}><Heart className={daYeuThich ? 'text-[#c62828]' : undefined} size={18} fill={daYeuThich ? 'currentColor' : 'none'} /> Yêu thích</Button>
              </div>
            ) : null}
          </div>
        </div>
      </section>

      <section className="mt-14 border-t border-[#c4c6cd] pt-8">
        <div className="mb-8 flex gap-2 border-b border-[#e4e2e2]">
          <button
            type="button"
            className={`pb-3 text-sm font-bold transition ${tabDangChon === 'mo-ta' ? 'border-b-2 border-[#03192e] text-[#03192e]' : 'border-b-2 border-transparent text-[#74777d] hover:text-[#03192e]'}`}
            onClick={() => setTabDangChon('mo-ta')}
          >
            Mô tả
          </button>
          <button
            type="button"
            className={`ml-6 pb-3 text-sm font-bold transition ${tabDangChon === 'danh-gia' ? 'border-b-2 border-[#03192e] text-[#03192e]' : 'border-b-2 border-transparent text-[#74777d] hover:text-[#03192e]'}`}
            onClick={() => setTabDangChon('danh-gia')}
          >
            Đánh giá ({sach.soLuongDanhGia ?? 0})
          </button>
        </div>

        {tabDangChon === 'mo-ta' ? (
          <div className="grid gap-8 lg:grid-cols-[1fr_380px]">
            <div>
              <h2 className="font-serif-display text-2xl font-bold text-[#03192e]">Mô tả sách</h2>
              <p className="mt-4 max-w-4xl text-lg leading-8 text-[#43474d]">{sach.moTa ?? 'Chưa có mô tả cho sách này.'}</p>
              {sach.tacGia?.tieuSu ? (
                <div className="mt-8 border-t border-[#e4e2e2] pt-6">
                  <p className="archival-label text-[#7d562d]">Tác giả</p>
                  <h3 className="font-serif-display mt-1 text-2xl font-bold text-[#03192e]">{sach.tacGia.ten}</h3>
                  <p className="mt-3 max-w-4xl text-base leading-7 text-[#43474d]">{sach.tacGia.tieuSu}</p>
                </div>
              ) : null}
            </div>
            <aside className="h-fit rounded border border-[#c4c6cd] bg-[#f5f3f3] p-5">
              <h2 className="text-sm font-bold uppercase text-[#03192e]">Thông tin chi tiết</h2>
              {thongTinChiTiet.length === 0 ? (
                <p className="mt-4 text-sm text-[#43474d]">Chưa có thông tin chi tiết cho sách này.</p>
              ) : (
                <dl className="mt-4 grid gap-3 text-sm">
                  {thongTinChiTiet.map(([nhan, giaTri]) => (
                    <div key={nhan} className="flex justify-between gap-4 border-b border-[#e4e2e2] pb-3 last:border-b-0 last:pb-0">
                      <dt className="font-semibold text-[#43474d]">{nhan}</dt>
                      <dd className="max-w-[190px] text-right font-semibold text-[#03192e]">{giaTri}</dd>
                    </div>
                  ))}
                </dl>
              )}
            </aside>
          </div>
        ) : null}
      </section>
      {tabDangChon === 'danh-gia' ? <section className="paper-panel mt-6 rounded p-6">
        <div className="flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between">
          <h2 className="font-serif-display text-2xl font-bold text-[#03192e]">Đánh giá sách</h2>
          {laNguoiDungMuaHang && !danhGiaCuaToi && !dangThemDanhGia ? <Button kieu="phu" onClick={() => { setSoSao(0); setNoiDung(''); setDangThemDanhGia(true); }}>Thêm đánh giá</Button> : null}
          {danhGiaCuaToi && !dangSuaDanhGia ? <Button kieu="phu" onClick={() => setDangSuaDanhGia(true)}>Sửa đánh giá</Button> : null}
        </div>

        <div className="mt-5 rounded border border-[#e4e2e2] bg-white p-5">
          <h3 className="text-lg font-bold text-[#03192e]">Đánh giá sản phẩm</h3>
          <div className="mt-5 grid gap-6 md:grid-cols-[160px_1fr] md:items-center">
            <div className="text-center md:text-left">
              <div className="flex items-end justify-center gap-1 md:justify-start">
                <span className="font-mono-label text-5xl font-bold text-[#03192e]">{(sach.diemDanhGiaTrungBinh ?? 0).toFixed(1).replace('.0', '')}</span>
                <span className="mb-2 text-2xl font-bold text-[#03192e]">/5</span>
              </div>
              <div className="mt-2 flex justify-center gap-1 text-[#d8d8d8] md:justify-start">
                {[1, 2, 3, 4, 5].map((sao) => (
                  <Star key={sao} size={18} fill={sao <= Math.round(sach.diemDanhGiaTrungBinh ?? 0) ? '#f59e0b' : 'currentColor'} className={sao <= Math.round(sach.diemDanhGiaTrungBinh ?? 0) ? 'text-[#f59e0b]' : undefined} />
                ))}
              </div>
              <p className="mt-2 text-sm text-[#74777d]">({tongSoDanhGia} đánh giá)</p>
            </div>
            <div className="grid gap-2">
              {thongKeSao.map((item) => (
                <div key={item.sao} className="grid grid-cols-[48px_1fr_42px] items-center gap-3 text-sm">
                  <span className="text-[#03192e]">{item.sao} sao</span>
                  <div className="h-1.5 overflow-hidden rounded bg-[#efeded]">
                    <div className="h-full bg-[#f59e0b]" style={{ width: `${item.phanTram}%` }} />
                  </div>
                  <span className="font-mono-label text-right text-xs text-[#03192e]">{item.phanTram}%</span>
                </div>
              ))}
            </div>
          </div>
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
                  {danhGiaCuaToi.ngayTao ? <span className="text-xs font-semibold text-[#74777d]">{dinhDangNgay(danhGiaCuaToi.ngayTao)}</span> : null}
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
                    <div className="min-w-0">
                      <span className="block truncate font-bold">{item.tenNguoiDung ?? 'Độc giả'}</span>
                      {item.ngayTao ? <span className="mt-1 block text-xs font-semibold text-[#74777d]">{dinhDangNgay(item.ngayTao)}</span> : null}
                    </div>
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
        <div className="mt-5">
          <PhanTrang data={danhGia} page={trangDanhGia} onPageChange={setTrangDanhGia} />
        </div>
      </section> : null}
      {sachLienQuan.length > 0 ? (
        <section className="mt-12">
          <div className="mb-5 flex items-center justify-between gap-3">
            <div>
              <p className="archival-label text-[#7d562d]">Gợi ý đọc tiếp</p>
              <h2 className="font-serif-display mt-1 text-3xl font-bold text-[#03192e]">Sách liên quan</h2>
            </div>
          </div>
          <div className="grid gap-5 sm:grid-cols-2 lg:grid-cols-4">
            {sachLienQuan.map((item) => <SachCard key={item.maSach} sach={item} daYeuThich={maSachYeuThich.has(item.maSach)} />)}
          </div>
        </section>
      ) : null}
    </div>
  );
}
