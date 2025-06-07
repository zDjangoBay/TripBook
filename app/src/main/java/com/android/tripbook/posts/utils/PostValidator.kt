package com.android.tripbook.posts.utils

import com.android.tripbook.posts.model.PostModel

class PostValidator {
    
    fun validatePost(post: PostModel): ValidationResult {
        val errors = mutableListOf<String>()
        
        if (post.title.isBlank()) {
            errors.add("Title is required")
        }
        
        if (post.title.length > 100) {
            errors.add("Title must be less than 100 characters")
        }
        
        if (post.description.isBlank()) {
            errors.add("Description is required")
        }
        
        if (post.description.length > 2000) {
            errors.add("Description must be less than 2000 characters")
        }
        
        return ValidationResult(
            isValid = errors.isEmpty(),
            errors = errors
        )
    }
    
    fun validateTitle(title: String): FieldValidationResult {
        return when {
            title.isBlank() -> FieldValidationResult(false, "Title is required")
            title.length > 100 -> FieldValidationResult(false, "Title must be less than 100 characters")
            else -> FieldValidationResult(true, "Valid title")
        }
    }
    
    fun validateDescription(description: String): FieldValidationResult {
        return when {
            description.isBlank() -> FieldValidationResult(false, "Description is required")
            description.length > 2000 -> FieldValidationResult(false, "Description must be less than 2000 characters")
            else -> FieldValidationResult(true, "Valid description")
        }
    }
}

data class ValidationResult(
    val isValid: Boolean,
    val errors: List<String>
)

data class FieldValidationResult(
    val isValid: Boolean,
    val message: String,
    val isWarning: Boolean = false
)
