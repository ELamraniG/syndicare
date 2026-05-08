import { useEffect, useState } from 'react';
import { dashboardApi, ticketsApi, announcementsApi } from '../lib/api';
import { PageHeader, StatCard, Badge, fmtMoney, fmtDateTime, Skeleton } from '../components/UI';
import { Building2, Users, Coins, Wrench, TrendingUp, AlertCircle, CheckCircle2, Clock } from 'lucide-react';
import { LineChart, Line, BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, CartesianGrid } from 'recharts';

const ticketStatusColor = (s) => ({
  OPEN: 'red', IN_PROGRESS: 'yellow', RESOLVED: 'green', CLOSED: 'gray',
}[s] || 'gray');

const severityColor = (s) => ({ INFO: 'blue', WARNING: 'yellow', URGENT: 'red' }[s] || 'gray');

export default function AdminDashboard() {
  const [stats, setStats] = useState(null);
  const [tickets, setTickets] = useState([]);
  const [announcements, setAnnouncements] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    (async () => {
      try {
        const [s, t, a] = await Promise.all([
          dashboardApi.admin(),
          ticketsApi.list(),
          announcementsApi.list(),
        ]);
        setStats(s.data);
        setTickets(t.data.slice(0, 5));
        setAnnouncements(a.data.slice(0, 3));
      } finally {
        setLoading(false);
      }
    })();
  }, []);

  if (loading) {
    return (
      <div>
        <PageHeader eyebrow="Tableau de bord" title="Vue d'ensemble" />
        <div className="grid sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
          {[...Array(4)].map((_, i) => <Skeleton key={i} className="h-28" />)}
        </div>
        <Skeleton className="h-72" />
      </div>
    );
  }

  const chartData = stats.revenueByMonth?.map((m) => ({
    month: m.month?.slice(5),
    revenue: Number(m.amount),
  })) || [];

  return (
    <div>
      <PageHeader
        eyebrow="Tableau de bord — Syndic"
        title="Vue d'ensemble de la copropriété"
        description="Suivez en temps réel l'activité financière, les incidents et les communications."
      />

      {/* Top stats */}
      <div className="grid sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
        <StatCard label="Recettes du mois" value={fmtMoney(stats.totalRevenueThisMonth)} icon={TrendingUp} accent />
        <StatCard label="Impayés" value={fmtMoney(stats.totalUnpaidAmount)} sub={`${stats.pendingCharges} charge(s)`} icon={AlertCircle} />
        <StatCard label="Tickets ouverts" value={stats.openTickets + stats.inProgressTickets} sub={`${stats.openTickets} nouveaux`} icon={Wrench} />
        <StatCard label="Patrimoine" value={`${stats.totalApartments} lots`} sub={`${stats.totalBuildings} immeuble(s)`} icon={Building2} />
      </div>

      {/* Inventory */}
      <div className="grid sm:grid-cols-3 gap-4 mb-8">
        <div className="card p-4 flex items-center gap-3">
          <div className="w-10 h-10 rounded-lg bg-sage-100 grid place-items-center">
            <Users className="w-5 h-5 text-sage-700" />
          </div>
          <div>
            <div className="text-xs uppercase text-ink/50 tracking-wide">Propriétaires</div>
            <div className="font-display text-2xl font-bold">{stats.totalOwners}</div>
          </div>
        </div>
        <div className="card p-4 flex items-center gap-3">
          <div className="w-10 h-10 rounded-lg bg-sage-100 grid place-items-center">
            <Users className="w-5 h-5 text-sage-700" />
          </div>
          <div>
            <div className="text-xs uppercase text-ink/50 tracking-wide">Résidents</div>
            <div className="font-display text-2xl font-bold">{stats.totalResidents}</div>
          </div>
        </div>
        <div className="card p-4 flex items-center gap-3">
          <div className="w-10 h-10 rounded-lg bg-sage-100 grid place-items-center">
            <CheckCircle2 className="w-5 h-5 text-sage-700" />
          </div>
          <div>
            <div className="text-xs uppercase text-ink/50 tracking-wide">Charges payées</div>
            <div className="font-display text-2xl font-bold">{stats.paidCharges}</div>
          </div>
        </div>
      </div>

      <div className="grid lg:grid-cols-3 gap-6">
        {/* Revenue chart */}
        <div className="card p-6 lg:col-span-2">
          <div className="flex items-center justify-between mb-4">
            <div>
              <div className="text-xs uppercase tracking-wider text-ink/50 font-medium">Évolution</div>
              <h3 className="font-display text-xl font-bold">Recettes — 6 derniers mois</h3>
            </div>
          </div>
          {chartData.length === 0 ? (
            <div className="h-64 grid place-items-center text-ink/40 text-sm">Aucune donnée</div>
          ) : (
            <div className="h-64">
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={chartData} margin={{ top: 10, right: 10, bottom: 0, left: -10 }}>
                  <CartesianGrid strokeDasharray="3 3" stroke="#0a0e1a10" vertical={false} />
                  <XAxis dataKey="month" tick={{ fill: '#0a0e1a80', fontSize: 12 }} axisLine={false} tickLine={false} />
                  <YAxis tick={{ fill: '#0a0e1a80', fontSize: 12 }} axisLine={false} tickLine={false} />
                  <Tooltip
                    contentStyle={{ background: '#0a0e1a', border: 'none', borderRadius: 8, color: '#f7f4ee' }}
                    formatter={(v) => fmtMoney(v)}
                  />
                  <Bar dataKey="revenue" fill="#d18e2e" radius={[6, 6, 0, 0]} />
                </BarChart>
              </ResponsiveContainer>
            </div>
          )}
        </div>

        {/* Tickets summary */}
        <div className="card p-6">
          <div className="text-xs uppercase tracking-wider text-ink/50 font-medium">Helpdesk</div>
          <h3 className="font-display text-xl font-bold mb-4">Réclamations</h3>
          <div className="space-y-3">
            <SummaryLine icon={AlertCircle} label="Ouverts" value={stats.openTickets} color="text-red-600" />
            <SummaryLine icon={Clock} label="En traitement" value={stats.inProgressTickets} color="text-amber-600" />
            <SummaryLine icon={CheckCircle2} label="Résolus" value={stats.resolvedTickets} color="text-sage-600" />
          </div>
        </div>
      </div>

      {/* Latest tickets & announcements */}
      <div className="grid lg:grid-cols-2 gap-6 mt-6">
        <div className="card p-6">
          <h3 className="font-display text-lg font-bold mb-4">Dernières réclamations</h3>
          {tickets.length === 0 ? (
            <p className="text-ink/50 text-sm">Aucune réclamation récente.</p>
          ) : (
            <div className="space-y-3">
              {tickets.map((t) => (
                <div key={t.id} className="flex items-start justify-between gap-3 pb-3 border-b border-ink/5 last:border-0">
                  <div className="min-w-0">
                    <div className="font-medium text-sm truncate">{t.title}</div>
                    <div className="text-xs text-ink/50 mt-0.5">
                      {t.submitterName} · {fmtDateTime(t.createdAt)}
                    </div>
                  </div>
                  <Badge color={ticketStatusColor(t.status)}>{t.status}</Badge>
                </div>
              ))}
            </div>
          )}
        </div>

        <div className="card p-6">
          <h3 className="font-display text-lg font-bold mb-4">Annonces récentes</h3>
          {announcements.length === 0 ? (
            <p className="text-ink/50 text-sm">Aucune annonce publiée.</p>
          ) : (
            <div className="space-y-3">
              {announcements.map((a) => (
                <div key={a.id} className="pb-3 border-b border-ink/5 last:border-0">
                  <div className="flex items-center gap-2 mb-1">
                    <Badge color={severityColor(a.severity)}>{a.severity}</Badge>
                    {a.pinned && <Badge color="ochre">Épinglée</Badge>}
                  </div>
                  <div className="font-medium text-sm">{a.title}</div>
                  <div className="text-xs text-ink/50 mt-1">{fmtDateTime(a.createdAt)}</div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

const SummaryLine = ({ icon: Icon, label, value, color }) => (
  <div className="flex items-center justify-between">
    <div className="flex items-center gap-2">
      <Icon className={`w-4 h-4 ${color}`} />
      <span className="text-sm text-ink/70">{label}</span>
    </div>
    <span className="font-display font-bold text-lg">{value}</span>
  </div>
);
