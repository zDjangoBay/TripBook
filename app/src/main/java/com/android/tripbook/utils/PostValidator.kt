package com.android.tripbook.utils

import com.android.tripbook.model.PostModel

object PostValidator {

    data class ValidationResult(
        val isValid: Boolean,
        val errorMessage: String? = null
    )

    fun validatePost(post: PostModel): ValidationResult {
        if (post.title.isBlank()) {
            return ValidationResult(false, "Title cannot be empty")
        }

        if (post.description.isBlank()) {
            return ValidationResult(false, "Description cannot be empty")
        }

        if (post.images.isEmpty()) {
            return ValidationResult(false, "Please upload at least one image")
        }

        if (post.location.isNullOrBlank()) {
            return ValidationResult(false, "Location is required")
        }

        if (post.tags.isEmpty()) {
            return ValidationResult(false, "Please select at least one tag")
        }

        return ValidationResult(true)
    }
}
