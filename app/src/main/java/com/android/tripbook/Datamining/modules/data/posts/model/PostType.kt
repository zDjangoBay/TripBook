package com.android.Tripbook.Datamining.modules.data.posts.model

import kotlinx.serialization.*

@Serializable
enum class PostType {// Here i want to make a distinction between the original post and a reply post and even a repost
    ORIGINAL,
    REPLY,
    REPOST
}