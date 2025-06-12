package com.tripbook.userprofile.fonchrisbright

sealed class RegistrationState {
    object Idle : RegistrationState()
    object Loading : RegistrationState()
    data class Success(val userId: String) : RegistrationState()
    data class Error(val message: String) : RegistrationState()
} 