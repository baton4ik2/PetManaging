import { useState, useEffect } from 'react';
import { ownerService } from '../services/api';
import type { Owner } from '../services/api';
import OwnerForm from '../components/OwnerForm';
import OwnerList from '../components/OwnerList';

function Owners() {
  const [owners, setOwners] = useState<Owner[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [showForm, setShowForm] = useState(false);
  const [editingOwner, setEditingOwner] = useState<Owner | null>(null);
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    loadOwners();
  }, [searchTerm]);

  const loadOwners = async () => {
    try {
      setLoading(true);
      const response = await ownerService.getAll(
        searchTerm.trim() || undefined
      );
      setOwners(response.data);
      setError(null);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load owners');
    } finally {
      setLoading(false);
    }
  };

  const handleCreate = async (ownerData: Omit<Owner, 'id' | 'createdAt' | 'updatedAt' | 'pets'>) => {
    try {
      await ownerService.create(ownerData);
      await loadOwners();
      setShowForm(false);
      setError(null);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to create owner');
    }
  };

  const handleUpdate = async (id: number, ownerData: Omit<Owner, 'id' | 'createdAt' | 'updatedAt' | 'pets'>) => {
    try {
      await ownerService.update(id, ownerData);
      await loadOwners();
      setEditingOwner(null);
      setError(null);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to update owner');
    }
  };

  const handleDelete = async (id: number) => {
    if (!confirm('Are you sure you want to delete this owner?')) return;
    
    try {
      await ownerService.delete(id);
      await loadOwners();
      setError(null);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to delete owner');
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="text-xl text-gray-600">Loading...</div>
      </div>
    );
  }

  return (
    <div className="px-4 py-6 sm:px-0">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold text-gray-900">Owners</h1>
        <button
          onClick={() => {
            setEditingOwner(null);
            setShowForm(true);
          }}
          className="bg-indigo-600 text-white px-4 py-2 rounded-md hover:bg-indigo-700 transition-colors"
        >
          + Add Owner
        </button>
      </div>

      <div className="mb-4">
        <input
          type="text"
          placeholder="Search owners by name or email..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
        />
      </div>

      {error && (
        <div className="mb-4 bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
          {error}
        </div>
      )}

      {showForm && (
        <OwnerForm
          owner={null}
          onSubmit={handleCreate}
          onCancel={() => setShowForm(false)}
        />
      )}

      {editingOwner && (
        <OwnerForm
          owner={editingOwner}
          onSubmit={(data) => handleUpdate(editingOwner.id!, data)}
          onCancel={() => setEditingOwner(null)}
        />
      )}

      <OwnerList
        owners={owners}
        onEdit={setEditingOwner}
        onDelete={handleDelete}
      />
    </div>
  );
}

export default Owners;

