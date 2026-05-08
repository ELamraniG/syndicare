import { Routes, Route, Navigate } from 'react-router-dom';
import { useAuth } from './context/AuthContext';
import Login from './pages/Login';
import Register from './pages/Register';
import Layout from './components/Layout';
import AdminDashboard from './pages/AdminDashboard';
import OwnerDashboard from './pages/OwnerDashboard';
import Buildings from './pages/Buildings';
import Apartments from './pages/Apartments';
import Users from './pages/Users';
import Charges from './pages/Charges';
import Tickets from './pages/Tickets';
import Documents from './pages/Documents';
import Announcements from './pages/Announcements';

const Protected = ({ children, allow }) => {
  const { user, loading } = useAuth();
  if (loading) return <div className="min-h-screen flex items-center justify-center text-ink/50">Chargement...</div>;
  if (!user) return <Navigate to="/login" replace />;
  if (allow && !allow.includes(user.role)) return <Navigate to="/" replace />;
  return children;
};

const Home = () => {
  const { user } = useAuth();
  if (!user) return <Navigate to="/login" replace />;
  if (user.role === 'ADMIN') return <Navigate to="/admin" replace />;
  return <Navigate to="/dashboard" replace />;
};

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route path="/" element={<Home />} />

      <Route element={<Protected><Layout /></Protected>}>
        <Route path="/dashboard" element={<OwnerDashboard />} />
        <Route path="/admin" element={<Protected allow={['ADMIN']}><AdminDashboard /></Protected>} />
        <Route path="/buildings" element={<Protected allow={['ADMIN']}><Buildings /></Protected>} />
        <Route path="/apartments" element={<Apartments />} />
        <Route path="/users" element={<Protected allow={['ADMIN']}><Users /></Protected>} />
        <Route path="/charges" element={<Charges />} />
        <Route path="/tickets" element={<Tickets />} />
        <Route path="/documents" element={<Documents />} />
        <Route path="/announcements" element={<Announcements />} />
      </Route>

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
