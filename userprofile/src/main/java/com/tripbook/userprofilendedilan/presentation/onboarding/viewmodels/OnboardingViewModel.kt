package com.tripbook.userprofilendedilan.presentation.onboarding.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tripbook.userprofilendedilan.data.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    // StateFlow to observe the onboarding completion state
    val isOnboardingCompleted: StateFlow<Boolean> = userPreferencesRepository.isOnboardingCompleted
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            false // Default value while loading
        )

    // Function to mark onboarding as completed
    fun completeOnboarding() {
        viewModelScope.launch {
            userPreferencesRepository.completeOnboarding()
        }
    }

    // Factory to create the ViewModel with dependencies
    class Factory(private val userPreferencesRepository: UserPreferencesRepository) :
        ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(OnboardingViewModel::class.java)) {
                return OnboardingViewModel(userPreferencesRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
