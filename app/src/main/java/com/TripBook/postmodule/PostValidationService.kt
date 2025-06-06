package com.TripBook.postmodule

import android.net.Uri
import java.util.regex.Pattern

/**
 * Service for validating post data and user inputs.
 * Provides comprehensive validation rules for all post creation fields.
 *
 * @author Feukoun Marel
 * @version 1.0
 * @since TripBook v1.0
 */
class PostValidationService {
    
    companion object {
        // Validation constants
        const val MIN_TITLE_LENGTH = 3
        const val MAX_TITLE_LENGTH = 100
        const val MIN_DESCRIPTION_LENGTH = 10
        const val MAX_DESCRIPTION_LENGTH = 1000
        const val MAX_IMAGES_COUNT = 10
        const val MAX_TAGS_COUNT = 20
        const val MAX_TAG_LENGTH = 30
        const val MIN_TAG_LENGTH = 2
        
        // Coordinate bounds
        const val MIN_LATITUDE = -90.0
        const val MAX_LATITUDE = 90.0
        const val MIN_LONGITUDE = -180.0
        const val MAX_LONGITUDE = 180.0
        
        // Regex patterns
        private val HASHTAG_PATTERN = Pattern.compile("^[a-zA-Z0-9_]+$")
        private val PROFANITY_WORDS = setOf(
            "spam", "fake", "scam", "inappropriate"
        )
    }
    
    /**
     * Validates the post title
     */
    fun validateTitle(title: String): FieldValidationResult {
        return when {
            title.isBlank() -> FieldValidationResult.error("Title is required")
            title.length < MIN_TITLE_LENGTH -> FieldValidationResult.error("Title must be at least $MIN_TITLE_LENGTH characters")
            title.length > MAX_TITLE_LENGTH -> FieldValidationResult.error("Title must be less than $MAX_TITLE_LENGTH characters")
            containsProfanity(title) -> FieldValidationResult.error("Title contains inappropriate content")
            title.trim() != title -> FieldValidationResult.warning("Title has leading/trailing spaces")
            else -> FieldValidationResult.valid()
        }
    }
    
    /**
     * Validates the post description
     */
    fun validateDescription(description: String): FieldValidationResult {
        return when {
            description.isBlank() -> FieldValidationResult.error("Description is required")
            description.length < MIN_DESCRIPTION_LENGTH -> FieldValidationResult.error("Description must be at least $MIN_DESCRIPTION_LENGTH characters")
            description.length > MAX_DESCRIPTION_LENGTH -> FieldValidationResult.error("Description must be less than $MAX_DESCRIPTION_LENGTH characters")
            containsProfanity(description) -> FieldValidationResult.error("Description contains inappropriate content")
            else -> FieldValidationResult.valid()
        }
    }
    
    /**
     * Validates the list of images
     */
    fun validateImages(images: List<Uri>): FieldValidationResult {
        return when {
            images.size > MAX_IMAGES_COUNT -> FieldValidationResult.error("Maximum $MAX_IMAGES_COUNT images allowed")
            images.isEmpty() -> FieldValidationResult.warning("Consider adding images to make your post more engaging")
            else -> FieldValidationResult.valid()
        }
    }
    
    /**
     * Validates location coordinates
     */
    fun validateLocation(latitude: Double, longitude: Double, locationName: String?): FieldValidationResult {
        return when {
            latitude < MIN_LATITUDE || latitude > MAX_LATITUDE -> 
                FieldValidationResult.error("Invalid latitude. Must be between $MIN_LATITUDE and $MAX_LATITUDE")
            longitude < MIN_LONGITUDE || longitude > MAX_LONGITUDE -> 
                FieldValidationResult.error("Invalid longitude. Must be between $MIN_LONGITUDE and $MAX_LONGITUDE")
            locationName != null && locationName.length > 100 -> 
                FieldValidationResult.error("Location name must be less than 100 characters")
            locationName != null && containsProfanity(locationName) -> 
                FieldValidationResult.error("Location name contains inappropriate content")
            else -> FieldValidationResult.valid()
        }
    }
    
    /**
     * Validates a single tag
     */
    fun validateTag(tag: String): FieldValidationResult {
        return when {
            tag.isBlank() -> FieldValidationResult.error("Tag cannot be empty")
            tag.length < MIN_TAG_LENGTH -> FieldValidationResult.error("Tag must be at least $MIN_TAG_LENGTH characters")
            tag.length > MAX_TAG_LENGTH -> FieldValidationResult.error("Tag must be less than $MAX_TAG_LENGTH characters")
            !HASHTAG_PATTERN.matcher(tag).matches() -> FieldValidationResult.error("Tag can only contain letters, numbers, and underscores")
            containsProfanity(tag) -> FieldValidationResult.error("Tag contains inappropriate content")
            else -> FieldValidationResult.valid()
        }
    }
    
    /**
     * Validates the list of tags
     */
    fun validateTags(tags: List<String>): FieldValidationResult {
        return when {
            tags.size > MAX_TAGS_COUNT -> FieldValidationResult.error("Maximum $MAX_TAGS_COUNT tags allowed")
            tags.distinct().size != tags.size -> FieldValidationResult.error("Duplicate tags are not allowed")
            else -> {
                // Validate each individual tag
                tags.forEach { tag ->
                    val tagValidation = validateTag(tag)
                    if (!tagValidation.isValid) {
                        return tagValidation
                    }
                }
                FieldValidationResult.valid()
            }
        }
    }
    
    /**
     * Validates the category
     */
    fun validateCategory(category: String): FieldValidationResult {
        val validCategories = setOf(
            "Adventure", "Beach", "City", "Culture", "Food", "Nature", 
            "Photography", "Relaxation", "Shopping", "Sports", "Other"
        )
        
        return when {
            category.isBlank() -> FieldValidationResult.warning("Consider selecting a category")
            category !in validCategories -> FieldValidationResult.error("Invalid category selected")
            else -> FieldValidationResult.valid()
        }
    }
    
    /**
     * Validates the visibility setting
     */
    fun validateVisibility(visibility: String): FieldValidationResult {
        val validVisibilities = setOf("Public", "Friends", "Private", "Followers", "Custom")
        
        return when {
            visibility.isBlank() -> FieldValidationResult.error("Visibility setting is required")
            visibility !in validVisibilities -> FieldValidationResult.error("Invalid visibility setting")
            else -> FieldValidationResult.valid()
        }
    }
    
    /**
     * Validates the entire post for submission
     */
    fun validateCompletePost(
        title: String,
        description: String,
        images: List<Uri>,
        location: PostLocationData?,
        category: String,
        tags: List<String>,
        visibility: String
    ): CompleteValidationResult {
        val validations = mutableListOf<FieldValidationResult>()
        
        validations.add(validateTitle(title))
        validations.add(validateDescription(description))
        validations.add(validateImages(images))
        validations.add(validateCategory(category))
        validations.add(validateTags(tags))
        validations.add(validateVisibility(visibility))
        
        location?.let {
            validations.add(validateLocation(it.latitude, it.longitude, it.name))
        }
        
        val errors = validations.filter { !it.isValid }.map { it.message }
        val warnings = validations.filter { it.isWarning }.map { it.message }
        
        return CompleteValidationResult(
            isValid = errors.isEmpty(),
            errors = errors,
            warnings = warnings,
            canSubmit = errors.isEmpty()
        )
    }
    
    /**
     * Checks if text contains profanity
     */
    private fun containsProfanity(text: String): Boolean {
        val lowerText = text.lowercase()
        return PROFANITY_WORDS.any { lowerText.contains(it) }
    }
    
    /**
     * Validates hashtag format
     */
    fun validateHashtag(hashtag: String): Boolean {
        return hashtag.startsWith("#") && 
               hashtag.length > 1 && 
               HASHTAG_PATTERN.matcher(hashtag.substring(1)).matches()
    }
    
    /**
     * Extracts and validates hashtags from text
     */
    fun extractAndValidateHashtags(text: String): List<String> {
        val hashtagPattern = Pattern.compile("#\\w+")
        val matcher = hashtagPattern.matcher(text)
        val hashtags = mutableListOf<String>()
        
        while (matcher.find()) {
            val hashtag = matcher.group()
            if (validateHashtag(hashtag)) {
                hashtags.add(hashtag)
            }
        }
        
        return hashtags.distinct()
    }
}

/**
 * Represents the result of field validation
 */
data class FieldValidationResult(
    val isValid: Boolean,
    val isWarning: Boolean = false,
    val message: String
) {
    companion object {
        fun valid() = FieldValidationResult(true, false, "")
        fun error(message: String) = FieldValidationResult(false, false, message)
        fun warning(message: String) = FieldValidationResult(true, true, message)
    }
}

/**
 * Represents the result of complete post validation
 */
data class CompleteValidationResult(
    val isValid: Boolean,
    val errors: List<String>,
    val warnings: List<String>,
    val canSubmit: Boolean
)
