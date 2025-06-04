package com.android.Tripbook.Datamining.modules.data.comments.model

import kotlinx.serialization.*

@kotlinx.serialization.Serializable
data class CreateCommentRequest(
    val Post_id: String,
    val User_id: String, // After doing it without authentication , we are going to do it with Kotlin Jwt
    val value: String,
    val Parent_Comment_id: String? = null
)