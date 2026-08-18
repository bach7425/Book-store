import { useQuery } from '@tanstack/react-query';
import { useState } from 'react';
import { quanTriApi } from '../../api/quanTriApi';
import { AvatarNguoiDung } from '../../components/ui/AvatarNguoiDung';
import { Bang } from '../../components/ui/Bang';
import { Input } from '../../components/ui/Input';
import { PhanTrang } from '../../components/ui/PhanTrang';
import { dinhDangTien } from '../../utils/dinhDang';

export function QuanTriKhachHangPage() {
  const [tuKhoa, setTuKhoa] = useState('');
  const [page, setPage] = useState(0);
  const { data } = useQuery({
    queryKey: ['quan-tri', 'khach-hang', tuKhoa, page],
    queryFn: () => quanTriApi.layKhachHang({ tuKhoa: tuKhoa || undefined, page }),
  });

  return (
    <div>
      <div className="mb-4 flex flex-wrap items-center justify-between gap-3">
        <div>
          <p className="archival-label text-[#7d562d]">Người mua</p>
          <h1 className="font-serif-display mt-1 text-3xl font-bold text-[#03192e]">Khách hàng</h1>
        </div>
        <Input
          className="max-w-xs"
          placeholder="Tìm khách hàng"
          value={tuKhoa}
          onChange={(event) => {
            setTuKhoa(event.target.value);
            setPage(0);
          }}
        />
      </div>
      <Bang>
        <table className="w-full min-w-[760px] text-left text-sm">
          <thead className="du-lieu-heading">
            <tr>
              <th className="px-4 py-3">Khách hàng</th>
              <th className="px-4 py-3">Liên hệ</th>
              <th className="px-4 py-3">Số đơn</th>
              <th className="px-4 py-3">Tổng chi tiêu</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-[#ece6df]">
            {(data?.duLieu ?? []).map((khach) => (
              <tr className="du-lieu-row" key={khach.maNguoiDung}>
                <td className="px-4 py-3">
                  <div className="flex items-center gap-3">
                    <AvatarNguoiDung ten={khach.hoVaTen} anhDaiDien={khach.anhDaiDien} />
                    <div className="min-w-0 font-semibold text-[#03192e]">
                      <span className="block truncate">{khach.hoVaTen}</span>
                      <span className="block truncate font-normal text-[#43474d]">{khach.tenDangNhap}</span>
                    </div>
                  </div>
                </td>
                <td className="px-4 py-3 text-[#43474d]">{khach.email}<br />{khach.soDienThoai}</td>
                <td className="font-mono-label px-4 py-3">{khach.soDonHang ?? 0}</td>
                <td className="font-mono-label px-4 py-3 text-[#7d562d]">{dinhDangTien(khach.tongChiTieu)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </Bang>
      <div className="mt-4">
        <PhanTrang data={data} page={page} onPageChange={setPage} />
      </div>
    </div>
  );
}
