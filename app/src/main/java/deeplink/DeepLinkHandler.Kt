package com.android.tripbook.deeplink

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Deep Link Handler for TripBook App
 * Handles various deep link patterns:
 * - tripbook://post/{postId}
 * - tripbook://user/{userId}
 * - tripbook://location/{latitude},{longitude}
 * - tripbook://search?query={searchTerm}
 * - tripbook://create/post
 * - https://tripbook.com/post/{postId}
 */
class DeepLinkHandler(private val activity: ComponentActivity) {
    
    companion object {
        private const val TAG = "DeepLinkHandler"
        
        // Deep link schemes
        const val CUSTOM_SCHEME = "tripbook"
        const val HTTPS_SCHEME = "https"
        const val HTTP_SCHEME = "http"
        
        // Deep link hosts
        const val TRIPBOOK_HOST = "tripbook.com"
        const val WWW_TRIPBOOK_HOST = "www.tripbook.com"
        
        // Deep link paths
        const val PATH_POST = "post"
        const val PATH_USER = "user"
        const val PATH_LOCATION = "location"
        const val PATH_SEARCH = "search"
        const val PATH_CREATE = "create"
        const val PATH_PROFILE = "profile"
        const val PATH_SETTINGS = "settings"
        const val PATH_CAMERA = "camera"
        const val PATH_GALLERY = "gallery"
        
        // Query parameters
        const val QUERY_SEARCH = "query"
        const val QUERY_LAT = "lat"
        const val QUERY_LNG = "lng"
        const val QUERY_ZOOM = "zoom"
        const val QUERY_FILTER = "filter"
        const val QUERY_CATEGORY = "category"
        const val QUERY_DATE = "date"
    }
    
    // Deep link state management
    var currentDeepLink by mutableStateOf<DeepLinkData?>(null)
        private set
    
    var isProcessingDeepLink by mutableStateOf(false)
        private set
    
    data class DeepLinkData(
        val type: DeepLinkType,
        val parameters: Map<String, String> = emptyMap(),
        val uri: Uri? = null,
        val timestamp: Long = System.currentTimeMillis()
    )
    
    enum class DeepLinkType {
        POST_DETAIL,
        USER_PROFILE,
        LOCATION_VIEW,
        SEARCH_RESULTS,
        CREATE_POST,
        SETTINGS,
        CAMERA,
        GALLERY,
        HOME,
        UNKNOWN
    }
    
    /**
     * Handle deep link from intent
     */
    fun handleDeepLink(intent: Intent?): Boolean {
        if (intent == null) return false
        
        val uri = intent.data
        if (uri == null) {
            Log.d(TAG, "No URI found in intent")
            return false
        }
        
        Log.d(TAG, "Processing deep link: $uri")
        isProcessingDeepLink = true
        
        try {
            val deepLinkData = parseDeepLink(uri)
            if (deepLinkData != null) {
                currentDeepLink = deepLinkData
                executeDeepLinkAction(deepLinkData)
                return true
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error processing deep link", e)
        } finally {
            isProcessingDeepLink = false
        }
        
        return false
    }
    
    /**
     * Parse URI to extract deep link data
     */
    private fun parseDeepLink(uri: Uri): DeepLinkData? {
        val scheme = uri.scheme?.lowercase()
        val host = uri.host?.lowercase()
        
        Log.d(TAG, "Parsing URI - Scheme: $scheme, Host: $host, Path: ${uri.path}")
        
        return when {
            // Custom scheme deep links (tripbook://)
            scheme == CUSTOM_SCHEME -> parseCustomSchemeDeepLink(uri)
            
            // HTTPS deep links (https://tripbook.com/)
            (scheme == HTTPS_SCHEME || scheme == HTTP_SCHEME) && 
            (host == TRIPBOOK_HOST || host == WWW_TRIPBOOK_HOST) -> parseWebDeepLink(uri)
            
            else -> {
                Log.w(TAG, "Unknown deep link format: $uri")
                null
            }
        }
    }
    
    /**
     * Parse custom scheme deep links (tripbook://)
     */
    private fun parseCustomSchemeDeepLink(uri: Uri): DeepLinkData? {
        val pathSegments = uri.pathSegments
        val parameters = mutableMapOf<String, String>()
        
        // Extract query parameters
        uri.queryParameterNames.forEach { paramName ->
            uri.getQueryParameter(paramName)?.let { value ->
                parameters[paramName] = value
            }
        }
        
        return when {
            // tripbook://post/123
            pathSegments.size >= 2 && pathSegments[0] == PATH_POST -> {
                parameters["postId"] = pathSegments[1]
                DeepLinkData(DeepLinkType.POST_DETAIL, parameters, uri)
            }
            
            // tripbook://user/456
            pathSegments.size >= 2 && pathSegments[0] == PATH_USER -> {
                parameters["userId"] = pathSegments[1]
                DeepLinkData(DeepLinkType.USER_PROFILE, parameters, uri)
            }
            
            // tripbook://location/40.7128,-74.0060
            pathSegments.size >= 2 && pathSegments[0] == PATH_LOCATION -> {
                val coords = pathSegments[1].split(",")
                if (coords.size == 2) {
                    parameters["latitude"] = coords[0]
                    parameters["longitude"] = coords[1]
                    DeepLinkData(DeepLinkType.LOCATION_VIEW, parameters, uri)
                } else null
            }
            
            // tripbook://search?query=beach
            pathSegments.size >= 1 && pathSegments[0] == PATH_SEARCH -> {
                if (parameters.containsKey(QUERY_SEARCH)) {
                    DeepLinkData(DeepLinkType.SEARCH_RESULTS, parameters, uri)
                } else null
            }
            
            // tripbook://create/post
            pathSegments.size >= 2 && pathSegments[0] == PATH_CREATE && pathSegments[1] == PATH_POST -> {
                DeepLinkData(DeepLinkType.CREATE_POST, parameters, uri)
            }
            
            // tripbook://camera
            pathSegments.size >= 1 && pathSegments[0] == PATH_CAMERA -> {
                DeepLinkData(DeepLinkType.CAMERA, parameters, uri)
            }
            
            // tripbook://gallery
            pathSegments.size >= 1 && pathSegments[0] == PATH_GALLERY -> {
                DeepLinkData(DeepLinkType.GALLERY, parameters, uri)
            }
            
            // tripbook://settings
            pathSegments.size >= 1 && pathSegments[0] == PATH_SETTINGS -> {
                DeepLinkData(DeepLinkType.SETTINGS, parameters, uri)
            }
            
            // tripbook://
            pathSegments.isEmpty() -> {
                DeepLinkData(DeepLinkType.HOME, parameters, uri)
            }
            
            else -> {
                Log.w(TAG, "Unknown custom scheme path: ${uri.path}")
                DeepLinkData(DeepLinkType.UNKNOWN, parameters, uri)
            }
        }
    }
    
    /**
     * Parse web deep links (https://tripbook.com/)
     */
    private fun parseWebDeepLink(uri: Uri): DeepLinkData? {
        val pathSegments = uri.pathSegments
        val parameters = mutableMapOf<String, String>()
        
        // Extract query parameters
        uri.queryParameterNames.forEach { paramName ->
            uri.getQueryParameter(paramName)?.let { value ->
                parameters[paramName] = value
            }
        }
        
        return when {
            // https://tripbook.com/post/123
            pathSegments.size >= 2 && pathSegments[0] == PATH_POST -> {
                parameters["postId"] = pathSegments[1]
                DeepLinkData(DeepLinkType.POST_DETAIL, parameters, uri)
            }
            
            // https://tripbook.com/user/456
            pathSegments.size >= 2 && pathSegments[0] == PATH_USER -> {
                parameters["userId"] = pathSegments[1]
                DeepLinkData(DeepLinkType.USER_PROFILE, parameters, uri)
            }
            
            // https://tripbook.com/search?query=beach
            pathSegments.size >= 1 && pathSegments[0] == PATH_SEARCH -> {
                if (parameters.containsKey(QUERY_SEARCH)) {
                    DeepLinkData(DeepLinkType.SEARCH_RESULTS, parameters, uri)
                } else null
            }
            
            // https://tripbook.com/
            pathSegments.isEmpty() -> {
                DeepLinkData(DeepLinkType.HOME, parameters, uri)
            }
            
            else -> {
                Log.w(TAG, "Unknown web path: ${uri.path}")
                DeepLinkData(DeepLinkType.UNKNOWN, parameters, uri)
            }
        }
    }
    
    /**
     * Execute action based on deep link data
     */
    private fun executeDeepLinkAction(deepLinkData: DeepLinkData) {
        Log.d(TAG, "Executing deep link action: ${deepLinkData.type}")
        
        when (deepLinkData.type) {
            DeepLinkType.POST_DETAIL -> {
                val postId = deepLinkData.parameters["postId"]
                if (postId != null) {
                    navigateToPostDetail(postId)
                }
            }
            
            DeepLinkType.USER_PROFILE -> {
                val userId = deepLinkData.parameters["userId"]
                if (userId != null) {
                    navigateToUserProfile(userId)
                }
            }
            
            DeepLinkType.LOCATION_VIEW -> {
                val lat = deepLinkData.parameters["latitude"]
                val lng = deepLinkData.parameters["longitude"]
                if (lat != null && lng != null) {
                    navigateToLocation(lat.toDoubleOrNull(), lng.toDoubleOrNull())
                }
            }
            
            DeepLinkType.SEARCH_RESULTS -> {
                val query = deepLinkData.parameters[QUERY_SEARCH]
                val filter = deepLinkData.parameters[QUERY_FILTER]
                val category = deepLinkData.parameters[QUERY_CATEGORY]
                if (query != null) {
                    navigateToSearch(query, filter, category)
                }
            }
            
            DeepLinkType.CREATE_POST -> {
                navigateToCreatePost()
            }
            
            DeepLinkType.CAMERA -> {
                navigateToCamera()
            }
            
            DeepLinkType.GALLERY -> {
                navigateToGallery()
            }
            
            DeepLinkType.SETTINGS -> {
                navigateToSettings()
            }
            
            DeepLinkType.HOME -> {
                navigateToHome()
            }
            
            DeepLinkType.UNKNOWN -> {
                Log.w(TAG, "Unknown deep link type, navigating to home")
                navigateToHome()
            }
        }
    }
    
    // Navigation methods - these would integrate with your navigation system
    private fun navigateToPostDetail(postId: String) {
        Log.d(TAG, "Navigating to post detail: $postId")
        // TODO: Implement navigation to post detail screen
        // Example: navController.navigate("post_detail/$postId")
    }
    
    private fun navigateToUserProfile(userId: String) {
        Log.d(TAG, "Navigating to user profile: $userId")
        // TODO: Implement navigation to user profile screen
        // Example: navController.navigate("user_profile/$userId")
    }
    
    private fun navigateToLocation(latitude: Double?, longitude: Double?) {
        if (latitude != null && longitude != null) {
            Log.d(TAG, "Navigating to location: $latitude, $longitude")
            // TODO: Implement navigation to location/map screen
            // Example: navController.navigate("location/$latitude/$longitude")
        }
    }
    
    private fun navigateToSearch(query: String, filter: String?, category: String?) {
        Log.d(TAG, "Navigating to search: query=$query, filter=$filter, category=$category")
        // TODO: Implement navigation to search screen
        // Example: navController.navigate("search?query=$query&filter=$filter&category=$category")
    }
    
    private fun navigateToCreatePost() {
        Log.d(TAG, "Navigating to create post")
        // TODO: Implement navigation to create post screen
        // Example: navController.navigate("create_post")
    }
    
    private fun navigateToCamera() {
        Log.d(TAG, "Navigating to camera")
        // TODO: Implement navigation to camera screen
        // Example: navController.navigate("camera")
    }
    
    private fun navigateToGallery() {
        Log.d(TAG, "Navigating to gallery")
        // TODO: Implement navigation to gallery screen
        // Example: navController.navigate("gallery")
    }
    
    private fun navigateToSettings() {
        Log.d(TAG, "Navigating to settings")
        // TODO: Implement navigation to settings screen
        // Example: navController.navigate("settings")
    }
    
    private fun navigateToHome() {
        Log.d(TAG, "Navigating to home")
        // TODO: Implement navigation to home screen
        // Example: navController.navigate("home")
    }
    
    /**
     * Generate deep link URI for sharing
     */
    fun generateDeepLink(type: DeepLinkType, parameters: Map<String, String> = emptyMap()): String {
        return when (type) {
            DeepLinkType.POST_DETAIL -> {
                val postId = parameters["postId"] ?: ""
                "tripbook://post/$postId"
            }
            
            DeepLinkType.USER_PROFILE -> {
                val userId = parameters["userId"] ?: ""
                "tripbook://user/$userId"
            }
            
            DeepLinkType.LOCATION_VIEW -> {
                val lat = parameters["latitude"] ?: "0"
                val lng = parameters["longitude"] ?: "0"
                "tripbook://location/$lat,$lng"
            }
            
            DeepLinkType.SEARCH_RESULTS -> {
                val query = parameters[QUERY_SEARCH] ?: ""
                val filter = parameters[QUERY_FILTER]
                val category = parameters[QUERY_CATEGORY]
                
                var url = "tripbook://search?query=$query"
                if (filter != null) url += "&filter=$filter"
                if (category != null) url += "&category=$category"
                url
            }
            
            DeepLinkType.CREATE_POST -> "tripbook://create/post"
            DeepLinkType.CAMERA -> "tripbook://camera"
            DeepLinkType.GALLERY -> "tripbook://gallery"
            DeepLinkType.SETTINGS -> "tripbook://settings"
            DeepLinkType.HOME -> "tripbook://"
            DeepLinkType.UNKNOWN -> "tripbook://"
        }
    }
    
    /**
     * Generate web deep link for sharing
     */
    fun generateWebDeepLink(type: DeepLinkType, parameters: Map<String, String> = emptyMap()): String {
        return when (type) {
            DeepLinkType.POST_DETAIL -> {
                val postId = parameters["postId"] ?: ""
                "https://tripbook.com/post/$postId"
            }
            
            DeepLinkType.USER_PROFILE -> {
                val userId = parameters["userId"] ?: ""
                "https://tripbook.com/user/$userId"
            }
            
            DeepLinkType.SEARCH_RESULTS -> {
                val query = parameters[QUERY_SEARCH] ?: ""
                val filter = parameters[QUERY_FILTER]
                val category = parameters[QUERY_CATEGORY]
                
                var url = "https://tripbook.com/search?query=$query"
                if (filter != null) url += "&filter=$filter"
                if (category != null) url += "&category=$category"
                url
            }
            
            else -> "https://tripbook.com"
        }
    }
    
    /**
     * Clear current deep link
     */
    fun clearDeepLink() {
        currentDeepLink = null
    }
    
    /**
     * Check if deep link is being processed
     */
    fun isProcessing(): Boolean = isProcessingDeepLink
}