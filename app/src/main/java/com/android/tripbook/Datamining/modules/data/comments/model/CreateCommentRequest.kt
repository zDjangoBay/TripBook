package com.android.comments.model

import kotlinx.serialization.*

/*
* This is the data class that represent the format used to transmit comment object from the mobile application to the server and vice-versa
* */


@kotlinx.serialization.Serializable
data class CreateCommentRequest(
    val Post_id: String,
    val User_id: String, // After doing it without authentication , we are going to do it with Kotlin Jwt
    val value: String,
    val Parent_Comment_id: String? = null
)