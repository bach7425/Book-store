import { useQuery } from '@tanstack/react-query';
import { ArrowRight, BookMarked, BriefcaseBusiness, GraduationCap, Search, Sparkles } from 'lucide-react';
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { sachApi } from '../../api/sachApi';
import { DangTai } from '../../components/ui/Bang';
import { Button } from '../../components/ui/Button';
import { Input } from '../../components/ui/Input';
import { SachCard } from './SachCard';
import { useMaSachYeuThich } from './useMaSachYeuThich';

export function TrangChuPage() {
  const navigate = useNavigate();
  const [tuKhoa, setTuKhoa] = useState('');
  const maSachYeuThich = useMaSachYeuThich();
  const { data, isLoading } = useQuery({ queryKey: ['sach', 'trang-chu'], queryFn: () => sachApi.layDanhSachSach({ size: 8 }) });
  const { data: theLoai } = useQuery({ queryKey: ['the-loai'], queryFn: sachApi.layDanhSachTheLoai });
  const timKiem = () => navigate(`/sach?tuKhoa=${encodeURIComponent(tuKhoa)}`);

  const danhMuc = [
    { ten: theLoai?.[0]?.ten ?? 'Văn học', moTa:theLoai?.[0]?.moTa , icon: BookMarked, to: theLoai?.[0]?.maTheLoai },
    { ten: theLoai?.[1]?.ten ?? 'Kỹ năng', moTa: theLoai?.[1]?.moTa , icon: GraduationCap, to: theLoai?.[1]?.maTheLoai },
    { ten: theLoai?.[2]?.ten ?? 'Kinh tế', moTa: theLoai?.[2]?.moTa , icon: BriefcaseBusiness, to: theLoai?.[2]?.maTheLoai },
  ];

  return (
    <div>
      <section className="relative overflow-hidden border-b border-[#c4c6cd] bg-[#f5f3f3]">
        <div className="absolute inset-0 opacity-40" style={{ backgroundImage: 'linear-gradient(90deg, rgba(3,25,46,.05) 1px, transparent 1px), linear-gradient(rgba(3,25,46,.04) 1px, transparent 1px)', backgroundSize: '34px 34px' }} />
        <div className="relative mx-auto grid min-h-[640px] max-w-[1280px] items-center gap-12 px-4 py-16 md:px-10 lg:grid-cols-[1fr_440px]">
          <div className="max-w-3xl">
            <p className="archival-label mb-4 inline-flex items-center gap-2 rounded bg-[#ffdcbd] px-3 py-2 text-[#623f18]"><Sparkles size={15} /> Di sản tri thức</p>
            <h1 className="font-serif-display text-5xl font-bold leading-[1.05] text-[#03192e] md:text-7xl">
              Khám phá thế giới tri thức mỗi ngày
            </h1>
            <p className="mt-6 max-w-2xl text-lg leading-8 text-[#43474d]">
              Tìm kiếm sách, theo dõi giỏ hàng, đặt hàng và quản lý nhà sách trong một giao diện gọn như bàn đọc, sắc như hệ thống lưu trữ.
            </p>
            <div className="paper-panel mt-8 flex max-w-2xl gap-2 rounded p-2">
              <div className="relative flex-1">
                <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-[#74777d]" size={18} />
                <Input className="pl-10" value={tuKhoa} onChange={(event) => setTuKhoa(event.target.value)} placeholder="Tìm sách, tác giả, thể loại..." onKeyDown={(event) => event.key === 'Enter' && timKiem()} />
              </div>
              <Button onClick={timKiem}>Tìm kiếm</Button>
            </div>
          </div>

          <div className="relative hidden lg:block">
            <div className="absolute -left-8 top-10 h-80 w-56 rotate-[-10deg] rounded bg-[#7d562d] shadow-2xl" />
            <div className="book-shadow relative z-10 h-[460px] rounded bg-[#03192e] p-8 text-white">
              <p className="archival-label text-[#ffdcbd]">Bộ sưu tập nổi bật</p>
              <h2 className="font-serif-display mt-16 text-4xl font-bold leading-tight">Những tựa sách đang chờ bạn mở ra</h2>
              <p className="mt-5 text-[#b4c8e4]">Danh mục được sắp xếp để độc giả đi nhanh từ cảm hứng đến giỏ hàng.</p>
            </div>
          </div>
        </div>
      </section>

      <section className="mx-auto max-w-[1280px] px-4 py-16 md:px-10">
        <div className="mb-8 flex items-end justify-between gap-4">
          <div>
            <p className="archival-label text-[#7d562d]">Duyệt theo danh mục</p>
            <h2 className="font-serif-display mt-2 text-3xl font-bold text-[#03192e]">Danh mục phổ biến</h2>
          </div>
          <Button kieu="rong" onClick={() => navigate('/sach')}>Xem tất cả <ArrowRight size={16} /></Button>
        </div>
        <div className="grid gap-5 md:grid-cols-3">
          {danhMuc.map((item, index) => (
            <button key={item.ten} onClick={() => navigate(item.to ? `/sach?theLoaiId=${item.to}` : '/sach')} className={`group min-h-52 rounded border border-[#c4c6cd] p-6 text-left transition hover:-translate-y-1 hover:border-[#7d562d]/60 hover:shadow-[0_18px_36px_rgba(26,46,68,0.08)] ${index === 0 ? 'bg-[#03192e] text-white md:col-span-1' : 'bg-white text-[#03192e]'}`}>
              <item.icon className={index === 0 ? 'text-[#ffdcbd]' : 'text-[#7d562d]'} size={30} />
              <h3 className="font-serif-display mt-10 text-2xl font-bold">{item.ten}</h3>
              <p className={`mt-2 text-sm ${index === 0 ? 'text-[#b4c8e4]' : 'text-[#43474d]'}`}>{item.moTa}</p>
            </button>
          ))}
        </div>
      </section>

      <section className="mx-auto max-w-[1280px] px-4 pb-20 md:px-10">
        <div className="mb-8 text-center">
          <p className="archival-label text-[#7d562d]">Tuyển chọn tuần này</p>
          <h2 className="font-serif-display mt-2 text-3xl font-bold text-[#03192e]">Sách nổi bật</h2>
          <p className="mt-2 text-[#43474d]">Những tựa sách đang được độc giả quan tâm.</p>
        </div>
        {isLoading ? <DangTai /> : <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-4">{(data?.duLieu ?? []).map((sach) => <SachCard key={sach.maSach} sach={sach} daYeuThich={maSachYeuThich.has(sach.maSach)} />)}</div>}
      </section>
    </div>
  );
}
