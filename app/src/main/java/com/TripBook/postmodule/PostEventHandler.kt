package com.TripBook.postmodule

import android.net.Uri
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Handles processing of PostEvent instances and manages post creation state.
 * Acts as the central processor for all post-related user interactions.
 *
 * @author Feukoun Marel
 * @version 1.0
 * @since TripBook v1.0
 */
class PostEventHandler {
    
    private val _currentState = MutableStateFlow(PostCreationState())
    val currentState: StateFlow<PostCreationState> = _currentState.asStateFlow()
    
    private val _eventHistory = mutableListOf<PostEvent>()
    val eventHistory: List<PostEvent> get() = _eventHistory.toList()
    
    /**
     * Processes a PostEvent and updates the current state accordingly
     */
    suspend fun handleEvent(event: PostEvent) {
        _eventHistory.add(event)
        
        val currentState = _currentState.value
        val newState = when (event) {
            is PostEvent.TitleChanged -> currentState.copy(
                title = event.newTitle,
                hasUnsavedChanges = true
            )
            
            is PostEvent.DescriptionChanged -> currentState.copy(
                description = event.newDescription,
                hasUnsavedChanges = true
            )
            
            is PostEvent.ImageAdded -> currentState.copy(
                images = currentState.images + event.imageUri,
                hasUnsavedChanges = true
            )
            
            is PostEvent.ImageRemoved -> currentState.copy(
                images = currentState.images - event.imageUri,
                hasUnsavedChanges = true
            )
            
            is PostEvent.ClearAllImages -> currentState.copy(
                images = emptyList(),
                hasUnsavedChanges = true
            )
            
            is PostEvent.LocationAdded -> currentState.copy(
                location = PostLocationData(
                    latitude = event.latitude,
                    longitude = event.longitude,
                    name = event.locationName
                ),
                hasUnsavedChanges = true
            )
            
            is PostEvent.ClearLocation -> currentState.copy(
                location = null,
                hasUnsavedChanges = true
            )
            
            is PostEvent.CategoryChanged -> currentState.copy(
                category = event.category,
                hasUnsavedChanges = true
            )
            
            is PostEvent.TagAdded -> currentState.copy(
                tags = currentState.tags + event.tag,
                hasUnsavedChanges = true
            )
            
            is PostEvent.TagRemoved -> currentState.copy(
                tags = currentState.tags - event.tag,
                hasUnsavedChanges = true
            )
            
            is PostEvent.VisibilityChanged -> currentState.copy(
                visibility = event.visibility,
                hasUnsavedChanges = true
            )
            
            is PostEvent.SubmitPost -> currentState.copy(
                isSubmitting = true,
                errorMessage = null
            )
            
            is PostEvent.ClearForm -> PostCreationState()
            
            is PostEvent.SaveDraft -> currentState.copy(
                isDraftSaved = true,
                hasUnsavedChanges = false,
                lastSavedTime = System.currentTimeMillis()
            )
            
            is PostEvent.ShowError -> currentState.copy(
                errorMessage = event.message,
                isSubmitting = false
            )
            
            is PostEvent.DismissError -> currentState.copy(
                errorMessage = null
            )
            
            is PostEvent.PostCreated -> currentState.copy(
                isSubmitting = false,
                isSubmitted = true,
                createdPostId = event.postId,
                hasUnsavedChanges = false
            )
        }
        
        _currentState.value = newState
    }
    
    /**
     * Gets the current form validation status
     */
    fun getValidationStatus(): ValidationResult {
        val state = _currentState.value
        val errors = mutableListOf<String>()
        
        if (state.title.isBlank()) {
            errors.add("Title is required")
        }
        
        if (state.description.isBlank()) {
            errors.add("Description is required")
        }
        
        if (state.title.length > 100) {
            errors.add("Title must be less than 100 characters")
        }
        
        if (state.description.length > 1000) {
            errors.add("Description must be less than 1000 characters")
        }
        
        if (state.images.size > 10) {
            errors.add("Maximum 10 images allowed")
        }
        
        return ValidationResult(
            isValid = errors.isEmpty(),
            errors = errors
        )
    }
    
    /**
     * Checks if the form can be submitted
     */
    fun canSubmit(): Boolean {
        val state = _currentState.value
        val validation = getValidationStatus()
        
        return validation.isValid && 
               !state.isSubmitting && 
               state.title.isNotBlank() && 
               state.description.isNotBlank()
    }
    
    /**
     * Gets the number of events processed
     */
    fun getEventCount(): Int = _eventHistory.size
    
    /**
     * Clears the event history
     */
    fun clearHistory() {
        _eventHistory.clear()
    }
    
    /**
     * Gets events of a specific type
     */
    fun <T : PostEvent> getEventsOfType(eventClass: Class<T>): List<T> {
        return eventHistory.filterIsInstance(eventClass)
    }
}

/**
 * Represents the current state of post creation
 */
data class PostCreationState(
    val title: String = "",
    val description: String = "",
    val images: List<Uri> = emptyList(),
    val location: PostLocationData? = null,
    val category: String = "",
    val tags: List<String> = emptyList(),
    val visibility: String = "Public",
    val isSubmitting: Boolean = false,
    val isSubmitted: Boolean = false,
    val isDraftSaved: Boolean = false,
    val hasUnsavedChanges: Boolean = false,
    val errorMessage: String? = null,
    val createdPostId: String? = null,
    val lastSavedTime: Long? = null
)

/**
 * Represents location data for a post
 */
data class PostLocationData(
    val latitude: Double,
    val longitude: Double,
    val name: String? = null
)

/**
 * Represents validation result
 */
data class ValidationResult(
    val isValid: Boolean,
    val errors: List<String>
)
