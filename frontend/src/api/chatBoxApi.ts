import type { ChatRequest } from '../types';
import { goiApi } from './client';

export const chatBoxApi = {
  hoi: (duLieu: ChatRequest) => goiApi<string>({ url: '/api/ai/hoi', method: 'POST', data: duLieu }),
};
