package com.android.tripbook.posts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CreatePostViewModel : ViewModel() {
    private val _isFormValid = MutableStateFlow(false)
    val isFormValid = _isFormValid.asStateFlow()

    // In a real app, you would have more form state here

    fun submitPost() {
        viewModelScope.launch {
            // TODO: Connect to actual backend when available
            // For now, we'll just simulate network call
            kotlinx.coroutines.delay(1000) // Simulate network delay
        }
    }

    // Call this when form validation changes
    fun updateFormValidity(isValid: Boolean) {
        _isFormValid.value = isValid
    }
}

