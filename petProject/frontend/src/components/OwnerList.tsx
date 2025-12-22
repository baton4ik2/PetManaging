import type { Owner } from '../services/api';

interface OwnerListProps {
  owners: Owner[];
  onEdit: (owner: Owner) => void;
  onDelete: (id: number) => void;
}

function OwnerList({ owners, onEdit, onDelete }: OwnerListProps) {
  if (owners.length === 0) {
    return (
      <div className="text-center py-12 bg-white rounded-lg shadow">
        <p className="text-gray-500 text-lg">No owners found. Add your first owner!</p>
      </div>
    );
  }

  return (
    <div className="bg-white shadow overflow-hidden sm:rounded-md">
      <ul className="divide-y divide-gray-200">
        {owners.map((owner) => (
          <li key={owner.id} className="px-6 py-4 hover:bg-gray-50">
            <div className="flex items-center justify-between">
              <div className="flex-1">
                <div className="flex items-center">
                  <h3 className="text-lg font-medium text-gray-900">
                    {owner.firstName} {owner.lastName}
                  </h3>
                </div>
                <div className="mt-2 text-sm text-gray-500">
                  <p>📧 {owner.email}</p>
                  <p>📞 {owner.phone}</p>
                  <p>📍 {owner.address}</p>
                  {owner.pets && owner.pets.length > 0 && (
                    <p className="mt-1">🐾 {owner.pets.length} pet(s)</p>
                  )}
                </div>
              </div>
              <div className="flex space-x-2">
                <button
                  onClick={() => onEdit(owner)}
                  className="px-3 py-1 bg-indigo-600 text-white text-sm rounded hover:bg-indigo-700"
                >
                  Edit
                </button>
                <button
                  onClick={() => onDelete(owner.id!)}
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

export default OwnerList;

