package com.android.Tripbook.Datamining.modules.data.posts.model

import kotlinx.serialization.*


@Serializable
enum class PostVisibility {
    PUBLIC,
    FOLLOWERS_ONLY
}