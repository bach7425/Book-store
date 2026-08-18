import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { HeartOff, LibraryBig, ShoppingBag } from 'lucide-react';
import { Link, useSearchParams } from 'react-router-dom';
import { gioHangApi } from '../../api/gioHangApi';
import { sachApi } from '../../api/sachApi';
import { DangTai, OTrong } from '../../components/ui/Bang';
import { Button } from '../../components/ui/Button';
import { useToastStore } from '../../components/ui/toastStore';
import { dinhDangTien, duongDanAnh } from '../../utils/dinhDang';

export function YeuThichPage() {
  const [params, setParams] = useSearchParams();
  const page = Number(params.get('page') ?? 0);
  const queryClient = useQueryClient();
  const baoTin = useToastStore((state) => state.baoTin);
  const baoLoi = useToastStore((state) => state.baoLoi);
  const { data, isLoading, error } = useQuery({
    queryKey: ['sach-yeu-thich', page],
    queryFn: () => sachApi.laySachYeuThich(page),
  });

  const themGio = useMutation({
    mutationFn: (maSach: number) => gioHangApi.themVaoGioHang(maSach, 1),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['gio-hang'] });
      baoTin('Đã thêm sách vào giỏ hàng');
    },
    onError: (err) => baoLoi(err.message),
  });

  const xoaYeuThich = useMutation({
    mutationFn: sachApi.xoaSachYeuThich,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['sach-yeu-thich'] });
      baoTin('Đã bỏ sách khỏi danh sách yêu thích');
    },
    onError: (err) => baoLoi(err.message),
  });

  const capNhatTrang = (trangMoi: number) => {
    const next = new URLSearchParams(params);
    next.set('page', String(trangMoi));
    setParams(next);
  };

  return (
    <div className="mx-auto max-w-[1280px] px-4 py-10 md:px-10">
      <div className="mb-8 border-b border-[#c4c6cd] pb-5">
        <div>
          <p className="archival-label text-[#7d562d]">Kệ sách cá nhân</p>
          <h1 className="font-serif-display mt-1 text-4xl font-bold text-[#03192e]">Danh sách yêu thích</h1>
          <p className="mt-1 text-sm text-[#43474d]">{data?.tongSoPhanTu ?? 0} tựa sách đã lưu</p>
        </div>
      </div>

      {isLoading ? (
        <DangTai />
      ) : error ? (
        <OTrong tieuDe="Không tải được danh sách yêu thích" moTa={error.message} />
      ) : (data?.duLieu.length ?? 0) === 0 ? (
        <OTrong tieuDe="Chưa có sách yêu thích" moTa="Hãy bấm biểu tượng trái tim ở sách bạn muốn lưu lại." />
      ) : (
        <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
          {data?.duLieu.map((sach) => (
            <article key={sach.maSach} className="group flex h-full flex-col rounded border border-[#c4c6cd]/70 bg-white p-4 transition hover:-translate-y-1 hover:border-[#7d562d]/50 hover:shadow-[0_18px_36px_rgba(26,46,68,0.08)]">
              <Link to={`/sach/${sach.maSach}`} className="block">
                <div className="book-shadow relative aspect-[2/3] overflow-hidden rounded bg-[#efeded]">
                  {sach.anhBia ? (
                    <img src={duongDanAnh(sach.anhBia)} alt={sach.tenSach} className="h-full w-full object-cover transition duration-500 group-hover:scale-[1.025]" />
                  ) : (
                    <div className="grid h-full place-items-center bg-[#03192e] px-7 text-center">
                      <span className="font-serif-display text-xl font-bold leading-tight text-white">{sach.tenSach}</span>
                    </div>
                  )}
                </div>
              </Link>
              <div className="mt-4 flex flex-1 flex-col">
                <Link to={`/sach/${sach.maSach}`} className="font-serif-display line-clamp-2 min-h-14 text-xl font-bold leading-tight text-[#03192e] transition group-hover:text-[#7d562d]">
                  {sach.tenSach}
                </Link>
                <p className="mt-1 flex items-center gap-2 text-sm text-[#43474d]"><LibraryBig size={16} /> {sach.tacGia?.ten ?? 'Chưa rõ tác giả'}</p>
                <div className="mt-auto flex items-center justify-between border-t border-[#e4e2e2] pt-4">
                  <span className="font-mono-label text-base font-bold text-[#1b1c1c]">{dinhDangTien(sach.gia)}</span>
                  <div className="flex gap-1">
                    <Button kieu="rong" onClick={() => xoaYeuThich.mutate(sach.maSach)} disabled={xoaYeuThich.isPending}>
                      <HeartOff size={16} />
                    </Button>
                    <Button className="h-9 min-h-9 px-3" onClick={() => themGio.mutate(sach.maSach)} disabled={themGio.isPending || (sach.soLuongTon ?? 1) <= 0}>
                      <ShoppingBag size={16} />
                    </Button>
                  </div>
                </div>
              </div>
            </article>
          ))}
        </div>
      )}

      {data?.trangTruoc || data?.tiepTheo ? (
        <div className="mt-8 flex justify-end gap-2">
          {data.trangTruoc ? <Button kieu="phu" onClick={() => capNhatTrang(page - 1)}>Trang trước</Button> : null}
          {data.tiepTheo ? <Button kieu="phu" onClick={() => capNhatTrang(page + 1)}>Trang sau</Button> : null}
        </div>
      ) : null}
    </div>
  );
}
