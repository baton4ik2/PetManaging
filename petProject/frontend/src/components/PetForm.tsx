import { useState, useEffect, useRef } from 'react';
import type { Pet, Owner } from '../services/api';

interface PetFormProps {
  pet: Pet | null;
  owners: Owner[];
  onSubmit: (data: Omit<Pet, 'id' | 'createdAt' | 'updatedAt' | 'ownerName'>) => void;
  onCancel: () => void;
}

function PetForm({ pet, owners, onSubmit, onCancel }: PetFormProps) {
  const [formData, setFormData] = useState({
    name: '',
    type: 'DOG' as Pet['type'],
    breed: '',
    dateOfBirth: '',
    color: '',
    description: '',
    ownerId: 0,
  });
  const [ownerSearch, setOwnerSearch] = useState('');
  const [showOwnerDropdown, setShowOwnerDropdown] = useState(false);
  const ownerInputRef = useRef<HTMLInputElement>(null);
  const ownerDropdownRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (pet) {
      const owner = owners.find(o => o.id === pet.ownerId);
      setFormData({
        name: pet.name,
        type: pet.type,
        breed: pet.breed,
        dateOfBirth: pet.dateOfBirth,
        color: pet.color || '',
        description: pet.description || '',
        ownerId: pet.ownerId,
      });
      setOwnerSearch(owner ? `${owner.firstName} ${owner.lastName}` : '');
    } else {
      setFormData({
        name: '',
        type: 'DOG' as Pet['type'],
        breed: '',
        dateOfBirth: '',
        color: '',
        description: '',
        ownerId: 0,
      });
      setOwnerSearch('');
    }
  }, [pet, owners]);

  // Закрываем dropdown при клике вне его
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (
        ownerDropdownRef.current &&
        !ownerDropdownRef.current.contains(event.target as Node) &&
        ownerInputRef.current &&
        !ownerInputRef.current.contains(event.target as Node)
      ) {
        setShowOwnerDropdown(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);

  const filteredOwners = owners.filter(owner => {
    const fullName = `${owner.firstName} ${owner.lastName}`.toLowerCase();
    return fullName.includes(ownerSearch.toLowerCase());
  });

  const handleOwnerSelect = (owner: Owner) => {
    setFormData({ ...formData, ownerId: owner.id! });
    setOwnerSearch(`${owner.firstName} ${owner.lastName}`);
    setShowOwnerDropdown(false);
  };

  const handleOwnerInputChange = (value: string) => {
    setOwnerSearch(value);
    setShowOwnerDropdown(true);
    // Если введенное значение точно совпадает с одним из владельцев, устанавливаем его ID
    const exactMatch = owners.find(
      owner => `${owner.firstName} ${owner.lastName}`.toLowerCase() === value.toLowerCase()
    );
    if (exactMatch) {
      setFormData({ ...formData, ownerId: exactMatch.id! });
    } else {
      setFormData({ ...formData, ownerId: 0 });
    }
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!formData.ownerId) {
      alert('Please select an owner');
      return;
    }
    onSubmit(formData);
  };

  return (
    <div className="mb-6 bg-white p-6 rounded-lg shadow-md">
      <h2 className="text-2xl font-semibold mb-4">
        {pet ? 'Edit Pet' : 'Add New Pet'}
      </h2>
      <form onSubmit={handleSubmit} className="space-y-4">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Name *
            </label>
            <input
              type="text"
              required
              value={formData.name}
              onChange={(e) => setFormData({ ...formData, name: e.target.value })}
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Type *
            </label>
            <select
              required
              value={formData.type}
              onChange={(e) => setFormData({ ...formData, type: e.target.value as Pet['type'] })}
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
            >
              <option value="DOG">Dog</option>
              <option value="CAT">Cat</option>
              <option value="BIRD">Bird</option>
              <option value="FISH">Fish</option>
              <option value="RABBIT">Rabbit</option>
              <option value="HAMSTER">Hamster</option>
              <option value="OTHER">Other</option>
            </select>
          </div>
        </div>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Breed *
            </label>
            <input
              type="text"
              required
              value={formData.breed}
              onChange={(e) => setFormData({ ...formData, breed: e.target.value })}
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Date of Birth *
            </label>
            <input
              type="date"
              required
              value={formData.dateOfBirth}
              onChange={(e) => setFormData({ ...formData, dateOfBirth: e.target.value })}
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
            />
          </div>
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Color
          </label>
          <input
            type="text"
            value={formData.color}
            onChange={(e) => setFormData({ ...formData, color: e.target.value })}
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
          />
        </div>
        <div className="relative">
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Owner *
          </label>
          <input
            ref={ownerInputRef}
            type="text"
            required
            value={ownerSearch}
            onChange={(e) => handleOwnerInputChange(e.target.value)}
            onFocus={() => setShowOwnerDropdown(true)}
            placeholder="Search owner by name..."
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
          />
          {showOwnerDropdown && filteredOwners.length > 0 && (
            <div
              ref={ownerDropdownRef}
              className="absolute z-10 w-full mt-1 bg-white border border-gray-300 rounded-md shadow-lg max-h-60 overflow-auto"
            >
              {filteredOwners.map((owner) => (
                <div
                  key={owner.id}
                  onClick={() => handleOwnerSelect(owner)}
                  className="px-4 py-2 hover:bg-indigo-50 cursor-pointer border-b border-gray-100 last:border-b-0"
                >
                  {owner.firstName} {owner.lastName}
                </div>
              ))}
            </div>
          )}
          {showOwnerDropdown && ownerSearch && filteredOwners.length === 0 && (
            <div
              ref={ownerDropdownRef}
              className="absolute z-10 w-full mt-1 bg-white border border-gray-300 rounded-md shadow-lg"
            >
              <div className="px-4 py-2 text-gray-500">No owners found</div>
            </div>
          )}
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Description
          </label>
          <textarea
            value={formData.description}
            onChange={(e) => setFormData({ ...formData, description: e.target.value })}
            rows={3}
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
          />
        </div>
        <div className="flex justify-end space-x-3">
          <button
            type="button"
            onClick={onCancel}
            className="px-4 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50"
          >
            Cancel
          </button>
          <button
            type="submit"
            className="px-4 py-2 bg-indigo-600 text-white rounded-md hover:bg-indigo-700"
          >
            {pet ? 'Update' : 'Create'}
          </button>
        </div>
      </form>
    </div>
  );
}

export default PetForm;

