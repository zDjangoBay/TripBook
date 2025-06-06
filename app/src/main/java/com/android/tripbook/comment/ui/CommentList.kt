package com.android.tripbook.comment.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.android.tripbook.comment.model.Comment

@Composable
fun CommentList(
    comments: List<Comment>,
    currentUserId: String,
    onReply: (Comment) -> Unit,
    onLike: (Comment) -> Unit,
    onDelete: (Comment) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(comments) { comment ->
            CommentItem(
                comment = comment,
                currentUserId = currentUserId,
                onReply = onReply,
                onLike = onLike,
                onDelete = onDelete
            )
        }
    }
}
