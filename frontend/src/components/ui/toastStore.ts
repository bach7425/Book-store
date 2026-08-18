import { toast } from 'sonner';

interface ToastState {
  baoTin: (noiDung: string) => string | number;
  baoLoi: (noiDung: string) => string | number;
}

const toastState: ToastState = {
  baoTin: (noiDung) => toast.success(noiDung),
  baoLoi: (noiDung) => toast.error(noiDung),
};

export function useToastStore<T = ToastState>(selector?: (state: ToastState) => T) {
  return selector ? selector(toastState) : (toastState as T);
}
