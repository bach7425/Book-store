import type { KiemTraMaGiamGiaRequest, KiemTraMaGiamGiaResponse } from '../types';
import { goiApi } from './client';

export const maGiamGiaApi = {
  kiemTraMaGiamGia: (duLieu: KiemTraMaGiamGiaRequest) =>
    goiApi<KiemTraMaGiamGiaResponse>({ url: '/api/ma-giam-gia/kiem-tra', method: 'POST', data: duLieu }),
};
