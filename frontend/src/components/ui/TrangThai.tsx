import { tenTrangThai } from '../../utils/dinhDang';

export function TrangThai({ giaTri }: { giaTri?: string }) {
  const mau =
    giaTri?.includes('HUY') || giaTri?.includes('TU_CHOI')
      ? 'bg-[#ffdad6] text-[#93000a] ring-[#f1b8b3]'
      : giaTri?.includes('DA') || giaTri?.includes('HOAT_DONG')
        ? 'bg-[#d8efe1] text-[#14532d] ring-[#b7d5c1]'
        : 'bg-[#ffdcbd] text-[#623f18] ring-[#f0bd8b]';

  return (
    <span className={`archival-label inline-flex rounded px-2 py-1 ring-1 ${mau}`}>
      {tenTrangThai(giaTri)}
    </span>
  );
}
