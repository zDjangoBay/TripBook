package com.android.tripbook.ui.theme.state


data class PostUIState(
    val title: String = "",
    val description: String = "",
    val images: List<String> = emptyList(),
    val location: String = "",
    val tags: List<String> = emptyList()
)
