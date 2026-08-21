import { BookOpen, Heart, LayoutDashboard, LogOut, Menu, Search, ShoppingBag } from 'lucide-react';
import { useEffect, useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { Link, NavLink, Outlet, useNavigate } from 'react-router-dom';
import { nguoiDungApi } from '../api/nguoiDungApi';
import { ChatBoxWidget } from '../components/ChatBoxWidget';
import { ThongBaoChuong } from '../components/ThongBaoChuong';
import { AvatarNguoiDung } from '../components/ui/AvatarNguoiDung';
import { Button } from '../components/ui/Button';
import { useXacThucStore } from '../features/xac-thuc/xacThucStore';
import { chuanHoaVaiTro } from '../utils/vaiTro';

const nav = [
  { to: '/sach', label: 'Thư viện' },
];

export function PublicLayout() {
  const navigate = useNavigate();
  const [moMenu, setMoMenu] = useState(false);
  const { daDangNhap, nguoiDung, dangXuat, capNhatNguoiDung } = useXacThucStore();
  const vaiTro = chuanHoaVaiTro(nguoiDung?.vaiTro);
  const laNguoiDungMuaHang = vaiTro === 'ROLE_NGUOI_DUNG';
  const { data: profile } = useQuery({
    queryKey: ['nguoi-dung', 'navbar'],
    queryFn: nguoiDungApi.layThongTinNguoiDung,
    enabled: daDangNhap,
    staleTime: 60_000,
  });

  useEffect(() => {
    if (profile) capNhatNguoiDung(profile);
  }, [capNhatNguoiDung, profile]);

  const dangXuatVaVeTrangChu = () => {
    dangXuat();
    navigate('/');
  };

  return (
    <div className="min-h-screen text-[#1b1c1c]">
      <header className="sticky top-0 z-40 border-b border-[#c4c6cd]/80 bg-[#fbf9f8]/92 backdrop-blur-xl">
        <div className="mx-auto flex h-16 max-w-[1280px] items-center justify-between gap-6 px-4 md:px-10">
          <div className="flex min-w-0 items-center gap-8">
            <Link to="/" className="flex shrink-0 items-center gap-3">
              <span className="grid h-10 w-10 place-items-center rounded bg-[#03192e] text-white shadow-[0_10px_22px_rgba(3,25,46,0.18)]">
                <BookOpen size={22} />
              </span>
              <span>
                <span className="font-serif-display block text-xl font-bold leading-none text-[#03192e]">Book Store</span>
                <span className="archival-label text-[#7d562d]">Nhà sách </span>
              </span>
            </Link>

            <nav className="hidden items-center gap-7 md:flex">
              {nav.map((item) => (
                <NavLink
                  key={item.to}
                  to={item.to}
                  className={({ isActive }) =>
                    `archival-label transition ${isActive ? 'text-[#03192e]' : 'text-[#43474d] hover:text-[#7d562d]'}`
                  }
                >
                  {item.label}
                </NavLink>
              ))}
              {vaiTro === 'ROLE_QUAN_TRI_VIEN' ? (
                <NavLink to="/quan-tri" className="archival-label text-[#43474d] hover:text-[#7d562d]">
                  Quản trị
                </NavLink>
              ) : null}
              {laNguoiDungMuaHang ? (
                <NavLink to="/don-hang" className="archival-label text-[#43474d] hover:text-[#7d562d]">
                  Đơn hàng
                </NavLink>
              ) : null}
            </nav>
          </div>

          <div className="hidden shrink-0 items-center gap-2 md:flex">
            {laNguoiDungMuaHang ? (
              <>
                <button onClick={() => navigate('/yeu-thich')} className="rounded-full p-2 text-[#43474d] hover:bg-[#efeded] hover:text-[#7d562d]" aria-label="Yêu thích">
                  <Heart size={19} />
                </button>
                <button onClick={() => navigate('/gio-hang')} className="rounded-full p-2 text-[#43474d] hover:bg-[#efeded] hover:text-[#7d562d]" aria-label="Giỏ hàng">
                  <ShoppingBag size={19} />
                </button>
              </>
            ) : null}
            {daDangNhap ? (
              <>
                <ThongBaoChuong />
                <button onClick={() => navigate('/tai-khoan')} className="flex items-center gap-2 rounded-full py-1 pl-1 pr-3 hover:bg-[#efeded]" aria-label="Tài khoản">
                  <AvatarNguoiDung ten={nguoiDung?.hoVaTen} anhDaiDien={nguoiDung?.anhDaiDien} kichThuoc="sm" />
                  <span className="max-w-36 truncate text-sm font-semibold text-[#43474d]">{nguoiDung?.hoVaTen}</span>
                </button>
                <Button kieu="rong" onClick={dangXuatVaVeTrangChu}>
                  <LogOut size={16} /> Đăng xuất
                </Button>
              </>
            ) : (
              <>
                <Button kieu="phu" onClick={() => navigate('/dang-nhap')}>Đăng nhập</Button>
                <Button onClick={() => navigate('/dang-ky')}>Đăng ký</Button>
              </>
            )}
          </div>

          <button className="rounded p-2 text-[#03192e] md:hidden" onClick={() => setMoMenu((value) => !value)} aria-label="Mở menu">
            <Menu />
          </button>
        </div>

        {moMenu ? (
          <div className="border-t border-[#c4c6cd] bg-white px-4 py-3 md:hidden">
            <div className="grid gap-2 text-sm font-semibold">
              <Link to="/sach" className="flex items-center gap-2 py-2"><Search size={18} /> Thư viện</Link>
              {laNguoiDungMuaHang ? (
                <>
                  <Link to="/yeu-thich" className="flex items-center gap-2 py-2"><Heart size={18} /> Yêu thích</Link>
                  <Link to="/gio-hang" className="flex items-center gap-2 py-2"><ShoppingBag size={18} /> Giỏ hàng</Link>
                </>
              ) : null}
              {daDangNhap ? <div className="flex items-center gap-2 py-2"><ThongBaoChuong /> <Link to="/thong-bao">Thông báo</Link></div> : null}
              {laNguoiDungMuaHang ? <Link to="/don-hang" className="flex items-center gap-2 py-2"><Heart size={18} /> Đơn hàng</Link> : null}
              {daDangNhap ? <Link to="/tai-khoan" className="flex items-center gap-2 py-2"><AvatarNguoiDung ten={nguoiDung?.hoVaTen} anhDaiDien={nguoiDung?.anhDaiDien} kichThuoc="sm" /> Tài khoản</Link> : null}
              {vaiTro === 'ROLE_QUAN_TRI_VIEN' ? <Link to="/quan-tri" className="flex items-center gap-2 py-2"><LayoutDashboard size={18} /> Quản trị</Link> : null}
            </div>
          </div>
        ) : null}
      </header>
      <main>
        <Outlet />
      </main>
      <ChatBoxWidget />
    </div>
  );
}
