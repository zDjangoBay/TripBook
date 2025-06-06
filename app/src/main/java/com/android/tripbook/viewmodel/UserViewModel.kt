package com.android.tripbook.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.tripbook.repository.UserRepository
import com.android.tripbook.ui.uis.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel(
    private val userRepository: UserRepository = UserRepository.getInstance()
) : ViewModel() {

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun fetchUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val users = userRepository.getUsers()
                _users.value = users
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateUserName(userId: Int, newName: String) {
        viewModelScope.launch {
            try {
                userRepository.updateUserName(userId, newName)
                fetchUsers()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun updateUserDestination(userId: Int, newDestination: String) {
        viewModelScope.launch {
            try {
                userRepository.updateUserDestination(userId, newDestination)
                fetchUsers()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
}

