import type { ButtonHTMLAttributes, PropsWithChildren } from 'react';
import { twMerge } from 'tailwind-merge';

type KieuButton = 'chinh' | 'phu' | 'canh-bao' | 'rong';

const lopTheoKieu: Record<KieuButton, string> = {
  chinh: 'bg-[#03192e] text-white hover:bg-[#1a2e44] shadow-[0_8px_18px_rgba(3,25,46,0.16)]',
  phu: 'bg-white text-[#7d562d] ring-1 ring-[#d8c6b4] hover:bg-[#fff7ef]',
  'canh-bao': 'bg-[#ba1a1a] text-white hover:bg-[#93000a]',
  rong: 'bg-transparent text-[#7d562d] hover:bg-[#ffdcbd]/50',
};

export function Button({
  children,
  className,
  kieu = 'chinh',
  ...props
}: PropsWithChildren<ButtonHTMLAttributes<HTMLButtonElement> & { kieu?: KieuButton }>) {
  return (
    <button
      className={twMerge(
        'inline-flex min-h-10 items-center justify-center gap-2 rounded px-4 py-2 text-sm font-bold transition disabled:cursor-not-allowed disabled:opacity-50',
        lopTheoKieu[kieu],
        className,
      )}
      {...props}
    >
      {children}
    </button>
  );
}
