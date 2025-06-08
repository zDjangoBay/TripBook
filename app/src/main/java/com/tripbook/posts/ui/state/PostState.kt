package com.tripbook.posts.ui.state

data class PostState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)


