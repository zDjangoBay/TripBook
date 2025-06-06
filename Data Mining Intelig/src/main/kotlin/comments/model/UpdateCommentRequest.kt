package com.android.comments.model

import kotlinx.serialization.*

@kotlinx.serialization.Serializable
data class UpdateCommentRequest(
    val value: String
)