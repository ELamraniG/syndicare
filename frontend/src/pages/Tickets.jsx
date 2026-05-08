import { useEffect, useState } from 'react';
import { ticketsApi, apartmentsApi, buildingsApi } from '../lib/api';
import { useAuth } from '../context/AuthContext';
import { PageHeader, Modal, Empty, Skeleton, Badge, fmtDateTime } from '../components/UI';
import { Wrench, Plus, ImageIcon, Trash2 } from 'lucide-react';
import toast from 'react-hot-toast';

const statusColor = (s) => ({ OPEN: 'red', IN_PROGRESS: 'yellow', RESOLVED: 'green', CLOSED: 'gray' }[s] || 'gray');
const priorityColor = (p) => ({ LOW: 'gray', NORMAL: 'blue', HIGH: 'yellow', URGENT: 'red' }[p] || 'gray');

const CATEGORY_LABELS = {
  PLUMBING: 'Plomberie',
  ELECTRICITY: 'Électricité',
  ELEVATOR: 'Ascenseur',
  CLEANING: 'Propreté',
  SECURITY: 'Sécurité',
  NOISE: 'Nuisance sonore',
  OTHER: 'Autre',
};

const STATUS_LABELS = {
  OPEN: 'Ouvert',
  IN_PROGRESS: 'En traitement',
  RESOLVED: 'Résolu',
  CLOSED: 'Clôturé',
};

const empty = { title: '', description: '', category: 'PLUMBING', priority: 'NORMAL', apartmentId: '', buildingId: '' };

export default function Tickets() {
  const { user } = useAuth();
  const isAdmin = user.role === 'ADMIN';

  const [items, setItems] = useState([]);
  const [apartments, setApartments] = useState([]);
  const [buildings, setBuildings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState('');

  const [open, setOpen] = useState(false);
  const [form, setForm] = useState(empty);
  const [photo, setPhoto] = useState(null);

  const [editOpen, setEditOpen] = useState(false);
  const [editing, setEditing] = useState(null);

  const load = async () => {
    setLoading(true);
    try {
      const calls = [ticketsApi.list()];
      if (isAdmin) calls.push(buildingsApi.list());
      else calls.push(apartmentsApi.mine());

      const [t, second] = await Promise.all(calls);
      setItems(t.data);
      if (isAdmin) setBuildings(second.data);
      else setApartments(second.data);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { load(); /* eslint-disable-next-line */ }, []);

  const filtered = items.filter((t) => !filter || t.status === filter);

  const submit = async (e) => {
    e.preventDefault();
    try {
      const payload = {
        title: form.title,
        description: form.description,
        category: form.category,
        priority: form.priority,
        apartmentId: form.apartmentId ? Number(form.apartmentId) : null,
        buildingId: form.buildingId ? Number(form.buildingId) : null,
      };
      if (photo) {
        await ticketsApi.createWithPhoto(payload, photo);
      } else {
        await ticketsApi.createJson(payload);
      }
      toast.success('Réclamation envoyée');
      setOpen(false);
      setForm(empty);
      setPhoto(null);
      load();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Erreur');
    }
  };

  const updateStatus = async (e) => {
    e.preventDefault();
    try {
      await ticketsApi.update(editing.id, {
        status: editing.status,
        priority: editing.priority,
        adminNotes: editing.adminNotes,
      });
      toast.success('Mis à jour');
      setEditOpen(false);
      load();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Erreur');
    }
  };

  const remove = async (id) => {
    if (!confirm('Supprimer cette réclamation ?')) return;
    await ticketsApi.delete(id);
    toast.success('Supprimé');
    load();
  };

  return (
    <div>
      <PageHeader
        eyebrow="Helpdesk"
        title={isAdmin ? 'Réclamations' : 'Mes réclamations'}
        description={isAdmin
          ? "Suivez et traitez les incidents signalés par les copropriétaires et résidents."
          : "Signalez un incident technique en quelques clics, avec photo si nécessaire."}
        actions={
          <button className="btn-primary" onClick={() => { setForm(empty); setPhoto(null); setOpen(true); }}>
            <Plus className="w-4 h-4" /> Déclarer un incident
          </button>
        }
      />

      <div className="mb-4 flex gap-2 flex-wrap">
        {['', 'OPEN', 'IN_PROGRESS', 'RESOLVED'].map((s) => (
          <button
            key={s || 'all'}
            onClick={() => setFilter(s)}
            className={`px-3 py-1.5 rounded-lg text-xs font-medium uppercase tracking-wide transition-colors ${
              filter === s ? 'bg-ink text-cream' : 'bg-cream border border-ink/10 hover:bg-sage-50'
            }`}
          >
            {s ? STATUS_LABELS[s] : 'Tous'}
          </button>
        ))}
      </div>

      {loading ? (
        <Skeleton className="h-64" />
      ) : filtered.length === 0 ? (
        <Empty icon={Wrench} title="Aucune réclamation" />
      ) : (
        <div className="grid md:grid-cols-2 gap-4">
          {filtered.map((t) => (
            <div key={t.id} className="card p-5">
              <div className="flex items-start justify-between gap-3 mb-3">
                <div className="min-w-0">
                  <div className="flex items-center gap-2 flex-wrap mb-1">
                    <Badge color={statusColor(t.status)}>{STATUS_LABELS[t.status]}</Badge>
                    <Badge color={priorityColor(t.priority)}>{t.priority}</Badge>
                    <Badge color="gray">{CATEGORY_LABELS[t.category]}</Badge>
                  </div>
                  <h3 className="font-display font-bold text-lg">{t.title}</h3>
                </div>
                {isAdmin && (
                  <button onClick={() => remove(t.id)} className="p-1.5 text-ink/50 hover:text-red-600 hover:bg-red-50 rounded">
                    <Trash2 className="w-4 h-4" />
                  </button>
                )}
              </div>
              <p className="text-sm text-ink/70 mb-3 line-clamp-3">{t.description}</p>

              {t.photoPath && (
                <a
                  href={`/api/files/${t.photoPath}`}
                  target="_blank"
                  rel="noreferrer"
                  className="inline-flex items-center gap-2 text-xs text-ochre-600 hover:underline mb-3"
                >
                  <ImageIcon className="w-3.5 h-3.5" />
                  Voir la photo jointe
                </a>
              )}

              {t.adminNotes && (
                <div className="bg-sage-50 border border-sage-200 rounded-lg p-3 mb-3">
                  <div className="text-xs uppercase tracking-wide text-sage-700 font-medium mb-1">Note du syndic</div>
                  <p className="text-sm text-sage-900">{t.adminNotes}</p>
                </div>
              )}

              <div className="flex items-center justify-between text-xs text-ink/50">
                <span>{t.submitterName} · {fmtDateTime(t.createdAt)}</span>
                {isAdmin && (
                  <button
                    onClick={() => { setEditing({ ...t }); setEditOpen(true); }}
                    className="btn-ghost text-xs px-2 py-1"
                  >
                    Mettre à jour
                  </button>
                )}
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Create modal */}
      <Modal open={open} onClose={() => setOpen(false)} title="Déclarer un incident">
        <form onSubmit={submit} className="space-y-4">
          <div>
            <label className="label">Titre</label>
            <input className="input" required value={form.title} onChange={(e) => setForm({ ...form, title: e.target.value })} placeholder="ex. Fuite dans la salle de bain" />
          </div>
          <div>
            <label className="label">Description</label>
            <textarea
              className="input min-h-[120px] resize-none"
              required
              value={form.description}
              onChange={(e) => setForm({ ...form, description: e.target.value })}
              placeholder="Décrivez le problème en détail (date, lieu précis, fréquence...)"
            />
          </div>
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="label">Catégorie</label>
              <select className="input" value={form.category} onChange={(e) => setForm({ ...form, category: e.target.value })}>
                {Object.entries(CATEGORY_LABELS).map(([k, v]) => <option key={k} value={k}>{v}</option>)}
              </select>
            </div>
            <div>
              <label className="label">Priorité</label>
              <select className="input" value={form.priority} onChange={(e) => setForm({ ...form, priority: e.target.value })}>
                <option value="LOW">Faible</option>
                <option value="NORMAL">Normale</option>
                <option value="HIGH">Élevée</option>
                <option value="URGENT">Urgente</option>
              </select>
            </div>
          </div>

          {!isAdmin && apartments.length > 0 && (
            <div>
              <label className="label">Appartement concerné</label>
              <select className="input" value={form.apartmentId} onChange={(e) => setForm({ ...form, apartmentId: e.target.value })}>
                <option value="">— Aucun spécifique —</option>
                {apartments.map((a) => <option key={a.id} value={a.id}>{a.buildingName} · Apt {a.number}</option>)}
              </select>
            </div>
          )}

          {isAdmin && (
            <div>
              <label className="label">Immeuble concerné</label>
              <select className="input" value={form.buildingId} onChange={(e) => setForm({ ...form, buildingId: e.target.value })}>
                <option value="">— Aucun —</option>
                {buildings.map((b) => <option key={b.id} value={b.id}>{b.name}</option>)}
              </select>
            </div>
          )}

          <div>
            <label className="label">Photo (optionnel)</label>
            <input
              type="file"
              accept="image/*"
              onChange={(e) => setPhoto(e.target.files?.[0] || null)}
              className="block w-full text-sm text-ink/60 file:mr-4 file:py-2 file:px-4 file:rounded-lg file:border-0 file:text-xs file:font-medium file:bg-sage-100 file:text-sage-700 hover:file:bg-sage-200"
            />
          </div>

          <div className="flex gap-2 pt-2">
            <button type="button" className="btn-secondary flex-1" onClick={() => setOpen(false)}>Annuler</button>
            <button type="submit" className="btn-primary flex-1">Envoyer</button>
          </div>
        </form>
      </Modal>

      {/* Update (admin) modal */}
      <Modal open={editOpen} onClose={() => setEditOpen(false)} title="Traiter la réclamation">
        {editing && (
          <form onSubmit={updateStatus} className="space-y-4">
            <div className="bg-sage-50 rounded-lg p-3">
              <div className="font-medium">{editing.title}</div>
              <div className="text-xs text-ink/60 mt-0.5">{editing.submitterName}</div>
            </div>
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="label">Statut</label>
                <select className="input" value={editing.status} onChange={(e) => setEditing({ ...editing, status: e.target.value })}>
                  {Object.entries(STATUS_LABELS).map(([k, v]) => <option key={k} value={k}>{v}</option>)}
                </select>
              </div>
              <div>
                <label className="label">Priorité</label>
                <select className="input" value={editing.priority} onChange={(e) => setEditing({ ...editing, priority: e.target.value })}>
                  <option value="LOW">Faible</option>
                  <option value="NORMAL">Normale</option>
                  <option value="HIGH">Élevée</option>
                  <option value="URGENT">Urgente</option>
                </select>
              </div>
            </div>
            <div>
              <label className="label">Note du syndic</label>
              <textarea
                className="input min-h-[100px] resize-none"
                value={editing.adminNotes || ''}
                onChange={(e) => setEditing({ ...editing, adminNotes: e.target.value })}
                placeholder="ex. Plombier contacté, intervention prévue le..."
              />
            </div>
            <div className="flex gap-2 pt-2">
              <button type="button" className="btn-secondary flex-1" onClick={() => setEditOpen(false)}>Annuler</button>
              <button type="submit" className="btn-primary flex-1">Enregistrer</button>
            </div>
          </form>
        )}
      </Modal>
    </div>
  );
}
