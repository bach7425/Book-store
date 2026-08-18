import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { Bell } from 'lucide-react';
import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { thongBaoApi } from '../api/thongBaoApi';
import type { ThongBao } from '../types';
import { dinhDangNgay } from '../utils/dinhDang';
import { nhanLoaiThongBao } from '../utils/thongBaoHienThi';
import { useXacThucStore } from '../features/xac-thuc/xacThucStore';

function TomTatThongBao({ thongBao }: { thongBao: ThongBao }) {
  const loai = nhanLoaiThongBao(thongBao.loai);
  const Icon = loai.icon;

  return (
    <div className={`rounded border p-3 text-left transition ${thongBao.daDoc ? 'border-[#e4e2e2] bg-white' : 'border-[#d8c6b4] bg-[#fff7ef]'}`}>
      <div className="flex items-start gap-3">
        <span className={`mt-0.5 grid h-8 w-8 shrink-0 place-items-center rounded-full ${loai.lop}`}>
          <Icon size={16} />
        </span>
        <span className="min-w-0 flex-1">
          <span className="block truncate text-sm font-bold text-[#03192e]">{thongBao.tieuDe}</span>
          <span className="mt-1 line-clamp-2 block text-xs leading-5 text-[#43474d]">{thongBao.noiDung}</span>
          <span className="mt-2 block text-[11px] font-semibold text-[#7d562d]">{dinhDangNgay(thongBao.ngayTao)}</span>
        </span>
      </div>
    </div>
  );
}

export function ThongBaoChuong({ className = '' }: { className?: string }) {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const daDangNhap = useXacThucStore((state) => state.daDangNhap);
  const [dangMo, setDangMo] = useState(false);
  const { data: soChuaDoc } = useQuery({
    queryKey: ['thong-bao', 'chua-doc'],
    queryFn: thongBaoApi.demThongBaoChuaDoc,
    enabled: daDangNhap,
    refetchInterval: 30_000,
    meta: { anThongBao: true },
  });
  const { data: danhSach } = useQuery({
    queryKey: ['thong-bao', 'dropdown'],
    queryFn: () => thongBaoApi.layDanhSachThongBao(0, 5),
    enabled: daDangNhap && dangMo,
    meta: { anThongBao: true },
  });
  const danhDauDaDoc = useMutation({
    mutationFn: thongBaoApi.danhDauDaDoc,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['thong-bao'] });
    },
  });

  if (!daDangNhap) return null;

  const chonThongBao = (thongBao: ThongBao) => {
    if (!thongBao.daDoc) danhDauDaDoc.mutate(thongBao.maThongBao);
    setDangMo(false);
    if (thongBao.duongDan) navigate(thongBao.duongDan);
  };

  return (
    <div className={`relative ${className}`}>
      <button
        type="button"
        onClick={() => setDangMo((value) => !value)}
        className="relative rounded-full p-2 text-[#43474d] transition hover:bg-[#efeded] hover:text-[#7d562d]"
        aria-label="Thông báo"
      >
        <Bell size={19} />
        {(soChuaDoc ?? 0) > 0 ? (
          <span className="absolute -right-1 -top-1 grid min-h-5 min-w-5 place-items-center rounded-full bg-[#ba1a1a] px-1 text-[10px] font-bold text-white">
            {(soChuaDoc ?? 0) > 99 ? '99+' : soChuaDoc}
          </span>
        ) : null}
      </button>

      {dangMo ? (
        <div className="absolute right-0 top-12 z-50 w-[min(360px,calc(100vw-2rem))] rounded border border-[#c4c6cd] bg-[#fbf9f8] p-3 shadow-[0_18px_44px_rgba(26,46,68,0.16)]">
          <div className="mb-3 flex items-center justify-between gap-3">
            <p className="font-serif-display text-lg font-bold text-[#03192e]">Thông báo</p>
            <Link to="/thong-bao" onClick={() => setDangMo(false)} className="text-xs font-bold text-[#7d562d] hover:text-[#03192e]">
              Xem tất cả
            </Link>
          </div>
          <div className="grid max-h-[420px] gap-2 overflow-y-auto">
            {(danhSach?.duLieu.length ?? 0) === 0 ? (
              <p className="rounded border border-dashed border-[#c4c6cd] px-4 py-8 text-center text-sm text-[#43474d]">Chưa có thông báo mới.</p>
            ) : (
              danhSach?.duLieu.map((thongBao) => (
                <button key={thongBao.maThongBao} type="button" onClick={() => chonThongBao(thongBao)}>
                  <TomTatThongBao thongBao={thongBao} />
                </button>
              ))
            )}
          </div>
        </div>
      ) : null}
    </div>
  );
}

export { TomTatThongBao };
