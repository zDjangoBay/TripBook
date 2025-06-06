package com.android.tripbook.posts.viewmodel

import com.android.tripbook.posts.model.ImageModel
import com.android.tripbook.data.model.LocationSearchItem // <-- Import canonique pour LocationSearchItem

import com.android.tripbook.posts.model.TagModel

data class PostUIState(
    val title: String = "",
    val description: String = "",
    val images: List<ImageModel> = emptyList(),
    val selectedLocation: LocationSearchItem? = null, // <-- Type mis à jour vers LocationSearchItem
    val selectedTags: List<TagModel> = emptyList(),
    val hashtagsInput: String = "",
    val isPublishing: Boolean = false,
    val titleError: String? = null,
    val descriptionError: String? = null,
    val imagesError: String? = null,
    val locationError: String? = null,
    val tagsError: String? = null,
    val isSearchingLocation: Boolean = false,
    val locationSearchResults: List<LocationSearchItem> = emptyList(),
    val locationSearchError: String? = null
)
