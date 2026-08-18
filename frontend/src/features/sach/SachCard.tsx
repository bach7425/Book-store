import { useMutation, useQueryClient } from '@tanstack/react-query';
import { Heart, ShoppingBag, Star } from 'lucide-react';
import { Link } from 'react-router-dom';
import { gioHangApi } from '../../api/gioHangApi';
import { sachApi } from '../../api/sachApi';
import { Button } from '../../components/ui/Button';
import { useToastStore } from '../../components/ui/toastStore';
import type { Sach } from '../../types';
import { dinhDangTien, duongDanAnh } from '../../utils/dinhDang';
import { chuanHoaVaiTro } from '../../utils/vaiTro';
import { useXacThucStore } from '../xac-thuc/xacThucStore';

export function SachCard({ sach, daYeuThich = false }: { sach: Sach; daYeuThich?: boolean }) {
  const queryClient = useQueryClient();
  const baoTin = useToastStore((state) => state.baoTin);
  const baoLoi = useToastStore((state) => state.baoLoi);
  const daDangNhap = useXacThucStore((state) => state.daDangNhap);
  const nguoiDung = useXacThucStore((state) => state.nguoiDung);
  const vaiTro = chuanHoaVaiTro(nguoiDung?.vaiTro);
  const laNguoiDungMuaHang = vaiTro === 'ROLE_NGUOI_DUNG';
  const themGio = useMutation({
    mutationFn: () => gioHangApi.themVaoGioHang(sach.maSach, 1),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['gio-hang'] });
      baoTin('Đã thêm sách vào giỏ hàng');
    },
    onError: (error) => baoLoi(error.message),
  });
  const chuyenTrangThaiYeuThich = useMutation({
    mutationFn: async () => {
      if (daYeuThich) await sachApi.xoaSachYeuThich(sach.maSach);
      else await sachApi.themSachYeuThich(sach.maSach);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['sach-yeu-thich'] });
      baoTin(daYeuThich ? 'Đã bỏ sách khỏi danh sách yêu thích' : 'Đã lưu vào danh sách yêu thích');
    },
    onError: (error) => baoLoi(error.message),
  });

  return (
    <article className="group flex h-full flex-col rounded border border-[#c4c6cd]/70 bg-white p-4 transition duration-300 hover:-translate-y-1 hover:border-[#7d562d]/50 hover:shadow-[0_18px_36px_rgba(26,46,68,0.08)]">
      <Link to={`/sach/${sach.maSach}`} className="block">
        <div className="book-shadow relative aspect-[2/3] overflow-hidden rounded bg-[#efeded]">
          {sach.anhBia ? (
            <img src={duongDanAnh(sach.anhBia)} alt={sach.tenSach} className="h-full w-full object-cover transition duration-500 group-hover:scale-[1.025]" />
          ) : (
            <div className="grid h-full place-items-center bg-[#03192e] px-7 text-center">
              <span className="font-serif-display text-xl font-bold leading-tight text-white">{sach.tenSach}</span>
            </div>
          )}
          {(sach.soLuongTon ?? 0) <= 0 ? (
            <span className="archival-label absolute right-2 top-2 rounded bg-[#ffdad6] px-2 py-1 text-[#93000a]">Hết hàng</span>
          ) : (
            <span className="archival-label absolute right-2 top-2 rounded bg-[#ffdcbd] px-2 py-1 text-[#623f18]">Còn hàng</span>
          )}
        </div>
      </Link>
      <div className="mt-4 flex flex-1 flex-col">
        <div className="mb-2 flex items-center gap-1 text-[#f59e0b]">
          <Star size={15} fill="currentColor" />
          <span className="font-mono-label text-xs text-[#43474d]">{(sach.diemDanhGiaTrungBinh ?? 0).toFixed(1)} ({sach.soLuongDanhGia ?? 0})</span>
        </div>
        <Link to={`/sach/${sach.maSach}`} className="font-serif-display line-clamp-2 min-h-14 text-xl font-bold leading-tight text-[#03192e] transition group-hover:text-[#7d562d]">
          {sach.tenSach}
        </Link>
        <p className="mt-1 text-sm text-[#43474d]">{sach.tacGia?.ten ?? 'Chưa rõ tác giả'}</p>
        <div className="mt-auto flex items-center justify-between border-t border-[#e4e2e2] pt-4">
          <span className="font-mono-label text-base font-bold text-[#1b1c1c]">{dinhDangTien(sach.gia)}</span>
          {daDangNhap && laNguoiDungMuaHang ? <div className="flex gap-1">
            <button
              onClick={() => chuyenTrangThaiYeuThich.mutate()}
              disabled={chuyenTrangThaiYeuThich.isPending}
              className={`rounded-full p-2 transition hover:bg-[#efeded] ${daYeuThich ? 'text-[#c62828] hover:text-[#a31515]' : 'text-[#43474d] hover:text-[#7d562d]'}`}
              aria-label={daYeuThich ? 'Bỏ yêu thích' : 'Thêm yêu thích'}
              title={daYeuThich ? 'Bỏ yêu thích' : 'Thêm yêu thích'}
            >
              <Heart size={18} fill={daYeuThich ? 'currentColor' : 'none'} />
            </button>
            <Button className="h-9 min-h-9 px-3" onClick={() => themGio.mutate()} disabled={themGio.isPending || (sach.soLuongTon ?? 1) <= 0}>
              <ShoppingBag size={16} />
            </Button>
          </div> : null}
        </div>
      </div>
    </article>
  );
}
