package com.tripbook.domain.validator

import com.tripbook.data.model.Post
import java.util.regex.Pattern

/**
 * Enhanced validator that ensures all required fields are complete and properly formatted before submission
 */
class PostValidator {

    companion object {
        private const val MIN_TITLE_LENGTH = 3
        private const val MAX_TITLE_LENGTH = 100
        private const val MIN_CONTENT_LENGTH = 10
        private const val MAX_CONTENT_LENGTH = 2000
        private const val MIN_LOCATION_LENGTH = 2
        private const val MAX_LOCATION_LENGTH = 100
        private const val MAX_TAGS_COUNT = 10
        private const val MAX_TAG_LENGTH = 30
        private const val MAX_IMAGES_COUNT = 10
        
        // Regex patterns for validation
        private val TITLE_PATTERN = Pattern.compile("^[a-zA-Z0-9\\s\\-_.,!?()]+$")
        private val LOCATION_PATTERN = Pattern.compile("^[a-zA-Z0-9\\s\\-_.,()]+$")
        private val TAG_PATTERN = Pattern.compile("^[a-zA-Z0-9_]+$")
        private val URL_PATTERN = Pattern.compile(
            "^(https?://)?" +
            "([a-zA-Z0-9-]+\\.)*[a-zA-Z0-9-]+" +
            "(\\.[a-zA-Z]{2,})?" +
            "(/[^\\s]*)?$"
        )
    }

    /**
     * Enhanced validation result containing success status, detailed error messages, and warnings
     */
    data class ValidationResult(
        val isValid: Boolean,
        val errors: Map<String, String> = emptyMap(),
        val warnings: Map<String, String> = emptyMap(),
        val score: ValidationScore = ValidationScore.POOR
    )

    /**
     * Validation score indicating the quality of the post
     */
    enum class ValidationScore {
        EXCELLENT, GOOD, FAIR, POOR
    }

    /**
     * Validates a post for completeness, correctness, and quality
     * @param post The post to validate
     * @return ValidationResult indicating if the post is valid and any validation errors/warnings
     */
    fun validate(post: Post): ValidationResult {
        val errors = mutableMapOf<String, String>()
        val warnings = mutableMapOf<String, String>()

        // Validate required fields
        validateTitle(post.title).let { result ->
            if (!result.isValid) {
                errors.putAll(result.errors)
            }
            warnings.putAll(result.warnings)
        }

        validateContent(post.content).let { result ->
            if (!result.isValid) {
                errors.putAll(result.errors)
            }
            warnings.putAll(result.warnings)
        }

        validateLocation(post.location).let { result ->
            if (!result.isValid) {
                errors.putAll(result.errors)
            }
            warnings.putAll(result.warnings)
        }

        validateImages(post.images).let { result ->
            if (!result.isValid) {
                errors.putAll(result.errors)
            }
            warnings.putAll(result.warnings)
        }

        validateUserId(post.userId).let { result ->
            if (!result.isValid) {
                errors.putAll(result.errors)
            }
        }

        validateTags(post.tags).let { result ->
            if (!result.isValid) {
                errors.putAll(result.errors)
            }
            warnings.putAll(result.warnings)
        }

        // Calculate validation score
        val score = calculateValidationScore(post, errors.isEmpty(), warnings.size)

        return ValidationResult(errors.isEmpty(), errors, warnings, score)
    }

    /**
     * Validates the title field with enhanced checks
     * @param title The title to validate
     * @return ValidationResult for the title field
     */
    fun validateTitle(title: String?): ValidationResult {
        val errors = mutableMapOf<String, String>()
        val warnings = mutableMapOf<String, String>()

        when {
            title.isNullOrBlank() -> {
                errors["title"] = "Title cannot be empty"
            }
            title.length < MIN_TITLE_LENGTH -> {
                errors["title"] = "Title must be at least $MIN_TITLE_LENGTH characters long"
            }
            title.length > MAX_TITLE_LENGTH -> {
                errors["title"] = "Title cannot exceed $MAX_TITLE_LENGTH characters"
            }
            !TITLE_PATTERN.matcher(title).matches() -> {
                errors["title"] = "Title contains invalid characters"
            }
            else -> {
                // Check for quality warnings
                if (title.length < 10) {
                    warnings["title"] = "Consider making your title more descriptive"
                }
                if (title.all { it.isUpperCase() || !it.isLetter() }) {
                    warnings["title"] = "Avoid using ALL CAPS in titles"
                }
            }
        }

        return ValidationResult(errors.isEmpty(), errors, warnings)
    }

    /**
     * Validates the content field with enhanced checks
     * @param content The content to validate
     * @return ValidationResult for the content field
     */
    fun validateContent(content: String?): ValidationResult {
        val errors = mutableMapOf<String, String>()
        val warnings = mutableMapOf<String, String>()

        when {
            content.isNullOrBlank() -> {
                errors["content"] = "Content cannot be empty"
            }
            content.length < MIN_CONTENT_LENGTH -> {
                errors["content"] = "Content must be at least $MIN_CONTENT_LENGTH characters long"
            }
            content.length > MAX_CONTENT_LENGTH -> {
                errors["content"] = "Content cannot exceed $MAX_CONTENT_LENGTH characters"
            }
            else -> {
                // Quality checks
                if (content.length < 50) {
                    warnings["content"] = "Consider adding more details to your post"
                }
                if (content.count { it == '!' } > 5) {
                    warnings["content"] = "Consider reducing the number of exclamation marks"
                }
            }
        }

        return ValidationResult(errors.isEmpty(), errors, warnings)
    }

    /**
     * Validates the location field
     * @param location The location to validate
     * @return ValidationResult for the location field
     */
    fun validateLocation(location: String?): ValidationResult {
        val errors = mutableMapOf<String, String>()
        val warnings = mutableMapOf<String, String>()

        when {
            location.isNullOrBlank() -> {
                errors["location"] = "Location is required"
            }
            location.length < MIN_LOCATION_LENGTH -> {
                errors["location"] = "Location must be at least $MIN_LOCATION_LENGTH characters long"
            }
            location.length > MAX_LOCATION_LENGTH -> {
                errors["location"] = "Location cannot exceed $MAX_LOCATION_LENGTH characters"
            }
            !LOCATION_PATTERN.matcher(location).matches() -> {
                errors["location"] = "Location contains invalid characters"
            }
            else -> {
                if (location.length < 5) {
                    warnings["location"] = "Consider being more specific about the location"
                }
            }
        }

        return ValidationResult(errors.isEmpty(), errors, warnings)
    }

    /**
     * Validates the images list
     * @param images The list of image URLs to validate
     * @return ValidationResult for the images field
     */
    fun validateImages(images: List<String>?): ValidationResult {
        val errors = mutableMapOf<String, String>()
        val warnings = mutableMapOf<String, String>()

        when {
            images.isNullOrEmpty() -> {
                errors["images"] = "At least one image is required"
            }
            images.size > MAX_IMAGES_COUNT -> {
                errors["images"] = "Cannot have more than $MAX_IMAGES_COUNT images"
            }
            else -> {
                // Validate each image URL
                images.forEachIndexed { index, imageUrl ->
                    if (imageUrl.isBlank()) {
                        errors["images"] = "Image URL at position ${index + 1} is empty"
                        return@forEachIndexed
                    }
                    if (!isValidImageUrl(imageUrl)) {
                        errors["images"] = "Invalid image URL at position ${index + 1}"
                        return@forEachIndexed
                    }
                }

                // Quality checks
                if (images.size == 1) {
                    warnings["images"] = "Consider adding more images to make your post more engaging"
                }
            }
        }

        return ValidationResult(errors.isEmpty(), errors, warnings)
    }

    /**
     * Validates the user ID field
     * @param userId The user ID to validate
     * @return ValidationResult for the user ID field
     */
    fun validateUserId(userId: String?): ValidationResult {
        val errors = mutableMapOf<String, String>()

        if (userId.isNullOrBlank()) {
            errors["userId"] = "User ID is required"
        } else if (userId.length < 3) {
            errors["userId"] = "Invalid user ID format"
        }

        return ValidationResult(errors.isEmpty(), errors)
    }

    /**
     * Validates the tags list
     * @param tags The list of tags to validate
     * @return ValidationResult for the tags field
     */
    fun validateTags(tags: List<String>?): ValidationResult {
        val errors = mutableMapOf<String, String>()
        val warnings = mutableMapOf<String, String>()

        if (!tags.isNullOrEmpty()) {
            when {
                tags.size > MAX_TAGS_COUNT -> {
                    errors["tags"] = "Cannot have more than $MAX_TAGS_COUNT tags"
                }
                else -> {
                    // Validate each tag
                    tags.forEachIndexed { index, tag ->
                        when {
                            tag.isBlank() -> {
                                errors["tags"] = "Tag at position ${index + 1} is empty"
                                return@forEachIndexed
                            }
                            tag.length > MAX_TAG_LENGTH -> {
                                errors["tags"] = "Tag '${tag}' exceeds maximum length of $MAX_TAG_LENGTH characters"
                                return@forEachIndexed
                            }
                            !TAG_PATTERN.matcher(tag).matches() -> {
                                errors["tags"] = "Tag '${tag}' contains invalid characters. Use only letters, numbers, and underscores"
                                return@forEachIndexed
                            }
                        }
                    }

                    // Check for duplicate tags
                    val duplicates = tags.groupingBy { it.lowercase() }.eachCount().filter { it.value > 1 }
                    if (duplicates.isNotEmpty()) {
                        warnings["tags"] = "Duplicate tags found: ${duplicates.keys.joinToString(", ")}"
                    }

                    // Quality suggestions
                    if (tags.isEmpty()) {
                        warnings["tags"] = "Consider adding tags to help others discover your post"
                    }
                }
            }
        } else {
            warnings["tags"] = "Consider adding tags to categorize your post"
        }

        return ValidationResult(errors.isEmpty(), errors, warnings)
    }

    /**
     * Validates a single image URL
     * @param imageUrl The image URL to validate
     * @return True if the URL is valid
     */
    private fun isValidImageUrl(imageUrl: String): Boolean {
        if (!URL_PATTERN.matcher(imageUrl).matches()) return false
        
        val lowercaseUrl = imageUrl.lowercase()
        return lowercaseUrl.endsWith(".jpg") || 
               lowercaseUrl.endsWith(".jpeg") || 
               lowercaseUrl.endsWith(".png") || 
               lowercaseUrl.endsWith(".webp") ||
               lowercaseUrl.contains("firebasestorage") ||
               lowercaseUrl.contains("amazonaws.com") ||
               lowercaseUrl.contains("cloudinary.com")
    }

    /**
     * Calculates a validation score based on various factors
     */
    private fun calculateValidationScore(
        post: Post, 
        hasNoErrors: Boolean, 
        warningCount: Int
    ): ValidationScore {
        if (!hasNoErrors) return ValidationScore.POOR

        var score = 100

        // Deduct points for warnings
        score -= warningCount * 10

        // Add points for quality factors
        if (post.title.length in 10..50) score += 10
        if (post.content.length >= 100) score += 15
        if (post.images.size >= 2) score += 10
        if (post.tags.isNotEmpty()) score += 10
        if (post.location.length >= 10) score += 5

        return when {
            score >= 90 -> ValidationScore.EXCELLENT
            score >= 75 -> ValidationScore.GOOD
            score >= 60 -> ValidationScore.FAIR
            else -> ValidationScore.POOR
        }
    }

    /**
     * Quick validation for real-time feedback
     * @param field The field name to validate
     * @param value The value to validate
     * @return Simple validation result
     */
    fun validateField(field: String, value: String?): Boolean {
        return when (field) {
            "title" -> validateTitle(value).isValid
            "content" -> validateContent(value).isValid
            "location" -> validateLocation(value).isValid
            "userId" -> validateUserId(value).isValid
            else -> true
        }
    }
}
