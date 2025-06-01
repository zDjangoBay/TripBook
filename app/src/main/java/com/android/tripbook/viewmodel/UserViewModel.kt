package com.android.tripbook.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.tripbook.data.UserRepository
import com.android.tripbook.data.model.User
import com.android.tripbook.util.Resource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * ViewModel for user-related operations
 */
class UserViewModel(private val repository: UserRepository) : ViewModel() {
    
    // Current user profile
    private val _currentUserState = MutableLiveData<Resource<User>>()
    val currentUserState: LiveData<Resource<User>> = _currentUserState
    
    // User profile update state
    private val _updateProfileState = MutableLiveData<Resource<Boolean>>()
    val updateProfileState: LiveData<Resource<Boolean>> = _updateProfileState
    
    // All users for display in search results
    private val _usersState = MutableLiveData<Resource<List<User>>>()
    val usersState: LiveData<Resource<List<User>>> = _usersState
    
    /**
     * Load the current user's profile
     */
    fun loadCurrentUserProfile() {
        _currentUserState.value = Resource.Loading()
        
        viewModelScope.launch {
            try {
                val user = repository.getCurrentUser()
                _currentUserState.value = Resource.Success(user)
            } catch (e: Exception) {
                _currentUserState.value = Resource.Error("Failed to load user profile: ${e.message}")
            }
        }
    }
    
    /**
     * Update the current user's profile
     */
    fun updateProfile(displayName: String, bio: String?, profileImage: String?) {
        _updateProfileState.value = Resource.Loading()
        
        viewModelScope.launch {
            try {
                val success = repository.updateUserProfile(
                    displayName = displayName,
                    bio = bio,
                    profileImage = profileImage
                )
                
                if (success) {
                    _updateProfileState.value = Resource.Success(true)
                    // Refresh the user profile
                    loadCurrentUserProfile()
                } else {
                    _updateProfileState.value = Resource.Error("Failed to update profile")
                }
            } catch (e: Exception) {
                _updateProfileState.value = Resource.Error("Error updating profile: ${e.message}")
            }
        }
    }
    
    /**
     * Search for users by name or username
     */
    fun searchUsers(query: String) {
        _usersState.value = Resource.Loading()
        
        viewModelScope.launch {
            try {
                repository.searchUsers(query).collectLatest { users ->
                    _usersState.value = Resource.Success(users)
                }
            } catch (e: Exception) {
                _usersState.value = Resource.Error("Error searching users: ${e.message}")
            }
        }
    }
}
