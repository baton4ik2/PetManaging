import { useState, useEffect, useRef, useCallback } from 'react';
import { useSearchParams } from 'react-router-dom';
import { petService, ownerService } from '../services/api';
import type { Pet, Owner } from '../services/api';
import PetForm from '../components/PetForm';
import PetList from '../components/PetList';
import { useAuth } from '../contexts/AuthContext';

function Pets() {
  const { user } = useAuth();
  const [searchParams, setSearchParams] = useSearchParams();
  // Инициализируем filterType из URL параметра сразу
  const [filterType, setFilterType] = useState<string>(() => {
    return searchParams.get('type') || '';
  });
  const [pets, setPets] = useState<Pet[]>([]);
  const [filteredPets, setFilteredPets] = useState<Pet[]>([]);
  const [owners, setOwners] = useState<Owner[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [showForm, setShowForm] = useState(false);
  const [editingPet, setEditingPet] = useState<Pet | null>(null);
  const [filterOwnerId, setFilterOwnerId] = useState<number | undefined>();
  const [searchTerm, setSearchTerm] = useState('');
  const [viewMode, setViewMode] = useState<'all' | 'my'>('all');
  const [showSearchDropdown, setShowSearchDropdown] = useState(false);
  const searchInputRef = useRef<HTMLInputElement | null>(null);
  const searchDropdownRef = useRef<HTMLDivElement | null>(null);
  
  const isAdmin = user?.roles?.includes('ADMIN') || false;

  // Синхронизируем filterType с URL параметром при изменении URL
  useEffect(() => {
    const typeParam = searchParams.get('type') || '';
    setFilterType(typeParam);
  }, [searchParams]);

  const loadPets = useCallback(async (search?: string) => {
    try {
      setLoading(true);
      // Читаем актуальное значение filterType из URL параметров (приоритет URL)
      const urlType = searchParams.get('type') || '';
      const currentFilterType = urlType || filterType;
      let response;
      if (viewMode === 'my') {
        // Загружаем только своих питомцев
        response = await petService.getMyPets();
        // Применяем фильтры на клиенте для своих питомцев
        let allPets = response.data;
        if (currentFilterType) {
          allPets = allPets.filter(pet => pet.type === currentFilterType);
        }
        setPets(allPets);
        
        // Фильтруем для dropdown
        if (search?.trim()) {
          const searchLower = search.trim().toLowerCase();
          const filtered = allPets.filter(pet => 
            pet.name.toLowerCase().includes(searchLower) ||
            pet.breed.toLowerCase().includes(searchLower) ||
            pet.ownerName?.toLowerCase().includes(searchLower)
          );
          setFilteredPets(filtered);
        } else {
          setFilteredPets(allPets);
        }
      } else {
        // Загружаем всех питомцев с фильтрами
        response = await petService.getAll(
          currentFilterType || undefined,
          filterOwnerId,
          search?.trim() || undefined
        );
        const allPets = response.data;
        setPets(allPets);
        
        // Фильтруем для dropdown
        if (search?.trim()) {
          const searchLower = search.trim().toLowerCase();
          const filtered = allPets.filter(pet => 
            pet.name.toLowerCase().includes(searchLower) ||
            pet.breed.toLowerCase().includes(searchLower) ||
            pet.ownerName?.toLowerCase().includes(searchLower)
          );
          setFilteredPets(filtered);
        } else {
          setFilteredPets(allPets);
        }
      }
      setError(null);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load pets');
    } finally {
      setLoading(false);
    }
  }, [viewMode, filterType, filterOwnerId, searchParams]);

  const loadOwners = useCallback(async () => {
    try {
      const response = await ownerService.getAll();
      setOwners(response.data);
    } catch (err) {
      console.error('Failed to load owners:', err);
    }
  }, []);

  // Initial load and when filters change (except search)
  // Используем значение из URL напрямую для отслеживания изменений
  const urlType = searchParams.get('type') || '';
  useEffect(() => {
    loadPets();
    if (viewMode === 'all') {
      loadOwners();
    }
  }, [urlType, filterType, filterOwnerId, viewMode, loadPets, loadOwners]);

  // Update filtered pets when pets change
  useEffect(() => {
    setFilteredPets(pets);
  }, [pets]);

  // Instant search with filtering
  useEffect(() => {
    if (searchTerm.trim()) {
      const searchLower = searchTerm.trim().toLowerCase();
      const filtered = pets.filter(pet => 
        pet.name.toLowerCase().includes(searchLower) ||
        pet.breed.toLowerCase().includes(searchLower) ||
        pet.ownerName?.toLowerCase().includes(searchLower)
      );
      setFilteredPets(filtered);
    } else {
      setFilteredPets(pets);
    }
  }, [searchTerm, pets]);


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

  const handleCreate = async (petData: Omit<Pet, 'id' | 'createdAt' | 'updatedAt' | 'ownerName'>) => {
    try {
      await petService.create(petData);
      await loadPets(searchTerm);
      setShowForm(false);
      setError(null);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to create pet');
    }
  };

  const handleUpdate = async (id: number, petData: Omit<Pet, 'id' | 'createdAt' | 'updatedAt' | 'ownerName'>) => {
    try {
      await petService.update(id, petData);
      await loadPets(searchTerm);
      setEditingPet(null);
      setError(null);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to update pet');
    }
  };

  const handleDelete = async (id: number) => {
    if (!confirm('Are you sure you want to delete this pet?')) return;
    
    try {
      await petService.delete(id);
      await loadPets(searchTerm);
      setError(null);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to delete pet');
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
        <h1 className="text-3xl font-bold text-gray-900">Pets</h1>
        <div className="flex items-center gap-4">
          <div className="flex bg-gray-100 rounded-lg p-1">
            <button
              onClick={() => setViewMode('all')}
              className={`px-4 py-2 rounded-md transition-colors ${
                viewMode === 'all'
                  ? 'bg-indigo-600 text-white'
                  : 'text-gray-700 hover:bg-gray-200'
              }`}
            >
              Все питомцы
            </button>
            <button
              onClick={() => setViewMode('my')}
              className={`px-4 py-2 rounded-md transition-colors ${
                viewMode === 'my'
                  ? 'bg-indigo-600 text-white'
                  : 'text-gray-700 hover:bg-gray-200'
              }`}
            >
              Мои питомцы
            </button>
          </div>
          <button
            onClick={() => {
              setEditingPet(null);
              setShowForm(true);
            }}
            className="bg-indigo-600 text-white px-4 py-2 rounded-md hover:bg-indigo-700 transition-colors"
          >
            + Add Pet
          </button>
        </div>
      </div>

      {error && (
        <div className="mb-4 bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
          {error}
        </div>
      )}

      <div className="mb-4 space-y-4">
        <div className="relative">
          <input
            ref={searchInputRef}
            type="text"
            placeholder="Search pets by name, breed, or owner..."
            value={searchTerm}
            onChange={(e) => {
              setSearchTerm(e.target.value);
              setShowSearchDropdown(true);
            }}
            onFocus={() => {
              if (filteredPets.length > 0) {
                setShowSearchDropdown(true);
              }
            }}
            className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
          />
          {showSearchDropdown && filteredPets.length > 0 && searchTerm.trim() && (
            <div
              ref={searchDropdownRef}
              className="absolute z-10 w-full mt-1 bg-white border border-gray-300 rounded-md shadow-lg max-h-60 overflow-auto"
            >
              {filteredPets.map((pet) => (
                <div
                  key={pet.id}
                  onClick={() => {
                    setSearchTerm(pet.name);
                    setShowSearchDropdown(false);
                  }}
                  className="px-4 py-2 hover:bg-indigo-50 cursor-pointer border-b border-gray-100 last:border-b-0"
                >
                  <div className="font-medium">{pet.name}</div>
                  <div className="text-sm text-gray-500">
                    {pet.breed} • {pet.type} {pet.ownerName && `• ${pet.ownerName}`}
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
        <div className="bg-white p-4 rounded-lg shadow flex gap-4">
          <div className="flex-1">
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Filter by Type
            </label>
            <select
              value={filterType}
              onChange={(e) => {
                setFilterType(e.target.value);
                // Обновляем URL параметр
                if (e.target.value) {
                  setSearchParams({ type: e.target.value });
                } else {
                  setSearchParams({});
                }
              }}
              className="w-full px-3 py-2 border border-gray-300 rounded-md"
            >
              <option value="">All Types</option>
              <option value="DOG">Dog</option>
              <option value="CAT">Cat</option>
              <option value="BIRD">Bird</option>
              <option value="FISH">Fish</option>
              <option value="RABBIT">Rabbit</option>
              <option value="HAMSTER">Hamster</option>
              <option value="OTHER">Other</option>
            </select>
          </div>
          {viewMode === 'all' && (
            <div className="flex-1">
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Filter by Owner
              </label>
              <select
                value={filterOwnerId || ''}
                onChange={(e) => setFilterOwnerId(e.target.value ? Number(e.target.value) : undefined)}
                className="w-full px-3 py-2 border border-gray-300 rounded-md"
              >
                <option value="">All Owners</option>
                {owners.map((owner) => (
                  <option key={owner.id} value={owner.id}>
                    {owner.firstName} {owner.lastName}
                  </option>
                ))}
              </select>
            </div>
          )}
        </div>
      </div>

      {showForm && (
        <PetForm
          pet={null}
          owners={owners}
          onSubmit={handleCreate}
          onCancel={() => setShowForm(false)}
        />
      )}

      {editingPet && (
        <PetForm
          pet={editingPet}
          owners={owners}
          onSubmit={(data) => handleUpdate(editingPet.id!, data)}
          onCancel={() => setEditingPet(null)}
        />
      )}

      <PetList
        pets={searchTerm.trim() ? filteredPets : pets}
        onEdit={setEditingPet}
        onDelete={handleDelete}
        isAdmin={isAdmin}
        canEditDelete={viewMode === 'my'} // В режиме "Мои питомцы" показываем кнопки Edit/Delete
      />
    </div>
  );
}

export default Pets;

