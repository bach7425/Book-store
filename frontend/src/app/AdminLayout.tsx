import { useQueryClient } from '@tanstack/react-query';
import { BarChart3, Bell, BookMarked, ClipboardList, Home, LogOut, MessageSquareText, Percent, Tags, UserPen, Users } from 'lucide-react';
import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { useXacThucStore } from '../features/xac-thuc/xacThucStore';

const nav = [
  { to: '/quan-tri', label: 'Tổng quan', icon: BarChart3, end: true },
  { to: '/quan-tri/sach', label: 'Kho sách', icon: BookMarked },
  { to: '/quan-tri/tac-gia', label: 'Tác giả', icon: UserPen },
  { to: '/quan-tri/the-loai', label: 'Thể loại', icon: Tags },
  { to: '/quan-tri/don-hang', label: 'Đơn hàng', icon: ClipboardList },
  { to: '/quan-tri/khach-hang', label: 'Khách hàng', icon: Users },
  { to: '/quan-tri/danh-gia', label: 'Đánh giá', icon: MessageSquareText },
  { to: '/quan-tri/ma-giam-gia', label: 'Khuyến mãi', icon: Percent },
  { to: '/quan-tri/thong-bao', label: 'Thông báo', icon: Bell },
];

export function AdminLayout() {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const dangXuat = useXacThucStore((state) => state.dangXuat);

  const dangXuatQuanTri = () => {
    dangXuat();
    queryClient.clear();
    navigate('/dang-nhap', { replace: true });
  };

  return (
    <div className="min-h-screen bg-[#fbf9f8]">
      <aside className="fixed inset-y-0 left-0 hidden w-72 border-r border-[#c4c6cd] bg-[#f5f3f3] p-4 lg:flex lg:flex-col">
        <div className="mb-8 px-4 pt-3">
          <p className="archival-label text-[#7d562d]">Cổng quản trị</p>
          <h1 className="font-serif-display mt-1 text-2xl font-bold text-[#03192e]">Quản trị nhà sách</h1>
        </div>
        <nav className="grid gap-1">
          {nav.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              end={item.end}
              className={({ isActive }) =>
                `flex items-center gap-3 rounded px-4 py-3 text-sm font-bold transition ${isActive ? 'bg-[#03192e] text-white shadow-[0_10px_24px_rgba(3,25,46,0.16)]' : 'text-[#43474d] hover:translate-x-1 hover:bg-[#eae8e7] hover:text-[#03192e]'}`
              }
            >
              <item.icon size={18} /> {item.label}
            </NavLink>
          ))}
        </nav>
        <div className="mt-auto border-t border-[#c4c6cd] pt-4">
          <NavLink
            to="/"
            className="mb-2 flex w-full items-center gap-3 rounded px-4 py-3 text-left text-sm font-bold text-[#43474d] transition hover:bg-[#eae8e7] hover:text-[#03192e]"
          >
            <Home size={18} /> Về trang chủ
          </NavLink>
          <button
            type="button"
            onClick={dangXuatQuanTri}
            className="flex w-full items-center gap-3 rounded px-4 py-3 text-left text-sm font-bold text-[#43474d] transition hover:bg-[#eae8e7] hover:text-[#9b2c2c]"
          >
            <LogOut size={18} /> Đăng xuất
          </button>
        </div>
      </aside>

      <div className="lg:pl-72">
        <div className="border-b border-[#c4c6cd] bg-[#f5f3f3] px-4 py-3 lg:hidden">
          <NavLink to="/" className="mb-3 inline-flex items-center gap-2 rounded bg-white px-3 py-2 text-sm font-bold text-[#43474d]">
            <Home size={16} /> Về trang chủ
          </NavLink>
          <div className="flex gap-2 overflow-x-auto">
            {nav.map((item) => (
              <NavLink key={item.to} to={item.to} end={item.end} className="whitespace-nowrap rounded bg-white px-3 py-2 text-sm font-bold text-[#43474d]">
                {item.label}
              </NavLink>
            ))}
          </div>
        </div>
        <main className="mx-auto max-w-[1320px] px-4 py-6 md:px-10">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
