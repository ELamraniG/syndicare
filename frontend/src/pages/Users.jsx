import { useEffect, useState } from 'react';
import { usersApi } from '../lib/api';
import { PageHeader, Badge, Skeleton, Empty } from '../components/UI';
import { Users as UsersIcon } from 'lucide-react';

const roleColor = (r) => ({ ADMIN: 'ochre', OWNER: 'green', RESIDENT: 'blue' }[r] || 'gray');

export default function Users() {
  const [items, setItems] = useState([]);
  const [filter, setFilter] = useState('');
  const [loading, setLoading] = useState(true);

  const load = async () => {
    setLoading(true);
    try {
      const { data } = await usersApi.list(filter || undefined);
      setItems(data);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { load(); /* eslint-disable-next-line */ }, [filter]);

  return (
    <div>
      <PageHeader
        eyebrow="Annuaire"
        title="Utilisateurs"
        description="Liste des syndics, propriétaires et résidents enregistrés sur la plateforme."
      />

      <div className="mb-4 flex gap-2">
        <select className="input max-w-xs" value={filter} onChange={(e) => setFilter(e.target.value)}>
          <option value="">Tous les rôles</option>
          <option value="ADMIN">Syndics</option>
          <option value="OWNER">Propriétaires</option>
          <option value="RESIDENT">Résidents</option>
        </select>
      </div>

      {loading ? (
        <Skeleton className="h-64" />
      ) : items.length === 0 ? (
        <Empty icon={UsersIcon} title="Aucun utilisateur" />
      ) : (
        <div className="card overflow-hidden">
          <div className="overflow-x-auto">
            <table className="table-clean">
              <thead>
                <tr>
                  <th>Nom</th>
                  <th>Email</th>
                  <th>Téléphone</th>
                  <th>Rôle</th>
                  <th>Statut</th>
                </tr>
              </thead>
              <tbody>
                {items.map((u) => (
                  <tr key={u.id}>
                    <td>
                      <div className="flex items-center gap-3">
                        <div className="w-8 h-8 rounded-full bg-sage-100 grid place-items-center text-xs font-medium text-sage-700">
                          {u.firstName?.[0]}{u.lastName?.[0]}
                        </div>
                        <span className="font-medium">{u.firstName} {u.lastName}</span>
                      </div>
                    </td>
                    <td className="text-ink/70">{u.email}</td>
                    <td className="text-ink/70 font-mono text-xs">{u.phone || '—'}</td>
                    <td><Badge color={roleColor(u.role)}>{u.role}</Badge></td>
                    <td>
                      {u.enabled
                        ? <Badge color="green">Actif</Badge>
                        : <Badge color="red">Inactif</Badge>}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  );
}
