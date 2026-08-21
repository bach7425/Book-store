import { useMutation } from '@tanstack/react-query';
import { ChevronDown, Globe, MessageCircle, Send, X } from 'lucide-react';
import { type KeyboardEvent, useEffect, useRef, useState } from 'react';
import { chatBoxApi } from '../api/chatBoxApi';
import type { ChatRequest, TinNhanChat } from '../types';
import { Button } from './ui/Button';

function taoId() {
  return globalThis.crypto?.randomUUID?.() ?? `${Date.now()}-${Math.random().toString(36).slice(2)}`;
}

function taoTinNhan(vaiTro: TinNhanChat['vaiTro'], noiDung: string, trangThai?: TinNhanChat['trangThai']): TinNhanChat {
  return {
    id: taoId(),
    vaiTro,
    noiDung,
    thoiGian: new Date().toISOString(),
    trangThai,
  };
}

interface BienGuiChat extends ChatRequest {
  maTinNhanCho: string;
}

export function ChatBoxWidget() {
  const [dangMo, setDangMo] = useState(false);
  const [noiDungNhap, setNoiDungNhap] = useState('');
  const [isSearchWeb, setIsSearchWeb] = useState(false);
  const [tinNhans, setTinNhans] = useState<TinNhanChat[]>(() => [
    taoTinNhan('tro-ly', 'Xin chào, mình có thể tư vấn sách hoặc trả lời câu hỏi về nhà sách.'),
  ]);
  const cuoiDanhSachRef = useRef<HTMLDivElement | null>(null);
  const cauHoiTheoTinNhanChoRef = useRef<Record<string, ChatRequest>>({});

  const mutation = useMutation({
    mutationFn: ({ cauHoi, isSearchWeb: timWeb }: BienGuiChat) => chatBoxApi.hoi({ cauHoi, isSearchWeb: timWeb }),
    onSuccess: (phanHoi, bien) => {
      delete cauHoiTheoTinNhanChoRef.current[bien.maTinNhanCho];
      setTinNhans((hienTai) =>
        hienTai.map((tinNhan) =>
          tinNhan.id === bien.maTinNhanCho
            ? { ...tinNhan, noiDung: phanHoi || 'Mình chưa tìm được câu trả lời phù hợp.', trangThai: undefined }
            : tinNhan,
        ),
      );
    },
    onError: (_error, bien) => {
      setTinNhans((hienTai) =>
        hienTai.map((tinNhan) =>
          tinNhan.id === bien.maTinNhanCho
            ? {
                ...tinNhan,
                noiDung: 'Mình chưa thể kết nối tới trợ lý AI. Bạn có thể thử gửi lại câu hỏi này.',
                trangThai: 'loi',
              }
            : tinNhan,
        ),
      );
    },
  });

  useEffect(() => {
    if (!dangMo) return;
    cuoiDanhSachRef.current?.scrollIntoView({ behavior: 'smooth', block: 'end' });
  }, [dangMo, tinNhans]);

  const guiCauHoi = (duLieu?: ChatRequest) => {
    const cauHoi = (duLieu?.cauHoi ?? noiDungNhap).trim();
    const timWeb = duLieu?.isSearchWeb ?? isSearchWeb;
    if (!cauHoi || mutation.isPending) return;

    const tinNhanNguoiDung = taoTinNhan('nguoi-dung', cauHoi);
    const tinNhanCho = taoTinNhan('tro-ly', 'Đang trả lời...', 'dang-gui');
    cauHoiTheoTinNhanChoRef.current[tinNhanCho.id] = { cauHoi, isSearchWeb: timWeb };

    setTinNhans((hienTai) => [...hienTai, tinNhanNguoiDung, tinNhanCho]);
    setNoiDungNhap('');
    mutation.mutate({ cauHoi, isSearchWeb: timWeb, maTinNhanCho: tinNhanCho.id });
  };

  const thuGuiLai = (maTinNhanCho: string) => {
    const yeuCau = cauHoiTheoTinNhanChoRef.current[maTinNhanCho];
    if (!yeuCau || mutation.isPending) return;

    const tinNhanChoMoi = taoTinNhan('tro-ly', 'Đang trả lời...', 'dang-gui');
    cauHoiTheoTinNhanChoRef.current[tinNhanChoMoi.id] = yeuCau;
    delete cauHoiTheoTinNhanChoRef.current[maTinNhanCho];

    setTinNhans((hienTai) => hienTai.map((tinNhan) => (tinNhan.id === maTinNhanCho ? tinNhanChoMoi : tinNhan)));
    mutation.mutate({ ...yeuCau, maTinNhanCho: tinNhanChoMoi.id });
  };

  const xuLyNhapPhim = (event: KeyboardEvent<HTMLTextAreaElement>) => {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      guiCauHoi();
    }
  };

  return (
    <div className="fixed bottom-4 right-4 z-50 sm:bottom-6 sm:right-6">
      {dangMo ? (
        <section className="paper-panel flex h-[min(620px,calc(100vh-6rem))] w-[calc(100vw-2rem)] max-w-[410px] flex-col overflow-hidden rounded bg-[#fbf9f8] shadow-[0_18px_45px_rgba(3,25,46,0.18)]">
          <header className="flex min-h-14 items-center justify-between border-b border-[#c4c6cd]/80 bg-[#03192e] px-4 text-white">
            <div className="flex items-center gap-3">
              <span className="grid h-9 w-9 place-items-center rounded bg-white/12">
                <MessageCircle size={19} />
              </span>
              <div>
                <h2 className="text-sm font-bold leading-tight">Trợ lý tư vấn sách</h2>
                <p className="text-xs text-white/72">Book Store AI</p>
              </div>
            </div>
            <div className="flex items-center gap-1">
              <button
                type="button"
                className="grid h-9 w-9 place-items-center rounded text-white/82 transition hover:bg-white/12 hover:text-white"
                onClick={() => setDangMo(false)}
                aria-label="Thu gọn chatbox"
              >
                <ChevronDown size={19} />
              </button>
              <button
                type="button"
                className="grid h-9 w-9 place-items-center rounded text-white/82 transition hover:bg-white/12 hover:text-white"
                onClick={() => setDangMo(false)}
                aria-label="Đóng chatbox"
              >
                <X size={18} />
              </button>
            </div>
          </header>

          <div className="flex-1 overflow-y-auto px-4 py-4">
            <div className="grid gap-3">
              {tinNhans.map((tinNhan) => {
                const laNguoiDung = tinNhan.vaiTro === 'nguoi-dung';
                return (
                  <article key={tinNhan.id} className={`flex ${laNguoiDung ? 'justify-end' : 'justify-start'}`}>
                    <div
                      className={`max-w-[86%] rounded px-3 py-2 text-sm leading-relaxed ${
                        laNguoiDung
                          ? 'bg-[#03192e] text-white'
                          : tinNhan.trangThai === 'loi'
                            ? 'border border-[#e0a7a7] bg-[#fff4f4] text-[#6f1515]'
                            : 'border border-[#d8c6b4] bg-white text-[#1b1c1c]'
                      }`}
                    >
                      <p className="whitespace-pre-wrap break-words">{tinNhan.noiDung}</p>
                      {tinNhan.trangThai === 'loi' ? (
                        <button
                          type="button"
                          className="mt-2 text-xs font-bold text-[#7d562d] underline-offset-4 hover:underline disabled:opacity-50"
                          onClick={() => thuGuiLai(tinNhan.id)}
                          disabled={mutation.isPending}
                        >
                          Thử lại
                        </button>
                      ) : null}
                    </div>
                  </article>
                );
              })}
              <div ref={cuoiDanhSachRef} />
            </div>
          </div>

          <div className="border-t border-[#c4c6cd]/80 bg-white/80 p-3">
            <label className="mb-3 flex items-center justify-between gap-3 rounded border border-[#d8c6b4] bg-[#fffaf5] px-3 py-2 text-sm font-semibold text-[#43474d]">
              <span className="flex items-center gap-2">
                <Globe size={17} className="text-[#7d562d]" />
                Tìm kiếm web
              </span>
              <input
                type="checkbox"
                className="h-4 w-4 accent-[#03192e]"
                checked={isSearchWeb}
                onChange={(event) => setIsSearchWeb(event.target.checked)}
              />
            </label>
            <div className="flex items-end gap-2">
              <textarea
                value={noiDungNhap}
                onChange={(event) => setNoiDungNhap(event.target.value)}
                onKeyDown={xuLyNhapPhim}
                className="textarea-paper max-h-32 min-h-11 flex-1 resize-none px-3 py-2 text-sm"
                rows={1}
                placeholder="Bạn muốn tìm sách gì?"
                disabled={mutation.isPending}
              />
              <Button
                type="button"
                className="h-11 w-11 shrink-0 px-0"
                onClick={() => guiCauHoi()}
                disabled={!noiDungNhap.trim() || mutation.isPending}
                aria-label="Gửi câu hỏi"
              >
                <Send size={18} />
              </Button>
            </div>
          </div>
        </section>
      ) : (
        <button
          type="button"
          className="grid h-14 w-14 place-items-center rounded bg-[#03192e] text-white shadow-[0_16px_35px_rgba(3,25,46,0.28)] transition hover:bg-[#1a2e44] focus:outline-none focus:ring-4 focus:ring-[#b4c8e4]/55"
          onClick={() => setDangMo(true)}
          aria-label="Mở chatbox tư vấn sách"
        >
          <MessageCircle size={25} />
        </button>
      )}
    </div>
  );
}
