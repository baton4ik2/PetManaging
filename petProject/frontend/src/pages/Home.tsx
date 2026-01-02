import { Link } from 'react-router-dom';

function Home() {
  return (
    <div className="px-4 py-6 sm:px-0">
      <div className="text-center">
        <h1 className="text-4xl font-bold text-gray-900 dark:text-white mb-4">
          Welcome to Pet Management System
        </h1>
        <p className="text-xl text-gray-600 dark:text-gray-300 mb-8">
          Manage your pets and their owners with ease
        </p>
        
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mt-12">
          <Link
            to="/owners"
            className="block p-6 bg-white dark:bg-gray-800 rounded-lg shadow-md hover:shadow-lg transition-shadow"
          >
            <div className="text-4xl mb-4">👥</div>
            <h2 className="text-2xl font-semibold text-gray-900 dark:text-white mb-2">Owners</h2>
            <p className="text-gray-600 dark:text-gray-300">
              Manage pet owners, add new owners, and view their information
            </p>
          </Link>
          
          <Link
            to="/pets"
            className="block p-6 bg-white dark:bg-gray-800 rounded-lg shadow-md hover:shadow-lg transition-shadow"
          >
            <div className="text-4xl mb-4">🐾</div>
            <h2 className="text-2xl font-semibold text-gray-900 dark:text-white mb-2">Pets</h2>
            <p className="text-gray-600 dark:text-gray-300">
              Manage pets, add new pets, and view their details
            </p>
          </Link>

          <Link
            to="/statistics"
            className="block p-6 bg-white dark:bg-gray-800 rounded-lg shadow-md hover:shadow-lg transition-shadow"
          >
            <div className="text-4xl mb-4">📊</div>
            <h2 className="text-2xl font-semibold text-gray-900 dark:text-white mb-2">Statistics</h2>
            <p className="text-gray-600 dark:text-gray-300">
              View statistics about pets and owners
            </p>
          </Link>
        </div>
      </div>
    </div>
  );
}

export default Home;

