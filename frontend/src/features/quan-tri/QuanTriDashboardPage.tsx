import { useMemo, useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { Bar, BarChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts';
import { quanTriApi } from '../../api/quanTriApi';
import { Bang, DangTai, OTrong } from '../../components/ui/Bang';
import { Button } from '../../components/ui/Button';
import { Input } from '../../components/ui/Input';
import { PhanTrang } from '../../components/ui/PhanTrang';
import { dinhDangNgay, dinhDangTien, duongDanAnh } from '../../utils/dinhDang';

function dinhDangInputDate(ngay: Date) {
  return ngay.toISOString().slice(0, 10);
}

function khoangThangHienTai() {
  const homNay = new Date();
  const ngayDauThang = new Date(homNay.getFullYear(), homNay.getMonth(), 1);
  return {
    tuNgay: dinhDangInputDate(ngayDauThang),
    denNgay: dinhDangInputDate(homNay),
  };
}

export function QuanTriDashboardPage() {
  const [boLoc, setBoLoc] = useState(khoangThangHienTai);
  const [trangChiTiet, setTrangChiTiet] = useState(0);
  const params = useMemo(() => ({ tuNgay: boLoc.tuNgay || undefined, denNgay: boLoc.denNgay || undefined }), [boLoc]);
  const ngayKhongHopLe = Boolean(boLoc.tuNgay && boLoc.denNgay && boLoc.denNgay < boLoc.tuNgay);

  const { data: baoCao, error, isLoading } = useQuery({
    queryKey: ['quan-tri', 'bao-cao', params],
    queryFn: () => quanTriApi.layBaoCaoDoanhThu(params),
    enabled: !ngayKhongHopLe,
  });
  const { data: topBanChay, isLoading: dangTaiTopBanChay } = useQuery({
    queryKey: ['quan-tri', 'sach-ban-chay', 'top', params],
    queryFn: () => quanTriApi.laySachBanChay({ ...params, size: 5 }),
    enabled: !ngayKhongHopLe,
  });
  const { data: chiTietBanChay, isLoading: dangTaiChiTietBanChay } = useQuery({
    queryKey: ['quan-tri', 'sach-ban-chay', 'chi-tiet', params, trangChiTiet],
    queryFn: () => quanTriApi.laySachBanChay({ ...params, page: trangChiTiet, size: 10 }),
    enabled: !ngayKhongHopLe,
  });

  const sachBanChayTop = topBanChay?.duLieu ?? [];
  const sachBanChayChiTiet = chiTietBanChay?.duLieu ?? [];

  const capNhatBoLoc = (truong: 'tuNgay' | 'denNgay', giaTri: string) => {
    setTrangChiTiet(0);
    setBoLoc((hienTai) => ({ ...hienTai, [truong]: giaTri }));
  };

  const datLaiThangNay = () => {
    setTrangChiTiet(0);
    setBoLoc(khoangThangHienTai());
  };

  return (
    <div className="grid gap-6">
      <section className="flex flex-wrap items-start justify-between gap-4">
        <div className="max-w-2xl">
          <p className="archival-label text-[#7d562d]">Báo cáo vận hành</p>
          <h1 className="font-serif-display mt-1 text-4xl font-bold text-[#03192e]">Tổng quan quản trị</h1>
          
        </div>
        <div className="flex flex-wrap items-end gap-2">
          <label className="text-sm font-semibold text-[#03192e]">
            Từ ngày
            <Input className="mt-1 min-w-40" type="date" value={boLoc.tuNgay} onChange={(event) => capNhatBoLoc('tuNgay', event.target.value)} />
          </label>
          <label className="text-sm font-semibold text-[#03192e]">
            Đến ngày
            <Input className="mt-1 min-w-40" type="date" value={boLoc.denNgay} onChange={(event) => capNhatBoLoc('denNgay', event.target.value)} />
          </label>
          <Button type="button" kieu="phu" onClick={datLaiThangNay}>Tháng này</Button>
        </div>
      </section>

      {ngayKhongHopLe ? <OTrong tieuDe="Khoảng ngày không hợp lệ" moTa="Ngày kết thúc phải sau hoặc bằng ngày bắt đầu." /> : null}
      {error ? <OTrong tieuDe="Không tải được báo cáo" moTa={error.message} /> : null}
      {isLoading || dangTaiTopBanChay || dangTaiChiTietBanChay ? <DangTai /> : null}

      {!ngayKhongHopLe && !isLoading ? (
        <>
          <section className="grid gap-4 md:grid-cols-3">
            <div className="paper-panel rounded p-5">
              <p className="archival-label text-[#43474d]">Doanh thu</p>
              <strong className="font-mono-label mt-3 block text-2xl text-[#7d562d]">{dinhDangTien(baoCao?.tongDoanhThu)}</strong>
              <p className="mt-2 text-xs font-semibold text-[#74777d]">{dinhDangNgay(baoCao?.tuNgay)} - {dinhDangNgay(baoCao?.denNgay)}</p>
            </div>
            <div className="paper-panel rounded p-5">
              <p className="archival-label text-[#43474d]">Đơn đã giao & thanh toán</p>
              <strong className="font-serif-display mt-3 block text-3xl text-[#03192e]">{baoCao?.soDonDaThanhToan ?? 0}</strong>
            </div>
            <div className="paper-panel rounded p-5">
              <p className="archival-label text-[#43474d]">Đầu sách có doanh thu</p>
              <strong className="font-serif-display mt-3 block text-3xl text-[#03192e]">{chiTietBanChay?.tongSoPhanTu ?? 0}</strong>
            </div>
          </section>

          <section className="grid gap-6 xl:grid-cols-[minmax(0,1fr)_380px]">
            <div className="paper-panel rounded p-5">
              <div>
                <h2 className="font-serif-display text-2xl font-bold text-[#03192e]">Doanh thu theo sách</h2>
                
              </div>
              {sachBanChayTop.length ? (
                <div className="mt-5 h-80">
                  <ResponsiveContainer width="100%" height="100%">
                    <BarChart data={sachBanChayTop} layout="vertical" margin={{ left: 8, right: 24 }}>
                      <CartesianGrid stroke="#e2d9d0" strokeDasharray="3 3" />
                      <XAxis type="number" tickFormatter={(value) => `${Math.round(Number(value) / 1000)}k`} />
                      <YAxis type="category" dataKey="tenSach" width={130} tick={{ fontSize: 12 }} />
                      <Tooltip formatter={(value) => dinhDangTien(Number(value))} />
                      <Bar dataKey="doanhThu" fill="#7d562d" radius={[0, 4, 4, 0]} />
                    </BarChart>
                  </ResponsiveContainer>
                </div>
              ) : (
                <div className="mt-5"><OTrong tieuDe="Chưa có doanh thu" moTa="Không có sách bán ra trong khoảng ngày này." /></div>
              )}
            </div>

            <aside className="paper-panel rounded p-5">
              <h2 className="font-serif-display text-2xl font-bold text-[#03192e]">Top sách bán chạy</h2>
              <div className="mt-4 divide-y divide-[#ece6df]">
                {sachBanChayTop.length ? sachBanChayTop.map((item, index) => (
                  <div key={item.maSach} className="flex items-center gap-3 py-3 first:pt-0 last:pb-0">
                    <span className="font-mono-label flex size-8 shrink-0 items-center justify-center rounded bg-[#f1e8df] text-[#7d562d]">{index + 1}</span>
                    {item.anhBia ? <img src={duongDanAnh(item.anhBia)} alt={item.tenSach} className="h-14 w-10 rounded object-cover" /> : null}
                    <div className="min-w-0 flex-1">
                      <p className="truncate font-semibold text-[#03192e]">{item.tenSach}</p>
                      <p className="truncate text-xs text-[#74777d]">{item.tacGia || 'Chưa có tác giả'}</p>
                    </div>
                    <div className="text-right">
                      <p className="font-mono-label font-semibold text-[#03192e]">{item.soLuongBan}</p>
                      <p className="text-xs text-[#74777d]">cuốn</p>
                    </div>
                  </div>
                )) : <OTrong tieuDe="Chưa có sách bán chạy" />}
              </div>
            </aside>
          </section>

          <section>
            <div className="mb-3">
              <h2 className="font-serif-display text-2xl font-bold text-[#03192e]">Chi tiết sách bán chạy</h2>
            </div>
            <Bang>
              <table className="w-full min-w-[760px] text-left text-sm">
                <thead className="du-lieu-heading">
                  <tr>
                    <th className="px-4 py-3">STT</th>
                    <th className="px-4 py-3">Sách</th>
                    <th className="px-4 py-3">Tác giả</th>
                    <th className="px-4 py-3">Số lượng bán</th>
                    <th className="px-4 py-3">Doanh thu</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-[#ece6df]">
                  {sachBanChayChiTiet.map((item, index) => (
                    <tr className="du-lieu-row" key={item.maSach}>
                      <td className="font-mono-label px-4 py-3 text-[#74777d]">{trangChiTiet * 10 + index + 1}</td>
                      <td className="px-4 py-3 font-semibold text-[#03192e]">{item.tenSach}</td>
                      <td className="px-4 py-3 text-[#43474d]">{item.tacGia || 'Chưa có'}</td>
                      <td className="font-mono-label px-4 py-3">{item.soLuongBan}</td>
                      <td className="font-mono-label px-4 py-3 text-[#7d562d]">{dinhDangTien(item.doanhThu)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </Bang>
            <div className="mt-4">
              <PhanTrang data={chiTietBanChay} page={trangChiTiet} onPageChange={setTrangChiTiet} />
            </div>
          </section>
        </>
      ) : null}
    </div>
  );
}
