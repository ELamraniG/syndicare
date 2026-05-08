import { useEffect, useState } from 'react';
import { chargesApi, buildingsApi } from '../lib/api';
import { useAuth } from '../context/AuthContext';
import { PageHeader, Modal, Empty, Skeleton, Badge, fmtMoney, fmtDate } from '../components/UI';
import { Coins, Sparkles, CheckCheck, FileDown, Trash2 } from 'lucide-react';
import toast from 'react-hot-toast';

const statusColor = (s) => ({ PENDING: 'yellow', PAID: 'green', OVERDUE: 'red' }[s] || 'gray');

const todayMonthInput = () => {
  const d = new Date();
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}`;
};

export default function Charges() {
  const { user } = useAuth();
  const isAdmin = user.role === 'ADMIN';

  const [items, setItems] = useState([]);
  const [buildings, setBuildings] = useState([]);
  const [filter, setFilter] = useState({ buildingId: '', status: '' });
  const [loading, setLoading] = useState(true);

  const [genOpen, setGenOpen] = useState(false);
  const [genForm, setGenForm] = useState({ buildingId: '', period: todayMonthInput(), description: '', ratePerSqm: '' });

  const [payOpen, setPayOpen] = useState(false);
  const [payCharge, setPayCharge] = useState(null);
  const [payRef, setPayRef] = useState('');

  const load = async () => {
    setLoading(true);
    try {
      if (isAdmin) {
        const [c, b] = await Promise.all([
          chargesApi.list(filter.buildingId ? { buildingId: filter.buildingId } : {}),
          buildingsApi.list(),
        ]);
        setItems(c.data);
        setBuildings(b.data);
      } else {
        const { data } = await chargesApi.mine();
        setItems(data);
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { load(); /* eslint-disable-next-line */ }, [filter.buildingId]);

  const filtered = items.filter((c) => !filter.status || c.status === filter.status);

  const generate = async (e) => {
    e.preventDefault();
    try {
      const payload = {
        buildingId: Number(genForm.buildingId),
        period: `${genForm.period}-01`,
        description: genForm.description || null,
        ratePerSqm: genForm.ratePerSqm ? Number(genForm.ratePerSqm) : null,
      };
      const { data } = await chargesApi.generate(payload);
      toast.success(`${data.length} charge(s) générée(s)`);
      setGenOpen(false);
      setGenForm({ buildingId: '', period: todayMonthInput(), description: '', ratePerSqm: '' });
      load();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Erreur de génération');
    }
  };

  const validate = async (e) => {
    e.preventDefault();
    try {
      await chargesApi.validatePayment({ chargeId: payCharge.id, paymentReference: payRef || null });
      toast.success('Paiement validé');
      setPayOpen(false);
      setPayRef('');
      load();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Erreur');
    }
  };

  const remove = async (id) => {
    if (!confirm('Supprimer cette charge ?')) return;
    await chargesApi.delete(id);
    toast.success('Supprimé');
    load();
  };

  const downloadReceipt = (c) => {
    const url = chargesApi.receiptUrl(c.id);
    const token = localStorage.getItem('syndicare_token');
    fetch(url, { headers: { Authorization: `Bearer ${token}` } })
      .then((r) => r.blob())
      .then((blob) => {
        const a = document.createElement('a');
        a.href = URL.createObjectURL(blob);
        a.download = `quittance-${c.id}.pdf`;
        a.click();
        URL.revokeObjectURL(a.href);
      })
      .catch(() => toast.error('Téléchargement impossible'));
  };

  return (
    <div>
      <PageHeader
        eyebrow="Finances"
        title={isAdmin ? 'Charges & paiements' : 'Mes charges'}
        description={isAdmin
          ? "Générez les appels de fonds mensuels et validez les encaissements."
          : "Suivez vos charges et téléchargez vos quittances une fois validées."}
        actions={
          isAdmin && (
            <button className="btn-primary" onClick={() => setGenOpen(true)}>
              <Sparkles className="w-4 h-4" /> Générer les charges du mois
            </button>
          )
        }
      />

      <div className="mb-4 flex flex-wrap gap-2">
        {isAdmin && (
          <select className="input max-w-xs" value={filter.buildingId} onChange={(e) => setFilter({ ...filter, buildingId: e.target.value })}>
            <option value="">Tous les immeubles</option>
            {buildings.map((b) => <option key={b.id} value={b.id}>{b.name}</option>)}
          </select>
        )}
        <select className="input max-w-xs" value={filter.status} onChange={(e) => setFilter({ ...filter, status: e.target.value })}>
          <option value="">Tous statuts</option>
          <option value="PENDING">En attente</option>
          <option value="PAID">Payées</option>
          <option value="OVERDUE">En retard</option>
        </select>
      </div>

      {loading ? (
        <Skeleton className="h-64" />
      ) : filtered.length === 0 ? (
        <Empty icon={Coins} title="Aucune charge" description="Aucun appel de fonds ne correspond aux critères." />
      ) : (
        <div className="card overflow-hidden">
          <div className="overflow-x-auto">
            <table className="table-clean">
              <thead>
                <tr>
                  <th>Période</th>
                  <th>Logement</th>
                  {isAdmin && <th>Propriétaire</th>}
                  <th>Description</th>
                  <th>Montant</th>
                  <th>Échéance</th>
                  <th>Statut</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                {filtered.map((c) => (
                  <tr key={c.id}>
                    <td className="font-medium">{fmtDate(c.period)}</td>
                    <td className="text-ink/70">{c.buildingName} · Apt {c.apartmentNumber}</td>
                    {isAdmin && <td className="text-ink/70">{c.ownerName || '—'}</td>}
                    <td className="text-ink/60 text-xs">{c.description}</td>
                    <td className="font-display font-bold">{fmtMoney(c.amount)}</td>
                    <td className="text-ink/70">{fmtDate(c.dueDate)}</td>
                    <td><Badge color={statusColor(c.status)}>{c.status}</Badge></td>
                    <td>
                      <div className="flex gap-1 justify-end">
                        {c.status === 'PAID' && (
                          <button onClick={() => downloadReceipt(c)} className="p-1.5 text-ink/50 hover:text-ink hover:bg-ink/5 rounded" title="Quittance">
                            <FileDown className="w-4 h-4" />
                          </button>
                        )}
                        {isAdmin && c.status !== 'PAID' && (
                          <button
                            onClick={() => { setPayCharge(c); setPayOpen(true); }}
                            className="btn-accent text-xs px-2 py-1"
                          >
                            <CheckCheck className="w-3 h-3" /> Valider
                          </button>
                        )}
                        {isAdmin && (
                          <button onClick={() => remove(c.id)} className="p-1.5 text-ink/50 hover:text-red-600 hover:bg-red-50 rounded">
                            <Trash2 className="w-4 h-4" />
                          </button>
                        )}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* Generate modal */}
      <Modal open={genOpen} onClose={() => setGenOpen(false)} title="Générer les charges">
        <form onSubmit={generate} className="space-y-4">
          <p className="text-sm text-ink/60">
            Le système calcule automatiquement le montant pour chaque appartement de l'immeuble :
            <span className="font-mono text-ochre-600"> surface (m²) × tarif</span>.
          </p>
          <div>
            <label className="label">Immeuble</label>
            <select className="input" required value={genForm.buildingId} onChange={(e) => setGenForm({ ...genForm, buildingId: e.target.value })}>
              <option value="">— Sélectionner —</option>
              {buildings.map((b) => <option key={b.id} value={b.id}>{b.name} · {b.baseRatePerSqm ?? '—'} MAD/m²</option>)}
            </select>
          </div>
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="label">Période (mois)</label>
              <input type="month" className="input" required value={genForm.period} onChange={(e) => setGenForm({ ...genForm, period: e.target.value })} />
            </div>
            <div>
              <label className="label">Tarif personnalisé (optionnel)</label>
              <input type="number" step="0.01" className="input" placeholder="laisser vide pour tarif par défaut" value={genForm.ratePerSqm} onChange={(e) => setGenForm({ ...genForm, ratePerSqm: e.target.value })} />
            </div>
          </div>
          <div>
            <label className="label">Description</label>
            <input className="input" placeholder="ex. Charges courantes mai 2026" value={genForm.description} onChange={(e) => setGenForm({ ...genForm, description: e.target.value })} />
          </div>
          <div className="flex gap-2 pt-2">
            <button type="button" className="btn-secondary flex-1" onClick={() => setGenOpen(false)}>Annuler</button>
            <button type="submit" className="btn-primary flex-1"><Sparkles className="w-4 h-4" /> Générer</button>
          </div>
        </form>
      </Modal>

      {/* Validate payment modal */}
      <Modal open={payOpen} onClose={() => setPayOpen(false)} title="Valider le paiement">
        {payCharge && (
          <form onSubmit={validate} className="space-y-4">
            <div className="bg-sage-50 border border-sage-200 rounded-lg p-4">
              <div className="text-xs uppercase tracking-wide text-sage-700 font-medium">Charge à valider</div>
              <div className="mt-1 font-display font-bold text-lg">{fmtMoney(payCharge.amount)}</div>
              <div className="text-sm text-ink/70 mt-0.5">
                Apt {payCharge.apartmentNumber} · {fmtDate(payCharge.period)}
              </div>
            </div>
            <div>
              <label className="label">Référence du paiement (optionnel)</label>
              <input className="input" placeholder="ex. Virement n°12345 ou chèque n°078" value={payRef} onChange={(e) => setPayRef(e.target.value)} />
            </div>
            <div className="flex gap-2 pt-2">
              <button type="button" className="btn-secondary flex-1" onClick={() => setPayOpen(false)}>Annuler</button>
              <button type="submit" className="btn-primary flex-1"><CheckCheck className="w-4 h-4" /> Confirmer</button>
            </div>
          </form>
        )}
      </Modal>
    </div>
  );
}
