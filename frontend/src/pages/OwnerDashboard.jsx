import { useEffect, useState } from 'react';
import { dashboardApi, chargesApi, announcementsApi, ticketsApi } from '../lib/api';
import { useAuth } from '../context/AuthContext';
import { PageHeader, StatCard, Badge, fmtMoney, fmtDate, fmtDateTime, Skeleton, Empty } from '../components/UI';
import { Wallet, AlertCircle, Home as HomeIcon, FileCheck, Megaphone } from 'lucide-react';
import { Link } from 'react-router-dom';

const severityColor = (s) => ({ INFO: 'blue', WARNING: 'yellow', URGENT: 'red' }[s] || 'gray');
const statusColor = (s) => ({ PENDING: 'yellow', PAID: 'green', OVERDUE: 'red' }[s] || 'gray');

export default function OwnerDashboard() {
  const { user } = useAuth();
  const [stats, setStats] = useState(null);
  const [charges, setCharges] = useState([]);
  const [announcements, setAnnouncements] = useState([]);
  const [tickets, setTickets] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    (async () => {
      try {
        const calls = [
          dashboardApi.owner().catch(() => ({ data: null })),
          chargesApi.mine().catch(() => ({ data: [] })),
          announcementsApi.list().catch(() => ({ data: [] })),
          ticketsApi.list().catch(() => ({ data: [] })),
        ];
        const [s, c, a, t] = await Promise.all(calls);
        setStats(s.data);
        setCharges(c.data.slice(0, 6));
        setAnnouncements(a.data.slice(0, 3));
        setTickets(t.data.slice(0, 5));
      } finally {
        setLoading(false);
      }
    })();
  }, []);

  if (loading) {
    return (
      <div>
        <PageHeader eyebrow="Espace personnel" title="Bienvenue" />
        <div className="grid sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
          {[...Array(4)].map((_, i) => <Skeleton key={i} className="h-28" />)}
        </div>
      </div>
    );
  }

  return (
    <div>
      <PageHeader
        eyebrow={user.role === 'OWNER' ? 'Espace propriétaire' : 'Espace résident'}
        title={`Bonjour, ${user.firstName} 👋`}
        description={
          user.role === 'OWNER'
            ? "Voici l'état de votre patrimoine et de vos charges."
            : "Suivez les annonces et signalez tout incident en quelques clics."
        }
      />

      {/* Stats */}
      {stats && user.role === 'OWNER' && (
        <div className="grid sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
          <StatCard
            label="Solde dû"
            value={fmtMoney(stats.totalDue)}
            sub={`${stats.pendingChargesCount} charge(s) en attente`}
            icon={AlertCircle}
            accent={Number(stats.totalDue) > 0}
          />
          <StatCard label="Total réglé" value={fmtMoney(stats.totalPaid)} icon={FileCheck} />
          <StatCard label="Vos lots" value={stats.apartments?.length || 0} icon={HomeIcon} />
          <StatCard label="Tickets actifs" value={tickets.filter(t => t.status !== 'RESOLVED' && t.status !== 'CLOSED').length} icon={Wallet} />
        </div>
      )}

      <div className="grid lg:grid-cols-3 gap-6">
        {/* Charges (owners only) */}
        {user.role === 'OWNER' && (
          <div className="card p-6 lg:col-span-2">
            <div className="flex items-center justify-between mb-4">
              <h3 className="font-display text-xl font-bold">Vos charges</h3>
              <Link to="/charges" className="text-xs text-ink/60 hover:text-ink underline underline-offset-4">
                Voir toutes →
              </Link>
            </div>
            {charges.length === 0 ? (
              <Empty icon={Wallet} title="Aucune charge" description="Aucun appel de fonds n'a été émis pour vos lots pour le moment." />
            ) : (
              <div className="overflow-x-auto -mx-6">
                <table className="table-clean">
                  <thead>
                    <tr>
                      <th>Période</th>
                      <th>Logement</th>
                      <th>Montant</th>
                      <th>Statut</th>
                    </tr>
                  </thead>
                  <tbody>
                    {charges.map((c) => (
                      <tr key={c.id}>
                        <td className="font-medium">{fmtDate(c.period)}</td>
                        <td>{c.buildingName} · Apt {c.apartmentNumber}</td>
                        <td className="font-display font-semibold">{fmtMoney(c.amount)}</td>
                        <td><Badge color={statusColor(c.status)}>{c.status}</Badge></td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        )}

        {/* Announcements */}
        <div className={`card p-6 ${user.role !== 'OWNER' ? 'lg:col-span-2' : ''}`}>
          <div className="flex items-center gap-2 mb-4">
            <Megaphone className="w-5 h-5 text-ochre-600" />
            <h3 className="font-display text-xl font-bold">Annonces</h3>
          </div>
          {announcements.length === 0 ? (
            <p className="text-sm text-ink/50">Aucune annonce.</p>
          ) : (
            <div className="space-y-4">
              {announcements.map((a) => (
                <div key={a.id} className="pb-4 border-b border-ink/5 last:border-0 last:pb-0">
                  <div className="flex items-center gap-2 mb-1">
                    <Badge color={severityColor(a.severity)}>{a.severity}</Badge>
                    {a.pinned && <Badge color="ochre">Épinglée</Badge>}
                  </div>
                  <div className="font-display font-bold">{a.title}</div>
                  <p className="text-sm text-ink/70 mt-1 line-clamp-3">{a.content}</p>
                  <div className="text-xs text-ink/40 mt-2">{fmtDateTime(a.createdAt)}</div>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* My tickets */}
        <div className="card p-6 lg:col-span-3">
          <div className="flex items-center justify-between mb-4">
            <h3 className="font-display text-xl font-bold">Mes réclamations</h3>
            <Link to="/tickets" className="text-xs text-ink/60 hover:text-ink underline underline-offset-4">
              Tout voir →
            </Link>
          </div>
          {tickets.length === 0 ? (
            <Empty
              icon={Wallet}
              title="Aucune réclamation"
              description="Signalez une fuite, une panne ou tout autre incident."
              action={<Link to="/tickets" className="btn-primary">Déclarer un incident</Link>}
            />
          ) : (
            <div className="grid sm:grid-cols-2 gap-3">
              {tickets.map((t) => (
                <div key={t.id} className="border border-ink/10 rounded-lg p-4">
                  <div className="flex items-start justify-between gap-2 mb-1">
                    <div className="font-medium text-sm">{t.title}</div>
                    <Badge color={t.status === 'OPEN' ? 'red' : t.status === 'IN_PROGRESS' ? 'yellow' : 'green'}>
                      {t.status}
                    </Badge>
                  </div>
                  <div className="text-xs text-ink/50">{t.category} · {fmtDateTime(t.createdAt)}</div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
