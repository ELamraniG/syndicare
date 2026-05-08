import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import toast from 'react-hot-toast';
import { Building2, ArrowRight, Lock, Mail } from 'lucide-react';

export default function Login() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [submitting, setSubmitting] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    try {
      const u = await login(email, password);
      toast.success(`Bienvenue, ${u.firstName}`);
      navigate(u.role === 'ADMIN' ? '/admin' : '/dashboard');
    } catch (err) {
      toast.error(err.response?.data?.message || 'Échec de connexion');
    } finally {
      setSubmitting(false);
    }
  };

  const fillDemo = (role) => {
    if (role === 'admin') { setEmail('admin@syndicare.ma'); setPassword('admin123'); }
    if (role === 'owner') { setEmail('owner@syndicare.ma'); setPassword('owner123'); }
    if (role === 'resident') { setEmail('resident@syndicare.ma'); setPassword('resident123'); }
  };

  return (
    <div className="min-h-screen grid lg:grid-cols-5 bg-cream">
      {/* Left brand panel */}
      <div className="hidden lg:flex lg:col-span-3 relative bg-ink text-cream overflow-hidden">
        <div className="absolute inset-0 opacity-30"
             style={{ backgroundImage: 'radial-gradient(circle at 20% 30%, rgba(224, 169, 85, 0.4), transparent 50%), radial-gradient(circle at 80% 70%, rgba(90, 130, 89, 0.3), transparent 50%)' }} />
        <div className="absolute top-0 right-0 w-96 h-96 border border-cream/10 rounded-full -translate-y-1/2 translate-x-1/2" />
        <div className="absolute bottom-0 left-0 w-72 h-72 border border-ochre-500/20 rounded-full translate-y-1/2 -translate-x-1/2" />

        <div className="relative z-10 flex flex-col justify-between p-12 w-full">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 bg-ochre-500 rounded-lg grid place-items-center">
              <Building2 className="w-5 h-5 text-ink" strokeWidth={2.5} />
            </div>
            <span className="font-display text-2xl font-bold tracking-tight">SyndiCare</span>
          </div>

          <div className="space-y-6 max-w-lg">
            <div className="text-xs uppercase tracking-[0.3em] text-ochre-400 font-medium">
              Property Management · Réinventé
            </div>
            <h1 className="font-display text-5xl xl:text-6xl font-bold leading-[1.05]">
              Gérez votre copropriété <span className="text-ochre-400 italic">sans friction.</span>
            </h1>
            <p className="text-cream/60 text-lg leading-relaxed font-light">
              Une plateforme unifiée pour les syndics, propriétaires et résidents.
              Charges, incidents, documents — tout au même endroit, en temps réel.
            </p>
          </div>

          <div className="grid grid-cols-3 gap-4 max-w-md">
            <Stat label="Transparence" value="100%" />
            <Stat label="Modules" value="06" />
            <Stat label="Rôles" value="03" />
          </div>
        </div>
      </div>

      {/* Right form */}
      <div className="lg:col-span-2 flex items-center justify-center p-6 sm:p-12">
        <div className="w-full max-w-md animate-slide-up">
          <div className="lg:hidden flex items-center gap-3 mb-12">
            <div className="w-10 h-10 bg-ink rounded-lg grid place-items-center">
              <Building2 className="w-5 h-5 text-ochre-500" strokeWidth={2.5} />
            </div>
            <span className="font-display text-2xl font-bold">SyndiCare</span>
          </div>

          <div className="mb-8">
            <div className="text-xs uppercase tracking-[0.3em] text-ink/40 mb-2">Connexion</div>
            <h2 className="font-display text-4xl font-bold">Bon retour.</h2>
            <p className="text-ink/60 mt-2">Entrez vos identifiants pour accéder à votre espace.</p>
          </div>

          <form onSubmit={handleSubmit} className="space-y-5">
            <div>
              <label className="label">Adresse email</label>
              <div className="relative">
                <Mail className="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-ink/30" />
                <input
                  type="email"
                  required
                  className="input pl-10"
                  placeholder="vous@example.com"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  autoFocus
                />
              </div>
            </div>

            <div>
              <label className="label">Mot de passe</label>
              <div className="relative">
                <Lock className="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-ink/30" />
                <input
                  type="password"
                  required
                  className="input pl-10"
                  placeholder="••••••••"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                />
              </div>
            </div>

            <button type="submit" disabled={submitting} className="btn-primary w-full py-3 group">
              {submitting ? 'Connexion...' : 'Se connecter'}
              <ArrowRight className="w-4 h-4 transition-transform group-hover:translate-x-1" />
            </button>
          </form>

          <div className="mt-8 pt-8 border-t border-ink/10">
            <div className="text-xs uppercase tracking-wide text-ink/40 mb-3 font-medium">
              Comptes de démonstration
            </div>
            <div className="grid grid-cols-3 gap-2">
              <button onClick={() => fillDemo('admin')} className="btn-secondary text-xs py-2 px-2">
                Syndic
              </button>
              <button onClick={() => fillDemo('owner')} className="btn-secondary text-xs py-2 px-2">
                Propriétaire
              </button>
              <button onClick={() => fillDemo('resident')} className="btn-secondary text-xs py-2 px-2">
                Résident
              </button>
            </div>
          </div>

          <p className="mt-8 text-center text-sm text-ink/50">
            Pas encore de compte ?{' '}
            <Link to="/register" className="font-medium text-ink hover:text-ochre-600 underline underline-offset-4">
              S'inscrire
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
}

const Stat = ({ label, value }) => (
  <div>
    <div className="font-display text-3xl font-bold text-ochre-400">{value}</div>
    <div className="text-xs uppercase tracking-wider text-cream/40 mt-1">{label}</div>
  </div>
);
