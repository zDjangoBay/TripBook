package com.TripBook.postmodule

/**
 * Custom exceptions for post-related operations.
 * Provides specific error types for better error handling and user feedback.
 *
 * @author Feukoun Marel
 * @version 1.0
 * @since TripBook v1.0
 */

/**
 * Base exception for all post-related errors
 */
sealed class PostException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {
    
    /**
     * Gets a user-friendly error message
     */
    abstract fun getUserMessage(): String
    
    /**
     * Gets the error code for logging/analytics
     */
    abstract fun getErrorCode(): String
}

/**
 * Exception thrown when post validation fails
 */
class PostValidationException(
    val field: String,
    val validationError: String,
    cause: Throwable? = null
) : PostException("Validation failed for field '$field': $validationError", cause) {
    
    override fun getUserMessage(): String {
        return when (field.lowercase()) {
            "title" -> "Please check your post title: $validationError"
            "description" -> "Please check your post description: $validationError"
            "images" -> "Please check your images: $validationError"
            "location" -> "Please check your location: $validationError"
            "tags" -> "Please check your tags: $validationError"
            "category" -> "Please select a valid category"
            "visibility" -> "Please select a valid privacy setting"
            else -> "Please check your input: $validationError"
        }
    }
    
    override fun getErrorCode(): String = "POST_VALIDATION_ERROR"
}

/**
 * Exception thrown when image processing fails
 */
class ImageProcessingException(
    val operation: String,
    message: String,
    cause: Throwable? = null
) : PostException("Image processing failed during '$operation': $message", cause) {
    
    override fun getUserMessage(): String {
        return when (operation.lowercase()) {
            "upload" -> "Failed to upload image. Please try again."
            "compression" -> "Failed to process image. The image might be corrupted."
            "validation" -> "Invalid image file. Please select a valid image."
            "resize" -> "Failed to resize image. Please try a different image."
            else -> "Image processing failed. Please try again."
        }
    }
    
    override fun getErrorCode(): String = "IMAGE_PROCESSING_ERROR"
}

/**
 * Exception thrown when location operations fail
 */
class LocationException(
    val operation: String,
    message: String,
    cause: Throwable? = null
) : PostException("Location operation failed during '$operation': $message", cause) {
    
    override fun getUserMessage(): String {
        return when (operation.lowercase()) {
            "permission" -> "Location permission is required to add location to your post."
            "unavailable" -> "Location services are not available. Please enable location services."
            "timeout" -> "Failed to get your location. Please try again."
            "invalid" -> "Invalid location coordinates. Please try again."
            "geocoding" -> "Failed to get location details. Please try again."
            else -> "Location operation failed. Please try again."
        }
    }
    
    override fun getErrorCode(): String = "LOCATION_ERROR"
}

/**
 * Exception thrown when draft operations fail
 */
class DraftException(
    val operation: String,
    message: String,
    cause: Throwable? = null
) : PostException("Draft operation failed during '$operation': $message", cause) {
    
    override fun getUserMessage(): String {
        return when (operation.lowercase()) {
            "save" -> "Failed to save draft. Please try again."
            "load" -> "Failed to load draft. The draft might be corrupted."
            "delete" -> "Failed to delete draft. Please try again."
            "export" -> "Failed to export drafts. Please try again."
            "import" -> "Failed to import drafts. Please check the file format."
            "cleanup" -> "Failed to clean up old drafts."
            else -> "Draft operation failed. Please try again."
        }
    }
    
    override fun getErrorCode(): String = "DRAFT_ERROR"
}

/**
 * Exception thrown when network operations fail
 */
class NetworkException(
    val operation: String,
    message: String,
    cause: Throwable? = null
) : PostException("Network operation failed during '$operation': $message", cause) {
    
    override fun getUserMessage(): String {
        return when (operation.lowercase()) {
            "submit" -> "Failed to submit post. Please check your internet connection."
            "upload" -> "Failed to upload content. Please check your internet connection."
            "sync" -> "Failed to sync data. Please try again later."
            "timeout" -> "Request timed out. Please try again."
            "server_error" -> "Server error occurred. Please try again later."
            else -> "Network error occurred. Please check your connection and try again."
        }
    }
    
    override fun getErrorCode(): String = "NETWORK_ERROR"
}

/**
 * Exception thrown when storage operations fail
 */
class StorageException(
    val operation: String,
    message: String,
    cause: Throwable? = null
) : PostException("Storage operation failed during '$operation': $message", cause) {
    
    override fun getUserMessage(): String {
        return when (operation.lowercase()) {
            "insufficient_space" -> "Not enough storage space. Please free up some space and try again."
            "permission" -> "Storage permission is required. Please grant storage permission."
            "read" -> "Failed to read file. The file might be corrupted."
            "write" -> "Failed to save file. Please try again."
            "delete" -> "Failed to delete file. Please try again."
            else -> "Storage operation failed. Please try again."
        }
    }
    
    override fun getErrorCode(): String = "STORAGE_ERROR"
}

/**
 * Exception thrown when post submission fails
 */
class PostSubmissionException(
    val reason: String,
    message: String,
    cause: Throwable? = null
) : PostException("Post submission failed: $reason - $message", cause) {
    
    override fun getUserMessage(): String {
        return when (reason.lowercase()) {
            "content_policy" -> "Your post violates our content policy. Please review and modify your content."
            "spam_detected" -> "Your post was flagged as spam. Please modify your content and try again."
            "rate_limit" -> "You're posting too frequently. Please wait a moment and try again."
            "server_error" -> "Server error occurred. Please try again later."
            "authentication" -> "Authentication failed. Please log in again."
            "quota_exceeded" -> "You've reached your posting limit. Please try again later."
            else -> "Failed to submit post. Please try again."
        }
    }
    
    override fun getErrorCode(): String = "POST_SUBMISSION_ERROR"
}

/**
 * Exception thrown when event processing fails
 */
class EventProcessingException(
    val eventType: String,
    message: String,
    cause: Throwable? = null
) : PostException("Event processing failed for '$eventType': $message", cause) {
    
    override fun getUserMessage(): String {
        return "An error occurred while processing your action. Please try again."
    }
    
    override fun getErrorCode(): String = "EVENT_PROCESSING_ERROR"
}

/**
 * Exception thrown when configuration is invalid
 */
class ConfigurationException(
    val configKey: String,
    message: String,
    cause: Throwable? = null
) : PostException("Configuration error for '$configKey': $message", cause) {
    
    override fun getUserMessage(): String {
        return "App configuration error. Please restart the app or contact support."
    }
    
    override fun getErrorCode(): String = "CONFIGURATION_ERROR"
}

/**
 * Utility object for creating common exceptions
 */
object PostExceptionFactory {
    
    fun createValidationException(field: String, error: String): PostValidationException {
        return PostValidationException(field, error)
    }
    
    fun createImageException(operation: String, error: String): ImageProcessingException {
        return ImageProcessingException(operation, error)
    }
    
    fun createLocationException(operation: String, error: String): LocationException {
        return LocationException(operation, error)
    }
    
    fun createNetworkException(operation: String, error: String): NetworkException {
        return NetworkException(operation, error)
    }
    
    fun createDraftException(operation: String, error: String): DraftException {
        return DraftException(operation, error)
    }
    
    fun createStorageException(operation: String, error: String): StorageException {
        return StorageException(operation, error)
    }
    
    fun createSubmissionException(reason: String, error: String): PostSubmissionException {
        return PostSubmissionException(reason, error)
    }
}

/**
 * Extension function to convert generic exceptions to PostExceptions
 */
fun Throwable.toPostException(operation: String = "unknown"): PostException {
    return when (this) {
        is PostException -> this
        is SecurityException -> StorageException("permission", "Permission denied: ${this.message}", this)
        is java.io.IOException -> StorageException("io_error", "IO error: ${this.message}", this)
        is java.net.SocketTimeoutException -> NetworkException("timeout", "Request timed out", this)
        is java.net.UnknownHostException -> NetworkException("no_connection", "No internet connection", this)
        else -> EventProcessingException(operation, "Unexpected error: ${this.message}", this)
    }
}
