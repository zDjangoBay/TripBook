package com.TripBook.postmodule

import android.net.Uri

/**
 * Represents the UI state for post creation screen.
 * Contains all the data needed to render the post creation interface.
 *
 * @author Feukoun Marel
 * @version 1.0
 * @since TripBook v1.0
 */
data class PostUIState(
    // Form fields
    val title: String = "",
    val description: String = "",
    val images: List<Uri> = emptyList(),
    val location: PostLocation? = null,
    val category: String = "",
    val tags: List<String> = emptyList(),
    val visibility: PostVisibility = PostVisibility.PUBLIC,
    
    // UI state
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val isDraftSaved: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    
    // Validation state
    val titleError: String? = null,
    val descriptionError: String? = null,
    val imageError: String? = null,
    val locationError: String? = null,
    
    // Form state
    val isFormValid: Boolean = false,
    val hasUnsavedChanges: Boolean = false,
    val lastSavedTime: Long? = null,
    
    // Created post info
    val createdPostId: String? = null
) {
    
    /**
     * Checks if the form has any content
     */
    fun hasContent(): Boolean {
        return title.isNotBlank() || 
               description.isNotBlank() || 
               images.isNotEmpty() || 
               location != null ||
               tags.isNotEmpty()
    }
    
    /**
     * Checks if the form is ready for submission
     */
    fun canSubmit(): Boolean {
        return isFormValid && 
               !isLoading && 
               !isSubmitting && 
               title.isNotBlank() &&
               description.isNotBlank()
    }
    
    /**
     * Gets the total number of characters in title and description
     */
    fun getTotalCharacterCount(): Int {
        return title.length + description.length
    }
    
    /**
     * Checks if the post has location information
     */
    fun hasLocation(): Boolean = location != null
    
    /**
     * Checks if the post has images
     */
    fun hasImages(): Boolean = images.isNotEmpty()
    
    /**
     * Gets the number of images
     */
    fun getImageCount(): Int = images.size
    
    /**
     * Checks if any validation errors exist
     */
    fun hasValidationErrors(): Boolean {
        return titleError != null || 
               descriptionError != null || 
               imageError != null || 
               locationError != null
    }
}

/**
 * Represents a location for a post
 */
data class PostLocation(
    val latitude: Double,
    val longitude: Double,
    val name: String? = null,
    val address: String? = null
)

/**
 * Enum representing post visibility options
 */
enum class PostVisibility(val displayName: String) {
    PUBLIC("Public"),
    FRIENDS("Friends Only"),
    PRIVATE("Private"),
    FOLLOWERS("Followers Only"),
    CUSTOM("Custom")
}
