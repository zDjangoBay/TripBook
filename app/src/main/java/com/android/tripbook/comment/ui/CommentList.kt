package com.android.tripbook.comment.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert // Use MoreVert as a fallback icon
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.tripbook.comment.model.Comment


enum class CommentSortOption {
    NEWEST, OLDEST, MOST_LIKED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentList(
    comments: List<Comment>,
    currentUserId: String,
    onReply: (Comment) -> Unit,
    onLike: (Comment) -> Unit,
    onDelete: (Comment) -> Unit,
    modifier: Modifier = Modifier
) {
    var sortOption by remember { mutableStateOf(CommentSortOption.NEWEST) }
    var showSortMenu by remember { mutableStateOf(false) }

    val sortedComments = when (sortOption) {
        CommentSortOption.NEWEST -> comments.sortedByDescending { it.timestamp }
        CommentSortOption.OLDEST -> comments.sortedBy { it.timestamp }
        CommentSortOption.MOST_LIKED -> comments.sortedByDescending { it.likes }
    }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${comments.size} Comments",
                style = MaterialTheme.typography.titleMedium
            )

            Box {
                IconButton(onClick = { showSortMenu = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert, // Use Sort if you add extended icons
                        contentDescription = "Sort comments"
                    )
                }

                DropdownMenu(
                    expanded = showSortMenu,
                    onDismissRequest = { showSortMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Newest first") },
                        onClick = {
                            sortOption = CommentSortOption.NEWEST
                            showSortMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Oldest first") },
                        onClick = {
                            sortOption = CommentSortOption.OLDEST
                            showSortMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Most liked") },
                        onClick = {
                            sortOption = CommentSortOption.MOST_LIKED
                            showSortMenu = false
                        }
                    )
                }
            }
        }

        LazyColumn {
            items(sortedComments) { comment ->
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
}
