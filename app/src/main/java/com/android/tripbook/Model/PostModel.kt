package com.android.tripbook.model

data class PostModel(
    val title: String = "",
    val description: String = "",
    val images: List<String> = emptyList(), // URIs or paths
    val location: String? = null,
    val tags: List<String> = emptyList()
)
