// PostUIState.kt
package com.android.tripbook.data.state

import com.android.tripbook.data.models.ImageModel
import com.android.tripbook.data.models.PostModel
import com.android.tripbook.data.models.TagModel

data class PostUIState(
    val title: String = "",
    val description: String = "",
    val images: List<ImageModel> = emptyList(),
    val location: String = "",
    val selectedTags: List<TagModel> = emptyList(),
    val availableTags: List<TagModel> = TagModel.getDefaultTags(),
    val hashtags: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isFormValid: Boolean = false,
    val posts: List<PostModel> = emptyList()
) {
    val hashtagsList: List<String>
        get() = hashtags.split(" ")
            .filter { it.startsWith("#") && it.length > 1 }
            .map { it.removePrefix("#") }
}