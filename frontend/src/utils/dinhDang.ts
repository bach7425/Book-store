export const API_BASE_URL = import.meta.env.VITE_API_URL ?? 'http://localhost:8080';

export function dinhDangTien(giaTri?: number | null) {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND',
    maximumFractionDigits: 0,
  }).format(Number(giaTri ?? 0));
}

export function dinhDangNgay(giaTri?: string | null) {
  if (!giaTri) return 'Chưa có';
  return new Intl.DateTimeFormat('vi-VN', {
    dateStyle: 'medium',
    timeStyle: giaTri.includes('T') ? 'short' : undefined,
  }).format(new Date(giaTri));
}

export function duongDanAnh(anh?: string | null) {
  if (!anh) return '';
  if (anh.startsWith('http')) return anh;
  if (anh.startsWith('/')) return `${API_BASE_URL}${anh}`;
  return `${API_BASE_URL}/${anh}`;
}

export function tenTrangThai(trangThai?: string) {
  const bangTen: Record<string, string> = {
    CHO_XU_LY: 'Chờ xử lý',
    DA_XAC_NHAN: 'Đã xác nhận',
    DANG_GIAO: 'Đang giao',
    DA_GIAO: 'Đã giao',
    DA_HUY: 'Đã hủy',
    DA_THANH_TOAN: 'Đã thanh toán',
    CHO_THANH_TOAN: 'Chưa thanh toán',
    THAT_BAI: 'Thanh toán thất bại',
    CHO_DUYET: 'Chờ duyệt',
    DA_DUYET: 'Đã duyệt',
    TU_CHOI: 'Từ chối',
    HOAT_DONG: 'Hoạt động',
    HET_HAN: 'Hết hạn',
    NGUNG: 'Ngưng',
    NGUNG_HOAT_DONG: 'Ngưng hoạt động',
    TIEN_MAT: 'Thanh toán khi nhận hàng',
    CHUYEN_KHOAN: 'Chuyển khoản',
  };
  return bangTen[trangThai ?? ''] ?? trangThai ?? 'Chưa có';
}
