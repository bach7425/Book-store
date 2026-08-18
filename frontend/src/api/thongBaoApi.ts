import type { GuiThongBaoRequest, PhanTrang, ThongBao } from '../types';
import { goiApi } from './client';

export const thongBaoApi = {
  layDanhSachThongBao: (page = 0, size = 10) => goiApi<PhanTrang<ThongBao>>({ url: '/api/thong-bao', params: { page, size } }),
  demThongBaoChuaDoc: () => goiApi<number>({ url: '/api/thong-bao/chua-doc/so-luong' }),
  danhDauDaDoc: (maThongBao: number) => goiApi<ThongBao>({ url: `/api/thong-bao/${maThongBao}/da-doc`, method: 'PATCH' }),
  danhDauTatCaDaDoc: () => goiApi<void>({ url: '/api/thong-bao/da-doc', method: 'PATCH' }),
  guiThongBaoQuanTri: (duLieu: GuiThongBaoRequest) => goiApi<void>({ url: '/api/quan-tri/thong-bao', method: 'POST', data: duLieu }),
};
