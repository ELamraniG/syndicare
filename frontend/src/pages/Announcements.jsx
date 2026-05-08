import { useEffect, useState } from 'react';
import { announcementsApi, buildingsApi } from '../lib/api';
import { useAuth } from '../context/AuthContext';
import { PageHeader, Modal, Empty, Skeleton, Badge, fmtDateTime } from '../components/UI';
import { Megaphone, Plus, Pencil, Trash2, Pin, AlertTriangle, Info, AlertCircle } from 'lucide-react';
import toast from 'react-hot-toast';

const SEVERITY_LABELS = { INFO: 'Info', WARNING: 'Avertissement', URGENT: 'Urgent' };
const severityColor = (s) => ({ INFO: 'blue', WARNING: 'yellow', URGENT: 'red' }[s] || 'gray');
const severityIcon = (s) => ({
  INFO: Info,
  WARNING: AlertTriangle,
  URGENT: AlertCircle,
}[s] || Info);

const empty = { title: '', content: '', severity: 'INFO', buildingId: '', pinned: false };

export default function Announcements() {
  const { user } = useAuth();
  const isAdmin = user.role === 'ADMIN';

  const [items, setItems] = useState([]);
  const [buildings, setBuildings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [open, setOpen] = useState(false);
  const [form, setForm] = useState(empty);
  const [editingId, setEditingId] = useState(null);

  const load = async () => {
    setLoading(true);
    try {
      const calls = [announcementsApi.list()];
      if (isAdmin) calls.push(buildingsApi.list());
      const [a, b] = await Promise.all(calls);
      setItems(a.data);
      if (isAdmin) setBuildings(b.data);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { load(); }, []);

  const submit = async (e) => {
    e.preventDefault();
    try {
      const payload = {
        title: form.title,
        content: form.content,
        severity: form.severity,
        buildingId: form.buildingId ? Number(form.buildingId) : null,
        pinned: !!form.pinned,
      };
      if (editingId) {
        await announcementsApi.update(editingId, payload);
        toast.success('Annonce mise à jour');
      } else {
        await announcementsApi.create(payload);
        toast.success('Annonce publiée');
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
      title: a.title,
      content: a.content,
      severity: a.severity || 'INFO',
      buildingId: a.buildingId || '',
      pinned: !!a.pinned,
    });
    setEditingId(a.id);
    setOpen(true);
  };

  const remove = async (id) => {
    if (!confirm('Supprimer cette annonce ?')) return;
    await announcementsApi.delete(id);
    toast.success('Supprimé');
    load();
  };

  return (
    <div>
      <PageHeader
        eyebrow="Communication"
        title="Annonces"
        description="Mur d'annonces de la copropriété : informations, alertes et avis officiels."
        actions={
          isAdmin && (
            <button className="btn-primary" onClick={() => { setForm(empty); setEditingId(null); setOpen(true); }}>
              <Plus className="w-4 h-4" /> Nouvelle annonce
            </button>
          )
        }
      />

      {loading ? (
        <div className="space-y-4">
          {[...Array(3)].map((_, i) => <Skeleton key={i} className="h-32" />)}
        </div>
      ) : items.length === 0 ? (
        <Empty icon={Megaphone} title="Aucune annonce" description="Aucune communication n'a encore été publiée." />
      ) : (
        <div className="space-y-4">
          {items.map((a) => {
            const Icon = severityIcon(a.severity);
            return (
              <div
                key={a.id}
                className={`card p-5 ${a.pinned ? 'border-ochre-500/40 ring-1 ring-ochre-500/20' : ''} ${
                  a.severity === 'URGENT' ? 'bg-red-50/50' : ''
                }`}
              >
                <div className="flex items-start justify-between gap-4 mb-3">
                  <div className="flex items-start gap-3 min-w-0">
                    <div className={`w-10 h-10 rounded-lg grid place-items-center flex-shrink-0 ${
                      a.severity === 'URGENT' ? 'bg-red-100' :
                      a.severity === 'WARNING' ? 'bg-amber-100' : 'bg-blue-100'
                    }`}>
                      <Icon className={`w-5 h-5 ${
                        a.severity === 'URGENT' ? 'text-red-600' :
                        a.severity === 'WARNING' ? 'text-amber-600' : 'text-blue-600'
                      }`} />
                    </div>
                    <div className="min-w-0 flex-1">
                      <div className="flex items-center gap-2 flex-wrap mb-1">
                        <Badge color={severityColor(a.severity)}>{SEVERITY_LABELS[a.severity]}</Badge>
                        {a.pinned && (
                          <Badge color="ochre">
                            <Pin className="w-3 h-3 inline mr-0.5" /> Épinglée
                          </Badge>
                        )}
                        {a.buildingName && <Badge color="gray">{a.buildingName}</Badge>}
                      </div>
                      <h3 className="font-display font-bold text-lg leading-tight">{a.title}</h3>
                    </div>
                  </div>

                  {isAdmin && (
                    <div className="flex gap-1 flex-shrink-0">
                      <button onClick={() => onEdit(a)} className="p-1.5 text-ink/50 hover:text-ink hover:bg-ink/5 rounded">
                        <Pencil className="w-4 h-4" />
                      </button>
                      <button onClick={() => remove(a.id)} className="p-1.5 text-ink/50 hover:text-red-600 hover:bg-red-50 rounded">
                        <Trash2 className="w-4 h-4" />
                      </button>
                    </div>
                  )}
                </div>

                <p className="text-sm text-ink/80 whitespace-pre-line leading-relaxed">{a.content}</p>

                <div className="mt-4 pt-3 border-t border-ink/5 flex items-center gap-2 text-xs text-ink/50">
                  <span>{a.authorName}</span>
                  <span>·</span>
                  <span>{fmtDateTime(a.createdAt)}</span>
                </div>
              </div>
            );
          })}
        </div>
      )}

      <Modal open={open} onClose={() => setOpen(false)} title={editingId ? 'Modifier l\'annonce' : 'Nouvelle annonce'}>
        <form onSubmit={submit} className="space-y-4">
          <div>
            <label className="label">Titre</label>
            <input
              className="input"
              required
              value={form.title}
              onChange={(e) => setForm({ ...form, title: e.target.value })}
              placeholder="ex. Coupure d'eau prévue"
            />
          </div>
          <div>
            <label className="label">Contenu</label>
            <textarea
              className="input min-h-[160px] resize-none"
              required
              value={form.content}
              onChange={(e) => setForm({ ...form, content: e.target.value })}
              placeholder="Détails de l'annonce..."
            />
          </div>
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="label">Niveau</label>
              <select
                className="input"
                value={form.severity}
                onChange={(e) => setForm({ ...form, severity: e.target.value })}
              >
                <option value="INFO">Information</option>
                <option value="WARNING">Avertissement</option>
                <option value="URGENT">Urgent</option>
              </select>
            </div>
            <div>
              <label className="label">Immeuble (optionnel)</label>
              <select
                className="input"
                value={form.buildingId}
                onChange={(e) => setForm({ ...form, buildingId: e.target.value })}
              >
                <option value="">Tous les immeubles</option>
                {buildings.map((b) => <option key={b.id} value={b.id}>{b.name}</option>)}
              </select>
            </div>
          </div>

          <label className="flex items-center gap-2 text-sm cursor-pointer select-none">
            <input
              type="checkbox"
              checked={form.pinned}
              onChange={(e) => setForm({ ...form, pinned: e.target.checked })}
              className="rounded border-ink/20 text-ochre-500 focus:ring-ochre-400"
            />
            <span className="flex items-center gap-1.5">
              <Pin className="w-3.5 h-3.5" />
              Épingler en haut de la liste
            </span>
          </label>

          <div className="flex gap-2 pt-2">
            <button type="button" className="btn-secondary flex-1" onClick={() => setOpen(false)}>Annuler</button>
            <button type="submit" className="btn-primary flex-1">
              {editingId ? 'Mettre à jour' : 'Publier'}
            </button>
          </div>
        </form>
      </Modal>
    </div>
  );
}
