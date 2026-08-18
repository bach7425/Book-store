import { Toaster as SonnerToaster } from 'sonner';

export function Toaster() {
  return (
    <SonnerToaster
      closeButton
      richColors
      position="bottom-right"
      toastOptions={{
        className: 'font-sans',
      }}
    />
  );
}
