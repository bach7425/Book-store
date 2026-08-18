import type { LamMoiTokenResponse, NguoiDung, XacThucResponse } from '../types';
import { goiApi } from './client';

export interface DangNhapRequest {
  tenDangNhap: string;
  matKhau: string;
}

export interface DangKyRequest extends DangNhapRequest {
  hoTen: string;
  email: string;
  soDienThoai?: string;
}

export interface LamMoiTokenRequest {
  maLamMoi: string;
}

export const xacThucApi = {
  dangNhap: (duLieu: DangNhapRequest) =>
    goiApi<XacThucResponse>({ url: '/api/xac-thuc/dang-nhap', method: 'POST', data: duLieu }),
  dangKy: (duLieu: DangKyRequest) =>
    goiApi<NguoiDung>({ url: '/api/xac-thuc/dang-ky', method: 'POST', data: duLieu }),
  lamMoiToken: (duLieu: LamMoiTokenRequest) =>
    goiApi<LamMoiTokenResponse>({ url: '/api/xac-thuc/lam-moi-token', method: 'POST', data: duLieu }),
};
