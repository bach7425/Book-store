export function chuanHoaVaiTro(vaiTro?: string | null) {
  if (!vaiTro) return '';
  return vaiTro.startsWith('ROLE_') ? vaiTro : `ROLE_${vaiTro}`;
}
