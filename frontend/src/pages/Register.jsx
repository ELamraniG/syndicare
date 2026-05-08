import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import toast from 'react-hot-toast';
import { Building2, ArrowLeft } from 'lucide-react';

export default function Register() {
  const { register } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    phone: '',
    role: 'OWNER',
  });
  const [submitting, setSubmitting] = useState(false);

  const handle = (k) => (e) => setForm((f) => ({ ...f, [k]: e.target.value }));

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    try {
      const u = await register(form);
      toast.success(`Compte créé. Bienvenue, ${u.firstName}.`);
      navigate(u.role === 'ADMIN' ? '/admin' : '/dashboard');
    } catch (err) {
      toast.error(err.response?.data?.message || 'Échec de la création de compte');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center p-6 bg-cream">
      <div className="w-full max-w-xl card p-8 animate-slide-up">
        <div className="flex items-center justify-between mb-8">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 bg-ink rounded-lg grid place-items-center">
              <Building2 className="w-5 h-5 text-ochre-500" strokeWidth={2.5} />
            </div>
            <span className="font-display text-xl font-bold">SyndiCare</span>
          </div>
          <Link to="/login" className="text-sm text-ink/60 hover:text-ink flex items-center gap-1">
            <ArrowLeft className="w-4 h-4" /> Retour
          </Link>
        </div>

        <div className="mb-6">
          <div className="text-xs uppercase tracking-[0.3em] text-ink/40 mb-2">Inscription</div>
          <h2 className="font-display text-3xl font-bold">Créer un compte</h2>
        </div>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="grid sm:grid-cols-2 gap-4">
            <div>
              <label className="label">Prénom</label>
              <input className="input" value={form.firstName} onChange={handle('firstName')} required />
            </div>
            <div>
              <label className="label">Nom</label>
              <input className="input" value={form.lastName} onChange={handle('lastName')} required />
            </div>
          </div>
          <div>
            <label className="label">Email</label>
            <input type="email" className="input" value={form.email} onChange={handle('email')} required />
          </div>
          <div className="grid sm:grid-cols-2 gap-4">
            <div>
              <label className="label">Mot de passe</label>
              <input type="password" minLength={6} className="input" value={form.password} onChange={handle('password')} required />
            </div>
            <div>
              <label className="label">Téléphone</label>
              <input className="input" value={form.phone} onChange={handle('phone')} />
            </div>
          </div>
          <div>
            <label className="label">Rôle</label>
            <select className="input" value={form.role} onChange={handle('role')}>
              <option value="OWNER">Propriétaire</option>
              <option value="RESIDENT">Résident / Locataire</option>
              <option value="ADMIN">Syndic (admin)</option>
            </select>
          </div>

          <button type="submit" disabled={submitting} className="btn-primary w-full py-3 mt-2">
            {submitting ? 'Création...' : 'Créer mon compte'}
          </button>
        </form>
      </div>
    </div>
  );
}
