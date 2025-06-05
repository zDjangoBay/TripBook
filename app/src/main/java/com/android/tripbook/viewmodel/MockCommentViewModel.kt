package com.android.tripbook.ViewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.android.tripbook.data.SampleComments
import com.android.tripbook.Model.Comment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MockCommentViewModel : ViewModel() {

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments.asStateFlow()

    private val commentsMap = mutableMapOf<Int, MutableList<Comment>>()

    fun loadCommentsForReview(reviewId: Int) {
        if (!commentsMap.containsKey(reviewId)) {
            // Generate mock comments for this review
            val mockComments = SampleComments.generateMockComments(reviewId)
            commentsMap[reviewId] = mockComments.toMutableList()
        }
        _comments.value = commentsMap[reviewId] ?: emptyList()
    }

    fun addComment(reviewId: Int, comment: Comment) {
        if (!commentsMap.containsKey(reviewId)) {
            commentsMap[reviewId] = mutableListOf()
        }
        commentsMap[reviewId]?.add(0, comment) // Add to beginning
        _comments.value = commentsMap[reviewId] ?: emptyList()
    }

    fun getCommentsForReview(reviewId: Int): List<Comment> {
        return commentsMap[reviewId] ?: emptyList()
    }
}