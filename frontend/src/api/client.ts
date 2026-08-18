import axios, { AxiosError, type AxiosRequestConfig, type InternalAxiosRequestConfig } from 'axios';
import { toast } from 'sonner';
import { useXacThucStore } from '../features/xac-thuc/xacThucStore';
import type { LamMoiTokenResponse, PhanHoiApi } from '../types';
import { API_BASE_URL } from '../utils/dinhDang';

interface YeuCauCoThuLai extends InternalAxiosRequestConfig {
  daThuLamMoiToken?: boolean;
}

export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: { 'Content-Type': 'application/json' },
});

const refreshClient = axios.create({
  baseURL: API_BASE_URL,
  headers: { 'Content-Type': 'application/json' },
});

let daBaoHetPhien = false;

function dangXuatDoHetPhien() {
  useXacThucStore.getState().dangXuat();
  if (!daBaoHetPhien) {
    daBaoHetPhien = true;
    toast.error('Phiên đăng nhập đã hết hạn, vui lòng đăng nhập lại');
    window.setTimeout(() => {
      daBaoHetPhien = false;
    }, 3000);
  }
}

apiClient.interceptors.request.use((config) => {
  const token = useXacThucStore.getState().maTruyCap;
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

apiClient.interceptors.response.use(
  (response) => response,
  async (error: AxiosError<PhanHoiApi<unknown>>) => {
    const requestGoc = error.config as YeuCauCoThuLai | undefined;
    const { maLamMoi, capNhatMaTruyCap } = useXacThucStore.getState();
    const laRequestLamMoiToken = requestGoc?.url === '/api/xac-thuc/lam-moi-token';

    if (error.response?.status === 401 && requestGoc && !requestGoc.daThuLamMoiToken && maLamMoi && !laRequestLamMoiToken) {
      requestGoc.daThuLamMoiToken = true;

      try {
        const response = await refreshClient.post<PhanHoiApi<LamMoiTokenResponse>>('/api/xac-thuc/lam-moi-token', {
          maLamMoi,
        });
        const maTruyCapMoi = response.data.duLieu.maTruyCap;
        capNhatMaTruyCap(maTruyCapMoi);
        requestGoc.headers.Authorization = `Bearer ${maTruyCapMoi}`;
        return apiClient.request(requestGoc);
      } catch {
        dangXuatDoHetPhien();
      }
    }

    if (error.response?.status === 401) {
      dangXuatDoHetPhien();
    }

    const thongBao = error.response?.data?.thongBao || error.message || 'Không thể kết nối máy chủ';
    return Promise.reject(new Error(thongBao));
  },
);

export async function goiApi<T>(config: AxiosRequestConfig) {
  const response = await apiClient.request<PhanHoiApi<T>>(config);
  return response.data.duLieu;
}
