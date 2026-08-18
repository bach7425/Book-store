import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useState } from 'react';
import { quanTriApi } from '../../api/quanTriApi';
import { Bang } from '../../components/ui/Bang';
import { Button } from '../../components/ui/Button';
import { PhanTrang } from '../../components/ui/PhanTrang';
import { TrangThai } from '../../components/ui/TrangThai';
import { useToastStore } from '../../components/ui/toastStore';

function coTheThaoTac(trangThai?: string) {
  return trangThai === 'CHO_DUYET';
}

export function QuanTriDanhGiaPage() {
  const [page, setPage] = useState(0);
  const queryClient = useQueryClient();
  const baoTin = useToastStore((state) => state.baoTin);
  const baoLoi = useToastStore((state) => state.baoLoi);
  const { data } = useQuery({
    queryKey: ['quan-tri', 'danh-gia', page],
    queryFn: () => quanTriApi.layDanhGiaQuanTri({ page }),
  });

  const useTaoMutationDanhGia = (fn: (id: number) => Promise<unknown>, thongBao: string) => useMutation({
    mutationFn: fn,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['quan-tri', 'danh-gia'] });
      baoTin(thongBao);
    },
    onError: (err) => baoLoi(err.message),
  });

  const duyet = useTaoMutationDanhGia(quanTriApi.duyetDanhGia, 'Đã duyệt đánh giá');
  const tuChoi = useTaoMutationDanhGia(quanTriApi.tuChoiDanhGia, 'Đã từ chối đánh giá');
  const dangThaoTac = duyet.isPending || tuChoi.isPending;

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
              <tr className="du-lieu-row align-top" key={item.maDanhGia}>
                <td className="px-4 py-3 font-semibold text-[#03192e]">{item.tenSach ?? item.maSach}</td>
                <td className="px-4 py-3 font-semibold text-[#03192e]">{item.tenNguoiDung ?? 'Chưa có'}</td>
                <td className="px-4 py-3 text-[#43474d]">{item.noiDung}</td>
                <td className="font-mono-label px-4 py-3">{item.soSao}/5</td>
                <td className="px-4 py-3"><TrangThai giaTri={item.trangThai} /></td>
                <td className="px-4 py-3">
                  {coTheThaoTac(item.trangThai) ? (
                    <div className="flex justify-end gap-2">
                      <Button kieu="phu" disabled={dangThaoTac} onClick={() => duyet.mutate(item.maDanhGia)}>Duyệt</Button>
                      <Button kieu="canh-bao" disabled={dangThaoTac} onClick={() => tuChoi.mutate(item.maDanhGia)}>Từ chối</Button>
                    </div>
                  ) : (
                    <span className="block text-right text-sm font-semibold text-[#74777d]">Không còn thao tác</span>
                  )}
                </td>
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
