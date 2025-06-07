package com.TripBook.postmodule

import android.net.Uri

/**
 * Factory class for creating PostEvent instances with validation and convenience methods.
 * Provides safe event creation with built-in validation and error handling.
 *
 * @author Feukoun Marel
 * @version 1.0
 * @since TripBook v1.0
 */
class PostEventFactory(private val validationService: PostValidationService) {
    
    /**
     * Creates a TitleChanged event with validation
     */
    fun createTitleChangedEvent(title: String): Result<PostEvent.TitleChanged> {
        val validation = validationService.validateTitle(title)
        return if (validation.isValid) {
            Result.success(PostEvent.TitleChanged(title.trim()))
        } else {
            Result.failure(PostValidationException("title", validation.message))
        }
    }
    
    /**
     * Creates a DescriptionChanged event with validation
     */
    fun createDescriptionChangedEvent(description: String): Result<PostEvent.DescriptionChanged> {
        val validation = validationService.validateDescription(description)
        return if (validation.isValid) {
            Result.success(PostEvent.DescriptionChanged(description.trim()))
        } else {
            Result.failure(PostValidationException("description", validation.message))
        }
    }
    
    /**
     * Creates an ImageAdded event with URI validation
     */
    fun createImageAddedEvent(imageUri: Uri): Result<PostEvent.ImageAdded> {
        return try {
            // Basic URI validation
            if (imageUri.toString().isBlank()) {
                Result.failure(PostValidationException("image", "Image URI cannot be empty"))
            } else {
                Result.success(PostEvent.ImageAdded(imageUri))
            }
        } catch (e: Exception) {
            Result.failure(PostValidationException("image", "Invalid image URI: ${e.message}"))
        }
    }
    
    /**
     * Creates an ImageRemoved event with URI validation
     */
    fun createImageRemovedEvent(imageUri: Uri): Result<PostEvent.ImageRemoved> {
        return try {
            if (imageUri.toString().isBlank()) {
                Result.failure(PostValidationException("image", "Image URI cannot be empty"))
            } else {
                Result.success(PostEvent.ImageRemoved(imageUri))
            }
        } catch (e: Exception) {
            Result.failure(PostValidationException("image", "Invalid image URI: ${e.message}"))
        }
    }
    
    /**
     * Creates a LocationAdded event with coordinate validation
     */
    fun createLocationAddedEvent(
        latitude: Double,
        longitude: Double,
        locationName: String? = null
    ): Result<PostEvent.LocationAdded> {
        val validation = validationService.validateLocation(latitude, longitude, locationName)
        return if (validation.isValid) {
            Result.success(PostEvent.LocationAdded(latitude, longitude, locationName?.trim()))
        } else {
            Result.failure(PostValidationException("location", validation.message))
        }
    }
    
    /**
     * Creates a CategoryChanged event with validation
     */
    fun createCategoryChangedEvent(category: String): Result<PostEvent.CategoryChanged> {
        val validation = validationService.validateCategory(category)
        return if (validation.isValid) {
            Result.success(PostEvent.CategoryChanged(category.trim()))
        } else {
            Result.failure(PostValidationException("category", validation.message))
        }
    }
    
    /**
     * Creates a TagAdded event with validation
     */
    fun createTagAddedEvent(tag: String): Result<PostEvent.TagAdded> {
        val validation = validationService.validateTag(tag)
        return if (validation.isValid) {
            Result.success(PostEvent.TagAdded(tag.trim().lowercase()))
        } else {
            Result.failure(PostValidationException("tag", validation.message))
        }
    }
    
    /**
     * Creates a TagRemoved event with validation
     */
    fun createTagRemovedEvent(tag: String): Result<PostEvent.TagRemoved> {
        val validation = validationService.validateTag(tag)
        return if (validation.isValid) {
            Result.success(PostEvent.TagRemoved(tag.trim().lowercase()))
        } else {
            Result.failure(PostValidationException("tag", validation.message))
        }
    }
    
    /**
     * Creates a VisibilityChanged event with validation
     */
    fun createVisibilityChangedEvent(visibility: String): Result<PostEvent.VisibilityChanged> {
        val validation = validationService.validateVisibility(visibility)
        return if (validation.isValid) {
            Result.success(PostEvent.VisibilityChanged(visibility))
        } else {
            Result.failure(PostValidationException("visibility", validation.message))
        }
    }
    
    /**
     * Creates a ShowError event with message validation
     */
    fun createShowErrorEvent(message: String): Result<PostEvent.ShowError> {
        return if (message.isNotBlank()) {
            Result.success(PostEvent.ShowError(message.trim()))
        } else {
            Result.failure(PostValidationException("error", "Error message cannot be empty"))
        }
    }
    
    /**
     * Creates a PostCreated event with ID validation
     */
    fun createPostCreatedEvent(postId: String): Result<PostEvent.PostCreated> {
        return if (postId.isNotBlank()) {
            Result.success(PostEvent.PostCreated(postId.trim()))
        } else {
            Result.failure(PostValidationException("postId", "Post ID cannot be empty"))
        }
    }
    
    /**
     * Creates events from a map of data (useful for deserialization)
     */
    fun createEventFromMap(eventData: Map<String, Any?>): Result<PostEvent> {
        val eventType = eventData["event_type"] as? String
            ?: return Result.failure(IllegalArgumentException("Missing event_type"))
        
        return try {
            when (eventType) {
                "TitleChanged" -> {
                    val title = eventData["title"] as? String
                        ?: return Result.failure(IllegalArgumentException("Missing title"))
                    createTitleChangedEvent(title)
                }
                "DescriptionChanged" -> {
                    val description = eventData["description"] as? String
                        ?: return Result.failure(IllegalArgumentException("Missing description"))
                    createDescriptionChangedEvent(description)
                }
                "ImageAdded" -> {
                    val uriString = eventData["image_uri"] as? String
                        ?: return Result.failure(IllegalArgumentException("Missing image_uri"))
                    val uri = Uri.parse(uriString)
                    createImageAddedEvent(uri)
                }
                "ImageRemoved" -> {
                    val uriString = eventData["image_uri"] as? String
                        ?: return Result.failure(IllegalArgumentException("Missing image_uri"))
                    val uri = Uri.parse(uriString)
                    createImageRemovedEvent(uri)
                }
                "LocationAdded" -> {
                    val latitude = eventData["latitude"] as? Double
                        ?: return Result.failure(IllegalArgumentException("Missing latitude"))
                    val longitude = eventData["longitude"] as? Double
                        ?: return Result.failure(IllegalArgumentException("Missing longitude"))
                    val locationName = eventData["location_name"] as? String
                    createLocationAddedEvent(latitude, longitude, locationName)
                }
                "CategoryChanged" -> {
                    val category = eventData["category"] as? String
                        ?: return Result.failure(IllegalArgumentException("Missing category"))
                    createCategoryChangedEvent(category)
                }
                "TagAdded" -> {
                    val tag = eventData["tag"] as? String
                        ?: return Result.failure(IllegalArgumentException("Missing tag"))
                    createTagAddedEvent(tag)
                }
                "TagRemoved" -> {
                    val tag = eventData["tag"] as? String
                        ?: return Result.failure(IllegalArgumentException("Missing tag"))
                    createTagRemovedEvent(tag)
                }
                "VisibilityChanged" -> {
                    val visibility = eventData["visibility"] as? String
                        ?: return Result.failure(IllegalArgumentException("Missing visibility"))
                    createVisibilityChangedEvent(visibility)
                }
                "ShowError" -> {
                    val message = eventData["error_message"] as? String
                        ?: return Result.failure(IllegalArgumentException("Missing error_message"))
                    createShowErrorEvent(message)
                }
                "PostCreated" -> {
                    val postId = eventData["post_id"] as? String
                        ?: return Result.failure(IllegalArgumentException("Missing post_id"))
                    createPostCreatedEvent(postId)
                }
                "ClearAllImages" -> Result.success(PostEvent.ClearAllImages)
                "ClearLocation" -> Result.success(PostEvent.ClearLocation)
                "SubmitPost" -> Result.success(PostEvent.SubmitPost)
                "ClearForm" -> Result.success(PostEvent.ClearForm)
                "SaveDraft" -> Result.success(PostEvent.SaveDraft)
                "DismissError" -> Result.success(PostEvent.DismissError)
                else -> Result.failure(IllegalArgumentException("Unknown event type: $eventType"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Creates multiple events from a list of data maps
     */
    fun createEventsFromMaps(eventDataList: List<Map<String, Any?>>): List<Result<PostEvent>> {
        return eventDataList.map { createEventFromMap(it) }
    }
    
    /**
     * Creates a safe event that won't throw exceptions
     */
    fun createSafeEvent(eventCreator: () -> PostEvent): PostEvent? {
        return try {
            eventCreator()
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Validates and creates an event, returning null if invalid
     */
    fun createValidatedEvent(eventCreator: () -> Result<PostEvent>): PostEvent? {
        return try {
            eventCreator().getOrNull()
        } catch (e: Exception) {
            null
        }
    }
}

/**
 * Companion object with static factory methods for common use cases
 */
object PostEventFactoryCompanion {
    
    /**
     * Creates a factory with default validation service
     */
    fun createDefault(): PostEventFactory {
        return PostEventFactory(PostValidationService())
    }
    
    /**
     * Creates simple events without validation (for testing)
     */
    fun createSimpleEvent(eventType: String, data: Map<String, Any?> = emptyMap()): PostEvent? {
        return when (eventType) {
            "TitleChanged" -> PostEvent.TitleChanged(data["title"] as? String ?: "")
            "DescriptionChanged" -> PostEvent.DescriptionChanged(data["description"] as? String ?: "")
            "ClearAllImages" -> PostEvent.ClearAllImages
            "ClearLocation" -> PostEvent.ClearLocation
            "SubmitPost" -> PostEvent.SubmitPost
            "ClearForm" -> PostEvent.ClearForm
            "SaveDraft" -> PostEvent.SaveDraft
            "DismissError" -> PostEvent.DismissError
            else -> null
        }
    }
}
