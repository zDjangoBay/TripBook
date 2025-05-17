// components/UserProfile.jsx
import React from 'react';

const UserProfile = ({ user }) => {
  return (
    <div className="max-w-2xl mx-auto p-6 bg-white rounded-2xl shadow-md">
      <div className="flex items-center space-x-6">
        <img
          src={user.avatar || '/default-avatar.png'}
          alt="User Avatar"
          className="w-24 h-24 rounded-full object-cover border"
        />
        <div>
          <h2 className="text-2xl font-bold">{user.name}</h2>
          <p className="text-gray-600">{user.email}</p>
        </div>
      </div>
      <div className="mt-6">
        <h3 className="text-lg font-semibold mb-2">About</h3>
        <p className="text-gray-700">{user.bio || 'No bio added yet.'}</p>
      </div>
    </div>
  );
};

export default UserProfile;
