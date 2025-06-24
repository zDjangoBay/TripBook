package com.android.tripbook.posts.ui.state

import com.android.tripbook.posts.model.PostModel

data class PostListState(
    val posts: List<PostModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)