import { useMutation } from '@tanstack/react-query';
import { Bot, ChevronDown, Globe, MessageCircle, Send, Sparkles, UserRound } from 'lucide-react';
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

const cauHoiGoiY = [
  'Gợi ý sách văn học Việt Nam dễ đọc',
  'Sách nào phù hợp để phát triển bản thân?',
  'Tìm sách công nghệ thông tin còn hàng',
];

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
        <section className="flex h-[min(680px,calc(100vh-5rem))] w-[calc(100vw-1.5rem)] max-w-[440px] flex-col overflow-hidden overflow-x-hidden rounded border border-[#c4c6cd] bg-[#fbf9f8] shadow-[0_24px_70px_rgba(3,25,46,0.24)]">
          <header className="flex min-h-16 items-center justify-between border-b border-[#1a2e44] bg-[#03192e] px-4 text-white">
            <div className="flex items-center gap-3">
              <span className="grid h-10 w-10 place-items-center rounded bg-white/12 ring-1 ring-white/15">
                <Bot size={21} />
              </span>
              <div>
                <h2 className="text-sm font-bold leading-tight">Trợ lý tư vấn sách</h2>
                <p className="mt-1 flex items-center gap-2 text-xs text-white/72">
                  <span className="h-2 w-2 rounded-full bg-[#6ee7b7]" />
                  Book Store AI đang sẵn sàng
                </p>
              </div>
            </div>
            <button
              type="button"
              className="grid h-9 w-9 place-items-center rounded text-white/82 transition hover:bg-white/12 hover:text-white"
              onClick={() => setDangMo(false)}
              aria-label="Thu gọn chatbox"
            >
              <ChevronDown size={20} />
            </button>
          </header>

          <div className="flex-1 overflow-x-hidden overflow-y-auto bg-[#fbf9f8] px-4 py-4">
            <div className="mb-4 rounded border border-[#e4e2e2] bg-white p-3">
              <p className="flex items-center gap-2 text-xs font-bold uppercase text-[#7d562d]">
                <Sparkles size={14} />
                Gợi ý nhanh
              </p>
              <div className="mt-3 grid gap-2">
                {cauHoiGoiY.map((cauHoi) => (
                  <button
                    key={cauHoi}
                    type="button"
                    className="min-w-0 rounded border border-[#e4e2e2] bg-[#fbf9f8] px-3 py-2 text-left text-sm font-semibold text-[#43474d] transition hover:border-[#d8c6b4] hover:bg-[#fff7ef] hover:text-[#03192e] disabled:opacity-50"
                    onClick={() => guiCauHoi({ cauHoi, isSearchWeb })}
                    disabled={mutation.isPending}
                  >
                    {cauHoi}
                  </button>
                ))}
              </div>
            </div>

            <div className="grid gap-4">
              {tinNhans.map((tinNhan) => {
                const laNguoiDung = tinNhan.vaiTro === 'nguoi-dung';
                return (
                  <article key={tinNhan.id} className={`flex min-w-0 items-end gap-2 ${laNguoiDung ? 'justify-end' : 'justify-start'}`}>
                    {!laNguoiDung ? (
                      <span className="grid h-8 w-8 shrink-0 place-items-center rounded bg-[#03192e] text-white">
                        <Bot size={16} />
                      </span>
                    ) : null}
                    <div
                      className={`min-w-0 max-w-[82%] overflow-hidden rounded px-3 py-2 text-sm leading-relaxed shadow-[0_8px_18px_rgba(3,25,46,0.05)] ${
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
                    {laNguoiDung ? (
                      <span className="grid h-8 w-8 shrink-0 place-items-center rounded bg-[#ffdcbd] text-[#623f18]">
                        <UserRound size={16} />
                      </span>
                    ) : null}
                  </article>
                );
              })}
              <div ref={cuoiDanhSachRef} />
            </div>
          </div>

          <div className="border-t border-[#c4c6cd]/80 bg-white p-3">
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
            <div className="flex items-end gap-2 rounded border border-[#c4c6cd] bg-[#fbf9f8] p-2 transition focus-within:border-[#03192e] focus-within:ring-2 focus-within:ring-[#b4c8e4]/45">
              <textarea
                value={noiDungNhap}
                onChange={(event) => setNoiDungNhap(event.target.value)}
                onKeyDown={xuLyNhapPhim}
                className="max-h-32 min-h-10 flex-1 resize-none border-0 bg-transparent px-1 py-2 text-sm text-[#03192e] outline-none placeholder:text-[#74777d]"
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
          className="group grid h-14 w-14 place-items-center rounded bg-[#03192e] text-white shadow-[0_16px_35px_rgba(3,25,46,0.28)] transition hover:-translate-y-0.5 hover:bg-[#1a2e44] focus:outline-none focus:ring-4 focus:ring-[#b4c8e4]/55"
          onClick={() => setDangMo(true)}
          aria-label="Mở chatbox tư vấn sách"
        >
          <MessageCircle size={25} className="transition group-hover:scale-105" />
        </button>
      )}
    </div>
  );
}
