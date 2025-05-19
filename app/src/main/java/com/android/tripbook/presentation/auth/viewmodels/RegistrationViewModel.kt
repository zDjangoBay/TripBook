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

class RegistrationViewModel : ViewModel() {

    // Store form data
    private val _registrationData = MutableStateFlow(UserRegistrationData())
    val registrationData: StateFlow<UserRegistrationData> = _registrationData.asStateFlow()

    // Current registration step
    var currentStep by mutableStateOf(0)
        private set

    // Total number of steps
    val totalSteps = 4

    // Update form data functions
    fun updateBasicInfo(name: String, email: String, password: String) {
        _registrationData.update { currentData ->
            currentData.copy(
                fullName = name,
                email = email,
                password = password
            )
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

    fun updateBio(bio: String) {
        _registrationData.update { currentData ->
            currentData.copy(bio = bio)
        }
    }

    // Navigation functions
    fun goToNextStep() {
        if (currentStep < totalSteps - 1) {
            currentStep++
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
