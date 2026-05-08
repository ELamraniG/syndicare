import { useEffect, useState } from 'react';
import { apartmentsApi, buildingsApi, usersApi } from '../lib/api';
import { useAuth } from '../context/AuthContext';
import { PageHeader, Modal, Empty, Skeleton, Badge } from '../components/UI';
import { Home as HomeIcon, Plus, Pencil, Trash2 } from 'lucide-react';
import toast from 'react-hot-toast';

const empty = { number: '', floor: 0, surface: '', buildingId: '', ownerId: '', residentId: '' };

export default function Apartments() {
  const { user } = useAuth();
  const isAdmin = user.role === 'ADMIN';

  const [items, setItems] = useState([]);
  const [buildings, setBuildings] = useState([]);
  const [owners, setOwners] = useState([]);
  const [residents, setResidents] = useState([]);
  const [filter, setFilter] = useState('');
  const [loading, setLoading] = useState(true);
  const [open, setOpen] = useState(false);
  const [form, setForm] = useState(empty);
  const [editingId, setEditingId] = useState(null);

  const load = async () => {
    setLoading(true);
    try {
      if (isAdmin) {
        const [a, b, o, r] = await Promise.all([
          apartmentsApi.list(filter ? { buildingId: filter } : {}),
          buildingsApi.list(),
          usersApi.list('OWNER'),
          usersApi.list('RESIDENT'),
        ]);
        setItems(a.data);
        setBuildings(b.data);
        setOwners(o.data);
        setResidents(r.data);
      } else {
        const { data } = await apartmentsApi.mine();
        setItems(data);
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { load(); /* eslint-disable-next-line */ }, [filter]);

  const onSave = async (e) => {
    e.preventDefault();
    try {
      const payload = {
        ...form,
        floor: Number(form.floor),
        surface: Number(form.surface),
        buildingId: Number(form.buildingId),
        ownerId: form.ownerId ? Number(form.ownerId) : null,
        residentId: form.residentId ? Number(form.residentId) : null,
      };
      if (editingId) {
        await apartmentsApi.update(editingId, payload);
        toast.success('Appartement mis à jour');
      } else {
        await apartmentsApi.create(payload);
        toast.success('Appartement créé');
      }
      setOpen(false);
      setForm(empty);
      setEditingId(null);
      load();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Erreur');
    }
  };

  const onEdit = (a) => {
    setForm({
      number: a.number,
      floor: a.floor,
      surface: a.surface,
      buildingId: a.buildingId || '',
      ownerId: a.ownerId || '',
      residentId: a.residentId || '',
    });
    setEditingId(a.id);
    setOpen(true);
  };

  const onDelete = async (id) => {
    if (!confirm('Supprimer cet appartement ?')) return;
    try {
      await apartmentsApi.delete(id);
      toast.success('Appartement supprimé');
      load();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Erreur');
    }
  };

  return (
    <div>
      <PageHeader
        eyebrow="Patrimoine"
        title={isAdmin ? 'Appartements' : 'Mes appartements'}
        description={isAdmin
          ? 'Liste hiérarchique des lots, leurs propriétaires et leurs occupants.'
          : 'Vue détaillée des lots qui vous concernent.'}
        actions={
          isAdmin && (
            <button className="btn-primary" onClick={() => { setForm(empty); setEditingId(null); setOpen(true); }}>
              <Plus className="w-4 h-4" /> Nouvel appartement
            </button>
          )
        }
      />

      {isAdmin && (
        <div className="mb-4 flex gap-2 items-center">
          <select className="input max-w-xs" value={filter} onChange={(e) => setFilter(e.target.value)}>
            <option value="">Tous les immeubles</option>
            {buildings.map((b) => <option key={b.id} value={b.id}>{b.name}</option>)}
          </select>
        </div>
      )}

      {loading ? (
        <Skeleton className="h-64" />
      ) : items.length === 0 ? (
        <Empty
          icon={HomeIcon}
          title="Aucun appartement"
          description={isAdmin ? "Créez votre premier appartement." : "Aucun lot n'est rattaché à votre profil."}
        />
      ) : (
        <div className="card overflow-hidden">
          <div className="overflow-x-auto">
            <table className="table-clean">
              <thead>
                <tr>
                  <th>N°</th>
                  <th>Étage</th>
                  <th>Surface</th>
                  <th>Immeuble</th>
                  <th>Propriétaire</th>
                  <th>Résident</th>
                  {isAdmin && <th className="w-24"></th>}
                </tr>
              </thead>
              <tbody>
                {items.map((a) => (
                  <tr key={a.id}>
                    <td className="font-medium">{a.number}</td>
                    <td>{a.floor}</td>
                    <td className="font-mono text-xs">{a.surface} m²</td>
                    <td>{a.buildingName || '—'}</td>
                    <td>{a.ownerName || <span className="text-ink/30">Non assigné</span>}</td>
                    <td>{a.residentName || <span className="text-ink/30">—</span>}</td>
                    {isAdmin && (
                      <td>
                        <div className="flex gap-1 justify-end">
                          <button onClick={() => onEdit(a)} className="p-1.5 text-ink/50 hover:text-ink hover:bg-ink/5 rounded">
                            <Pencil className="w-4 h-4" />
                          </button>
                          <button onClick={() => onDelete(a.id)} className="p-1.5 text-ink/50 hover:text-red-600 hover:bg-red-50 rounded">
                            <Trash2 className="w-4 h-4" />
                          </button>
                        </div>
                      </td>
                    )}
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      <Modal open={open} onClose={() => setOpen(false)} title={editingId ? 'Modifier l\'appartement' : 'Nouvel appartement'}>
        <form onSubmit={onSave} className="space-y-4">
          <div className="grid grid-cols-3 gap-4">
            <div>
              <label className="label">Numéro</label>
              <input className="input" required value={form.number} onChange={(e) => setForm({ ...form, number: e.target.value })} />
            </div>
            <div>
              <label className="label">Étage</label>
              <input type="number" className="input" required value={form.floor} onChange={(e) => setForm({ ...form, floor: e.target.value })} />
            </div>
            <div>
              <label className="label">Surface (m²)</label>
              <input type="number" step="0.01" min="0" className="input" required value={form.surface} onChange={(e) => setForm({ ...form, surface: e.target.value })} />
            </div>
          </div>
          <div>
            <label className="label">Immeuble</label>
            <select className="input" required value={form.buildingId} onChange={(e) => setForm({ ...form, buildingId: e.target.value })}>
              <option value="">— Sélectionner —</option>
              {buildings.map((b) => <option key={b.id} value={b.id}>{b.name}</option>)}
            </select>
          </div>
          <div>
            <label className="label">Propriétaire</label>
            <select className="input" value={form.ownerId} onChange={(e) => setForm({ ...form, ownerId: e.target.value })}>
              <option value="">— Aucun —</option>
              {owners.map((u) => <option key={u.id} value={u.id}>{u.firstName} {u.lastName} ({u.email})</option>)}
            </select>
          </div>
          <div>
            <label className="label">Résident</label>
            <select className="input" value={form.residentId} onChange={(e) => setForm({ ...form, residentId: e.target.value })}>
              <option value="">— Aucun —</option>
              {residents.map((u) => <option key={u.id} value={u.id}>{u.firstName} {u.lastName} ({u.email})</option>)}
            </select>
          </div>
          <div className="flex gap-2 pt-2">
            <button type="button" className="btn-secondary flex-1" onClick={() => setOpen(false)}>Annuler</button>
            <button type="submit" className="btn-primary flex-1">{editingId ? 'Mettre à jour' : 'Créer'}</button>
          </div>
        </form>
      </Modal>
    </div>
  );
}
