package com.TripBook.postmodule

import android.net.Uri

/**
 * Extension functions for PostEvent to provide additional functionality and convenience methods.
 * These extensions enhance the usability of PostEvent instances without modifying the core class.
 *
 * @author Feukoun Marel
 * @version 1.0
 * @since TripBook v1.0
 */

/**
 * Checks if this event modifies form content
 */
fun PostEvent.isContentModifyingEvent(): Boolean {
    return when (this) {
        is PostEvent.TitleChanged,
        is PostEvent.DescriptionChanged,
        is PostEvent.ImageAdded,
        is PostEvent.ImageRemoved,
        is PostEvent.LocationAdded,
        is PostEvent.CategoryChanged,
        is PostEvent.TagAdded,
        is PostEvent.TagRemoved,
        is PostEvent.VisibilityChanged -> true
        else -> false
    }
}

/**
 * Checks if this event is a form action (submit, clear, save)
 */
fun PostEvent.isFormActionEvent(): Boolean {
    return when (this) {
        is PostEvent.SubmitPost,
        is PostEvent.ClearForm,
        is PostEvent.SaveDraft -> true
        else -> false
    }
}

/**
 * Checks if this event is related to user feedback
 */
fun PostEvent.isFeedbackEvent(): Boolean {
    return when (this) {
        is PostEvent.ShowError,
        is PostEvent.DismissError,
        is PostEvent.PostCreated -> true
        else -> false
    }
}

/**
 * Checks if this event involves image operations
 */
fun PostEvent.isImageEvent(): Boolean {
    return when (this) {
        is PostEvent.ImageAdded,
        is PostEvent.ImageRemoved,
        is PostEvent.ClearAllImages -> true
        else -> false
    }
}

/**
 * Checks if this event involves location operations
 */
fun PostEvent.isLocationEvent(): Boolean {
    return when (this) {
        is PostEvent.LocationAdded,
        is PostEvent.ClearLocation -> true
        else -> false
    }
}

/**
 * Checks if this event involves tag operations
 */
fun PostEvent.isTagEvent(): Boolean {
    return when (this) {
        is PostEvent.TagAdded,
        is PostEvent.TagRemoved -> true
        else -> false
    }
}

/**
 * Gets the priority level of this event for processing order
 */
fun PostEvent.getPriority(): EventPriority {
    return when (this) {
        is PostEvent.ShowError -> EventPriority.HIGH
        is PostEvent.SubmitPost -> EventPriority.HIGH
        is PostEvent.PostCreated -> EventPriority.HIGH
        is PostEvent.SaveDraft -> EventPriority.MEDIUM
        is PostEvent.ClearForm -> EventPriority.MEDIUM
        is PostEvent.ClearAllImages -> EventPriority.MEDIUM
        is PostEvent.ClearLocation -> EventPriority.MEDIUM
        else -> EventPriority.LOW
    }
}

/**
 * Gets a human-readable description of this event
 */
fun PostEvent.getDescription(): String {
    return when (this) {
        is PostEvent.TitleChanged -> "Title updated to: ${newTitle.take(30)}${if (newTitle.length > 30) "..." else ""}"
        is PostEvent.DescriptionChanged -> "Description updated (${newDescription.length} characters)"
        is PostEvent.ImageAdded -> "Image added: ${imageUri.lastPathSegment ?: "unknown"}"
        is PostEvent.ImageRemoved -> "Image removed: ${imageUri.lastPathSegment ?: "unknown"}"
        is PostEvent.ClearAllImages -> "All images cleared"
        is PostEvent.LocationAdded -> "Location added: ${PostUtils.formatCoordinates(latitude, longitude)}"
        is PostEvent.ClearLocation -> "Location cleared"
        is PostEvent.CategoryChanged -> "Category changed to: $category"
        is PostEvent.TagAdded -> "Tag added: $tag"
        is PostEvent.TagRemoved -> "Tag removed: $tag"
        is PostEvent.VisibilityChanged -> "Visibility changed to: $visibility"
        is PostEvent.SubmitPost -> "Post submission initiated"
        is PostEvent.ClearForm -> "Form cleared"
        is PostEvent.SaveDraft -> "Draft saved"
        is PostEvent.ShowError -> "Error: $message"
        is PostEvent.DismissError -> "Error dismissed"
        is PostEvent.PostCreated -> "Post created successfully with ID: $postId"
    }
}

/**
 * Gets the category of this event for analytics
 */
fun PostEvent.getAnalyticsCategory(): String {
    return when (this) {
        is PostEvent.TitleChanged,
        is PostEvent.DescriptionChanged -> "content_input"
        is PostEvent.ImageAdded,
        is PostEvent.ImageRemoved,
        is PostEvent.ClearAllImages -> "media_management"
        is PostEvent.LocationAdded,
        is PostEvent.ClearLocation -> "location_management"
        is PostEvent.CategoryChanged -> "categorization"
        is PostEvent.TagAdded,
        is PostEvent.TagRemoved -> "tag_management"
        is PostEvent.VisibilityChanged -> "privacy_control"
        is PostEvent.SubmitPost,
        is PostEvent.ClearForm,
        is PostEvent.SaveDraft -> "form_action"
        is PostEvent.ShowError,
        is PostEvent.DismissError,
        is PostEvent.PostCreated -> "user_feedback"
    }
}

/**
 * Checks if this event should trigger auto-save
 */
fun PostEvent.shouldTriggerAutoSave(): Boolean {
    return isContentModifyingEvent() && when (this) {
        is PostEvent.ClearAllImages,
        is PostEvent.ClearLocation -> false // Don't auto-save on clear operations
        else -> true
    }
}

/**
 * Gets the validation requirements for this event
 */
fun PostEvent.getValidationRequirements(): List<ValidationRequirement> {
    return when (this) {
        is PostEvent.TitleChanged -> listOf(
            ValidationRequirement.NON_EMPTY,
            ValidationRequirement.LENGTH_CHECK,
            ValidationRequirement.PROFANITY_CHECK
        )
        is PostEvent.DescriptionChanged -> listOf(
            ValidationRequirement.NON_EMPTY,
            ValidationRequirement.LENGTH_CHECK,
            ValidationRequirement.PROFANITY_CHECK
        )
        is PostEvent.LocationAdded -> listOf(
            ValidationRequirement.COORDINATE_BOUNDS,
            ValidationRequirement.LOCATION_NAME_CHECK
        )
        is PostEvent.TagAdded -> listOf(
            ValidationRequirement.NON_EMPTY,
            ValidationRequirement.TAG_FORMAT,
            ValidationRequirement.PROFANITY_CHECK
        )
        is PostEvent.CategoryChanged -> listOf(
            ValidationRequirement.VALID_CATEGORY
        )
        is PostEvent.VisibilityChanged -> listOf(
            ValidationRequirement.VALID_VISIBILITY
        )
        else -> emptyList()
    }
}

/**
 * Converts this event to a map for serialization or logging
 */
fun PostEvent.toMap(): Map<String, Any?> {
    val baseMap = mutableMapOf<String, Any?>(
        "event_type" to this::class.simpleName,
        "timestamp" to System.currentTimeMillis(),
        "priority" to getPriority().name,
        "category" to getAnalyticsCategory()
    )
    
    when (this) {
        is PostEvent.TitleChanged -> baseMap["title"] = newTitle
        is PostEvent.DescriptionChanged -> baseMap["description_length"] = newDescription.length
        is PostEvent.ImageAdded -> baseMap["image_uri"] = imageUri.toString()
        is PostEvent.ImageRemoved -> baseMap["image_uri"] = imageUri.toString()
        is PostEvent.LocationAdded -> {
            baseMap["latitude"] = latitude
            baseMap["longitude"] = longitude
            baseMap["location_name"] = locationName
        }
        is PostEvent.CategoryChanged -> baseMap["category"] = category
        is PostEvent.TagAdded -> baseMap["tag"] = tag
        is PostEvent.TagRemoved -> baseMap["tag"] = tag
        is PostEvent.VisibilityChanged -> baseMap["visibility"] = visibility
        is PostEvent.ShowError -> baseMap["error_message"] = message
        is PostEvent.PostCreated -> baseMap["post_id"] = postId
        else -> {} // Object events don't have additional properties
    }
    
    return baseMap
}

/**
 * Creates a copy of this event with modified timestamp (for testing)
 */
fun PostEvent.withTimestamp(timestamp: Long): PostEvent {
    // Since PostEvent doesn't have timestamp, this is for future extension
    return this
}

/**
 * Checks if this event can be undone
 */
fun PostEvent.isUndoable(): Boolean {
    return when (this) {
        is PostEvent.TitleChanged,
        is PostEvent.DescriptionChanged,
        is PostEvent.ImageAdded,
        is PostEvent.ImageRemoved,
        is PostEvent.LocationAdded,
        is PostEvent.CategoryChanged,
        is PostEvent.TagAdded,
        is PostEvent.TagRemoved,
        is PostEvent.VisibilityChanged -> true
        is PostEvent.ClearAllImages,
        is PostEvent.ClearLocation,
        is PostEvent.ClearForm -> true
        else -> false
    }
}

/**
 * Priority levels for event processing
 */
enum class EventPriority {
    LOW, MEDIUM, HIGH
}

/**
 * Validation requirements for events
 */
enum class ValidationRequirement {
    NON_EMPTY,
    LENGTH_CHECK,
    PROFANITY_CHECK,
    COORDINATE_BOUNDS,
    LOCATION_NAME_CHECK,
    TAG_FORMAT,
    VALID_CATEGORY,
    VALID_VISIBILITY
}
