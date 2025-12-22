import type { Pet } from '../services/api';

interface PetListProps {
  pets: Pet[];
  onEdit: (pet: Pet) => void;
  onDelete: (id: number) => void;
}

const typeEmojis: Record<Pet['type'], string> = {
  DOG: '🐕',
  CAT: '🐈',
  BIRD: '🐦',
  FISH: '🐠',
  RABBIT: '🐰',
  HAMSTER: '🐹',
  OTHER: '🐾',
};

function PetList({ pets, onEdit, onDelete }: PetListProps) {
  if (pets.length === 0) {
    return (
      <div className="text-center py-12 bg-white rounded-lg shadow">
        <p className="text-gray-500 text-lg">No pets found. Add your first pet!</p>
      </div>
    );
  }

  return (
    <div className="bg-white shadow overflow-hidden sm:rounded-md">
      <ul className="divide-y divide-gray-200">
        {pets.map((pet) => (
          <li key={pet.id} className="px-6 py-4 hover:bg-gray-50">
            <div className="flex items-center justify-between">
              <div className="flex-1">
                <div className="flex items-center">
                  <span className="text-2xl mr-2">{typeEmojis[pet.type]}</span>
                  <h3 className="text-lg font-medium text-gray-900">
                    {pet.name}
                  </h3>
                  <span className="ml-2 px-2 py-1 text-xs bg-indigo-100 text-indigo-800 rounded">
                    {pet.type}
                  </span>
                </div>
                <div className="mt-2 text-sm text-gray-500">
                  <p>🏷️ Breed: {pet.breed}</p>
                  <p>📅 Born: {new Date(pet.dateOfBirth).toLocaleDateString()}</p>
                  {pet.age !== undefined && (
                    <p>🎂 Age: {pet.age} {pet.age === 1 ? 'year' : 'years'} old</p>
                  )}
                  {pet.color && <p>🎨 Color: {pet.color}</p>}
                  {pet.ownerName && <p>👤 Owner: {pet.ownerName}</p>}
                  {pet.description && (
                    <p className="mt-1 text-gray-600">{pet.description}</p>
                  )}
                </div>
              </div>
              <div className="flex space-x-2">
                <button
                  onClick={() => onEdit(pet)}
                  className="px-3 py-1 bg-indigo-600 text-white text-sm rounded hover:bg-indigo-700"
                >
                  Edit
                </button>
                <button
                  onClick={() => onDelete(pet.id!)}
                  className="px-3 py-1 bg-red-600 text-white text-sm rounded hover:bg-red-700"
                >
                  Delete
                </button>
              </div>
            </div>
          </li>
        ))}
      </ul>
    </div>
  );
}

export default PetList;

