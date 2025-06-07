package com.TripBook.postmodule

/**
 * Constants and configuration values for the post module.
 * Centralizes all magic numbers, limits, and configuration settings.
 *
 * @author Feukoun Marel
 * @version 1.0
 * @since TripBook v1.0
 */
object PostConstants {
    
    // Text limits
    object TextLimits {
        const val MIN_TITLE_LENGTH = 3
        const val MAX_TITLE_LENGTH = 100
        const val MIN_DESCRIPTION_LENGTH = 10
        const val MAX_DESCRIPTION_LENGTH = 1000
        const val MAX_TAG_LENGTH = 30
        const val MIN_TAG_LENGTH = 2
        const val MAX_LOCATION_NAME_LENGTH = 100
        const val MAX_HASHTAG_LENGTH = 50
    }
    
    // Media limits
    object MediaLimits {
        const val MAX_IMAGES_COUNT = 10
        const val MAX_IMAGE_SIZE_MB = 5
        const val MAX_IMAGE_SIZE_BYTES = MAX_IMAGE_SIZE_MB * 1024 * 1024
        const val SUPPORTED_IMAGE_FORMATS = "jpg,jpeg,png,webp"
        const val IMAGE_COMPRESSION_QUALITY = 85
        const val THUMBNAIL_SIZE = 200
    }
    
    // Collection limits
    object CollectionLimits {
        const val MAX_TAGS_COUNT = 20
        const val MAX_HASHTAGS_COUNT = 15
        const val MAX_DRAFTS_COUNT = 10
        const val MAX_RECENT_LOCATIONS = 50
        const val MAX_SEARCH_HISTORY = 100
    }
    
    // Geographic bounds
    object GeoBounds {
        const val MIN_LATITUDE = -90.0
        const val MAX_LATITUDE = 90.0
        const val MIN_LONGITUDE = -180.0
        const val MAX_LONGITUDE = 180.0
        const val DEFAULT_LOCATION_ACCURACY = 100.0 // meters
        const val MAX_LOCATION_AGE = 300_000L // 5 minutes
    }
    
    // Timing constants
    object Timing {
        const val AUTO_SAVE_INTERVAL_MS = 30_000L // 30 seconds
        const val DEBOUNCE_DELAY_MS = 500L // 0.5 seconds
        const val NETWORK_TIMEOUT_MS = 30_000L // 30 seconds
        const val IMAGE_UPLOAD_TIMEOUT_MS = 60_000L // 1 minute
        const val DRAFT_CLEANUP_INTERVAL_MS = 86_400_000L // 24 hours
    }
    
    // Post categories
    object Categories {
        val TRAVEL_CATEGORIES = listOf(
            "Adventure",
            "Beach",
            "City Break",
            "Cultural",
            "Food & Drink",
            "Nature",
            "Photography",
            "Relaxation",
            "Road Trip",
            "Shopping",
            "Sports",
            "Wildlife",
            "Other"
        )
        
        val CATEGORY_ICONS = mapOf(
            "Adventure" to "🏔️",
            "Beach" to "🏖️",
            "City Break" to "🏙️",
            "Cultural" to "🏛️",
            "Food & Drink" to "🍽️",
            "Nature" to "🌿",
            "Photography" to "📸",
            "Relaxation" to "🧘",
            "Road Trip" to "🚗",
            "Shopping" to "🛍️",
            "Sports" to "⚽",
            "Wildlife" to "🦁",
            "Other" to "📝"
        )
    }
    
    // Visibility options
    object Visibility {
        const val PUBLIC = "Public"
        const val FRIENDS = "Friends"
        const val PRIVATE = "Private"
        const val FOLLOWERS = "Followers"
        const val CUSTOM = "Custom"
        
        val ALL_OPTIONS = listOf(PUBLIC, FRIENDS, PRIVATE, FOLLOWERS, CUSTOM)
        
        val VISIBILITY_DESCRIPTIONS = mapOf(
            PUBLIC to "Anyone can see this post",
            FRIENDS to "Only your friends can see this post",
            PRIVATE to "Only you can see this post",
            FOLLOWERS to "Only your followers can see this post",
            CUSTOM to "Custom privacy settings"
        )
    }
    
    // Popular travel tags
    object PopularTags {
        val ACTIVITY_TAGS = listOf(
            "hiking", "swimming", "sightseeing", "photography", "shopping",
            "dining", "nightlife", "museums", "beaches", "mountains"
        )
        
        val MOOD_TAGS = listOf(
            "relaxing", "adventurous", "romantic", "family-friendly", "solo-travel",
            "budget-friendly", "luxury", "cultural", "spiritual", "educational"
        )
        
        val SEASON_TAGS = listOf(
            "spring", "summer", "autumn", "winter", "rainy-season", "dry-season"
        )
        
        val TRANSPORT_TAGS = listOf(
            "flight", "train", "bus", "car", "motorcycle", "bicycle", "walking", "boat"
        )
    }
    
    // Error messages
    object ErrorMessages {
        const val NETWORK_ERROR = "Network connection failed. Please check your internet connection."
        const val UPLOAD_FAILED = "Failed to upload images. Please try again."
        const val LOCATION_UNAVAILABLE = "Location services are not available."
        const val PERMISSION_DENIED = "Permission denied. Please grant necessary permissions."
        const val INVALID_INPUT = "Please check your input and try again."
        const val SERVER_ERROR = "Server error occurred. Please try again later."
        const val DRAFT_SAVE_FAILED = "Failed to save draft. Please try again."
        const val POST_SUBMIT_FAILED = "Failed to submit post. Please try again."
    }
    
    // Success messages
    object SuccessMessages {
        const val POST_CREATED = "Post created successfully!"
        const val DRAFT_SAVED = "Draft saved successfully"
        const val IMAGE_UPLOADED = "Image uploaded successfully"
        const val LOCATION_ADDED = "Location added successfully"
        const val TAG_ADDED = "Tag added successfully"
    }
    
    // Validation patterns
    object ValidationPatterns {
        const val HASHTAG_REGEX = "^[a-zA-Z0-9_]+$"
        const val EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        const val URL_REGEX = "^https?://[\\w\\-]+(\\.[\\w\\-]+)+[/#?]?.*$"
        const val PHONE_REGEX = "^[+]?[1-9]\\d{1,14}$"
    }
    
    // API endpoints (if needed)
    object ApiEndpoints {
        const val CREATE_POST = "/api/posts"
        const val UPLOAD_IMAGE = "/api/posts/images"
        const val SAVE_DRAFT = "/api/posts/drafts"
        const val GET_LOCATION_SUGGESTIONS = "/api/locations/search"
        const val GET_TAG_SUGGESTIONS = "/api/tags/suggestions"
    }
    
    // Cache keys
    object CacheKeys {
        const val RECENT_LOCATIONS = "recent_locations"
        const val POPULAR_TAGS = "popular_tags"
        const val USER_PREFERENCES = "user_preferences"
        const val DRAFT_AUTO_SAVE = "draft_auto_save"
        const val LAST_CATEGORY = "last_selected_category"
    }
    
    // Feature flags
    object FeatureFlags {
        const val ENABLE_AUTO_SAVE = true
        const val ENABLE_LOCATION_SUGGESTIONS = true
        const val ENABLE_TAG_SUGGESTIONS = true
        const val ENABLE_IMAGE_COMPRESSION = true
        const val ENABLE_OFFLINE_MODE = true
        const val ENABLE_ANALYTICS = true
    }
    
    // Analytics events
    object AnalyticsEvents {
        const val POST_CREATED = "post_created"
        const val DRAFT_SAVED = "draft_saved"
        const val IMAGE_ADDED = "image_added"
        const val LOCATION_ADDED = "location_added"
        const val TAG_ADDED = "tag_added"
        const val FORM_ABANDONED = "form_abandoned"
        const val VALIDATION_ERROR = "validation_error"
    }
    
    // Notification types
    object NotificationTypes {
        const val DRAFT_AUTO_SAVED = "draft_auto_saved"
        const val POST_UPLOAD_PROGRESS = "post_upload_progress"
        const val POST_UPLOAD_COMPLETE = "post_upload_complete"
        const val POST_UPLOAD_FAILED = "post_upload_failed"
        const val LOCATION_PERMISSION_NEEDED = "location_permission_needed"
    }
    
    // Default values
    object Defaults {
        const val DEFAULT_CATEGORY = "Other"
        const val DEFAULT_VISIBILITY = Visibility.PUBLIC
        const val DEFAULT_AUTO_SAVE_ENABLED = true
        const val DEFAULT_LOCATION_ENABLED = true
        const val DEFAULT_IMAGE_QUALITY = MediaLimits.IMAGE_COMPRESSION_QUALITY
    }
}
