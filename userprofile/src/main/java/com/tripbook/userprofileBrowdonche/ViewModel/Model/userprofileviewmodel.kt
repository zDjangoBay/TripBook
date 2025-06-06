package com.tripbook.userprofileBrowdonche.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UserProfileViewModel(private val repository: UserProfileRepository) : ViewModel() {
    private val _userProfile = MutableLiveData<UserProfile>()
    val userProfile: LiveData<UserProfile> get() = _userProfile

    fun fetchUserProfile(userId: String) {
        // Fetch user profile from the repository
        _userProfile.value = repository.getUserProfile(userId)
    }
}