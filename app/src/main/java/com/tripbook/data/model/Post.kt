package com.tripbook.data.model

import java.util.Date

/**
 * Data model representing a post in the TripBook app
 */
data class Post(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val content: String = "",
    val location: String = "",
    val images: List<String> = emptyList(),
    val timestamp: Date = Date(),
    val likes: Int = 0,
    val comments: List<Comment> = emptyList(),
    val tags: List<String> = emptyList()
)

/**
 * Data model representing a comment on a post
 */
data class Comment(
    val id: String = "",
    val userId: String = "",
    val content: String = "",
    val timestamp: Date = Date()
)
