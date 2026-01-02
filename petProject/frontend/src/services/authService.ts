import api from './api';

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  username: string;
  email: string;
  roles: string[];
}

const TOKEN_KEY = 'auth_token';
const USER_KEY = 'auth_user';

export const authService = {
  login: async (credentials: LoginRequest): Promise<AuthResponse> => {
    const response = await api.post<AuthResponse>('/auth/login', credentials);
    if (response.data.token) {
      localStorage.setItem(TOKEN_KEY, response.data.token);
      // Если email из ответа содержит '@example.com', используем существующий email из localStorage
      // (если username совпадает и существующий email НЕ содержит '@example.com'), иначе используем email из ответа
      const existingUser = authService.getUser();
      let email = response.data.email;
      if (email.includes('@example.com') && existingUser && existingUser.username === response.data.username) {
        // Используем существующий email, если он не содержит '@example.com' (т.е. это реальный email)
        if (!existingUser.email.includes('@example.com')) {
          email = existingUser.email;
        }
      }
      localStorage.setItem(USER_KEY, JSON.stringify({
        username: response.data.username,
        email: email,
        roles: response.data.roles,
      }));
    }
    const savedUser = authService.getUser();
    return { ...response.data, email: savedUser?.email || response.data.email };
  },

  register: async (data: RegisterRequest): Promise<AuthResponse> => {
    const response = await api.post<AuthResponse>('/auth/register', data);
    if (response.data.token) {
      localStorage.setItem(TOKEN_KEY, response.data.token);
      localStorage.setItem(USER_KEY, JSON.stringify({
        username: response.data.username,
        email: response.data.email,
        roles: response.data.roles,
      }));
    }
    return response.data;
  },

  logout: () => {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
  },

  getToken: (): string | null => {
    return localStorage.getItem(TOKEN_KEY);
  },

  getUser: (): { username: string; email: string; roles: string[] } | null => {
    const userStr = localStorage.getItem(USER_KEY);
    if (!userStr) return null;
    try {
      return JSON.parse(userStr);
    } catch {
      return null;
    }
  },

  isAuthenticated: (): boolean => {
    return !!authService.getToken();
  },

  clearAllUsers: () => {
    // Очищает всех пользователей из localStorage
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    console.log('All users cleared from localStorage');
  },
};

export interface UserProfile {
  id: number;
  username: string;
  email: string;
  roles: string[];
  enabled: boolean;
  createdAt: string;
  updatedAt: string;
}

export const userService = {
  getProfile: () => {
    // Передаем данные пользователя через query параметры, так как заголовки могут не проходить через nginx
    const user = authService.getUser();
    if (user && user.username && user.email) {
      const url = `/users/me?username=${encodeURIComponent(user.username)}&email=${encodeURIComponent(user.email)}`;
      console.log('Loading profile with:', { username: user.username, email: user.email });
      return api.get<UserProfile>(url);
    }
    console.warn('No user data in localStorage, using default');
    return api.get<UserProfile>('/users/me');
  },
  updateProfile: (email: string) => api.put<UserProfile>('/users/me', { email }),
  changePassword: (currentPassword: string, newPassword: string) =>
    api.put('/users/me/password', { currentPassword, newPassword }),
};

