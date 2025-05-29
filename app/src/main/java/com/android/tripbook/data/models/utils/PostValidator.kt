package com.android.tripbook.utils

import com.android.tripbook.data.state.PostUIState

class PostValidator {
    fun isValid(state: PostUIState): Boolean {
        return state.title.isNotBlank() && 
               state.description.isNotBlank() &&
               state.title.length >= 3 &&
               state.description.length >= 10
    }

    fun validateTitle(title: String): String? {
        return when {
            title.isBlank() -> "Title is required"
            title.length < 3 -> "Title must be at least 3 characters"
            title.length > 100 -> "Title must be less than 100 characters"
            else -> null
        }
    }

    fun validateDescription(description: String): String? {
        return when {
            description.isBlank() -> "Description is required"
            description.length < 10 -> "Description must be at least 10 characters"
            description.length > 500 -> "Description must be less than 500 characters"
            else -> null
        }
    }

    fun validateHashtags(hashtags: String): String? {
        val tags = hashtags.split(" ").filter { it.isNotBlank() }
        val invalidTags = tags.filter { !it.startsWith("#") || it.length <= 1 }
        
        return when {
            tags.size > 10 -> "Maximum 10 hashtags allowed"
            invalidTags.isNotEmpty() -> "Hashtags must start with # and have at least one character"
            else -> null
        }
    }
}