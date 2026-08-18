import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { Link, useParams } from 'react-router-dom';
import { donHangApi } from '../../api/donHangApi';
import { quanTriApi } from '../../api/quanTriApi';
import { Bang, OTrong } from '../../components/ui/Bang';
import { Button } from '../../components/ui/Button';
import { NutQuayLai } from '../../components/ui/NutQuayLai';
import { TrangThai } from '../../components/ui/TrangThai';
import { useToastStore } from '../../components/ui/toastStore';
import type { DonHang } from '../../types';
import { dinhDangNgay, dinhDangTien, tenTrangThai } from '../../utils/dinhDang';

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

export function QuanTriChiTietDonHangPage() {
  const { maDonHang } = useParams();
  const maDonHangSo = Number(maDonHang);
  const queryClient = useQueryClient();
  const baoTin = useToastStore((state) => state.baoTin);
  const baoLoi = useToastStore((state) => state.baoLoi);

  const { data: donHang, isLoading, error } = useQuery({
    queryKey: ['quan-tri', 'don-hang', maDonHangSo],
    queryFn: () => donHangApi.layChiTietDonHang(maDonHangSo),
    enabled: Number.isFinite(maDonHangSo) && maDonHangSo > 0,
  });

  const capNhat = useMutation({
    mutationFn: (trangThaiMoi: string) => quanTriApi.capNhatTrangThaiDonHang(maDonHangSo, trangThaiMoi),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['quan-tri', 'don-hang'] });
      queryClient.invalidateQueries({ queryKey: ['quan-tri', 'don-hang', maDonHangSo] });
      baoTin('Đã cập nhật trạng thái đơn');
    },
    onError: (err) => baoLoi(err.message),
  });

  if (!Number.isFinite(maDonHangSo) || maDonHangSo <= 0) {
    return (
      <div>
        <NutQuayLai veDau="/quan-tri/don-hang" className="mb-6" />
        <OTrong tieuDe="Mã đơn hàng không hợp lệ" />
      </div>
    );
  }

  if (isLoading) {
    return (
      <div>
        <NutQuayLai veDau="/quan-tri/don-hang" className="mb-6" />
        <div className="grid gap-4">
          <div className="h-36 animate-pulse rounded bg-[#e4e2e2]" />
          <div className="h-72 animate-pulse rounded bg-[#e4e2e2]" />
        </div>
      </div>
    );
  }

  if (error || !donHang) {
    return (
      <div>
        <NutQuayLai veDau="/quan-tri/don-hang" className="mb-6" />
        <OTrong tieuDe="Không tải được chi tiết đơn hàng" moTa={error?.message} />
      </div>
    );
  }

  const sanPhams = donHang.sanPhams ?? donHang.items ?? [];
  const trangThaiHopLe = trangThaiTiepTheo(donHang);

  return (
    <div>
      <NutQuayLai veDau="/quan-tri/don-hang" className="mb-6" />
      <div className="paper-panel rounded p-6">
        <div className="flex flex-col gap-4 md:flex-row md:items-start md:justify-between">
          <div>
            <p className="archival-label text-[#7d562d]">Chi tiết vận đơn</p>
            <h1 className="font-serif-display mt-1 text-3xl font-bold text-[#03192e]">Đơn hàng #{donHang.maDonHang}</h1>
            <div className="mt-4 flex flex-wrap gap-2">
              <TrangThai giaTri={donHang.trangThai} />
              <span className="rounded bg-[#f5f3f3] px-3 py-1 text-xs font-bold text-[#43474d]">{tenTrangThai(donHang.trangThaiThanhToan)}</span>
              {donHang.phuongThucThanhToan ? <span className="rounded bg-[#fff7ef] px-3 py-1 text-xs font-bold text-[#7d562d]">{tenTrangThai(donHang.phuongThucThanhToan)}</span> : null}
            </div>
          </div>
          {trangThaiHopLe.length > 0 ? (
            <div className="flex flex-wrap gap-2">
              {trangThaiHopLe.map((trangThaiMoi) => (
                <Button key={trangThaiMoi} kieu={trangThaiMoi === 'DA_HUY' ? 'canh-bao' : 'phu'} disabled={capNhat.isPending} onClick={() => capNhat.mutate(trangThaiMoi)}>
                  {tenThaoTacTrangThai(trangThaiMoi)}
                </Button>
              ))}
            </div>
          ) : (
            <span className="rounded bg-[#efeded] px-3 py-2 text-sm font-bold text-[#74777d]">Không còn thao tác</span>
          )}
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

      <div className="mt-6 grid gap-6 xl:grid-cols-[1fr_340px]">
        <section className="paper-panel rounded p-5">
          <h2 className="font-serif-display text-2xl font-bold text-[#03192e]">Sản phẩm trong đơn</h2>
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
            <div className="border-t border-[#ece6df] pt-3 text-xs font-semibold text-[#74777d]">Thời gian thanh toán: {dinhDangNgay(donHang.thoiGianThanhToan)}</div>
          </div>
        </aside>
      </div>
    </div>
  );
}
