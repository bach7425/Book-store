import { QueryCache, QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import { toast } from 'sonner';
import { AdminLayout } from './app/AdminLayout';
import { PublicLayout } from './app/PublicLayout';
import { Toaster } from './components/ui/Toaster';
import { ChiTietDonHangPage } from './features/don-hang/ChiTietDonHangPage';
import { DonHangPage } from './features/don-hang/DonHangPage';
import { ThanhToanPage } from './features/don-hang/ThanhToanPage';
import { GioHangPage } from './features/gio-hang/GioHangPage';
import { TaiKhoanPage } from './features/nguoi-dung/TaiKhoanPage';
import { QuanTriChiTietDonHangPage } from './features/quan-tri/QuanTriChiTietDonHangPage';
import { QuanTriDanhGiaPage } from './features/quan-tri/QuanTriDanhGiaPage';
import { QuanTriDashboardPage } from './features/quan-tri/QuanTriDashboardPage';
import { QuanTriDonHangPage } from './features/quan-tri/QuanTriDonHangPage';
import { QuanTriKhachHangPage } from './features/quan-tri/QuanTriKhachHangPage';
import { QuanTriMaGiamGiaPage } from './features/quan-tri/QuanTriMaGiamGiaPage';
import { QuanTriSachPage } from './features/quan-tri/QuanTriSachPage';
import { QuanTriTacGiaPage } from './features/quan-tri/QuanTriTacGiaPage';
import { QuanTriTheLoaiPage } from './features/quan-tri/QuanTriTheLoaiPage';
import { QuanTriThongBaoPage } from './features/quan-tri/QuanTriThongBaoPage';
import { ChiTietSachPage } from './features/sach/ChiTietSachPage';
import { DanhSachSachPage } from './features/sach/DanhSachSachPage';
import { TrangChuPage } from './features/sach/TrangChuPage';
import { YeuThichPage } from './features/sach/YeuThichPage';
import { ThongBaoPage } from './features/thong-bao/ThongBaoPage';
import { BaoVeRoute } from './features/xac-thuc/BaoVeRoute';
import { DangKyPage } from './features/xac-thuc/DangKyPage';
import { DangNhapPage } from './features/xac-thuc/DangNhapPage';

function tenDuLieuDaTai(queryKey: readonly unknown[]) {
  const key = String(queryKey[0] ?? 'du-lieu');
  const bangTen: Record<string, string> = {
    sach: 'sách',
    'the-loai': 'thể loại',
    'danh-gia': 'đánh giá',
    'gio-hang': 'giỏ hàng',
    'dia-chi': 'địa chỉ',
    'don-hang': 'đơn hàng',
    'nguoi-dung': 'tài khoản',
    'sach-yeu-thich': 'danh sách yêu thích',
    'thong-bao': 'thông báo',
    'quan-tri': 'dữ liệu quản trị',
  };
  return bangTen[key] ?? 'dữ liệu';
}

const queryClient = new QueryClient({
  queryCache: new QueryCache({
    onSuccess: (_data, query) => {
      if (query.meta?.anThongBao) return;
      toast.success(`Đã tải ${tenDuLieuDaTai(query.queryKey)} thành công`);
    },
    onError: (error, query) => {
      if (query.meta?.anThongBao) return;
      toast.error(error.message || `Không tải được ${tenDuLieuDaTai(query.queryKey)}`);
    },
  }),
  defaultOptions: {
    queries: { retry: 1, staleTime: 30_000 },
  },
});

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        <Routes>
          <Route element={<PublicLayout />}>
            <Route index element={<TrangChuPage />} />
            <Route path="/sach" element={<DanhSachSachPage />} />
            <Route path="/sach/:maSach" element={<ChiTietSachPage />} />
            <Route path="/dang-nhap" element={<DangNhapPage />} />
            <Route path="/dang-ky" element={<DangKyPage />} />
            <Route element={<BaoVeRoute vaiTro={['ROLE_NGUOI_DUNG']} />}>
              <Route path="/gio-hang" element={<GioHangPage />} />
              <Route path="/yeu-thich" element={<YeuThichPage />} />
              <Route path="/thanh-toan" element={<ThanhToanPage />} />
              <Route path="/don-hang" element={<DonHangPage />} />
              <Route path="/don-hang/:maDonHang" element={<ChiTietDonHangPage />} />
            </Route>
            <Route element={<BaoVeRoute vaiTro={['ROLE_NGUOI_DUNG', 'ROLE_QUAN_TRI_VIEN']} />}>
              <Route path="/tai-khoan" element={<TaiKhoanPage />} />
              <Route path="/thong-bao" element={<ThongBaoPage />} />
            </Route>
          </Route>
          <Route element={<BaoVeRoute vaiTro={['ROLE_QUAN_TRI_VIEN']} />}>
            <Route path="/quan-tri" element={<AdminLayout />}>
              <Route index element={<QuanTriDashboardPage />} />
              <Route path="sach" element={<QuanTriSachPage />} />
              <Route path="tac-gia" element={<QuanTriTacGiaPage />} />
              <Route path="the-loai" element={<QuanTriTheLoaiPage />} />
              <Route path="don-hang" element={<QuanTriDonHangPage />} />
              <Route path="don-hang/:maDonHang" element={<QuanTriChiTietDonHangPage />} />
              <Route path="khach-hang" element={<QuanTriKhachHangPage />} />
              <Route path="danh-gia" element={<QuanTriDanhGiaPage />} />
              <Route path="ma-giam-gia" element={<QuanTriMaGiamGiaPage />} />
              <Route path="thong-bao" element={<QuanTriThongBaoPage />} />
            </Route>
          </Route>
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
        <Toaster />
      </BrowserRouter>
    </QueryClientProvider>
  );
}

export default App;
