package com.yourpackage.userprofile.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yourpackage.userprofile.data.models.User
import com.yourpackage.userprofile.data.repository.UserRepository

class UserViewModel : ViewModel() {

    private val repository = UserRepository()

    private val _userProfile = MutableLiveData<User>()
    val userProfile: LiveData<User> get() = _userProfile

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        val user = repository.getUserProfile()
        _userProfile.value = user
    }

    fun updateUser(updatedUser: User) {
        if (repository.updateUserProfile(updatedUser)) {
            _userProfile.value = updatedUser
        }
    }
}
