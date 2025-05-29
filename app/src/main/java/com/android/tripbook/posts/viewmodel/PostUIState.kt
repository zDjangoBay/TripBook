package com.android.tripbook.posts.viewmodel

import com.android.tripbook.posts.model.PostModel
import com.android.tripbook.posts.model.ImageModel
import com.android.tripbook.posts.model.TagModel
import com.android.tripbook.posts.model.Location // Assuming Location model is in this package

data class PostUIState(
    val posts: List<PostModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedPost: PostModel? = null,

    // Create post form state
    val title: String = "",
    val description: String = "",
    val selectedImages: List<ImageModel> = emptyList(),
    val selectedLocation: Location? = null,
    val selectedTags: List<TagModel> = emptyList(),
    val hashtags: String = "",
    val isSubmitting: Boolean = false,
    val isFormValid: Boolean = false,

    // Available options
    val availableTags: List<TagModel> = emptyList(),

    // Location Search State - THESE MUST BE PRESENT
    val locationSearchResults: List<Location> = emptyList(),
    val isSearchingLocation: Boolean = false,
    val locationSearchError: String? = null
)
