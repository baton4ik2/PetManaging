import { useState, useEffect, useRef, useCallback } from 'react';
import { ownerService } from '../services/api';
import type { Owner } from '../services/api';
import OwnerForm from '../components/OwnerForm';
import OwnerList from '../components/OwnerList';
import { useAuth } from '../contexts/AuthContext';

function Owners() {
  const { user } = useAuth();
  const [owners, setOwners] = useState<Owner[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [showForm, setShowForm] = useState(false);
  const [editingOwner, setEditingOwner] = useState<Owner | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const debounceTimerRef = useRef<number | null>(null);
  const searchInputRef = useRef<HTMLInputElement | null>(null);
  const cursorPositionRef = useRef<number | null>(null);
  
  const isAdmin = user?.roles?.includes('ADMIN') || false;

  const loadOwners = useCallback(async (search?: string) => {
    try {
      setLoading(true);
      const response = await ownerService.getAll(search?.trim() || undefined);
      setOwners(response.data);
      setError(null);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load owners');
    } finally {
      setLoading(false);
      // Восстанавливаем фокус после загрузки с несколькими попытками
      setTimeout(() => {
        requestAnimationFrame(() => {
          if (searchInputRef.current) {
            searchInputRef.current.focus();
            if (cursorPositionRef.current !== null) {
              searchInputRef.current.setSelectionRange(
                cursorPositionRef.current,
                cursorPositionRef.current
              );
            }
          }
        });
      }, 0);
    }
  }, []);

  // Initial load
  useEffect(() => {
    loadOwners();
  }, [loadOwners]);

  // Debounce search
  useEffect(() => {
    if (debounceTimerRef.current) {
      clearTimeout(debounceTimerRef.current);
    }

    debounceTimerRef.current = window.setTimeout(() => {
      loadOwners(searchTerm);
    }, 1000);

    return () => {
      if (debounceTimerRef.current) {
        clearTimeout(debounceTimerRef.current);
      }
    };
  }, [searchTerm, loadOwners]);

  // Восстанавливаем фокус после каждого изменения loading
  useEffect(() => {
    if (!loading && searchInputRef.current && document.activeElement !== searchInputRef.current) {
      const timer = setTimeout(() => {
        if (searchInputRef.current) {
          searchInputRef.current.focus();
          if (cursorPositionRef.current !== null) {
            searchInputRef.current.setSelectionRange(
              cursorPositionRef.current,
              cursorPositionRef.current
            );
          }
        }
      }, 50);
      return () => clearTimeout(timer);
    }
  }, [loading]);

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
        <div className="text-xl text-gray-600">Loading...</div>
      </div>
    );
  }

  return (
    <div className="px-4 py-6 sm:px-0">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold text-gray-900">Owners</h1>
        {isAdmin && (
          <button
            onClick={() => {
              setEditingOwner(null);
              setShowForm(true);
            }}
            className="bg-indigo-600 text-white px-4 py-2 rounded-md hover:bg-indigo-700 transition-colors"
          >
            + Add Owner
          </button>
        )}
      </div>

      <div className="mb-4">
        <input
          ref={searchInputRef}
          type="text"
          placeholder="Search owners by name or email..."
          value={searchTerm}
          onChange={(e) => {
            cursorPositionRef.current = e.target.selectionStart;
            setSearchTerm(e.target.value);
          }}
          onFocus={(e) => {
            if (cursorPositionRef.current !== null) {
              e.target.setSelectionRange(
                cursorPositionRef.current,
                cursorPositionRef.current
              );
            }
          }}
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
        isAdmin={isAdmin}
      />
    </div>
  );
}

export default Owners;

