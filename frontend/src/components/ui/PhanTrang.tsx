import type { PhanTrang as KieuPhanTrang } from '../../types';
import { Button } from './Button';

type Props = {
  data?: KieuPhanTrang<unknown>;
  page: number;
  onPageChange: (page: number) => void;
};

export function PhanTrang({ data, page, onPageChange }: Props) {
  const tongSoTrang = data?.tongSoTrang ?? 0;
  const tongSoPhanTu = data?.tongSoPhanTu ?? 0;
  if (tongSoTrang <= 1) return null;

  const trangHienTai = data?.trang ?? page;
  const trangDau = Math.max(0, trangHienTai - 1);
  const trangCuoi = Math.min(tongSoTrang - 1, trangDau + 2);
  const cacTrang = Array.from({ length: trangCuoi - trangDau + 1 }, (_, index) => trangDau + index);

  return (
    <div className="flex flex-wrap items-center justify-between gap-3 rounded border border-[#e4e2e2] bg-white px-4 py-3 text-sm">
      <p className="font-semibold text-[#43474d]">
        Trang {trangHienTai + 1}/{tongSoTrang} · {tongSoPhanTu} mục
      </p>
      <div className="flex flex-wrap items-center gap-2">
        <Button type="button" kieu="phu" disabled={trangHienTai <= 0} onClick={() => onPageChange(trangHienTai - 1)}>
          Trước
        </Button>
        {cacTrang.map((trang) => (
          <Button
            key={trang}
            type="button"
            kieu={trang === trangHienTai ? 'chinh' : 'phu'}
            onClick={() => onPageChange(trang)}
          >
            {trang + 1}
          </Button>
        ))}
        <Button type="button" kieu="phu" disabled={trangHienTai >= tongSoTrang - 1} onClick={() => onPageChange(trangHienTai + 1)}>
          Sau
        </Button>
      </div>
    </div>
  );
}
