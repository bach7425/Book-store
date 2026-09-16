import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { CheckCircle2, MessageSquareText, X } from 'lucide-react';
import { Fragment, useState } from 'react';
import { quanTriApi } from '../../api/quanTriApi';
import { Bang } from '../../components/ui/Bang';
import { Button } from '../../components/ui/Button';
import { PhanTrang } from '../../components/ui/PhanTrang';
import { TrangThai } from '../../components/ui/TrangThai';
import { useToastStore } from '../../components/ui/toastStore';

type HanhDongDanhGia = 'duyet' | 'tu-choi';

function coTheThaoTac(trangThai?: string) {
  return trangThai === 'CHO_DUYET';
}

export function QuanTriDanhGiaPage() {
  const [page, setPage] = useState(0);
  const [maDanhGiaDangPhanHoi, setMaDanhGiaDangPhanHoi] = useState<number | null>(null);
  const [hanhDongDangChon, setHanhDongDangChon] = useState<HanhDongDanhGia | null>(null);
  const [phanHoi, setPhanHoi] = useState('');
  const queryClient = useQueryClient();
  const baoTin = useToastStore((state) => state.baoTin);
  const baoLoi = useToastStore((state) => state.baoLoi);
  const { data } = useQuery({
    queryKey: ['quan-tri', 'danh-gia', page],
    queryFn: () => quanTriApi.layDanhGiaQuanTri({ page }),
  });

  const useTaoMutationDanhGia = (fn: (id: number, phanHoi?: string) => Promise<unknown>, thongBao: string) => useMutation({
    mutationFn: ({ maDanhGia, noiDungPhanHoi }: { maDanhGia: number; noiDungPhanHoi?: string }) => fn(maDanhGia, noiDungPhanHoi),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['quan-tri', 'danh-gia'] });
      setMaDanhGiaDangPhanHoi(null);
      setHanhDongDangChon(null);
      setPhanHoi('');
      baoTin(thongBao);
    },
    onError: (err) => baoLoi(err.message),
  });

  const duyet = useTaoMutationDanhGia(quanTriApi.duyetDanhGia, 'Đã duyệt đánh giá');
  const tuChoi = useTaoMutationDanhGia(quanTriApi.tuChoiDanhGia, 'Đã từ chối đánh giá');
  const dangThaoTac = duyet.isPending || tuChoi.isPending;
  const tieuDeFormPhanHoi = hanhDongDangChon === 'duyet' ? 'Phản hồi khi duyệt' : 'Lý do từ chối';

  const moFormPhanHoi = (maDanhGia: number, hanhDong: HanhDongDanhGia, noiDungMacDinh = '') => {
    setMaDanhGiaDangPhanHoi(maDanhGia);
    setHanhDongDangChon(hanhDong);
    setPhanHoi(noiDungMacDinh);
  };

  const huyPhanHoi = () => {
    setMaDanhGiaDangPhanHoi(null);
    setHanhDongDangChon(null);
    setPhanHoi('');
  };

  const xacNhanXuLyDanhGia = (maDanhGia: number) => {
    const noiDungPhanHoi = phanHoi.trim();
    if (hanhDongDangChon === 'tu-choi' && !noiDungPhanHoi) {
      baoLoi('Vui lòng nhập lý do từ chối đánh giá');
      return;
    }

    if (hanhDongDangChon === 'duyet') {
      duyet.mutate({ maDanhGia, noiDungPhanHoi: noiDungPhanHoi || undefined });
      return;
    }

    if (hanhDongDangChon === 'tu-choi') {
      tuChoi.mutate({ maDanhGia, noiDungPhanHoi });
    }
  };

  return (
    <div>
      <p className="archival-label text-[#7d562d]">Kiểm duyệt nội dung</p>
      <h1 className="font-serif-display mb-4 mt-1 text-3xl font-bold text-[#03192e]">Duyệt đánh giá</h1>
      <Bang>
        <table className="w-full min-w-[920px] text-left text-sm">
          <thead className="du-lieu-heading">
            <tr>
              <th className="px-4 py-3">Sách</th>
              <th className="px-4 py-3">Người đánh giá</th>
              <th className="px-4 py-3">Nội dung</th>
              <th className="px-4 py-3">Sao</th>
              <th className="px-4 py-3">Trạng thái</th>
              <th className="px-4 py-3">Thao tác</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-[#ece6df]">
            {(data?.duLieu ?? []).map((item) => (
              <Fragment key={item.maDanhGia}>
                <tr className="du-lieu-row align-top">
                  <td className="px-4 py-3 font-semibold text-[#03192e]">{item.tenSach ?? item.maSach}</td>
                  <td className="px-4 py-3 font-semibold text-[#03192e]">{item.tenNguoiDung ?? 'Chưa có'}</td>
                  <td className="px-4 py-3 text-[#43474d]">
                    <p>{item.noiDung}</p>
                    {item.phanHoi ? (
                      <div className="mt-3 rounded border border-[#d8c6b4] bg-[#fff7ef] px-3 py-2 text-xs leading-5 text-[#43474d]">
                        <span className="font-bold text-[#7d562d]">Phản hồi:</span> {item.phanHoi}
                      </div>
                    ) : null}
                  </td>
                  <td className="font-mono-label px-4 py-3">{item.soSao}/5</td>
                  <td className="px-4 py-3"><TrangThai giaTri={item.trangThai} /></td>
                  <td className="px-4 py-3">
                    {coTheThaoTac(item.trangThai) ? (
                      <div className="flex justify-end gap-2">
                        <Button kieu="phu" disabled={dangThaoTac} onClick={() => moFormPhanHoi(item.maDanhGia, 'duyet', item.phanHoi ?? '')}>
                          <CheckCircle2 size={16} /> Duyệt
                        </Button>
                        <Button kieu="canh-bao" disabled={dangThaoTac} onClick={() => moFormPhanHoi(item.maDanhGia, 'tu-choi', item.phanHoi ?? '')}>
                          <X size={16} /> Từ chối
                        </Button>
                      </div>
                    ) : (
                      <span className="block text-right text-sm font-semibold text-[#74777d]">Không còn thao tác</span>
                    )}
                  </td>
                </tr>
                {maDanhGiaDangPhanHoi === item.maDanhGia && hanhDongDangChon ? (
                  <tr className="bg-[#fbf9f8]">
                    <td className="px-4 py-4" colSpan={6}>
                      <div className="rounded border border-[#e4e2e2] bg-white p-4">
                        <div className="flex flex-wrap items-center justify-between gap-3">
                          <p className="flex items-center gap-2 font-bold text-[#03192e]">
                            <MessageSquareText size={18} className="text-[#7d562d]" /> {tieuDeFormPhanHoi}
                          </p>
                          {hanhDongDangChon === 'duyet' ? (
                            <span className="text-xs font-semibold text-[#74777d]">Có thể để trống phản hồi.</span>
                          ) : (
                            <span className="text-xs font-semibold text-[#93000a]">Bắt buộc nhập lý do từ chối.</span>
                          )}
                        </div>
                        <textarea
                          className="mt-3 min-h-24 w-full rounded border border-[#c4c6cd] bg-white px-3 py-3 text-sm outline-none transition focus:border-[#7d562d] focus:ring-2 focus:ring-[#ffdcbd]"
                          placeholder={hanhDongDangChon === 'duyet' ? 'Nhập phản hồi cho khách hàng nếu cần...' : 'Nhập lý do từ chối đánh giá...'}
                          value={phanHoi}
                          onChange={(event) => setPhanHoi(event.target.value)}
                          disabled={dangThaoTac}
                        />
                        <div className="mt-3 flex flex-wrap justify-end gap-2">
                          <Button type="button" kieu="rong" disabled={dangThaoTac} onClick={huyPhanHoi}>Hủy</Button>
                          <Button type="button" kieu={hanhDongDangChon === 'duyet' ? 'chinh' : 'canh-bao'} disabled={dangThaoTac} onClick={() => xacNhanXuLyDanhGia(item.maDanhGia)}>
                            {hanhDongDangChon === 'duyet' ? <CheckCircle2 size={16} /> : <X size={16} />}
                            Xác nhận
                          </Button>
                        </div>
                      </div>
                    </td>
                  </tr>
                ) : null}
              </Fragment>
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
