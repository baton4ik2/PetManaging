import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import { AuthProvider, useAuth } from './contexts/AuthContext';
import { useTheme } from './contexts/ThemeContext';
import { ProtectedRoute } from './components/ProtectedRoute';
import Owners from './pages/Owners';
import Pets from './pages/Pets';
import Home from './pages/Home';
import StatisticsPage from './pages/Statistics';
import Login from './pages/Login';
import Register from './pages/Register';
import Profile from './pages/Profile';

function Navigation() {
  const { isAuthenticated, user, logout } = useAuth();
  const { theme, toggleTheme } = useTheme();

  return (
    <nav className="bg-white dark:bg-gray-800 shadow-lg transition-colors">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between h-16">
          <div className="flex">
            <div className="flex-shrink-0 flex items-center">
              <Link to="/" className="text-2xl font-bold text-indigo-600 dark:text-indigo-400">
                🐾 Pet Management
              </Link>
            </div>
            {isAuthenticated && (
              <div className="hidden sm:ml-6 sm:flex sm:space-x-8">
                <Link
                  to="/"
                  className="border-transparent text-gray-500 dark:text-gray-300 hover:border-gray-300 dark:hover:border-gray-600 hover:text-gray-700 dark:hover:text-gray-100 inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium transition-colors"
                >
                  Home
                </Link>
                <Link
                  to="/owners"
                  className="border-transparent text-gray-500 dark:text-gray-300 hover:border-gray-300 dark:hover:border-gray-600 hover:text-gray-700 dark:hover:text-gray-100 inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium transition-colors"
                >
                  Owners
                </Link>
                <Link
                  to="/pets"
                  className="border-transparent text-gray-500 dark:text-gray-300 hover:border-gray-300 dark:hover:border-gray-600 hover:text-gray-700 dark:hover:text-gray-100 inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium transition-colors"
                >
                  Pets
                </Link>
                <Link
                  to="/statistics"
                  className="border-transparent text-gray-500 dark:text-gray-300 hover:border-gray-300 dark:hover:border-gray-600 hover:text-gray-700 dark:hover:text-gray-100 inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium transition-colors"
                >
                  Statistics
                </Link>
                <Link
                  to="/profile"
                  className="border-transparent text-gray-500 dark:text-gray-300 hover:border-gray-300 dark:hover:border-gray-600 hover:text-gray-700 dark:hover:text-gray-100 inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium transition-colors"
                >
                  Profile
                </Link>
              </div>
            )}
          </div>
          <div className="flex items-center">
            {/* Theme Toggle */}
            <button
              onClick={toggleTheme}
              className="text-gray-500 dark:text-gray-300 hover:text-gray-700 dark:hover:text-gray-100 transition-colors mr-4 p-2 rounded-md hover:bg-gray-100 dark:hover:bg-gray-700"
              aria-label="Toggle theme"
              title={theme === 'light' ? 'Switch to dark mode' : 'Switch to light mode'}
            >
              {theme === 'light' ? (
                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M20.354 15.354A9 9 0 018.646 3.646 9.003 9.003 0 0012 21a9.003 9.003 0 008.354-5.646z" />
                </svg>
              ) : (
                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 3v1m0 16v1m9-9h-1M4 12H3m15.364 6.364l-.707-.707M6.343 6.343l-.707-.707m12.728 0l-.707.707M6.343 17.657l-.707.707M16 12a4 4 0 11-8 0 4 4 0 018 0z" />
                </svg>
              )}
            </button>
            {/* Telegram Icon */}
            <a
              href={import.meta.env.VITE_TELEGRAM_URL || 'https://t.me/akbirovb'}
              target="_blank"
              rel="noopener noreferrer"
              className="text-gray-500 dark:text-gray-300 hover:text-blue-500 dark:hover:text-blue-400 transition-colors mr-4 flex items-center"
              aria-label="Telegram"
              title="Telegram"
            >
              <svg
                className="w-6 h-6"
                fill="currentColor"
                viewBox="0 0 24 24"
                xmlns="http://www.w3.org/2000/svg"
              >
                <path d="M12 0C5.373 0 0 5.373 0 12s5.373 12 12 12 12-5.373 12-12S18.627 0 12 0zm5.894 8.221l-1.97 9.28c-.145.658-.537.818-1.084.508l-3-2.21-1.446 1.394c-.14.18-.357.295-.6.295-.002 0-.003 0-.005 0l.213-3.054 5.56-5.022c.24-.213-.054-.334-.373-.121l-6.869 4.326-2.96-.924c-.64-.203-.658-.64.135-.954l11.566-4.458c.538-.196 1.006.128.832.941z"/>
              </svg>
            </a>
            {isAuthenticated ? (
              <div className="flex items-center space-x-4">
                <Link
                  to="/profile"
                  className="text-sm text-gray-700 dark:text-gray-200 hover:text-gray-900 dark:hover:text-white"
                >
                  <span className="font-medium">{user?.username}</span>
                </Link>
                <button
                  onClick={logout}
                  className="text-sm text-gray-500 dark:text-gray-300 hover:text-gray-700 dark:hover:text-gray-100 px-3 py-2 rounded-md transition-colors"
                >
                  Logout
                </button>
              </div>
            ) : (
              <div className="flex items-center space-x-4">
                <Link
                  to="/login"
                  className="text-sm text-gray-500 dark:text-gray-300 hover:text-gray-700 dark:hover:text-gray-100 px-3 py-2 rounded-md transition-colors"
                >
                  Login
                </Link>
                <Link
                  to="/register"
                  className="text-sm text-white bg-indigo-600 hover:bg-indigo-700 dark:bg-indigo-500 dark:hover:bg-indigo-600 px-3 py-2 rounded-md transition-colors"
                >
                  Register
                </Link>
              </div>
            )}
          </div>
        </div>
      </div>
    </nav>
  );
}

function AppContent() {
  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-900 transition-colors">
      <Navigation />
      <main className="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route
            path="/"
            element={
              <ProtectedRoute>
                <Home />
              </ProtectedRoute>
            }
          />
          <Route
            path="/owners"
            element={
              <ProtectedRoute>
                <Owners />
              </ProtectedRoute>
            }
          />
          <Route
            path="/pets"
            element={
              <ProtectedRoute>
                <Pets />
              </ProtectedRoute>
            }
          />
          <Route
            path="/statistics"
            element={
              <ProtectedRoute>
                <StatisticsPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/profile"
            element={
              <ProtectedRoute>
                <Profile />
              </ProtectedRoute>
            }
          />
        </Routes>
      </main>
    </div>
  );
}

function App() {
  return (
    <Router>
      <AuthProvider>
        <AppContent />
      </AuthProvider>
    </Router>
  );
}

export default App;
