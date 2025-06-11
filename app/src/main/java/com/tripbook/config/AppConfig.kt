package com.tripbook.config

import com.android.tripbook.BuildConfig

/**
 * Configuration constants for the TripBook application
 */
object AppConfig {

    // API Configuration
    object Api {
        const val BASE_URL = "https://api.tripbook.com/" // Replace with your actual API URL
        const val CONNECT_TIMEOUT = 30L // seconds
        const val READ_TIMEOUT = 30L // seconds
        const val WRITE_TIMEOUT = 30L // seconds

        // API Endpoints
        const val POSTS_ENDPOINT = "posts"
        const val UPLOAD_ENDPOINT = "upload/image"
        const val USERS_ENDPOINT = "users"
    }

    // Image Configuration
    object Images {
        const val MAX_IMAGE_SIZE = 1024 * 1024 * 5 // 5MB
        const val COMPRESSION_QUALITY = 80
        const val MAX_WIDTH = 1920
        const val MAX_HEIGHT = 1080
        const val MAX_IMAGES_PER_POST = 10

        // Supported formats
        val SUPPORTED_FORMATS = listOf("image/jpeg", "image/png", "image/webp")
        val SUPPORTED_EXTENSIONS = listOf(".jpg", ".jpeg", ".png", ".webp")
    }

    // Validation Configuration
    object Validation {
        // Post validation
        const val MIN_TITLE_LENGTH = 3
        const val MAX_TITLE_LENGTH = 100
        const val MIN_CONTENT_LENGTH = 10
        const val MAX_CONTENT_LENGTH = 2000
        const val MIN_LOCATION_LENGTH = 2
        const val MAX_LOCATION_LENGTH = 100

        // Tags validation
        const val MAX_TAGS_COUNT = 10
        const val MAX_TAG_LENGTH = 30

        // User validation
        const val MIN_USER_ID_LENGTH = 3
        const val MAX_USER_ID_LENGTH = 50
    }

    // Pagination Configuration
    object Pagination {
        const val DEFAULT_PAGE_SIZE = 20
        const val MAX_PAGE_SIZE = 100
        const val MIN_PAGE_SIZE = 5
    }

    // Cache Configuration
    object Cache {
        const val POSTS_CACHE_DURATION = 5 * 60 * 1000L // 5 minutes in milliseconds
        const val IMAGES_CACHE_DURATION = 24 * 60 * 60 * 1000L // 24 hours in milliseconds
        const val USER_CACHE_DURATION = 10 * 60 * 1000L // 10 minutes in milliseconds
    }

    // File Provider Configuration
    object FileProvider {
        const val AUTHORITY = "com.android.tripbook.fileprovider"
        const val IMAGES_DIRECTORY = "Pictures"
        const val CACHE_DIRECTORY = "cache"
    }

    // Database Configuration
    object Database {
        const val DATABASE_NAME = "tripbook_database"
        const val DATABASE_VERSION = 1
    }

    // Feature Flags
    object Features {
        const val ENABLE_OFFLINE_MODE = true
        const val ENABLE_IMAGE_COMPRESSION = true
        const val ENABLE_ANALYTICS = false
        const val ENABLE_CRASH_REPORTING = false
        const val ENABLE_DETAILED_LOGGING = BuildConfig.DEBUG
    }

    // UI Configuration
    object UI {
        const val SPLASH_SCREEN_DURATION = 2000L // milliseconds
        const val ANIMATION_DURATION = 300L // milliseconds
        const val DEBOUNCE_DELAY = 500L // milliseconds for search debouncing
    }

    // Retry Configuration
    object Retry {
        const val MAX_RETRY_ATTEMPTS = 3
        const val INITIAL_RETRY_DELAY = 1000L // milliseconds
        const val MAX_RETRY_DELAY = 10000L // milliseconds
        const val RETRY_MULTIPLIER = 2.0
    }

    // Security Configuration
    object Security {
        const val ENABLE_CERTIFICATE_PINNING = false // Enable in production
        const val ENABLE_TLS_1_3 = true
        const val SESSION_TIMEOUT = 30 * 60 * 1000L // 30 minutes in milliseconds
    }

    // Performance Configuration
    object Performance {
        const val MEMORY_CACHE_SIZE = 50 // Number of items to keep in memory
        const val DISK_CACHE_SIZE = 100 * 1024 * 1024L // 100MB
        const val IMAGE_POOL_SIZE = 20 // Maximum number of concurrent image operations
    }

    // Analytics Events (if analytics is enabled)
    object Analytics {
        const val POST_CREATED = "post_created"
        const val POST_VIEWED = "post_viewed"
        const val POST_LIKED = "post_liked"
        const val POST_SHARED = "post_shared"
        const val IMAGE_UPLOADED = "image_uploaded"
        const val SEARCH_PERFORMED = "search_performed"
        const val ERROR_OCCURRED = "error_occurred"
    }

    // Error Messages
    object ErrorMessages {
        const val NETWORK_ERROR = "Please check your internet connection and try again"
        const val SERVER_ERROR = "Server is temporarily unavailable. Please try again later"
        const val VALIDATION_ERROR = "Please check your input and try again"
        const val PERMISSION_ERROR = "Required permissions are not granted"
        const val UNKNOWN_ERROR = "An unexpected error occurred. Please try again"
    }
}
