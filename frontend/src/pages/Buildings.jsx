import { useEffect, useState } from 'react';
import { buildingsApi } from '../lib/api';
import { PageHeader, Modal, Empty, Skeleton } from '../components/UI';
import { Building2, Plus, Pencil, Trash2, MapPin } from 'lucide-react';
import toast from 'react-hot-toast';

const empty = { name: '', address: '', city: '', postalCode: '', floors: 1, baseRatePerSqm: '' };

export default function Buildings() {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [open, setOpen] = useState(false);
  const [form, setForm] = useState(empty);
  const [editingId, setEditingId] = useState(null);

  const load = async () => {
    setLoading(true);
    try {
      const { data } = await buildingsApi.list();
      setItems(data);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { load(); }, []);

  const onSave = async (e) => {
    e.preventDefault();
    try {
      const payload = {
        ...form,
        floors: Number(form.floors),
        baseRatePerSqm: form.baseRatePerSqm === '' ? null : Number(form.baseRatePerSqm),
      };
      if (editingId) {
        await buildingsApi.update(editingId, payload);
        toast.success('Immeuble mis à jour');
      } else {
        await buildingsApi.create(payload);
        toast.success('Immeuble créé');
      }
      setOpen(false);
      setForm(empty);
      setEditingId(null);
      load();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Erreur');
    }
  };

  const onEdit = (b) => {
    setForm({
      name: b.name || '',
      address: b.address || '',
      city: b.city || '',
      postalCode: b.postalCode || '',
      floors: b.floors || 1,
      baseRatePerSqm: b.baseRatePerSqm ?? '',
    });
    setEditingId(b.id);
    setOpen(true);
  };

  const onDelete = async (id) => {
    if (!confirm('Supprimer cet immeuble ? Les appartements liés seront aussi supprimés.')) return;
    try {
      await buildingsApi.delete(id);
      toast.success('Immeuble supprimé');
      load();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Erreur');
    }
  };

  return (
    <div>
      <PageHeader
        eyebrow="Patrimoine"
        title="Immeubles"
        description="Gérez les immeubles, leurs adresses et le tarif au m² servant au calcul des charges."
        actions={
          <button className="btn-primary" onClick={() => { setForm(empty); setEditingId(null); setOpen(true); }}>
            <Plus className="w-4 h-4" /> Nouvel immeuble
          </button>
        }
      />

      {loading ? (
        <div className="grid sm:grid-cols-2 lg:grid-cols-3 gap-4">
          {[...Array(3)].map((_, i) => <Skeleton key={i} className="h-44" />)}
        </div>
      ) : items.length === 0 ? (
        <Empty
          icon={Building2}
          title="Aucun immeuble"
          description="Commencez par créer un immeuble pour pouvoir y rattacher des appartements."
          action={<button className="btn-primary" onClick={() => setOpen(true)}><Plus className="w-4 h-4" /> Ajouter</button>}
        />
      ) : (
        <div className="grid sm:grid-cols-2 lg:grid-cols-3 gap-4">
          {items.map((b) => (
            <div key={b.id} className="card p-5 group hover:border-ink/30 transition-colors">
              <div className="flex items-start justify-between mb-3">
                <div className="w-10 h-10 rounded-lg bg-sage-100 grid place-items-center">
                  <Building2 className="w-5 h-5 text-sage-700" />
                </div>
                <div className="flex gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
                  <button onClick={() => onEdit(b)} className="p-1.5 text-ink/50 hover:text-ink hover:bg-ink/5 rounded">
                    <Pencil className="w-4 h-4" />
                  </button>
                  <button onClick={() => onDelete(b.id)} className="p-1.5 text-ink/50 hover:text-red-600 hover:bg-red-50 rounded">
                    <Trash2 className="w-4 h-4" />
                  </button>
                </div>
              </div>
              <h3 className="font-display text-lg font-bold">{b.name}</h3>
              <div className="text-sm text-ink/60 mt-1 flex items-start gap-1.5">
                <MapPin className="w-3.5 h-3.5 mt-0.5 flex-shrink-0" />
                <span>{b.address}{b.city ? `, ${b.city}` : ''}</span>
              </div>
              <div className="mt-4 pt-4 border-t border-ink/5 grid grid-cols-3 gap-2 text-center">
                <div>
                  <div className="text-xs uppercase text-ink/40 tracking-wide">Étages</div>
                  <div className="font-display text-lg font-bold mt-0.5">{b.floors}</div>
                </div>
                <div>
                  <div className="text-xs uppercase text-ink/40 tracking-wide">Lots</div>
                  <div className="font-display text-lg font-bold mt-0.5">{b.apartmentCount}</div>
                </div>
                <div>
                  <div className="text-xs uppercase text-ink/40 tracking-wide">MAD/m²</div>
                  <div className="font-display text-lg font-bold mt-0.5 text-ochre-600">{b.baseRatePerSqm ?? '—'}</div>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      <Modal open={open} onClose={() => setOpen(false)} title={editingId ? 'Modifier l\'immeuble' : 'Nouvel immeuble'}>
        <form onSubmit={onSave} className="space-y-4">
          <div>
            <label className="label">Nom</label>
            <input className="input" required value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} />
          </div>
          <div>
            <label className="label">Adresse</label>
            <input className="input" required value={form.address} onChange={(e) => setForm({ ...form, address: e.target.value })} />
          </div>
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="label">Ville</label>
              <input className="input" value={form.city} onChange={(e) => setForm({ ...form, city: e.target.value })} />
            </div>
            <div>
              <label className="label">Code postal</label>
              <input className="input" value={form.postalCode} onChange={(e) => setForm({ ...form, postalCode: e.target.value })} />
            </div>
          </div>
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="label">Nombre d'étages</label>
              <input type="number" min="1" className="input" required value={form.floors} onChange={(e) => setForm({ ...form, floors: e.target.value })} />
            </div>
            <div>
              <label className="label">Tarif (MAD / m²)</label>
              <input type="number" step="0.01" min="0" className="input" placeholder="ex. 8.50" value={form.baseRatePerSqm} onChange={(e) => setForm({ ...form, baseRatePerSqm: e.target.value })} />
            </div>
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
