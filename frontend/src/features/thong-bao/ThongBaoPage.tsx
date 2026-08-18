import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { CheckCheck } from 'lucide-react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { thongBaoApi } from '../../api/thongBaoApi';
import { OTrong } from '../../components/ui/Bang';
import { Button } from '../../components/ui/Button';
import { TomTatThongBao } from '../../components/ThongBaoChuong';
import { useToastStore } from '../../components/ui/toastStore';
import type { ThongBao } from '../../types';
import { nhanLoaiThongBao } from '../../utils/thongBaoHienThi';

export function ThongBaoPage() {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const [params, setParams] = useSearchParams();
  const page = Number(params.get('page') ?? 0);
  const baoTin = useToastStore((state) => state.baoTin);
  const baoLoi = useToastStore((state) => state.baoLoi);
  const { data, isLoading, error } = useQuery({
    queryKey: ['thong-bao', page],
    queryFn: () => thongBaoApi.layDanhSachThongBao(page, 10),
    meta: { anThongBao: true },
  });
  const danhDauDaDoc = useMutation({
    mutationFn: thongBaoApi.danhDauDaDoc,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['thong-bao'] });
      baoTin('Đã đánh dấu thông báo là đã đọc');
    },
    onError: (err) => baoLoi(err.message),
  });
  const danhDauTatCa = useMutation({
    mutationFn: thongBaoApi.danhDauTatCaDaDoc,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['thong-bao'] });
      baoTin('Đã đánh dấu tất cả thông báo là đã đọc');
    },
    onError: (err) => baoLoi(err.message),
  });

  const capNhatTrang = (trangMoi: number) => {
    const next = new URLSearchParams(params);
    next.set('page', String(trangMoi));
    setParams(next);
  };
  const chonThongBao = (thongBao: ThongBao) => {
    if (!thongBao.daDoc) danhDauDaDoc.mutate(thongBao.maThongBao);
    if (thongBao.duongDan) navigate(thongBao.duongDan);
  };

  return (
    <div className="mx-auto max-w-[980px] px-4 py-10 md:px-10">
      <div className="mb-6 flex flex-wrap items-end justify-between gap-4 border-b border-[#c4c6cd] pb-5">
        <div>
          <p className="archival-label text-[#7d562d]">Hộp tin</p>
          <h1 className="font-serif-display mt-1 text-4xl font-bold text-[#03192e]">Thông báo</h1>
          <p className="mt-1 text-sm text-[#43474d]">{data?.tongSoPhanTu ?? 0} thông báo trong tài khoản</p>
        </div>
        <Button kieu="phu" onClick={() => danhDauTatCa.mutate()} disabled={danhDauTatCa.isPending || (data?.duLieu.length ?? 0) === 0}>
          <CheckCheck size={16} /> Đánh dấu tất cả đã đọc
        </Button>
      </div>

      {isLoading ? (
        <div className="grid gap-3">
          {[1, 2, 3].map((item) => <div key={item} className="h-24 animate-pulse rounded bg-[#e4e2e2]" />)}
        </div>
      ) : error ? (
        <OTrong tieuDe="Không tải được thông báo" moTa={error.message} />
      ) : (data?.duLieu.length ?? 0) === 0 ? (
        <OTrong tieuDe="Chưa có thông báo" moTa="Các cập nhật về đơn hàng, đánh giá và khuyến mãi sẽ xuất hiện tại đây." />
      ) : (
        <div className="grid gap-3">
          {data?.duLieu.map((thongBao) => {
            const loai = nhanLoaiThongBao(thongBao.loai);
            return (
              <button
                key={thongBao.maThongBao}
                type="button"
                onClick={() => chonThongBao(thongBao)}
                className="text-left"
                disabled={danhDauDaDoc.isPending}
              >
                <div className="relative">
                  <TomTatThongBao thongBao={thongBao} />
                  <span className={`absolute right-3 top-3 rounded px-2 py-1 text-[11px] font-bold ${loai.lop}`}>{loai.ten}</span>
                </div>
              </button>
            );
          })}
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
