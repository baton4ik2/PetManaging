import { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { userService } from '../services/authService';
import { formatPhoneNumber, normalizePhoneNumber } from '../utils/phoneUtils';
import { petService, ownerService } from '../services/api';
import type { Pet, Owner } from '../services/api';
import PetForm from '../components/PetForm';

interface UserProfile {
  id: number;
  username: string;
  email: string;
  firstName?: string;
  lastName?: string;
  phone?: string;
  roles: string[];
  enabled: boolean;
  createdAt: string;
  updatedAt: string;
}

function Profile() {
  const { user, updateUser } = useAuth();
  const [profile, setProfile] = useState<UserProfile | null>(null);
  const [isEditing, setIsEditing] = useState(false);
  const [email, setEmail] = useState('');
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [phone, setPhone] = useState('');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [isLoading, setIsLoading] = useState(true);
  const [isSaving, setIsSaving] = useState(false);

  // Password change
  const [isChangingPassword, setIsChangingPassword] = useState(false);
  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [passwordError, setPasswordError] = useState('');
  const [passwordSuccess, setPasswordSuccess] = useState('');

  // My Pets
  const [myPets, setMyPets] = useState<Pet[]>([]);
  const [petsLoading, setPetsLoading] = useState(false);
  const [editingPet, setEditingPet] = useState<Pet | null>(null);
  const [showPetForm, setShowPetForm] = useState(false);
  const [owners, setOwners] = useState<Owner[]>([]);

  useEffect(() => {
    loadProfile();
    loadMyPets();
    loadOwners();
  }, []);

  const loadProfile = async () => {
    try {
      setIsLoading(true);
      const response = await userService.getProfile();
      // Если данные из ответа неполные, используем данные из AuthContext или localStorage
      const profileData = response.data;
      
      // Проверяем localStorage напрямую для получения правильного email
      const storedUserStr = localStorage.getItem('auth_user');
      let storedUser = null;
      if (storedUserStr) {
        try {
          storedUser = JSON.parse(storedUserStr);
        } catch (e) {
          // ignore
        }
      }
      
      if (user && (!profileData.username || profileData.username === 'user')) {
        profileData.username = user.username;
      }
      
      // Используем email из localStorage, если он не содержит '@example.com'
      if (storedUser && storedUser.email && !storedUser.email.includes('@example.com')) {
        profileData.email = storedUser.email;
      } else if (user && (!profileData.email || profileData.email.includes('@example.com'))) {
        // Если в localStorage нет правильного email, используем из AuthContext
        if (user.email && !user.email.includes('@example.com')) {
          profileData.email = user.email;
        }
      }
      
      setProfile(profileData);
      setEmail(profileData.email);
      setFirstName(profileData.firstName || '');
      setLastName(profileData.lastName || '');
      // Форматируем номер телефона при загрузке, если он уже в БД в правильном формате
      setPhone(profileData.phone || '');
    } catch (err: any) {
      setError('Failed to load profile');
      // Если запрос не удался, используем данные из AuthContext или localStorage
      const storedUserStr = localStorage.getItem('auth_user');
      let storedUser = null;
      if (storedUserStr) {
        try {
          storedUser = JSON.parse(storedUserStr);
        } catch (e) {
          // ignore
        }
      }
      
      if (user || storedUser) {
        const fallbackProfile: UserProfile = {
          id: 1,
          username: (user || storedUser)?.username || 'user',
          email: (storedUser?.email && !storedUser.email.includes('@example.com')) 
            ? storedUser.email 
            : (user?.email && !user.email.includes('@example.com'))
              ? user.email
              : (user || storedUser)?.email || 'user@example.com',
          roles: (user || storedUser)?.roles || ['USER'],
          enabled: true,
          createdAt: new Date().toISOString(),
          updatedAt: new Date().toISOString(),
        };
        setProfile(fallbackProfile);
        setEmail(fallbackProfile.email);
      }
    } finally {
      setIsLoading(false);
    }
  };

  const handleUpdateProfile = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    setIsSaving(true);

    try {
      // Нормализуем номер телефона перед отправкой
      const normalizedPhone = normalizePhoneNumber(phone);
      const response = await userService.updateProfile(email, firstName, lastName, normalizedPhone);
      setProfile(response.data);
      // Update auth context and localStorage
      const updatedUser = {
        username: response.data.username,
        email: response.data.email,
        roles: response.data.roles,
      };
      updateUser(updatedUser);
      // Также обновляем localStorage напрямую, чтобы email сохранился
      localStorage.setItem('auth_user', JSON.stringify(updatedUser));
      setSuccess('Profile updated successfully');
      setIsEditing(false);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to update profile');
    } finally {
      setIsSaving(false);
    }
  };

  const loadMyPets = async () => {
    try {
      setPetsLoading(true);
      const response = await petService.getMyPets();
      setMyPets(response.data);
    } catch (err: any) {
      console.error('Failed to load pets:', err);
    } finally {
      setPetsLoading(false);
    }
  };

  const loadOwners = async () => {
    try {
      const response = await ownerService.getAll();
      setOwners(response.data);
    } catch (err) {
      console.error('Failed to load owners:', err);
    }
  };

  const handleUpdatePet = async (id: number, petData: Omit<Pet, 'id' | 'createdAt' | 'updatedAt' | 'ownerName'>) => {
    try {
      await petService.update(id, petData);
      await loadMyPets();
      setEditingPet(null);
      setShowPetForm(false);
      setSuccess('Pet updated successfully');
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to update pet');
    }
  };

  const handleDeletePet = async (id: number) => {
    if (!confirm('Are you sure you want to delete this pet?')) return;
    
    try {
      await petService.delete(id);
      await loadMyPets();
      setSuccess('Pet deleted successfully');
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to delete pet');
    }
  };

  const handleChangePassword = async (e: React.FormEvent) => {
    e.preventDefault();
    setPasswordError('');
    setPasswordSuccess('');

    if (newPassword !== confirmPassword) {
      setPasswordError('Passwords do not match');
      return;
    }

    if (newPassword.length < 6) {
      setPasswordError('Password must be at least 6 characters');
      return;
    }

    try {
      await userService.changePassword(currentPassword, newPassword);
      setPasswordSuccess('Password changed successfully');
      setCurrentPassword('');
      setNewPassword('');
      setConfirmPassword('');
      setIsChangingPassword(false);
    } catch (err: any) {
      setPasswordError(err.response?.data?.message || 'Failed to change password');
    }
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-lg text-gray-600">Loading...</div>
      </div>
    );
  }

  if (!profile) {
    return (
      <div className="px-4 py-6 sm:px-0">
        <div className="text-red-600">Failed to load profile</div>
      </div>
    );
  }

  return (
    <div className="px-4 py-6 sm:px-0">
      <div className="max-w-3xl mx-auto">
        <h1 className="text-3xl font-bold text-gray-900 mb-6">My Profile</h1>

        {/* Profile Information */}
        <div className="bg-white shadow rounded-lg p-6 mb-6">
          <div className="flex justify-between items-center mb-4">
            <h2 className="text-xl font-semibold text-gray-900">Profile Information</h2>
            {!isEditing && (
              <button
                onClick={() => setIsEditing(true)}
                className="px-4 py-2 bg-indigo-600 text-white rounded-md hover:bg-indigo-700"
              >
                Edit Profile
              </button>
            )}
          </div>

          {error && (
            <div className="mb-4 p-3 bg-red-50 border border-red-200 rounded-md text-red-800 text-sm">
              {error}
            </div>
          )}

          {success && (
            <div className="mb-4 p-3 bg-green-50 border border-green-200 rounded-md text-green-800 text-sm">
              {success}
            </div>
          )}

          {isEditing ? (
            <form onSubmit={handleUpdateProfile}>
              <div className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Username
                  </label>
                  <input
                    type="text"
                    value={profile.username}
                    disabled
                    className="w-full px-3 py-2 border border-gray-300 rounded-md bg-gray-50 text-gray-500"
                  />
                  <p className="mt-1 text-xs text-gray-500">Username cannot be changed</p>
                </div>

                <div>
                  <label htmlFor="firstName" className="block text-sm font-medium text-gray-700 mb-1">
                    First Name
                  </label>
                  <input
                    id="firstName"
                    type="text"
                    value={firstName}
                    onChange={(e) => setFirstName(e.target.value)}
                    required
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
                  />
                </div>

                <div>
                  <label htmlFor="lastName" className="block text-sm font-medium text-gray-700 mb-1">
                    Last Name
                  </label>
                  <input
                    id="lastName"
                    type="text"
                    value={lastName}
                    onChange={(e) => setLastName(e.target.value)}
                    required
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
                  />
                </div>

                <div>
                  <label htmlFor="phone" className="block text-sm font-medium text-gray-700 mb-1">
                    Phone Number
                  </label>
                  <input
                    id="phone"
                    type="tel"
                    value={phone}
                    onChange={(e) => {
                      const formatted = formatPhoneNumber(e.target.value);
                      setPhone(formatted);
                    }}
                    required
                    placeholder="+7 937 286 78 88"
                    maxLength={18}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
                  />
                </div>

                <div>
                  <label htmlFor="email" className="block text-sm font-medium text-gray-700 mb-1">
                    Email
                  </label>
                  <input
                    id="email"
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Roles
                  </label>
                  <div className="flex flex-wrap gap-2">
                    {profile.roles.map((role) => (
                      <span
                        key={role}
                        className="px-2 py-1 bg-indigo-100 text-indigo-800 text-xs font-medium rounded"
                      >
                        {role}
                      </span>
                    ))}
                  </div>
                </div>

                <div className="flex space-x-3">
                  <button
                    type="submit"
                    disabled={isSaving}
                    className="px-4 py-2 bg-indigo-600 text-white rounded-md hover:bg-indigo-700 disabled:opacity-50"
                  >
                    {isSaving ? 'Saving...' : 'Save Changes'}
                  </button>
                  <button
                    type="button"
                    onClick={() => {
                      setIsEditing(false);
                      setEmail(profile.email);
                      setFirstName(profile.firstName || '');
                      setLastName(profile.lastName || '');
                      setPhone(profile.phone || '');
                      setError('');
                      setSuccess('');
                    }}
                    className="px-4 py-2 bg-gray-200 text-gray-700 rounded-md hover:bg-gray-300"
                  >
                    Cancel
                  </button>
                </div>
              </div>
            </form>
          ) : (
            <div className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-500 mb-1">Username</label>
                <p className="text-gray-900">{profile.username}</p>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-500 mb-1">First Name</label>
                <p className="text-gray-900">{profile.firstName || 'Not set'}</p>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-500 mb-1">Last Name</label>
                <p className="text-gray-900">{profile.lastName || 'Not set'}</p>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-500 mb-1">Phone Number</label>
                <p className="text-gray-900">{profile.phone || 'Not set'}</p>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-500 mb-1">Email</label>
                <p className="text-gray-900">{profile.email}</p>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-500 mb-1">Roles</label>
                <div className="flex flex-wrap gap-2">
                  {profile.roles.map((role) => (
                    <span
                      key={role}
                      className="px-2 py-1 bg-indigo-100 text-indigo-800 text-xs font-medium rounded"
                    >
                      {role}
                    </span>
                  ))}
                </div>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-500 mb-1">Account Status</label>
                <p className="text-gray-900">
                  <span className={`px-2 py-1 rounded text-xs font-medium ${
                    profile.enabled 
                      ? 'bg-green-100 text-green-800' 
                      : 'bg-red-100 text-red-800'
                  }`}>
                    {profile.enabled ? 'Active' : 'Disabled'}
                  </span>
                </p>
              </div>

              <div className="grid grid-cols-2 gap-4 pt-4 border-t">
                <div>
                  <label className="block text-sm font-medium text-gray-500 mb-1">Created At</label>
                  <p className="text-gray-900 text-sm">
                    {new Date(profile.createdAt).toLocaleString()}
                  </p>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-500 mb-1">Last Updated</label>
                  <p className="text-gray-900 text-sm">
                    {new Date(profile.updatedAt).toLocaleString()}
                  </p>
                </div>
              </div>
            </div>
          )}
        </div>

        {/* My Pets */}
        <div className="bg-white shadow rounded-lg p-6 mb-6">
          <h2 className="text-xl font-semibold text-gray-900 mb-4">My Pets</h2>
          {petsLoading ? (
            <div className="text-center py-8">
              <div className="text-gray-500">Loading pets...</div>
            </div>
          ) : myPets.length === 0 ? (
            <div className="text-center py-8">
              <p className="text-gray-500">You don't have any pets yet.</p>
            </div>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {myPets.map((pet) => (
                <div key={pet.id} className="border border-gray-200 rounded-lg p-4 hover:shadow-md transition-shadow relative">
                  <div className="absolute top-2 right-2 flex gap-1">
                    <button
                      onClick={() => {
                        setEditingPet(pet);
                        setShowPetForm(true);
                      }}
                      className="p-1.5 text-indigo-600 hover:bg-indigo-50 rounded transition-colors"
                      title="Edit pet"
                    >
                      <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                      </svg>
                    </button>
                    <button
                      onClick={() => handleDeletePet(pet.id!)}
                      className="p-1.5 text-red-600 hover:bg-red-50 rounded transition-colors"
                      title="Delete pet"
                    >
                      <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                      </svg>
                    </button>
                  </div>
                  <div className="flex items-center mb-2 pr-8">
                    <span className="text-2xl mr-2">
                      {pet.type === 'DOG' ? '🐕' : 
                       pet.type === 'CAT' ? '🐈' : 
                       pet.type === 'BIRD' ? '🐦' : 
                       pet.type === 'FISH' ? '🐠' : 
                       pet.type === 'RABBIT' ? '🐰' : 
                       pet.type === 'HAMSTER' ? '🐹' : '🐾'}
                    </span>
                    <h3 className="text-lg font-medium text-gray-900">{pet.name}</h3>
                  </div>
                  <div className="text-sm text-gray-600 space-y-1">
                    <p><span className="font-medium">Breed:</span> {pet.breed}</p>
                    <p><span className="font-medium">Type:</span> {pet.type}</p>
                    <p><span className="font-medium">Born:</span> {new Date(pet.dateOfBirth).toLocaleDateString()}</p>
                    {pet.age !== undefined && (
                      <p><span className="font-medium">Age:</span> {pet.age} {pet.age === 1 ? 'year' : 'years'} old</p>
                    )}
                    {pet.color && <p><span className="font-medium">Color:</span> {pet.color}</p>}
                  </div>
                  {pet.description && (
                    <p className="mt-2 text-sm text-gray-500">{pet.description}</p>
                  )}
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Pet Form Modal */}
        {showPetForm && editingPet && (
          <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
            <div className="bg-white rounded-lg max-w-2xl w-full max-h-[90vh] overflow-y-auto">
              <PetForm
                pet={editingPet}
                owners={owners}
                onSubmit={(data) => handleUpdatePet(editingPet.id!, data)}
                onCancel={() => {
                  setShowPetForm(false);
                  setEditingPet(null);
                }}
              />
            </div>
          </div>
        )}

        {/* Change Password */}
        <div className="bg-white shadow rounded-lg p-6">
          <div className="flex justify-between items-center mb-4">
            <h2 className="text-xl font-semibold text-gray-900">Change Password</h2>
            {!isChangingPassword && (
              <button
                onClick={() => setIsChangingPassword(true)}
                className="px-4 py-2 bg-indigo-600 text-white rounded-md hover:bg-indigo-700"
              >
                Change Password
              </button>
            )}
          </div>

          {isChangingPassword && (
            <form onSubmit={handleChangePassword}>
              {passwordError && (
                <div className="mb-4 p-3 bg-red-50 border border-red-200 rounded-md text-red-800 text-sm">
                  {passwordError}
                </div>
              )}

              {passwordSuccess && (
                <div className="mb-4 p-3 bg-green-50 border border-green-200 rounded-md text-green-800 text-sm">
                  {passwordSuccess}
                </div>
              )}

              <div className="space-y-4">
                <div>
                  <label htmlFor="currentPassword" className="block text-sm font-medium text-gray-700 mb-1">
                    Current Password
                  </label>
                  <input
                    id="currentPassword"
                    type="password"
                    value={currentPassword}
                    onChange={(e) => setCurrentPassword(e.target.value)}
                    required
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
                  />
                </div>

                <div>
                  <label htmlFor="newPassword" className="block text-sm font-medium text-gray-700 mb-1">
                    New Password
                  </label>
                  <input
                    id="newPassword"
                    type="password"
                    value={newPassword}
                    onChange={(e) => setNewPassword(e.target.value)}
                    required
                    minLength={6}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
                  />
                </div>

                <div>
                  <label htmlFor="confirmPassword" className="block text-sm font-medium text-gray-700 mb-1">
                    Confirm New Password
                  </label>
                  <input
                    id="confirmPassword"
                    type="password"
                    value={confirmPassword}
                    onChange={(e) => setConfirmPassword(e.target.value)}
                    required
                    minLength={6}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
                  />
                </div>

                <div className="flex space-x-3">
                  <button
                    type="submit"
                    className="px-4 py-2 bg-indigo-600 text-white rounded-md hover:bg-indigo-700"
                  >
                    Change Password
                  </button>
                  <button
                    type="button"
                    onClick={() => {
                      setIsChangingPassword(false);
                      setCurrentPassword('');
                      setNewPassword('');
                      setConfirmPassword('');
                      setPasswordError('');
                      setPasswordSuccess('');
                    }}
                    className="px-4 py-2 bg-gray-200 text-gray-700 rounded-md hover:bg-gray-300"
                  >
                    Cancel
                  </button>
                </div>
              </div>
            </form>
          )}
        </div>
      </div>
    </div>
  );
}

export default Profile;

