package com.android.tripbook.posts.viewmodel

import com.android.tripbook.posts.model.ImageModel
import com.android.tripbook.posts.model.Location
import com.android.tripbook.posts.model.PostModel
import com.android.tripbook.posts.model.TagModel

data class PostUIState(
    // Post list state
    val posts: List<PostModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    
    // Selected post for detail view
    val selectedPost: PostModel? = null,
    
    // Form state for creating/editing posts
    val title: String = "",
    val description: String = "",
    val selectedImages: List<ImageModel> = emptyList(),
    val selectedLocation: Location? = null,
    val selectedTags: List<TagModel> = emptyList(),
    val hashtags: String = "",
    val availableTags: List<TagModel> = emptyList(),
    
    // Form validation and submission
    val isFormValid: Boolean = false,
    val isSubmitting: Boolean = false,
    
    // Location search state
    val locationSearchResults: List<Location> = emptyList(),
    val isSearchingLocation: Boolean = false,
    val locationSearchError: String? = null
)