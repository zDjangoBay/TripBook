// pages/profile.js
import React from 'react';
import UserProfile from '../components/UserProfile';

const dummyUser = {
  name: 'Jane Doe',
  email: 'jane.doe@example.com',
  avatar: 'https://i.pravatar.cc/150?img=47',
  bio: 'Traveler, photographer, and avid TripBook user.',
};

const ProfilePage = () => {
  return (
    <main className="min-h-screen bg-gray-100 py-10 px-4">
      <UserProfile user={dummyUser} />
    </main>
  );
};

export default ProfilePage;
