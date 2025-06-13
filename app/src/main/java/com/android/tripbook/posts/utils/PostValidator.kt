package com.android.tripbook.posts.utils

import com.android.tripbook.posts.model.ImageModel
import com.android.tripbook.posts.model.Location

class PostValidator {
    fun validatePost(
        title: String,
        description: String,
        location: Location?,
        images: List<ImageModel>
    ): Boolean {
        return title.isNotBlank() && description.isNotBlank() && location != null && images.isNotEmpty()
    }
}