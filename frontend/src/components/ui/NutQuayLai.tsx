import { ArrowLeft } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { Button } from './Button';

interface NutQuayLaiProps {
  veDau?: string;
  className?: string;
}

export function NutQuayLai({ veDau = '/', className }: NutQuayLaiProps) {
  const navigate = useNavigate();

  const quayLai = () => {
    if (window.history.length > 1) {
      navigate(-1);
      return;
    }
    navigate(veDau);
  };

  return (
    <Button type="button" kieu="rong" className={className} onClick={quayLai}>
      <ArrowLeft size={16} /> Quay lại
    </Button>
  );
}
