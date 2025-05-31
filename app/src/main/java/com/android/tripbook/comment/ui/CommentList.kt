package com.android.tripbook.comment.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.android.tripbook.comment.model.Comment
import java.util.*

@Composable
fun CommentList(comments: List<Comment>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        items(comments) { comment ->
            CommentItem(comment = comment)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCommentList() {
    val mockComments = listOf(
        Comment("1", "u1", "Alice", null, "Beautiful sunset!", System.currentTimeMillis()),
        Comment("2", "u2", "Bob", null, "Amazing view!", System.currentTimeMillis())
    )
    CommentList(comments = mockComments)
}
