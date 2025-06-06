package com.tripbook.domain.validator

import com.tripbook.data.model.Post

/**
 * Validates that all required fields are complete before submission
 */
class PostValidator {

    /**
     * Validation result containing success status and potential error messages
     */
    data class ValidationResult(
        val isValid: Boolean,
        val errors: Map<String, String> = emptyMap()
    )

    /**
     * Validates a post for completeness and correctness
     * @param post The post to validate
     * @return ValidationResult indicating if the post is valid and any validation errors
     */
    fun validate(post: Post): ValidationResult {
        val errors = mutableMapOf<String, String>()

        // Check title
        if (post.title.isNullOrBlank()) {
            errors["title"] = "Title cannot be empty"
        } else if (post.title.length < 3) {
            errors["title"] = "Title must be at least 3 characters long"
        }

        // Check content
        if (post.content.isNullOrBlank()) {
            errors["content"] = "Content cannot be empty"
        }

        // Check location
        if (post.location.isNullOrBlank()) {
            errors["location"] = "Location is required"
        }

        // Check if there's at least one image
        if (post.images.isNullOrEmpty()) {
            errors["images"] = "At least one image is required"
        }

        // Check user ID
        if (post.userId.isNullOrBlank()) {
            errors["userId"] = "User ID is required"
        }

        return ValidationResult(errors.isEmpty(), errors)
    }

    /**
     * Validates just the title field
     * @param title The title to validate
     * @return ValidationResult for the title field
     */
    fun validateTitle(title: String?): ValidationResult {
        val errors = mutableMapOf<String, String>()

        if (title.isNullOrBlank()) {
            errors["title"] = "Title cannot be empty"
        } else if (title.length < 3) {
            errors["title"] = "Title must be at least 3 characters long"
        }

        return ValidationResult(errors.isEmpty(), errors)
    }

    /**
     * Validates just the content field
     * @param content The content to validate
     * @return ValidationResult for the content field
     */
    fun validateContent(content: String?): ValidationResult {
        val errors = mutableMapOf<String, String>()

        if (content.isNullOrBlank()) {
            errors["content"] = "Content cannot be empty"
        }

        return ValidationResult(errors.isEmpty(), errors)
    }
}
