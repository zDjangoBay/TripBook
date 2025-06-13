package com.android.tripbook.posts.model

data class TagModel(
    val id: String,
    val name: String,
    val category: Category,
    val isTrending: Boolean
)