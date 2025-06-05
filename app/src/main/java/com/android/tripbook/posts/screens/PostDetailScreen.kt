package com.android.tripbook.posts.screens


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.tripbook.posts.model.PostModel
import com.android.tripbook.posts.model.Comment
import com.android.tripbook.posts.model.Location
import com.android.tripbook.posts.model.ImageModel
import com.android.tripbook.posts.model.PostVisibility
import com.android.tripbook.posts.ui.components.CommentItem
import com.android.tripbook.posts.ui.components.PostCard
import com.android.tripbook.ui.theme.TripBookTheme
import java.time.Instant
import android.net.Uri
import androidx.compose.material.icons.filled.Send

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(post: PostModel, currentUserId: String, onBack: () -> Unit) {
    var replyText by remember { mutableStateOf("") }
    var isReplyingToCommentId by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(post.title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            item {
                PostCard(
                    post = post,
                    onCardClick = { },
                    onLikeClick = { println("Like toggled on detail for post ${post.id}") },
                    onCommentClick = { println("Comment section scrolled to on post ${post.id}") }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Divider(modifier = Modifier.padding(horizontal = 16.dp))
                Text(
                    text = "Comments (${post.comments.size})",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                )
            }

            items(post.comments, key = { it.id }) { comment ->
                val isReplyingToThisComment = isReplyingToCommentId == comment.id

                CommentItem(
                    comment = comment,
                    currentUserId = currentUserId,
                    onReplyClick = {
                        isReplyingToCommentId = if (isReplyingToCommentId == comment.id) null else comment.id
                        replyText = ""
                    },
                    onSendReply = { text ->
                        println("Sending reply to ${comment.username}: $text")
                        isReplyingToCommentId = null
                        replyText = ""
                    },
                    isReplying = isReplyingToThisComment,
                    replyText = replyText,
                    onReplyTextChange = { replyText = it }
                )
                Divider(modifier = Modifier.padding(horizontal = 16.dp))
            }

            item {
                if (isReplyingToCommentId == null) {
                    OutlinedTextField(
                        value = replyText,
                        onValueChange = { replyText = it },
                        label = { Text("Add a comment...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    if (replyText.isNotBlank()) {
                                        println("Sending new comment: $replyText")
                                        replyText = ""
                                    }
                                },
                                enabled = replyText.isNotBlank()
                            ) {
                                Icon(Icons.Filled.Send, contentDescription = "Send Comment")
                            }
                        }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPostDetailScreen() {
    TripBookTheme {
        val samplePost = PostModel(
            id = "p1",
            userId = "user_host",
            username = "GlobalWanderer",
            userAvatar = null,
            isVerified = true,
            title = "My Epic Journey Through Southeast Asia!",
            description = "Just completed a fantastic backpacking trip across Thailand, Vietnam, and Cambodia. From the bustling streets of Bangkok to the serene temples of Angkor Wat, every moment was an adventure. The food, the people, the culture - absolutely mesmerizing! Can't wait to share more stories.",
            images = listOf(
                ImageModel("img1", Uri.parse("https://via.placeholder.com/600/FF0000/FFFFFF?text=Bangkok")),
                ImageModel("img2", Uri.parse("https://via.placeholder.com/600/00FF00/FFFFFF?text=AngkorWat")),
                ImageModel("img3", Uri.parse("https://via.placeholder.com/600/0000FF/FFFFFF?text=HaLongBay"))
            ),
            location = Location("locSEA", "Southeast Asia", "", "Multiple", null),
            tags = emptyList(),
            createdAt = Instant.now().minusSeconds(86400 * 5),
            lastEditedAt = Instant.now().minusSeconds(3600 * 2),
            visibility = PostVisibility.PUBLIC,
            collaborators = emptyList(),
            isEphemeral = false,
            ephemeralDurationMillis = null,
            likes = listOf("user_a", "user_b", "user_c"),
            comments = listOf(
                Comment(
                    id = "c1",
                    postId = "p1",
                    userId = "commenter1",
                    username = "ExplorerJohn",
                    userAvatar = null,
                    text = "Wow! This sounds incredible. Which country was your favorite?",
                    timestamp = Instant.now().minusSeconds(3600 * 3),
                    replies = listOf(
                        Comment(
                            id = "r1",
                            postId = "p1",
                            userId = "user_host",
                            username = "GlobalWanderer",
                            userAvatar = null,
                            text = "That's a tough one! I'd say Vietnam for its diverse landscapes and food, but Cambodia left a deep impression too.",
                            timestamp = Instant.now().minusSeconds(3600 * 1),
                            replies = emptyList()
                        ),
                        Comment(
                            id = "r2",
                            postId = "p1",
                            userId = "commenter2",
                            username = "TravelBuddy",
                            userAvatar = null,
                            text = "Agree on Vietnam! Did you try the egg coffee in Hanoi?",
                            timestamp = Instant.now().minusSeconds(1800),
                            replies = emptyList()
                        )
                    )
                ),
                Comment(
                    id = "c2",
                    postId = "p1",
                    userId = "commenter3",
                    username = "Wanderlust_Alice",
                    userAvatar = null,
                    text = "The photos are stunning! Makes me want to pack my bags right now. 😊",
                    timestamp = Instant.now().minusSeconds(7200),
                    replies = emptyList()
                )
            )
        )
        PostDetailScreen(post = samplePost, currentUserId = "user_a", onBack = {})
    }
}