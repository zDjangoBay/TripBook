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
        return title.isNotBlank() && 
               title.length >= 3 &&
               description.isNotBlank() && 
               description.length >= 10 &&
               location != null
    }
    
    fun validateTitle(title: String): String? {
        return when {
            title.isBlank() -> "Title cannot be empty"
            title.length < 3 -> "Title must be at least 3 characters"
            title.length > 100 -> "Title must be less than 100 characters"
            else -> null
        }
    }
    
    fun validateDescription(description: String): String? {
        return when {
            description.isBlank() -> "Description cannot be empty"
            description.length < 10 -> "Description must be at least 10 characters"
            description.length > 1000 -> "Description must be less than 1000 characters"
            else -> null
        }
    }
}