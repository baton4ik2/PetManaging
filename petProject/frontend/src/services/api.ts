import axios from 'axios';
import { authService } from './authService';

// Use relative URL in production (nginx proxy), absolute in dev
const API_BASE_URL = import.meta.env.VITE_API_URL || 
  (import.meta.env.PROD ? '/api' : 'http://localhost:8081/api');

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add JWT token to requests
api.interceptors.request.use(
  (config) => {
    const token = authService.getToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    // Добавляем данные пользователя в заголовки для профиля
    const user = authService.getUser();
    if (user) {
      config.headers['X-Username'] = user.username;
      config.headers['X-Email'] = user.email;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Handle 401 errors (unauthorized) and 403 errors (forbidden)
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      authService.logout();
      window.location.href = '/login';
    }
    // 403 errors are handled by the component showing the error message
    return Promise.reject(error);
  }
);

export interface Owner {
  id?: number;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  address: string;
  pets?: Pet[];
  createdAt?: string;
  updatedAt?: string;
}

export interface Pet {
  id?: number;
  name: string;
  type: 'DOG' | 'CAT' | 'BIRD' | 'FISH' | 'RABBIT' | 'HAMSTER' | 'OTHER';
  breed: string;
  dateOfBirth: string;
  color?: string;
  description?: string;
  ownerId: number;
  ownerName?: string;
  age?: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface Statistics {
  totalOwners: number;
  totalPets: number;
  petsByType: Record<string, number>;
  averagePetsPerOwner: number;
}

export const ownerService = {
  getAll: (search?: string) => {
    const params = new URLSearchParams();
    if (search) params.append('search', search);
    return api.get<Owner[]>(`/owners?${params.toString()}`);
  },
  getById: (id: number) => api.get<Owner>(`/owners/${id}`),
  create: (owner: Omit<Owner, 'id' | 'createdAt' | 'updatedAt' | 'pets'>) => 
    api.post<Owner>('/owners', owner),
  update: (id: number, owner: Omit<Owner, 'id' | 'createdAt' | 'updatedAt' | 'pets'>) => 
    api.put<Owner>(`/owners/${id}`, owner),
  delete: (id: number) => api.delete(`/owners/${id}`),
  getPets: (id: number) => api.get<Pet[]>(`/owners/${id}/pets`),
};

export const petService = {
  getAll: (type?: string, ownerId?: number, search?: string) => {
    const params = new URLSearchParams();
    if (type) params.append('type', type);
    if (ownerId) params.append('ownerId', ownerId.toString());
    if (search) params.append('search', search);
    return api.get<Pet[]>(`/pets?${params.toString()}`);
  },
  getMyPets: () => api.get<Pet[]>('/pets/my'),
  getById: (id: number) => api.get<Pet>(`/pets/${id}`),
  create: (pet: Omit<Pet, 'id' | 'createdAt' | 'updatedAt' | 'ownerName'>) => 
    api.post<Pet>('/pets', pet),
  update: (id: number, pet: Omit<Pet, 'id' | 'createdAt' | 'updatedAt' | 'ownerName'>) => 
    api.put<Pet>(`/pets/${id}`, pet),
  delete: (id: number) => api.delete(`/pets/${id}`),
};

export const statisticsService = {
  getStatistics: () => api.get<Statistics>('/statistics'),
};

export default api;

