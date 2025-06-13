package com.tripbook.userprofileMbahDavid.presentation.auth.viewmodels

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import android.net.Uri
import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.tripbook.userprofileMbahDavid.domain.model.UserRegistrationData
import com.tripbook.userprofileMbahDavid.util.ValidationResult
import com.tripbook.userprofileMbahDavid.util.ValidationUtil

class RegistrationViewModel : ViewModel() {
    private val _registrationData = MutableStateFlow(UserRegistrationData())
    val registrationData: StateFlow<UserRegistrationData> = _registrationData.asStateFlow()

    var currentStep by mutableStateOf(0)
        private set

    val totalSteps = 4

    // Form validation states
    var nameValidation by mutableStateOf(ValidationResult(true))
        private set
    var emailValidation by mutableStateOf(ValidationResult(true))
        private set
    var phoneValidation by mutableStateOf(ValidationResult(true))
        private set
    var passwordValidation by mutableStateOf(ValidationResult(true))
        private set
    var confirmPasswordValidation by mutableStateOf(ValidationResult(true))
        private set
    var bioValidation by mutableStateOf(ValidationResult(true))
        private set

    private val _latestTempUri = MutableStateFlow<Uri?>(null)
    val latestTempUri: StateFlow<Uri?> = _latestTempUri.asStateFlow()

    // Form field touch states
    private val _fieldTouchStates = MutableStateFlow(FieldTouchStates())
    val fieldTouchStates = _fieldTouchStates.asStateFlow()

    val isCurrentStepValid = derivedStateOf {
        when (currentStep) {
            0 -> nameValidation.isValid && emailValidation.isValid && 
                 phoneValidation.isValid && passwordValidation.isValid && 
                 confirmPasswordValidation.isValid
            1 -> true
            2 -> bioValidation.isValid
            3 -> true
            else -> false
        }
    }

    fun updateBasicInfo(name: String, email: String, phone: String, 
                       password: String, confirmPassword: String) {
        _registrationData.update { it.copy(
            fullName = name,
            email = email,
            phone = phone,
            password = password,
            confirmPassword = confirmPassword
        )}
        validateAllBasicInfo()
    }

    fun createImageUri(context: Context): Uri? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            .format(Date())
        val imageFile = File.createTempFile(
            "TRIPBOOK_${timeStamp}_",
            ".jpg",
            context.getExternalFilesDir("profile_pictures")
        )
        return androidx.core.content.FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            imageFile
        ).also { _latestTempUri.value = it }
    }

    fun handleCameraResult(success: Boolean) {
        if (success) {
            _latestTempUri.value?.toString()?.let { updateProfilePicture(it) }
        }
    }

    // Navigation methods
    fun goToNextStep() {
        if (isCurrentStepValid.value && currentStep < totalSteps - 1) {
            currentStep++
        }
    }

    fun goToPreviousStep() {
        if (currentStep > 0) currentStep--
    }

    private fun validateAllBasicInfo() {
        val data = _registrationData.value
        nameValidation = ValidationUtil.validateName(data.fullName)
        emailValidation = ValidationUtil.validateEmail(data.email)
        phoneValidation = ValidationUtil.validatePhone(data.phone)
        passwordValidation = ValidationUtil.validatePassword(data.password)
        confirmPasswordValidation = ValidationUtil.validateConfirmPassword(
            data.password, data.confirmPassword
        )
    }

    // Update methods
    fun updateProfilePicture(uri: String?) {
        _registrationData.update { it.copy(profilePictureUri = uri) }
    }

    fun updateBio(bio: String) {
        _registrationData.update { it.copy(bio = bio) }
        bioValidation = ValidationUtil.validateBio(bio)
    }

    fun updateTravelPreferences(preferences: List<String>) {
        _registrationData.update { it.copy(travelPreferences = preferences) }
    }
}

data class FieldTouchStates(
    val nameTouched: Boolean = false,
    val emailTouched: Boolean = false,
    val phoneTouched: Boolean = false,
    val passwordTouched: Boolean = false,
    val confirmPasswordTouched: Boolean = false,
    val bioTouched: Boolean = false
)