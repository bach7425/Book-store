import { forwardRef, type InputHTMLAttributes } from 'react';
import { twMerge } from 'tailwind-merge';

export const Input = forwardRef<HTMLInputElement, InputHTMLAttributes<HTMLInputElement>>(function Input(props, ref) {
  return (
    <input
      {...props}
      ref={ref}
      className={twMerge(
        'min-h-10 w-full rounded border border-[#c4c6cd] bg-[#fbf9f8] px-3 py-2 text-sm text-[#1b1c1c] outline-none transition placeholder:text-[#74777d] focus:border-[#03192e] focus:bg-white focus:ring-2 focus:ring-[#b4c8e4]/45',
        props.className,
      )}
    />
  );
});
