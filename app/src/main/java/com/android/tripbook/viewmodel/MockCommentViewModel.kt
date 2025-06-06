package com.android.tripbook.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.android.tripbook.data.SampleComments
import com.android.tripbook.model.CommentReaction
import com.android.tripbook.model.Comment
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

    fun addReaction(reviewId: Int, commentId: String, emoji: String, username: String = "You") {
        val commentsList = commentsMap[reviewId] ?: return
        val commentIndex = commentsList.indexOfFirst { it.id == commentId }
        if (commentIndex == -1) return

        val comment = commentsList[commentIndex]

        // Create a new reaction
        val reaction = CommentReaction(
            commentId = commentId,
            emoji = emoji,
            username = username
        )

        // Add or update the reaction
        val updatedComment = comment.copy()
        if (!updatedComment.reactions.containsKey(emoji)) {
            updatedComment.reactions[emoji] = mutableListOf()
        }

        // Check if user already reacted with this emoji
        val existingReactionIndex = updatedComment.reactions[emoji]?.indexOfFirst { it.username == username }
        if (existingReactionIndex != null && existingReactionIndex != -1) {
            // User already reacted with this emoji, remove it (toggle behavior)
            updatedComment.reactions[emoji]?.removeAt(existingReactionIndex)
            if (updatedComment.reactions[emoji]?.isEmpty() == true) {
                updatedComment.reactions.remove(emoji)
            }
        } else {
            // Add new reaction
            updatedComment.reactions[emoji]?.add(reaction)
        }

        // Update the comment in the list
        commentsList[commentIndex] = updatedComment
        _comments.value = commentsList.toList()
    }

    fun hasUserReacted(commentId: String, emoji: String, username: String = "You"): Boolean {
        val comment = _comments.value.find { it.id == commentId } ?: return false
        return comment.reactions[emoji]?.any { it.username == username } == true
    }

    fun addReply(reviewId: Int, parentCommentId: String, replyText: String, username: String = "You") {
        val commentsList = commentsMap[reviewId] ?: return
        val parentCommentIndex = commentsList.indexOfFirst { it.id == parentCommentId }
        if (parentCommentIndex == -1) return

        val parentComment = commentsList[parentCommentIndex]

        // Create a new reply comment
        val reply = Comment(
            text = replyText,
            authorName = username,
            parentId = parentCommentId
        )

        // Add the reply to the parent comment
        val updatedComment = parentComment.copy()
        updatedComment.replies.add(0, reply) // Add to beginning of replies

        // Update the comment in the list
        commentsList[parentCommentIndex] = updatedComment
        _comments.value = commentsList.toList()
    }

    fun getRepliesForComment(commentId: String): List<Comment> {
        val comment = _comments.value.find { it.id == commentId } ?: return emptyList()
        return comment.replies
    }
}
