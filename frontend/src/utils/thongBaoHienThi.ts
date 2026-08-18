import { Bell, ClipboardList, MessageSquareText, Percent, ShieldCheck } from 'lucide-react';

export const cauHinhLoaiThongBao = {
  DON_HANG: { ten: 'Đơn hàng', lop: 'bg-[#e9f2ff] text-[#0b5cad]', icon: ClipboardList },
  DANH_GIA: { ten: 'Đánh giá', lop: 'bg-[#fff1d6] text-[#8a4f00]', icon: MessageSquareText },
  MA_GIAM_GIA: { ten: 'Khuyến mãi', lop: 'bg-[#ffe8ef] text-[#b3184a]', icon: Percent },
  QUAN_TRI: { ten: 'Quản trị', lop: 'bg-[#e8f5e9] text-[#246b32]', icon: ShieldCheck },
} as const;

export function nhanLoaiThongBao(loai: string) {
  return cauHinhLoaiThongBao[loai as keyof typeof cauHinhLoaiThongBao] ?? { ten: loai || 'Thông báo', lop: 'bg-[#efeded] text-[#43474d]', icon: Bell };
}
