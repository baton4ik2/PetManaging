import type { Owner } from '../services/api';

interface OwnerListProps {
  owners: Owner[];
  onEdit: (owner: Owner) => void;
  onDelete: (id: number) => void;
  isAdmin: boolean;
}

// Маскировка номера телефона: +7 999 999 99 99 -> +7-9**-***-**-99
function maskPhone(phone: string): string {
  if (!phone) return '';
  // Извлекаем только цифры
  const digits = phone.replace(/\D/g, '');
  if (digits.length < 11) return phone; // Если номер некорректный, возвращаем как есть
  
  // Нормализуем: если начинается с 8, заменяем на 7
  const normalized = digits.startsWith('8') ? '7' + digits.slice(1) : digits;
  
  // Первая цифра после 7 (индекс 1)
  const firstDigit = normalized[1] || '*';
  // Последние 2 цифры
  const lastTwo = normalized.slice(-2);
  
  // Форматируем: +7-9**-***-**-99
  return `+7-${firstDigit}**-***-**-${lastTwo}`;
}

// Извлечение города из адреса (берем первую часть до запятой, или весь адрес если запятой нет)
function getCity(address: string): string {
  if (!address) return '';
  const parts = address.split(',');
  return parts[0].trim();
}

function OwnerList({ owners, onEdit, onDelete, isAdmin }: OwnerListProps) {
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
                  <p>📞 {isAdmin ? owner.phone : maskPhone(owner.phone)}</p>
                  <p>📍 {isAdmin ? owner.address : getCity(owner.address)}</p>
                  {owner.pets && owner.pets.length > 0 && (
                    <p className="mt-1">🐾 {owner.pets.length} pet(s)</p>
                  )}
                </div>
              </div>
              {isAdmin && (
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
              )}
            </div>
          </li>
        ))}
      </ul>
    </div>
  );
}

export default OwnerList;

