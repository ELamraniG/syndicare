import { useEffect, useState } from 'react';
import { documentsApi, buildingsApi } from '../lib/api';
import { useAuth } from '../context/AuthContext';
import { PageHeader, Modal, Empty, Skeleton, Badge, fmtDateTime } from '../components/UI';
import { FileText, Upload, Download, Trash2 } from 'lucide-react';
import toast from 'react-hot-toast';

const CATEGORY_LABELS = {
  AG_MINUTES: 'PV d\'AG',
  REGULATIONS: 'Règlement',
  CONTRACTS: 'Contrats',
  INVOICES: 'Factures',
  BUDGETS: 'Budgets',
  OTHER: 'Autre',
};

const categoryColor = (c) => ({
  AG_MINUTES: 'blue',
  REGULATIONS: 'ochre',
  CONTRACTS: 'green',
  INVOICES: 'yellow',
  BUDGETS: 'gray',
  OTHER: 'gray',
}[c] || 'gray');

const fmtSize = (n) => {
  if (!n) return '—';
  if (n < 1024) return `${n} B`;
  if (n < 1024 * 1024) return `${(n / 1024).toFixed(1)} KB`;
  return `${(n / 1024 / 1024).toFixed(1)} MB`;
};

const empty = { title: '', description: '', category: 'OTHER', buildingId: '', file: null };

export default function Documents() {
  const { user } = useAuth();
  const isAdmin = user.role === 'ADMIN';

  const [items, setItems] = useState([]);
  const [buildings, setBuildings] = useState([]);
  const [filter, setFilter] = useState('');
  const [loading, setLoading] = useState(true);
  const [open, setOpen] = useState(false);
  const [form, setForm] = useState(empty);
  const [submitting, setSubmitting] = useState(false);

  const load = async () => {
    setLoading(true);
    try {
      const calls = [documentsApi.list(filter || undefined)];
      if (isAdmin) calls.push(buildingsApi.list());
      const [d, b] = await Promise.all(calls);
      setItems(d.data);
      if (isAdmin) setBuildings(b.data);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { load(); /* eslint-disable-next-line */ }, [filter]);

  const submit = async (e) => {
    e.preventDefault();
    if (!form.file) {
      toast.error('Veuillez choisir un fichier');
      return;
    }
    setSubmitting(true);
    try {
      await documentsApi.upload(
        form.file,
        form.title,
        form.description || null,
        form.category,
        form.buildingId ? Number(form.buildingId) : null
      );
      toast.success('Document publié');
      setOpen(false);
      setForm(empty);
      load();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Erreur lors de l\'envoi');
    } finally {
      setSubmitting(false);
    }
  };

  const download = (d) => {
    const url = documentsApi.downloadUrl(d.id);
    const token = localStorage.getItem('syndicare_token');
    fetch(url, { headers: { Authorization: `Bearer ${token}` } })
      .then((r) => r.blob())
      .then((blob) => {
        const a = document.createElement('a');
        a.href = URL.createObjectURL(blob);
        a.download = d.originalFilename || d.title;
        a.click();
        URL.revokeObjectURL(a.href);
      })
      .catch(() => toast.error('Téléchargement impossible'));
  };

  const remove = async (id) => {
    if (!confirm('Supprimer ce document ?')) return;
    await documentsApi.delete(id);
    toast.success('Supprimé');
    load();
  };

  return (
    <div>
      <PageHeader
        eyebrow="Bibliothèque"
        title="Documents"
        description="Procès-verbaux d'AG, règlements, contrats, factures — tout l'historique de la copropriété centralisé."
        actions={
          isAdmin && (
            <button className="btn-primary" onClick={() => { setForm(empty); setOpen(true); }}>
              <Upload className="w-4 h-4" /> Publier un document
            </button>
          )
        }
      />

      <div className="mb-4 flex gap-2 flex-wrap">
        <button
          onClick={() => setFilter('')}
          className={`px-3 py-1.5 rounded-lg text-xs font-medium uppercase tracking-wide transition-colors ${
            !filter ? 'bg-ink text-cream' : 'bg-cream border border-ink/10 hover:bg-sage-50'
          }`}
        >
          Tous
        </button>
        {Object.entries(CATEGORY_LABELS).map(([k, v]) => (
          <button
            key={k}
            onClick={() => setFilter(k)}
            className={`px-3 py-1.5 rounded-lg text-xs font-medium uppercase tracking-wide transition-colors ${
              filter === k ? 'bg-ink text-cream' : 'bg-cream border border-ink/10 hover:bg-sage-50'
            }`}
          >
            {v}
          </button>
        ))}
      </div>

      {loading ? (
        <div className="grid sm:grid-cols-2 lg:grid-cols-3 gap-4">
          {[...Array(3)].map((_, i) => <Skeleton key={i} className="h-36" />)}
        </div>
      ) : items.length === 0 ? (
        <Empty
          icon={FileText}
          title="Aucun document"
          description={isAdmin
            ? "Publiez le premier document partagé avec les copropriétaires."
            : "Aucun document n'a encore été partagé."}
        />
      ) : (
        <div className="grid sm:grid-cols-2 lg:grid-cols-3 gap-4">
          {items.map((d) => (
            <div key={d.id} className="card p-5 group hover:border-ink/30 transition-colors">
              <div className="flex items-start justify-between mb-3">
                <div className="w-10 h-10 rounded-lg bg-ochre-500/10 grid place-items-center">
                  <FileText className="w-5 h-5 text-ochre-600" />
                </div>
                {isAdmin && (
                  <button
                    onClick={() => remove(d.id)}
                    className="opacity-0 group-hover:opacity-100 p-1.5 text-ink/50 hover:text-red-600 hover:bg-red-50 rounded transition-opacity"
                  >
                    <Trash2 className="w-4 h-4" />
                  </button>
                )}
              </div>

              <div className="mb-2">
                <Badge color={categoryColor(d.category)}>{CATEGORY_LABELS[d.category]}</Badge>
              </div>

              <h3 className="font-display font-bold text-base leading-snug mb-1">{d.title}</h3>
              {d.description && (
                <p className="text-xs text-ink/60 line-clamp-2 mb-3">{d.description}</p>
              )}

              <div className="text-xs text-ink/50 space-y-0.5 mb-4">
                {d.buildingName && <div>📍 {d.buildingName}</div>}
                <div>{fmtDateTime(d.uploadedAt)} · {fmtSize(d.fileSize)}</div>
                {d.uploadedBy && <div className="text-ink/40">par {d.uploadedBy}</div>}
              </div>

              <button onClick={() => download(d)} className="btn-secondary w-full text-xs py-2">
                <Download className="w-3.5 h-3.5" /> Télécharger
              </button>
            </div>
          ))}
        </div>
      )}

      {/* Upload modal */}
      <Modal open={open} onClose={() => setOpen(false)} title="Publier un document">
        <form onSubmit={submit} className="space-y-4">
          <div>
            <label className="label">Fichier</label>
            <input
              type="file"
              required
              onChange={(e) => setForm({ ...form, file: e.target.files?.[0] || null })}
              className="block w-full text-sm text-ink/60 file:mr-4 file:py-2 file:px-4 file:rounded-lg file:border-0 file:text-xs file:font-medium file:bg-sage-100 file:text-sage-700 hover:file:bg-sage-200"
            />
            <p className="text-xs text-ink/40 mt-1">PDF, Word, Excel, images... (max 10 Mo)</p>
          </div>
          <div>
            <label className="label">Titre</label>
            <input
              className="input"
              required
              value={form.title}
              onChange={(e) => setForm({ ...form, title: e.target.value })}
              placeholder="ex. PV de l'AG du 15 mars 2026"
            />
          </div>
          <div>
            <label className="label">Description (optionnelle)</label>
            <textarea
              className="input min-h-[80px] resize-none"
              value={form.description}
              onChange={(e) => setForm({ ...form, description: e.target.value })}
            />
          </div>
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="label">Catégorie</label>
              <select
                className="input"
                value={form.category}
                onChange={(e) => setForm({ ...form, category: e.target.value })}
              >
                {Object.entries(CATEGORY_LABELS).map(([k, v]) => (
                  <option key={k} value={k}>{v}</option>
                ))}
              </select>
            </div>
            <div>
              <label className="label">Immeuble (optionnel)</label>
              <select
                className="input"
                value={form.buildingId}
                onChange={(e) => setForm({ ...form, buildingId: e.target.value })}
              >
                <option value="">Tous</option>
                {buildings.map((b) => <option key={b.id} value={b.id}>{b.name}</option>)}
              </select>
            </div>
          </div>
          <div className="flex gap-2 pt-2">
            <button type="button" className="btn-secondary flex-1" onClick={() => setOpen(false)}>Annuler</button>
            <button type="submit" className="btn-primary flex-1" disabled={submitting}>
              <Upload className="w-4 h-4" /> {submitting ? 'Envoi...' : 'Publier'}
            </button>
          </div>
        </form>
      </Modal>
    </div>
  );
}
