import axios from 'axios';

const api = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' },
});

// Inject JWT
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('syndicare_token');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

// Handle 401 globally
api.interceptors.response.use(
  (r) => r,
  (err) => {
    if (err.response?.status === 401 && !err.config?.url?.includes('/auth/')) {
      localStorage.removeItem('syndicare_token');
      localStorage.removeItem('syndicare_user');
      if (window.location.pathname !== '/login') {
        window.location.href = '/login';
      }
    }
    return Promise.reject(err);
  }
);

export default api;

// === Auth ===
export const authApi = {
  login: (email, password) => api.post('/auth/login', { email, password }),
  register: (data) => api.post('/auth/register', data),
  me: () => api.get('/auth/me'),
};

// === Users ===
export const usersApi = {
  list: (role) => api.get('/users', { params: role ? { role } : {} }),
  get: (id) => api.get(`/users/${id}`),
};

// === Buildings ===
export const buildingsApi = {
  list: () => api.get('/buildings'),
  get: (id) => api.get(`/buildings/${id}`),
  create: (data) => api.post('/buildings', data),
  update: (id, data) => api.put(`/buildings/${id}`, data),
  delete: (id) => api.delete(`/buildings/${id}`),
};

// === Apartments ===
export const apartmentsApi = {
  list: (params) => api.get('/apartments', { params }),
  mine: () => api.get('/apartments/mine'),
  get: (id) => api.get(`/apartments/${id}`),
  create: (data) => api.post('/apartments', data),
  update: (id, data) => api.put(`/apartments/${id}`, data),
  delete: (id) => api.delete(`/apartments/${id}`),
};

// === Charges ===
export const chargesApi = {
  list: (params) => api.get('/charges', { params }),
  mine: () => api.get('/charges/mine'),
  generate: (data) => api.post('/charges/generate', data),
  validatePayment: (data) => api.post('/charges/validate-payment', data),
  delete: (id) => api.delete(`/charges/${id}`),
  receiptUrl: (id) => `/api/charges/${id}/receipt`,
};

// === Tickets ===
export const ticketsApi = {
  list: () => api.get('/tickets'),
  get: (id) => api.get(`/tickets/${id}`),
  createJson: (data) => api.post('/tickets', data),
  createWithPhoto: (data, photo) => {
    const fd = new FormData();
    fd.append('data', new Blob([JSON.stringify(data)], { type: 'application/json' }));
    if (photo) fd.append('photo', photo);
    return api.post('/tickets', fd, { headers: { 'Content-Type': 'multipart/form-data' } });
  },
  update: (id, data) => api.patch(`/tickets/${id}`, data),
  delete: (id) => api.delete(`/tickets/${id}`),
};

// === Documents ===
export const documentsApi = {
  list: (category) => api.get('/documents', { params: category ? { category } : {} }),
  upload: (file, title, description, category, buildingId) => {
    const fd = new FormData();
    fd.append('file', file);
    fd.append('title', title);
    if (description) fd.append('description', description);
    if (category) fd.append('category', category);
    if (buildingId) fd.append('buildingId', buildingId);
    return api.post('/documents', fd, { headers: { 'Content-Type': 'multipart/form-data' } });
  },
  delete: (id) => api.delete(`/documents/${id}`),
  downloadUrl: (id) => `/api/documents/${id}/download`,
};

// === Announcements ===
export const announcementsApi = {
  list: () => api.get('/announcements'),
  create: (data) => api.post('/announcements', data),
  update: (id, data) => api.put(`/announcements/${id}`, data),
  delete: (id) => api.delete(`/announcements/${id}`),
};

// === Dashboard ===
export const dashboardApi = {
  admin: () => api.get('/dashboard/admin'),
  owner: () => api.get('/dashboard/owner'),
};
