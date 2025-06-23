package com.android.tripbook.viewmodel

import androidx.lifecycle.ViewModel
import com.android.tripbook.model.BookingStep
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Stub ViewModel for booking functionality
 * This is a placeholder until team members implement the full booking system
 */
class BookingViewModel : ViewModel() {
    
    private val _currentStep = MutableStateFlow(BookingStep.DateSelection)
    val currentStep: StateFlow<BookingStep> = _currentStep
    
    fun nextStep() {
        _currentStep.value = when (_currentStep.value) {
            BookingStep.DateSelection -> BookingStep.TravelerInfo
            BookingStep.TravelerInfo -> BookingStep.AdditionalOptions
            BookingStep.AdditionalOptions -> BookingStep.Summary
            BookingStep.Summary -> BookingStep.Summary
        }
    }
    
    fun previousStep() {
        _currentStep.value = when (_currentStep.value) {
            BookingStep.DateSelection -> BookingStep.DateSelection
            BookingStep.TravelerInfo -> BookingStep.DateSelection
            BookingStep.AdditionalOptions -> BookingStep.TravelerInfo
            BookingStep.Summary -> BookingStep.AdditionalOptions
        }
    }
}
