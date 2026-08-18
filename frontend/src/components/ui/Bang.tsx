import type { PropsWithChildren } from 'react';

export function Bang({ children }: PropsWithChildren) {
  return (
    <div className="overflow-hidden rounded border border-[#c4c6cd] bg-white shadow-[0_10px_30px_rgba(26,46,68,0.04)]">
      <div className="overflow-x-auto">{children}</div>
    </div>
  );
}

export function OTrong({ tieuDe, moTa }: { tieuDe: string; moTa?: string }) {
  return (
    <div className="rounded border border-dashed border-[#c4c6cd] bg-[#fbf9f8] px-6 py-10 text-center">
      <p className="font-serif-display text-xl font-bold text-[#03192e]">{tieuDe}</p>
      {moTa ? <p className="mt-1 text-sm text-[#43474d]">{moTa}</p> : null}
    </div>
  );
}

export function DangTai() {
  return (
    <div className="grid gap-4 md:grid-cols-3">
      {[1, 2, 3].map((item) => (
        <div key={item} className="h-48 animate-pulse rounded bg-[#e4e2e2]" />
      ))}
    </div>
  );
}
