import { useQuery } from '@tanstack/react-query';
import { Filter, Search } from 'lucide-react';
import { useSearchParams } from 'react-router-dom';
import { sachApi } from '../../api/sachApi';
import { DangTai, OTrong } from '../../components/ui/Bang';
import { Button } from '../../components/ui/Button';
import { Input } from '../../components/ui/Input';
import { SachCard } from './SachCard';
import { useMaSachYeuThich } from './useMaSachYeuThich';

const khoangGiaMacDinh = [
  { nhan: 'Tất cả', giaMin: '', giaMax: '' },
  { nhan: 'Dưới 100.000đ', giaMin: '', giaMax: '100000' },
  { nhan: '100.000đ - 200.000đ', giaMin: '100000', giaMax: '200000' },
  { nhan: '200.000đ - 500.000đ', giaMin: '200000', giaMax: '500000' },
  { nhan: 'Trên 500.000đ', giaMin: '500000', giaMax: '' },
];

export function DanhSachSachPage() {
  const [params, setParams] = useSearchParams();
  const maSachYeuThich = useMaSachYeuThich();
  const page = Number(params.get('page') ?? 0);
  const thamSo = {
    tuKhoa: params.get('tuKhoa') ?? undefined,
    theLoaiId: params.get('theLoaiId') ?? undefined,
    giaMin: params.get('giaMin') ?? undefined,
    giaMax: params.get('giaMax') ?? undefined,
    sort: params.get('sort') ?? 'tenSach',
    page,
    size: 12,
  };
  const { data, isLoading, error } = useQuery({ queryKey: ['sach', thamSo], queryFn: () => sachApi.layDanhSachSach(thamSo) });
  const { data: theLoai } = useQuery({ queryKey: ['the-loai'], queryFn: sachApi.layDanhSachTheLoai });
  const capNhat = (ten: string, giaTri: string) => {
    const next = new URLSearchParams(params);
    if (giaTri) next.set(ten, giaTri); else next.delete(ten);
    if (ten !== 'page') next.set('page', '0');
    setParams(next);
  };
  const capNhatKhoangGia = (giaMin: string, giaMax: string) => {
    const next = new URLSearchParams(params);
    if (giaMin) next.set('giaMin', giaMin); else next.delete('giaMin');
    if (giaMax) next.set('giaMax', giaMax); else next.delete('giaMax');
    next.set('page', '0');
    setParams(next);
  };

  return (
    <div className="mx-auto grid max-w-[1440px] gap-8 px-4 py-10 md:px-10 lg:grid-cols-[280px_1fr]">
      <aside className="paper-panel h-fit rounded p-5">
        <div className="flex items-center gap-2">
          <Filter className="text-[#7d562d]" size={20} />
          <h1 className="font-serif-display text-2xl font-bold text-[#03192e]">Bộ lọc sách</h1>
        </div>
        <div className="mt-5 space-y-5">
          <label className="block text-sm font-bold text-[#43474d]">
            Từ khóa
            <div className="relative mt-2">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-[#74777d]" size={17} />
              <Input className="pl-10" defaultValue={thamSo.tuKhoa} onBlur={(event) => capNhat('tuKhoa', event.target.value)} placeholder="Tên sách..." />
            </div>
          </label>
          <label className="block text-sm font-bold text-[#43474d]">
            Thể loại
            <select className="mt-2 min-h-10 w-full rounded border border-[#c4c6cd] bg-[#fbf9f8] px-3 text-sm" value={thamSo.theLoaiId ?? ''} onChange={(event) => capNhat('theLoaiId', event.target.value)}>
              <option value="">Tất cả thể loại</option>
              {(theLoai ?? []).map((item) => <option key={item.maTheLoai} value={item.maTheLoai}>{item.ten}</option>)}
            </select>
          </label>
          <div>
            <p className="text-sm font-bold text-[#43474d]">Khoảng giá</p>
            <div className="mt-2 flex flex-wrap gap-2">
              {khoangGiaMacDinh.map((item) => {
                const dangChon = (thamSo.giaMin ?? '') === item.giaMin && (thamSo.giaMax ?? '') === item.giaMax;
                return (
                  <button
                    key={item.nhan}
                    type="button"
                    className={`rounded border px-3 py-2 text-xs font-bold transition ${dangChon ? 'border-[#03192e] bg-[#03192e] text-white' : 'border-[#c4c6cd] bg-white text-[#43474d] hover:border-[#7d562d] hover:text-[#7d562d]'}`}
                    onClick={() => capNhatKhoangGia(item.giaMin, item.giaMax)}
                  >
                    {item.nhan}
                  </button>
                );
              })}
            </div>
            <div className="mt-3 grid grid-cols-2 gap-2">
              <Input placeholder="Từ" defaultValue={thamSo.giaMin} onBlur={(event) => capNhat('giaMin', event.target.value)} />
              <Input placeholder="Đến" defaultValue={thamSo.giaMax} onBlur={(event) => capNhat('giaMax', event.target.value)} />
            </div>
          </div>
        </div>
      </aside>

      <section>
        <div className="mb-6 flex flex-wrap items-end justify-between gap-4 border-b border-[#c4c6cd] pb-5">
          <div>
            
            <h2 className="font-serif-display mt-1 text-4xl font-bold text-[#03192e]">Thư viện sách</h2>
            <p className="mt-1 text-sm text-[#43474d]">{data?.tongSoPhanTu ?? 0} kết quả được tìm thấy</p>
          </div>
          <select className="rounded border border-[#c4c6cd] bg-white px-3 py-2 text-sm font-semibold text-[#43474d]" value={thamSo.sort} onChange={(event) => capNhat('sort', event.target.value)}>
            <option value="tenSach">Sắp xếp theo tên</option>
            <option value="gia">Sắp xếp theo giá</option>
            <option value="ngayXuatBan">Ngày xuất bản</option>
          </select>
        </div>
        {isLoading ? <DangTai /> : error ? <OTrong tieuDe="Không tải được sách" moTa={error.message} /> : (data?.duLieu.length ?? 0) === 0 ? <OTrong tieuDe="Chưa có sách phù hợp" /> : <div className="grid gap-6 sm:grid-cols-2 xl:grid-cols-3 2xl:grid-cols-4">{data?.duLieu.map((sach) => <SachCard key={sach.maSach} sach={sach} daYeuThich={maSachYeuThich.has(sach.maSach)} />)}</div>}
        {data?.trangTruoc || data?.tiepTheo ? (
          <div className="mt-8 flex justify-end gap-2">
            {data.trangTruoc ? <Button kieu="phu" onClick={() => capNhat('page', String(page - 1))}>Trang trước</Button> : null}
            {data.tiepTheo ? <Button kieu="phu" onClick={() => capNhat('page', String(page + 1))}>Trang sau</Button> : null}
          </div>
        ) : null}
      </section>
    </div>
  );
}
