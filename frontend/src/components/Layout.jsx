import { Outlet, NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useState } from 'react';
import {
  LayoutDashboard, Building2, Home as HomeIcon, Users, Coins,
  Wrench, FileText, Megaphone, LogOut, Menu, X
} from 'lucide-react';

const ROLE_LABELS = {
  ADMIN: 'Syndic',
  OWNER: 'Propriétaire',
  RESIDENT: 'Résident',
};

export default function Layout() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [open, setOpen] = useState(false);

  const links = [
    { to: user.role === 'ADMIN' ? '/admin' : '/dashboard', icon: LayoutDashboard, label: 'Tableau de bord' },
    { to: '/buildings', icon: Building2, label: 'Immeubles', roles: ['ADMIN'] },
    { to: '/apartments', icon: HomeIcon, label: 'Appartements' },
    { to: '/users', icon: Users, label: 'Utilisateurs', roles: ['ADMIN'] },
    { to: '/charges', icon: Coins, label: 'Charges' },
    { to: '/tickets', icon: Wrench, label: 'Réclamations' },
    { to: '/documents', icon: FileText, label: 'Documents' },
    { to: '/announcements', icon: Megaphone, label: 'Annonces' },
  ];

  const visible = links.filter((l) => !l.roles || l.roles.includes(user.role));

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <div className="min-h-screen flex bg-cream">
      {/* Mobile top bar */}
      <div className="lg:hidden fixed top-0 inset-x-0 z-40 bg-ink text-cream px-4 py-3 flex items-center justify-between">
        <div className="flex items-center gap-2">
          <div className="w-8 h-8 bg-ochre-500 rounded-md grid place-items-center">
            <Building2 className="w-4 h-4 text-ink" strokeWidth={2.5} />
          </div>
          <span className="font-display font-bold text-lg">SyndiCare</span>
        </div>
        <button onClick={() => setOpen(!open)} className="p-2 -mr-2">
          {open ? <X className="w-5 h-5" /> : <Menu className="w-5 h-5" />}
        </button>
      </div>

      {/* Sidebar */}
      <aside
        className={`fixed lg:sticky top-0 left-0 h-screen w-72 bg-ink text-cream flex flex-col z-30 transition-transform lg:translate-x-0 ${
          open ? 'translate-x-0' : '-translate-x-full'
        }`}
      >
        <div className="p-6 border-b border-cream/10">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 bg-ochre-500 rounded-lg grid place-items-center">
              <Building2 className="w-5 h-5 text-ink" strokeWidth={2.5} />
            </div>
            <div>
              <div className="font-display font-bold text-xl leading-none">SyndiCare</div>
              <div className="text-xs text-cream/40 mt-1 tracking-wide uppercase">v1.0</div>
            </div>
          </div>
        </div>

        <nav className="flex-1 overflow-y-auto p-4 space-y-1">
          {visible.map(({ to, icon: Icon, label }) => (
            <NavLink
              key={to}
              to={to}
              end={to === '/admin' || to === '/dashboard'}
              onClick={() => setOpen(false)}
              className={({ isActive }) =>
                `flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm transition-colors ${
                  isActive
                    ? 'bg-ochre-500 text-ink font-semibold'
                    : 'text-cream/70 hover:text-cream hover:bg-cream/5'
                }`
              }
            >
              <Icon className="w-4 h-4" />
              {label}
            </NavLink>
          ))}
        </nav>

        <div className="p-4 border-t border-cream/10">
          <div className="flex items-center gap-3 mb-3 px-2">
            <div className="w-9 h-9 rounded-full bg-cream/10 grid place-items-center font-medium text-sm">
              {user.firstName?.[0]}{user.lastName?.[0]}
            </div>
            <div className="flex-1 min-w-0">
              <div className="text-sm font-medium truncate">
                {user.firstName} {user.lastName}
              </div>
              <div className="text-xs text-cream/50 truncate">
                {ROLE_LABELS[user.role]} · {user.email}
              </div>
            </div>
          </div>
          <button onClick={handleLogout} className="w-full flex items-center gap-2 text-sm text-cream/70 hover:text-cream hover:bg-cream/5 px-3 py-2 rounded-lg">
            <LogOut className="w-4 h-4" />
            Se déconnecter
          </button>
        </div>
      </aside>

      {/* Backdrop */}
      {open && <div className="lg:hidden fixed inset-0 bg-ink/40 z-20" onClick={() => setOpen(false)} />}

      {/* Main content */}
      <main className="flex-1 lg:ml-0 pt-14 lg:pt-0 min-w-0">
        <div className="p-6 sm:p-8 lg:p-10 max-w-7xl mx-auto">
          <Outlet />
        </div>
      </main>
    </div>
  );
}
