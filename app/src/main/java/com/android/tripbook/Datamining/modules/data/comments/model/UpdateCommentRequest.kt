package com.android.Tripbook.Datamining.modules.data.comments.model

import kotlinx.serialization.*

@kotlinx.serialization.Serializable
data class UpdateCommentRequest(
    val value: String
)