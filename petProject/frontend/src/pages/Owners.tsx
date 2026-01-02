import { useState, useEffect, useRef, useCallback } from 'react';
import { ownerService } from '../services/api';
import type { Owner } from '../services/api';
import OwnerForm from '../components/OwnerForm';
import OwnerList from '../components/OwnerList';
import { useAuth } from '../contexts/AuthContext';

function Owners() {
  const { user } = useAuth();
  const [owners, setOwners] = useState<Owner[]>([]);
  const [filteredOwners, setFilteredOwners] = useState<Owner[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [showForm, setShowForm] = useState(false);
  const [editingOwner, setEditingOwner] = useState<Owner | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [showSearchDropdown, setShowSearchDropdown] = useState(false);
  const searchInputRef = useRef<HTMLInputElement | null>(null);
  const searchDropdownRef = useRef<HTMLDivElement | null>(null);
  
  const isAdmin = user?.roles?.includes('ADMIN') || false;

  const loadOwners = useCallback(async (search?: string) => {
    try {
      setLoading(true);
      const response = await ownerService.getAll(search?.trim() || undefined);
      const loadedOwners = response.data;
      setOwners(loadedOwners);
      
      // Фильтруем результаты для dropdown
      if (search?.trim()) {
        const searchLower = search.trim().toLowerCase();
        const filtered = loadedOwners.filter(owner => {
          const fullName = `${owner.firstName} ${owner.lastName}`.toLowerCase();
          const email = owner.email.toLowerCase();
          return fullName.includes(searchLower) || email.includes(searchLower);
        });
        setFilteredOwners(filtered);
      } else {
        setFilteredOwners(loadedOwners);
      }
      
      setError(null);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load owners');
    } finally {
      setLoading(false);
    }
  }, []);

  // Initial load
  useEffect(() => {
    loadOwners();
  }, [loadOwners]);

  // Update filtered owners when owners change
  useEffect(() => {
    setFilteredOwners(owners);
  }, [owners]);

  // Instant search with filtering
  useEffect(() => {
    if (searchTerm.trim()) {
      const searchLower = searchTerm.trim().toLowerCase();
      const filtered = owners.filter(owner => {
        const fullName = `${owner.firstName} ${owner.lastName}`.toLowerCase();
        const email = owner.email.toLowerCase();
        return fullName.includes(searchLower) || email.includes(searchLower);
      });
      setFilteredOwners(filtered);
    } else {
      setFilteredOwners(owners);
    }
  }, [searchTerm, owners]);


  // Закрываем dropdown при клике вне его
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (
        searchDropdownRef.current &&
        !searchDropdownRef.current.contains(event.target as Node) &&
        searchInputRef.current &&
        !searchInputRef.current.contains(event.target as Node)
      ) {
        setShowSearchDropdown(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);

  const handleCreate = async (ownerData: Omit<Owner, 'id' | 'createdAt' | 'updatedAt' | 'pets'>) => {
    try {
      await ownerService.create(ownerData);
      await loadOwners(searchTerm);
      setShowForm(false);
      setError(null);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to create owner');
    }
  };

  const handleUpdate = async (id: number, ownerData: Omit<Owner, 'id' | 'createdAt' | 'updatedAt' | 'pets'>) => {
    try {
      await ownerService.update(id, ownerData);
      await loadOwners(searchTerm);
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
      await loadOwners(searchTerm);
      setError(null);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to delete owner');
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="text-xl text-gray-600 dark:text-gray-300">Loading...</div>
      </div>
    );
  }

  return (
    <div className="px-4 py-6 sm:px-0">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold text-gray-900 dark:text-white">Owners</h1>
        {isAdmin && (
          <button
            onClick={() => {
              setEditingOwner(null);
              setShowForm(true);
            }}
            className="bg-indigo-600 dark:bg-indigo-500 text-white px-4 py-2 rounded-md hover:bg-indigo-700 dark:hover:bg-indigo-600 transition-colors"
          >
            + Add Owner
          </button>
        )}
      </div>

      <div className="mb-4 relative">
        <input
          ref={searchInputRef}
          type="text"
          placeholder="Search owners by name or email..."
          value={searchTerm}
          onChange={(e) => {
            setSearchTerm(e.target.value);
            setShowSearchDropdown(true);
          }}
          onFocus={() => {
            if (filteredOwners.length > 0) {
              setShowSearchDropdown(true);
            }
          }}
          className="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500 dark:bg-gray-700 dark:text-white dark:placeholder-gray-400"
        />
        {showSearchDropdown && filteredOwners.length > 0 && searchTerm.trim() && (
          <div
            ref={searchDropdownRef}
            className="absolute z-10 w-full mt-1 bg-white dark:bg-gray-800 border border-gray-300 dark:border-gray-600 rounded-md shadow-lg max-h-60 overflow-auto"
          >
            {filteredOwners.map((owner) => (
              <div
                key={owner.id}
                onClick={() => {
                  setSearchTerm(`${owner.firstName} ${owner.lastName}`);
                  setShowSearchDropdown(false);
                }}
                className="px-4 py-2 hover:bg-indigo-50 dark:hover:bg-gray-700 cursor-pointer border-b border-gray-100 dark:border-gray-700 last:border-b-0"
              >
                <div className="font-medium dark:text-white">{owner.firstName} {owner.lastName}</div>
                <div className="text-sm text-gray-500 dark:text-gray-400">{owner.email}</div>
              </div>
            ))}
          </div>
        )}
      </div>

      {error && (
        <div className="mb-4 bg-red-100 dark:bg-red-900/20 border border-red-400 dark:border-red-600 text-red-700 dark:text-red-200 px-4 py-3 rounded">
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
        owners={searchTerm.trim() ? filteredOwners : owners}
        onEdit={setEditingOwner}
        onDelete={handleDelete}
        isAdmin={isAdmin}
      />
    </div>
  );
}

export default Owners;

