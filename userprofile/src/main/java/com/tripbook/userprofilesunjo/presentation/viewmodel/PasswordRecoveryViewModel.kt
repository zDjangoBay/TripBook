package com.tripbook.userprofilesunjo.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tripbook.userprofilesunjo.data.repository.PasswordRecoveryRepositoryImpl
import com.tripbook.userprofilesunjo.domain.usecase.RequestPasswordResetUseCase
import com.tripbook.userprofilesunjo.domain.usecase.VerifyOtpUseCase
import com.tripbook.userprofilesunjo.domain.usecase.ResetPasswordUseCase
import kotlinx.coroutines.launch

data class PasswordRecoveryUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isEmailSent: Boolean = false,
    val isOtpVerified: Boolean = false,
    val isPasswordReset: Boolean = false,
    val email: String = "",
    val resetToken: String = ""
)

class PasswordRecoveryViewModel : ViewModel() {

    private val repository = PasswordRecoveryRepositoryImpl()
    private val requestPasswordResetUseCase = RequestPasswordResetUseCase(repository)
    private val verifyOtpUseCase = VerifyOtpUseCase(repository)
    private val resetPasswordUseCase = ResetPasswordUseCase(repository)

    var uiState by mutableStateOf(PasswordRecoveryUiState())
        private set

    fun requestPasswordReset(email: String) {
        uiState = uiState.copy(isLoading = true, error = null)

        viewModelScope.launch {
            requestPasswordResetUseCase(email)
                .onSuccess { response ->
                    uiState = uiState.copy(
                        isLoading = false,
                        isEmailSent = true,
                        email = email,
                        error = null
                    )
                }
                .onFailure { exception ->
                    uiState = uiState.copy(
                        isLoading = false,
                        error = exception.message ?: "An error occurred"
                    )
                }
        }
    }

    fun setEmail(email: String) {
        uiState = uiState.copy(email = email)
    }

    fun verifyOtp(otp: String) {
        uiState = uiState.copy(isLoading = true, error = null)

        viewModelScope.launch {
            verifyOtpUseCase(uiState.email, otp)
                .onSuccess { response ->
                    uiState = uiState.copy(
                        isLoading = false,
                        isOtpVerified = true,
                        resetToken = response.token ?: "",
                        error = null
                    )
                }
                .onFailure { exception ->
                    uiState = uiState.copy(
                        isLoading = false,
                        error = exception.message ?: "An error occurred"
                    )
                }
        }
    }

    fun verifyOtpWithEmail(email: String, otp: String) {
        uiState = uiState.copy(isLoading = true, error = null, email = email)

        viewModelScope.launch {
            verifyOtpUseCase(email, otp)
                .onSuccess { response ->
                    uiState = uiState.copy(
                        isLoading = false,
                        isOtpVerified = true,
                        resetToken = response.token ?: "",
                        error = null
                    )
                }
                .onFailure { exception ->
                    uiState = uiState.copy(
                        isLoading = false,
                        error = exception.message ?: "An error occurred"
                    )
                }
        }
    }

    fun resetPassword(newPassword: String, confirmPassword: String) {
        uiState = uiState.copy(isLoading = true, error = null)

        viewModelScope.launch {
            resetPasswordUseCase(uiState.email, uiState.resetToken, newPassword, confirmPassword)
                .onSuccess { response ->
                    uiState = uiState.copy(
                        isLoading = false,
                        isPasswordReset = true,
                        error = null
                    )
                }
                .onFailure { exception ->
                    uiState = uiState.copy(
                        isLoading = false,
                        error = exception.message ?: "An error occurred"
                    )
                }
        }
    }

    fun clearError() {
        uiState = uiState.copy(error = null)
    }

    fun resetState() {
        uiState = PasswordRecoveryUiState()
    }
}
