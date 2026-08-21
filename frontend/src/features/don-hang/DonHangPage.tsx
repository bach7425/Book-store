import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { Link } from 'react-router-dom';
import { donHangApi } from '../../api/donHangApi';
import { Bang, OTrong } from '../../components/ui/Bang';
import { Button } from '../../components/ui/Button';
import { TrangThai } from '../../components/ui/TrangThai';
import { useToastStore } from '../../components/ui/toastStore';
import type { DonHang } from '../../types';
import { dinhDangTien, tenTrangThai } from '../../utils/dinhDang';
import { chuanHoaVaiTro } from '../../utils/vaiTro';
import { useXacThucStore } from '../xac-thuc/xacThucStore';

function coTheHuyDon(don: DonHang) {
  return don.trangThai === 'CHO_XU_LY' || don.trangThai === 'DA_XAC_NHAN';
}

function coTheThanhToan(don: DonHang) {
  return don.trangThai !== 'DA_HUY' && don.trangThaiThanhToan !== 'DA_THANH_TOAN' && don.trangThaiThanhToan !== 'THAT_BAI';
}

export function DonHangPage() {
  const queryClient = useQueryClient();
  const baoTin = useToastStore((state) => state.baoTin);
  const baoLoi = useToastStore((state) => state.baoLoi);
  const nguoiDung = useXacThucStore((state) => state.nguoiDung);
  const laQuanTriVien = chuanHoaVaiTro(nguoiDung?.vaiTro) === 'ROLE_QUAN_TRI_VIEN';
  const { data, isLoading, error } = useQuery({ queryKey: ['don-hang'], queryFn: () => donHangApi.layDanhSachDonHang() });
  const useTaoMutationDonHang = (fn: (id: number) => Promise<unknown>, thongBao: string) => useMutation({
    mutationFn: fn,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['don-hang'] });
      baoTin(thongBao);
    },
    onError: (err) => baoLoi(err.message),
  });
  const huy = useTaoMutationDonHang(donHangApi.huyDonHang, 'Đã hủy đơn hàng');
  const thanhToan = useTaoMutationDonHang(donHangApi.thanhToanDonHang, 'Đã thanh toán đơn hàng');
  const dangThaoTac = huy.isPending || thanhToan.isPending;

  return (
    <div className="mx-auto max-w-7xl px-4 py-10 md:px-10">
      <div className="mb-8 border-b border-[#c4c6cd] pb-5">
        <h1 className="font-serif-display mt-1 text-4xl font-bold text-[#03192e]">Đơn hàng của tôi</h1>
        <p className="mt-1 text-sm text-[#43474d]">{data?.tongSoPhanTu ?? 0} đơn hàng</p>
      </div>
      {isLoading ? (
        <p className="mt-4 text-stone-500">Đang tải đơn hàng...</p>
      ) : error ? (
        <OTrong tieuDe="Không tải được đơn hàng" moTa={error.message} />
      ) : (data?.duLieu.length ?? 0) === 0 ? (
        <OTrong tieuDe="Chưa có đơn hàng" />
      ) : (
        <div>
          <Bang>
            <table className="w-full min-w-[860px] text-left text-sm">
              <thead className="du-lieu-heading">
                <tr>
                  <th className="px-4 py-3">Mã đơn</th>
                  <th className="px-4 py-3">Trạng thái</th>
                  <th className="px-4 py-3">Thanh toán</th>
                  <th className="px-4 py-3">Tổng tiền</th>
                  <th className="px-4 py-3"></th>
                </tr>
              </thead>
              <tbody className="divide-y divide-[#ece6df]">
                {data?.duLieu.map((don) => (
                  <tr className="du-lieu-row" key={don.maDonHang}>
                    <td className="font-mono-label px-4 py-3 text-[#03192e]">#{don.maDonHang}</td>
                    <td className="px-4 py-3"><TrangThai giaTri={don.trangThai} /></td>
                    <td className="px-4 py-3 text-[#43474d]">{tenTrangThai(don.trangThaiThanhToan)}</td>
                    <td className="font-mono-label px-4 py-3 font-semibold text-[#7d562d]">{dinhDangTien(don.tongTienThanhToan)}</td>
                    <td className="px-4 py-3">
                      <div className="flex flex-wrap justify-end gap-2">
                        {!laQuanTriVien && coTheThanhToan(don) ? (
                          <Button kieu="phu" disabled={dangThaoTac} onClick={() => thanhToan.mutate(don.maDonHang)}>
                            Thanh toán
                          </Button>
                        ) : null}
                        {!laQuanTriVien && coTheHuyDon(don) ? (
                          <Button kieu="canh-bao" disabled={dangThaoTac} onClick={() => huy.mutate(don.maDonHang)}>
                            Hủy
                          </Button>
                        ) : null}
                        <Link to={`/don-hang/${don.maDonHang}`} className="inline-flex min-h-10 items-center justify-center rounded bg-white px-4 py-2 text-sm font-bold text-[#7d562d] ring-1 ring-[#d8c6b4] transition hover:bg-[#fff7ef]">
                          Xem chi tiết
                        </Link>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </Bang>
        </div>
      )}
    </div>
  );
}
