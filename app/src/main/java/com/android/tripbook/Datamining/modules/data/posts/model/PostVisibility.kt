package com.android.posts.model

import kotlinx.serialization.*


@Serializable
enum class PostVisibility {
    PUBLIC,
    FOLLOWERS_ONLY
}
