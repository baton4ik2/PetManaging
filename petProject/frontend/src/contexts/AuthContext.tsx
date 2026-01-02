import { createContext, useContext, useState, useEffect } from 'react';
import type { ReactNode } from 'react';
import { authService } from '../services/authService';
import type { AuthResponse } from '../services/authService';

interface User {
  username: string;
  email: string;
  roles: string[];
}

interface AuthContextType {
  user: User | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (username: string, password: string) => Promise<void>;
  register: (username: string, email: string, password: string, firstName: string, lastName: string, phone: string) => Promise<void>;
  logout: () => void;
  updateUser: (user: User) => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const loadUser = () => {
      const savedUser = authService.getUser();
      if (savedUser && authService.isAuthenticated()) {
        setUser(savedUser);
      }
      setIsLoading(false);
    };
    loadUser();
  }, []);

  const login = async (username: string, password: string) => {
    const response: AuthResponse = await authService.login({ username, password });
    setUser({
      username: response.username,
      email: response.email,
      roles: response.roles,
    });
  };

  const register = async (username: string, email: string, password: string, firstName: string, lastName: string, phone: string) => {
    const response: AuthResponse = await authService.register({ username, email, password, firstName, lastName, phone });
    setUser({
      username: response.username,
      email: response.email,
      roles: response.roles,
    });
  };

  const logout = () => {
    authService.logout();
    setUser(null);
  };

  const updateUser = (updatedUser: User) => {
    setUser(updatedUser);
    // Update localStorage
    localStorage.setItem('auth_user', JSON.stringify(updatedUser));
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        isAuthenticated: !!user,
        isLoading,
        login,
        register,
        logout,
        updateUser,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

