import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useState } from 'react';
import { Link } from 'react-router-dom';
import { quanTriApi } from '../../api/quanTriApi';
import { Bang } from '../../components/ui/Bang';
import { Button } from '../../components/ui/Button';
import { PhanTrang } from '../../components/ui/PhanTrang';
import { TrangThai } from '../../components/ui/TrangThai';
import { useToastStore } from '../../components/ui/toastStore';
import type { DonHang } from '../../types';
import { dinhDangTien, tenTrangThai } from '../../utils/dinhDang';

const trangThaiOptions = ['CHO_XU_LY', 'DA_XAC_NHAN', 'DANG_GIAO', 'DA_GIAO', 'DA_HUY'];

function trangThaiTiepTheo(don: DonHang) {
  const bangTrangThai: Record<string, string[]> = {
    CHO_XU_LY: ['DA_XAC_NHAN', 'DA_HUY'],
    DA_XAC_NHAN: ['DANG_GIAO'],
    DANG_GIAO: ['DA_GIAO'],
    DA_GIAO: [],
    DA_HUY: [],
  };
  return bangTrangThai[don.trangThai] ?? [];
}

function hienThi(giaTri?: string | null) {
  return giaTri?.trim() || 'Chưa có';
}

function tenThaoTacTrangThai(trangThai: string) {
  const bangTen: Record<string, string> = {
    DA_XAC_NHAN: 'Xác nhận',
    DA_HUY: 'Hủy',
    DANG_GIAO: 'Giao hàng',
    DA_GIAO: 'Hoàn tất',
  };
  return bangTen[trangThai] ?? tenTrangThai(trangThai);
}

export function QuanTriDonHangPage() {
  const [trangThai, setTrangThai] = useState('');
  const [page, setPage] = useState(0);
  const queryClient = useQueryClient();
  const baoTin = useToastStore((state) => state.baoTin);
  const baoLoi = useToastStore((state) => state.baoLoi);
  const { data } = useQuery({
    queryKey: ['quan-tri', 'don-hang', trangThai, page],
    queryFn: () => quanTriApi.layDonHangQuanTri({ trangThai: trangThai || undefined, page }),
  });

  const capNhat = useMutation({
    mutationFn: ({ maDonHang, trangThaiMoi }: { maDonHang: number; trangThaiMoi: string }) =>
      quanTriApi.capNhatTrangThaiDonHang(maDonHang, trangThaiMoi),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['quan-tri', 'don-hang'] });
      baoTin('Đã cập nhật trạng thái đơn');
    },
    onError: (err) => baoLoi(err.message),
  });

  return (
    <div>
      <div className="mb-4 flex flex-wrap items-center justify-between gap-3">
        <div>
          <p className="archival-label text-[#7d562d]">Xử lý vận đơn</p>
          <h1 className="font-serif-display mt-1 text-3xl font-bold text-[#03192e]">Quản lý đơn hàng</h1>
        </div>
        <select
          className="select-paper px-3 py-2"
          value={trangThai}
          onChange={(event) => {
            setTrangThai(event.target.value);
            setPage(0);
          }}
        >
          <option value="">Tất cả</option>
          {trangThaiOptions.map((item) => <option key={item} value={item}>{tenTrangThai(item)}</option>)}
        </select>
      </div>

      <Bang>
        <table className="w-full min-w-[1060px] text-left text-sm">
          <thead className="du-lieu-heading">
            <tr>
              <th className="px-4 py-3">Mã đơn</th>
              <th className="px-4 py-3">Trạng thái đơn hàng</th>
              <th className="px-4 py-3">Khách hàng</th>
              <th className="px-4 py-3">Địa chỉ</th>
              <th className="px-4 py-3">Thanh toán</th>
              <th className="px-4 py-3">Tiền</th>
              <th className="px-4 py-3">Thao tác</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-[#ece6df]">
            {(data?.duLieu ?? []).map((don) => {
              const trangThaiHopLe = trangThaiTiepTheo(don);

              return (
                <tr className="du-lieu-row align-top" key={don.maDonHang}>
                  <td className="font-mono-label px-4 py-3 text-[#03192e]">#{don.maDonHang}</td>
                  <td className="px-4 py-3"><TrangThai giaTri={don.trangThai} /></td>
                  <td className="px-4 py-3 font-semibold text-[#03192e]">{hienThi(don.tenKhachHang)}</td>
                  <td className="px-4 py-3">
                    <p className="max-w-72 break-words text-[#43474d]">{hienThi(don.diaChiGiaoHang)}</p>
                  </td>
                  <td className="px-4 py-3">
                    <div className="space-y-1 text-xs">
                      <p className="font-semibold text-[#03192e]">{tenTrangThai(don.phuongThucThanhToan)}</p>
                      <p className="font-semibold text-[#74777d]">{tenTrangThai(don.trangThaiThanhToan)}</p>
                    </div>
                  </td>
                  <td className="font-mono-label px-4 py-3 font-semibold text-[#7d562d]">{dinhDangTien(don.tongTienThanhToan)}</td>
                  <td className="px-4 py-3">
                    <div className="flex max-w-72 flex-wrap justify-end gap-2">
                      {trangThaiHopLe.map((trangThaiMoi) => (
                        <Button key={trangThaiMoi} kieu={trangThaiMoi === 'DA_HUY' ? 'canh-bao' : 'phu'} disabled={capNhat.isPending} onClick={() => capNhat.mutate({ maDonHang: don.maDonHang, trangThaiMoi })}>
                          {tenThaoTacTrangThai(trangThaiMoi)}
                        </Button>
                      ))}
                      <Link to={`/quan-tri/don-hang/${don.maDonHang}`} className="inline-flex min-h-10 items-center justify-center rounded bg-white px-4 py-2 text-sm font-bold text-[#7d562d] ring-1 ring-[#d8c6b4] transition hover:bg-[#fff7ef]">
                        Chi tiết
                      </Link>
                    </div>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </Bang>
      <div className="mt-4">
        <PhanTrang data={data} page={page} onPageChange={setPage} />
      </div>
    </div>
  );
}
