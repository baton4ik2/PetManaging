import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { statisticsService } from '../services/api';
import type { Statistics } from '../services/api';

const typeEmojis: Record<string, string> = {
  DOG: '🐕',
  CAT: '🐈',
  BIRD: '🐦',
  FISH: '🐠',
  RABBIT: '🐰',
  HAMSTER: '🐹',
  OTHER: '🐾',
};

function StatisticsPage() {
  const navigate = useNavigate();
  const [statistics, setStatistics] = useState<Statistics | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    loadStatistics();
  }, []);

  const loadStatistics = async () => {
    try {
      setLoading(true);
      const response = await statisticsService.getStatistics();
      setStatistics(response.data);
      setError(null);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load statistics');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="text-xl text-gray-600">Loading...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
        {error}
      </div>
    );
  }

  if (!statistics) {
    return null;
  }

  return (
    <div className="px-4 py-6 sm:px-0">
      <h1 className="text-3xl font-bold text-gray-900 mb-6">Statistics</h1>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
        <div className="bg-white p-6 rounded-lg shadow-md">
          <div className="text-4xl font-bold text-indigo-600 mb-2">
            {statistics.totalOwners}
          </div>
          <div className="text-gray-600">Total Owners</div>
        </div>

        <div className="bg-white p-6 rounded-lg shadow-md">
          <div className="text-4xl font-bold text-indigo-600 mb-2">
            {statistics.totalPets}
          </div>
          <div className="text-gray-600">Total Pets</div>
        </div>

        <div className="bg-white p-6 rounded-lg shadow-md">
          <div className="text-4xl font-bold text-indigo-600 mb-2">
            {statistics.averagePetsPerOwner}
          </div>
          <div className="text-gray-600">Avg Pets per Owner</div>
        </div>
      </div>

      <div className="bg-white p-6 rounded-lg shadow-md">
        <h2 className="text-2xl font-semibold text-gray-900 mb-4">
          Pets by Type
        </h2>
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
          {Object.entries(statistics.petsByType).map(([type, count]) => (
            <div
              key={type}
              onClick={() => navigate(`/pets?type=${type}`)}
              className="p-4 border border-gray-200 rounded-lg text-center cursor-pointer hover:bg-indigo-50 hover:border-indigo-300 transition-colors"
            >
              <div className="text-3xl mb-2">{typeEmojis[type] || '🐾'}</div>
              <div className="text-xl font-bold text-gray-900">{count}</div>
              <div className="text-sm text-gray-600">{type}</div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

export default StatisticsPage;

