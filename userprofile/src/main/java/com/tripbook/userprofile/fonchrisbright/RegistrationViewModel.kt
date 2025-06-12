package com.tripbook.userprofile.fonchrisbright

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegistrationViewModel(private val repository: RegistrationRepository) : ViewModel() {
    private val _registrationState = MutableStateFlow<RegistrationState>(RegistrationState.Idle)
    val registrationState: StateFlow<RegistrationState> = _registrationState

    fun registerUser(email: String, password: String, name: String) {
        if (!RegistrationValidator.isValidEmail(email) || !RegistrationValidator.isValidPassword(password) || name.isBlank()) {
            _registrationState.value = RegistrationState.Error("Invalid input")
            return
        }
        viewModelScope.launch {
            _registrationState.value = RegistrationState.Loading
            val result = repository.registerUser(email, password, name)
            _registrationState.value = result
        }
    }
}

