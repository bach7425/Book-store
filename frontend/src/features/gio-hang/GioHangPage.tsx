import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { Minus, Plus, ShoppingBag, Trash2 } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { gioHangApi } from '../../api/gioHangApi';
import { Bang, OTrong } from '../../components/ui/Bang';
import { Button } from '../../components/ui/Button';
import { useToastStore } from '../../components/ui/toastStore';
import { dinhDangTien, duongDanAnh } from '../../utils/dinhDang';

export function GioHangPage() {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const baoTin = useToastStore((state) => state.baoTin);
  const baoLoi = useToastStore((state) => state.baoLoi);
  const { data, isLoading, error } = useQuery({ queryKey: ['gio-hang'], queryFn: gioHangApi.layGioHang });
  const capNhat = useMutation({
    mutationFn: ({ id, soLuong }: { id: number; soLuong: number }) => gioHangApi.capNhatSoLuong(id, soLuong),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['gio-hang'] }),
    onError: (err) => baoLoi(err.message),
  });
  const xoa = useMutation({
    mutationFn: gioHangApi.xoaSanPham,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['gio-hang'] });
      baoTin('Đã xóa sản phẩm');
    },
    onError: (err) => baoLoi(err.message),
  });
  const items = data?.items?.duLieu ?? [];
  const coSanPham = items.length > 0;
  console.log('GioHangPage data:', items);
  return (
    <div className="mx-auto max-w-[1280px] px-4 py-10 md:px-10">
      <div className={`grid gap-8 ${coSanPham ? 'lg:grid-cols-[1fr_360px]' : ''}`}>
        <section>
        <h1 className="font-serif-display mt-1 text-4xl font-bold text-[#03192e]">Giỏ hàng</h1>
        <p></p>
        {isLoading ? <p className="mt-4 text-stone-500">Đang tải giỏ hàng...</p> : error ? <OTrong tieuDe="Chưa có sản phẩm trong giỏ hàng" moTa={error.message} /> : !coSanPham ? <OTrong tieuDe="Giỏ hàng đang trống" moTa="Hãy thêm sách vào giỏ hàng trước khi thanh toán." /> : (
          <Bang>
            <table className="w-full min-w-[720px] text-left text-sm">
              <thead className="bg-[#03192e] text-white"><tr><th className="px-4 py-3 font-semibold">Sách</th><th className="px-4 py-3 font-semibold">Đơn giá</th><th className="px-4 py-3 font-semibold">Số lượng</th><th className="px-4 py-3 font-semibold">Thành tiền</th><th className="px-4 py-3"></th></tr></thead>
              <tbody className="divide-y divide-[#e4e2e2]">
                {items.map((item) => (
                  <tr key={item.maChiTietGioHang}>
                    <td className="px-4 py-3">
                      <div className="flex items-center gap-3">
                        <div className="book-shadow h-20 w-14 shrink-0 overflow-hidden rounded bg-[#03192e]">
                          {item.anhBia ? (
                            <img src={duongDanAnh(item.anhBia)} alt={item.tenSach} className="h-full w-full object-cover" />
                          ) : (
                            <div className="grid h-full place-items-center px-2 text-center">
                              <span className="font-serif-display text-xs font-bold leading-tight text-white">{item.tenSach}</span>
                            </div>
                          )}
                        </div>
                        <span className="font-serif-display text-lg font-bold text-[#03192e]">{item.tenSach}</span>
                      </div>
                    </td>
                    <td className="px-4 py-3">{dinhDangTien(item.donGia)}</td>
                    <td className="px-4 py-3"><div className="inline-flex items-center rounded border border-[#c4c6cd] bg-[#fbf9f8]"><button className="p-2" disabled={item.soLuong <= 1} onClick={() => capNhat.mutate({ id: item.maChiTietGioHang, soLuong: item.soLuong - 1 })}><Minus size={16} /></button><span className="font-mono-label w-10 text-center">{item.soLuong}</span><button className="p-2" onClick={() => capNhat.mutate({ id: item.maChiTietGioHang, soLuong: item.soLuong + 1 })}><Plus size={16} /></button></div></td>
                    <td className="font-mono-label px-4 py-3 font-semibold">{dinhDangTien(item.donGia * item.soLuong)}</td>
                    <td className="px-4 py-3 text-right"><Button kieu="rong" onClick={() => xoa.mutate(item.maChiTietGioHang)}><Trash2 size={16} /></Button></td>
                  </tr>
                ))}
              </tbody>
            </table>
          </Bang>
        )}
        </section>
        {coSanPham ? (
        <aside className="paper-panel h-fit rounded p-5">
        <div className="flex items-center gap-2 text-[#7d562d]"><ShoppingBag size={20} /><span className="archival-label">Tóm tắt đơn</span></div>
        <h2 className="font-serif-display mt-2 text-2xl font-bold text-[#03192e]">Tóm tắt</h2>
        <div className="mt-4 space-y-3 text-sm">
          <div className="flex justify-between"><span>Tổng số lượng</span><strong>{data?.tongSoLuong ?? 0}</strong></div>
          <div className="flex justify-between text-lg"><span>Tổng tiền</span><strong className="font-mono-label text-[#7d562d]">{dinhDangTien(data?.tongTien)}</strong></div>
        </div>
        <Button className="mt-5 w-full" onClick={() => navigate('/thanh-toan')}>Thanh toán</Button>
        </aside>
        ) : null}
      </div>
    </div>
  );
}
