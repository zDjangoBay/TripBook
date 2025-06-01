package com.android.tripbook.viewmodel

import androidx.lifecycle.ViewModel
import com.android.tripbook.model.ReviewComment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.*
import java.util.Date
import java.util.Locale
class MockCommentViewModel : ViewModel() {

    private val _comments = MutableStateFlow<List<ReviewComment>>(emptyList())
    val comments: StateFlow<List<ReviewComment>> = _comments

    fun addComment(tripId: Int, content: String, username: String, imageUri: String? = null) {
        val newComment = ReviewComment(
            id = _comments.value.size + 1,
            tripId = tripId,
            username = username,
            content = content,
            timestamp = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(Date()),
            imageUri = imageUri
        )
        _comments.value = listOf(newComment) + _comments.value

        val additionalComment1 = ReviewComment(
            id = _comments.value.size + 2,
            tripId = tripId,
            username = "Traveler456",
            content = "Had a great time exploring the city!",
            timestamp = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(Date()),
            imageUri = "https://images.unsplash.com/photo-1506748686214-e9df14d4d9d0" // Example image URI
        )

        val additionalComment2 = ReviewComment(
            id = _comments.value.size + 3,
            tripId = tripId,
            username = "Adventurer789",
            content = "The views were breathtaking!",
            timestamp = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(Date()),
            imageUri =  "https://images.unsplash.com/photo-1562577300-3c8c3c7c0c4b" // Example image URI
        )}

    fun getCommentsForTrip(tripId: Int): List<ReviewComment> {
        return _comments.value.filter { it.tripId == tripId }

    }
}