import { User } from 'lucide-react';
import { duongDanAnh } from '../../utils/dinhDang';

type KichThuocAvatar = 'sm' | 'md' | 'lg' | 'xl';

interface AvatarNguoiDungProps {
  ten?: string | null;
  anhDaiDien?: string | null;
  kichThuoc?: KichThuocAvatar;
  className?: string;
}

const kichThuocClass: Record<KichThuocAvatar, string> = {
  sm: 'h-9 w-9 text-xs',
  md: 'h-11 w-11 text-sm',
  lg: 'h-16 w-16 text-xl',
  xl: 'h-28 w-28 text-4xl',
};

function layChuCaiDau(ten?: string | null) {
  const parts = (ten ?? '').trim().split(/\s+/).filter(Boolean);
  if (parts.length === 0) return '';
  return parts.length === 1 ? parts[0].slice(0, 1).toUpperCase() : `${parts[0][0]}${parts[parts.length - 1][0]}`.toUpperCase();
}

export function AvatarNguoiDung({ ten, anhDaiDien, kichThuoc = 'md', className = '' }: AvatarNguoiDungProps) {
  const src = duongDanAnh(anhDaiDien);
  const chuCai = layChuCaiDau(ten);

  return (
    <span className={`relative inline-grid shrink-0 place-items-center overflow-hidden rounded-full border border-[#c4c6cd] bg-[#03192e] font-bold text-white shadow-[0_8px_18px_rgba(3,25,46,0.14)] ${kichThuocClass[kichThuoc]} ${className}`}>
      {src ? <img src={src} alt={ten ? `Ảnh đại diện của ${ten}` : 'Ảnh đại diện'} className="h-full w-full object-cover" /> : chuCai ? <span>{chuCai}</span> : <User size={kichThuoc === 'xl' ? 36 : 18} />}
    </span>
  );
}
