package com.android.tripbook.presentation.auth.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.android.tripbook.domain.model.UserRegistrationData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// Validation helpers
import androidx.compose.runtime.derivedStateOf
import com.android.tripbook.util.ValidationResult
import com.android.tripbook.util.ValidationUtil

class RegistrationViewModel : ViewModel() {

    // Store form data
    private val _registrationData = MutableStateFlow(UserRegistrationData())
    val registrationData: StateFlow<UserRegistrationData> = _registrationData.asStateFlow()

    // Current registration step
    var currentStep by mutableStateOf(0)
        private set

    // Total number of steps
    val totalSteps = 4

    // Form validation states
    var nameValidation by mutableStateOf(ValidationResult(true))
        private set

    var emailValidation by mutableStateOf(ValidationResult(true))
        private set

    var passwordValidation by mutableStateOf(ValidationResult(true))
        private set

    var bioValidation by mutableStateOf(ValidationResult(true))
        private set

    // Computed property to check if current step is valid
    val isCurrentStepValid = derivedStateOf {
        when (currentStep) {
            0 -> nameValidation.isValid && emailValidation.isValid && passwordValidation.isValid
            1 -> true // Profile picture is optional
            2 -> bioValidation.isValid
            3 -> true // Preferences are optional
            else -> false
        }
    }

    // Update existing functions to include validation
    fun updateBasicInfo(name: String, email: String, password: String) {
        // Validate inputs
        nameValidation = ValidationUtil.validateName(name)
        emailValidation = ValidationUtil.validateEmail(email)
        passwordValidation = ValidationUtil.validatePassword(password)

        // Update data if everything is valid
        _registrationData.update { currentData ->
            currentData.copy(
                fullName = name,
                email = email,
                password = password
            )
        }
    }

    fun updateBio(bio: String) {
        // Validate bio
        bioValidation = ValidationUtil.validateBio(bio)

        // Update data
        _registrationData.update { currentData ->
            currentData.copy(bio = bio)
        }
    }

    // Only allow navigation to next step if current step is valid
    fun goToNextStep() {
        if (isCurrentStepValid.value && currentStep < totalSteps - 1) {
            currentStep++
        }
    }

    fun updateProfilePicture(uri: String?) {
        _registrationData.update { currentData ->
            currentData.copy(profilePictureUri = uri)
        }
    }

    fun updateBirthDate(date: String) {
        _registrationData.update { currentData ->
            currentData.copy(birthDate = date)
        }
    }

    fun updateTravelPreferences(preferences: List<String>) {
        _registrationData.update { currentData ->
            currentData.copy(travelPreferences = preferences)
        }
    }

    fun updateFavoriteDestinations(destinations: List<String>) {
        _registrationData.update { currentData ->
            currentData.copy(favoriteDestinations = destinations)
        }
    }

    fun goToPreviousStep() {
        if (currentStep > 0) {
            currentStep--
        }
    }

    fun goToStep(step: Int) {
        if (step in 0 until totalSteps) {
            currentStep = step
        }
    }

    // For later: Function to submit data to Firebase
    fun registerUser(onSuccess: () -> Unit, onError: (String) -> Unit) {
        // Will implement Firebase registration later
        // For now, just simulate success
        onSuccess()
    }
}
