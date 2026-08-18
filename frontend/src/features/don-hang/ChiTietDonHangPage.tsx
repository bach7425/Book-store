import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { Link, useParams } from 'react-router-dom';
import { donHangApi } from '../../api/donHangApi';
import { Bang, OTrong } from '../../components/ui/Bang';
import { Button } from '../../components/ui/Button';
import { NutQuayLai } from '../../components/ui/NutQuayLai';
import { TrangThai } from '../../components/ui/TrangThai';
import { useToastStore } from '../../components/ui/toastStore';
import type { DonHang } from '../../types';
import { dinhDangNgay, dinhDangTien, tenTrangThai } from '../../utils/dinhDang';
import { chuanHoaVaiTro } from '../../utils/vaiTro';
import { useXacThucStore } from '../xac-thuc/xacThucStore';

function coTheHuyDon(don: DonHang) {
  return don.trangThai === 'CHO_XU_LY' || don.trangThai === 'DA_XAC_NHAN';
}

function coTheThanhToan(don: DonHang) {
  return don.trangThai !== 'DA_HUY' && don.trangThaiThanhToan !== 'DA_THANH_TOAN' && don.trangThaiThanhToan !== 'THAT_BAI';
}

function hienThi(giaTri?: string | null) {
  return giaTri?.trim() || 'Chưa có';
}

export function ChiTietDonHangPage() {
  const { maDonHang } = useParams();
  const maDonHangSo = Number(maDonHang);
  const queryClient = useQueryClient();
  const baoTin = useToastStore((state) => state.baoTin);
  const baoLoi = useToastStore((state) => state.baoLoi);
  const nguoiDung = useXacThucStore((state) => state.nguoiDung);
  const laQuanTriVien = chuanHoaVaiTro(nguoiDung?.vaiTro) === 'ROLE_QUAN_TRI_VIEN';

  const { data: donHang, isLoading, error } = useQuery({
    queryKey: ['don-hang', maDonHangSo],
    queryFn: () => donHangApi.layChiTietDonHang(maDonHangSo),
    enabled: Number.isFinite(maDonHangSo) && maDonHangSo > 0,
  });

  const useTaoMutationDonHang = (fn: (id: number) => Promise<unknown>, thongBao: string) => useMutation({
    mutationFn: fn,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['don-hang'] });
      queryClient.invalidateQueries({ queryKey: ['don-hang', maDonHangSo] });
      baoTin(thongBao);
    },
    onError: (err) => baoLoi(err.message),
  });

  const huy = useTaoMutationDonHang(donHangApi.huyDonHang, 'Đã hủy đơn hàng');
  const thanhToan = useTaoMutationDonHang(donHangApi.thanhToanDonHang, 'Đã thanh toán đơn hàng');
  const dangThaoTac = huy.isPending || thanhToan.isPending;

  if (!Number.isFinite(maDonHangSo) || maDonHangSo <= 0) {
    return (
      <div className="mx-auto max-w-4xl px-4 py-8">
        <NutQuayLai veDau="/don-hang" className="mb-6" />
        <OTrong tieuDe="Mã đơn hàng không hợp lệ" />
      </div>
    );
  }

  if (isLoading) {
    return (
      <div className="mx-auto max-w-5xl px-4 py-8">
        <NutQuayLai veDau="/don-hang" className="mb-6" />
        <div className="grid gap-4">
          <div className="h-36 animate-pulse rounded bg-[#e4e2e2]" />
          <div className="h-72 animate-pulse rounded bg-[#e4e2e2]" />
        </div>
      </div>
    );
  }

  if (error || !donHang) {
    return (
      <div className="mx-auto max-w-4xl px-4 py-8">
        <NutQuayLai veDau="/don-hang" className="mb-6" />
        <OTrong tieuDe="Không tải được chi tiết đơn hàng" moTa={error?.message} />
      </div>
    );
  }

  const sanPhams = donHang.items ?? donHang.sanPhams ?? [];

  return (
    <div className="mx-auto max-w-6xl px-4 py-8">
      <NutQuayLai veDau="/don-hang" className="mb-6" />
      <div className="paper-panel rounded p-6">
        <div className="flex flex-col gap-4 md:flex-row md:items-start md:justify-between">
          <div>
            <p className="archival-label text-[#7d562d]">Chi tiết đơn hàng</p>
            <h1 className="font-serif-display mt-1 text-4xl font-bold text-[#03192e]">Đơn hàng #{donHang.maDonHang}</h1>
            <div className="mt-4 flex flex-wrap gap-2">
              <TrangThai giaTri={donHang.trangThai} />
              <span className="rounded bg-[#f5f3f3] px-3 py-1 text-xs font-bold text-[#43474d]">{tenTrangThai(donHang.trangThaiThanhToan)}</span>
              {donHang.phuongThucThanhToan ? <span className="rounded bg-[#fff7ef] px-3 py-1 text-xs font-bold text-[#7d562d]">{tenTrangThai(donHang.phuongThucThanhToan)}</span> : null}
            </div>
          </div>
          <div className="flex flex-wrap gap-2">
            {!laQuanTriVien && coTheThanhToan(donHang) ? <Button kieu="phu" disabled={dangThaoTac} onClick={() => thanhToan.mutate(donHang.maDonHang)}>Thanh toán</Button> : null}
            {!laQuanTriVien && coTheHuyDon(donHang) ? <Button kieu="canh-bao" disabled={dangThaoTac} onClick={() => huy.mutate(donHang.maDonHang)}>Hủy</Button> : null}
          </div>
        </div>
      </div>

      <section className="paper-panel mt-6 rounded p-5">
        <h2 className="font-serif-display text-2xl font-bold text-[#03192e]">Thông tin đơn hàng</h2>
        <div className="mt-4 grid gap-5 md:grid-cols-3">
          <div>
            <p className="archival-label text-[#7d562d]">Khách hàng</p>
            <p className="mt-2 font-semibold text-[#03192e]">{hienThi(donHang.tenKhachHang)}</p>
            <p className="mt-1 break-words text-sm text-[#43474d]">{hienThi(donHang.emailKhachHang)}</p>
            <p className="mt-1 text-sm font-semibold text-[#74777d]">{hienThi(donHang.soDienThoaiKhachHang)}</p>
          </div>
          <div>
            <p className="archival-label text-[#7d562d]">Giao hàng</p>
            <p className="mt-2 font-semibold text-[#03192e]">{hienThi(donHang.nguoiNhan)}</p>
            <p className="mt-1 text-sm font-semibold text-[#74777d]">{hienThi(donHang.soDienThoaiNhan)}</p>
            <p className="mt-1 break-words text-sm leading-6 text-[#43474d]">{hienThi(donHang.diaChiGiaoHang)}</p>
          </div>
          <div>
            <p className="archival-label text-[#7d562d]">Thanh toán</p>
            <div className="mt-2 space-y-1 text-sm">
              <p><span className="font-semibold text-[#74777d]">Ngày đặt:</span> {dinhDangNgay(donHang.ngayTao)}</p>
              <p><span className="font-semibold text-[#74777d]">Phương thức:</span> {tenTrangThai(donHang.phuongThucThanhToan)}</p>
              <p><span className="font-semibold text-[#74777d]">Trạng thái:</span> {tenTrangThai(donHang.trangThaiThanhToan)}</p>
              <p><span className="font-semibold text-[#74777d]">Số tiền:</span> <span className="font-mono-label text-[#7d562d]">{dinhDangTien(donHang.soTienThanhToan)}</span></p>
              <p><span className="font-semibold text-[#74777d]">Thời gian thanh toán:</span> {dinhDangNgay(donHang.thoiGianThanhToan)}</p>
            </div>
          </div>
        </div>
      </section>

      <div className="mt-6 grid gap-6 lg:grid-cols-[1fr_340px]">
        <section className="paper-panel rounded p-5">
          <h2 className="font-serif-display text-2xl font-bold text-[#03192e]">Sách trong đơn</h2>
          <div className="mt-4">
            <Bang>
              <table className="w-full min-w-[720px] text-left text-sm">
                <thead className="du-lieu-heading">
                  <tr>
                    <th className="px-4 py-3">Sách</th>
                    <th className="px-4 py-3">Số lượng</th>
                    <th className="px-4 py-3">Đơn giá</th>
                    <th className="px-4 py-3">Thành tiền</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-[#ece6df]">
                  {sanPhams.map((item) => (
                    <tr className="du-lieu-row" key={`${donHang.maDonHang}-${item.maSach}`}>
                      <td className="px-4 py-3">
                        <Link to={`/sach/${item.maSach}`} className="font-semibold text-[#03192e] hover:text-[#7d562d]">{item.tenSach}</Link>
                        <span className="font-mono-label mt-1 block text-xs text-[#74777d]">Mã sách #{item.maSach}</span>
                      </td>
                      <td className="font-mono-label px-4 py-3 text-[#43474d]">{item.soLuong}</td>
                      <td className="font-mono-label px-4 py-3 text-[#43474d]">{dinhDangTien(item.donGia)}</td>
                      <td className="font-mono-label px-4 py-3 font-semibold text-[#7d562d]">{dinhDangTien(item.thanhTien)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </Bang>
          </div>
        </section>

        <aside className="paper-panel h-fit rounded p-5">
          <h2 className="font-serif-display text-2xl font-bold text-[#03192e]">Tóm tắt tiền</h2>
          <div className="mt-4 space-y-3 text-sm">
            <div className="flex justify-between gap-4"><span className="text-[#43474d]">Tạm tính</span><strong className="font-mono-label text-[#03192e]">{dinhDangTien(donHang.tongTien)}</strong></div>
            <div className="flex justify-between gap-4"><span className="text-[#43474d]">Phí vận chuyển</span><strong className="font-mono-label text-[#03192e]">{dinhDangTien(donHang.phiVanChuyen)}</strong></div>
            {donHang.soTienGiam > 0 ? <div className="flex justify-between gap-4"><span className="text-[#43474d]">Giảm giá{donHang.maGiamGia ? ` (${donHang.maGiamGia})` : ''}</span><strong className="font-mono-label text-[#246b32]">- {dinhDangTien(donHang.soTienGiam)}</strong></div> : null}
            <div className="border-t border-[#ece6df] pt-3">
              <div className="flex justify-between gap-4 text-base"><span className="font-bold text-[#03192e]">Tổng thanh toán</span><strong className="font-mono-label text-xl text-[#7d562d]">{dinhDangTien(donHang.tongTienThanhToan)}</strong></div>
            </div>
          </div>
        </aside>
      </div>
    </div>
  );
}
