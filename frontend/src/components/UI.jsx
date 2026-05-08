import { X } from 'lucide-react';

export const PageHeader = ({ eyebrow, title, description, actions }) => (
  <div className="flex flex-col sm:flex-row sm:items-end justify-between gap-4 mb-8 animate-slide-up">
    <div>
      {eyebrow && <div className="text-xs uppercase tracking-[0.25em] text-ink/40 mb-2 font-medium">{eyebrow}</div>}
      <h1 className="font-display text-3xl sm:text-4xl font-bold tracking-tight">{title}</h1>
      {description && <p className="text-ink/60 mt-2 max-w-2xl">{description}</p>}
    </div>
    {actions && <div className="flex gap-2 flex-wrap">{actions}</div>}
  </div>
);

export const StatCard = ({ label, value, sub, icon: Icon, accent = false }) => (
  <div className={`card p-5 ${accent ? 'bg-ink text-cream border-ink' : ''}`}>
    <div className="flex items-start justify-between">
      <div className="text-xs uppercase tracking-wider font-medium opacity-60">{label}</div>
      {Icon && <Icon className={`w-4 h-4 ${accent ? 'text-ochre-400' : 'text-ink/40'}`} />}
    </div>
    <div className={`font-display text-3xl font-bold mt-3 ${accent ? 'text-ochre-400' : ''}`}>{value}</div>
    {sub && <div className="text-xs opacity-50 mt-1">{sub}</div>}
  </div>
);

export const Badge = ({ children, color = 'gray' }) => {
  const colors = {
    gray: 'bg-ink/10 text-ink/70',
    green: 'bg-sage-100 text-sage-800',
    yellow: 'bg-amber-100 text-amber-800',
    red: 'bg-red-100 text-red-800',
    blue: 'bg-blue-100 text-blue-800',
    ochre: 'bg-ochre-500/20 text-ochre-600',
  };
  return <span className={`badge ${colors[color] || colors.gray}`}>{children}</span>;
};

export const Modal = ({ open, onClose, title, children, size = 'md' }) => {
  if (!open) return null;
  const sizes = { sm: 'max-w-sm', md: 'max-w-lg', lg: 'max-w-2xl', xl: 'max-w-4xl' };
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 animate-fade-in">
      <div className="absolute inset-0 bg-ink/40 backdrop-blur-sm" onClick={onClose} />
      <div className={`relative w-full ${sizes[size]} card p-6 max-h-[90vh] overflow-y-auto animate-slide-up`}>
        <div className="flex items-center justify-between mb-5">
          <h2 className="font-display text-xl font-bold">{title}</h2>
          <button onClick={onClose} className="p-1 -m-1 text-ink/50 hover:text-ink">
            <X className="w-5 h-5" />
          </button>
        </div>
        {children}
      </div>
    </div>
  );
};

export const Empty = ({ icon: Icon, title, description, action }) => (
  <div className="card p-12 text-center">
    {Icon && (
      <div className="w-12 h-12 rounded-full bg-sage-100 grid place-items-center mx-auto mb-4">
        <Icon className="w-5 h-5 text-sage-600" />
      </div>
    )}
    <h3 className="font-display text-lg font-bold">{title}</h3>
    {description && <p className="text-ink/60 mt-2 max-w-md mx-auto text-sm">{description}</p>}
    {action && <div className="mt-5">{action}</div>}
  </div>
);

export const Skeleton = ({ className = '' }) => (
  <div className={`animate-pulse bg-ink/5 rounded ${className}`} />
);

export const fmtMoney = (n) => {
  if (n == null) return '—';
  return new Intl.NumberFormat('fr-FR', { minimumFractionDigits: 2, maximumFractionDigits: 2 }).format(Number(n)) + ' MAD';
};

export const fmtDate = (s) => {
  if (!s) return '—';
  try {
    return new Date(s).toLocaleDateString('fr-FR', { day: '2-digit', month: 'short', year: 'numeric' });
  } catch { return s; }
};

export const fmtDateTime = (s) => {
  if (!s) return '—';
  try {
    return new Date(s).toLocaleString('fr-FR', { day: '2-digit', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit' });
  } catch { return s; }
};
