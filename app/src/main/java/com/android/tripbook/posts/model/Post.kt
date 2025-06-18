package com.android.tripbook.posts.model

import androidx.compose.runtime.Immutable
import android.net.Uri

@Immutable
data class Post(
    val id: String,
    val content: String,
    val originalLanguage: String? = null,
    val translatedContent: String? = null,
    val targetLanguage: String? = null,
    val author: String,
    val timestamp: Long,
    val isTranslated: Boolean = false,
    val photos: List<Uri> = emptyList()
)

data class TranslationState(
    val isTranslating: Boolean = false,
    val error: String? = null,
    val availableLanguages: List<String> = emptyList()
)

