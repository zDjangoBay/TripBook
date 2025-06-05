package com.android.tripbook.posts.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.tripbook.posts.model.PostModel
import com.android.tripbook.posts.model.PostVisibility
import com.android.tripbook.posts.ui.components.PostCard
import com.android.tripbook.ui.theme.TripBookTheme
import java.time.Instant
import com.android.tripbook.posts.model.Comment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportedPostsScreen(reportedPosts: List<PostModel>, onResolveReport: (PostModel) -> Unit, onDeletePost: (PostModel) -> Unit, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reported Posts (Moderation)") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (reportedPosts.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text("No reported posts at the moment.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(horizontal = 8.dp)
            ) {
                items(reportedPosts, key = { it.id }) { post ->
                    PostCard(
                        post = post,
                        onCardClick = { println("Clicked reported post: ${post.id}") },
                        onLikeClick = {  },
                        onCommentClick = {  },
                        currentUserId = "moderator_id"
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(onClick = { onResolveReport(post) }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)) {
                            Text("Resolve")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = { onDeletePost(post) }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                            Text("Delete")
                        }
                    }
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewReportedPostsScreen() {
    TripBookTheme {
        val sampleReportedPosts = listOf(
            PostModel(
                id = "rp1",
                userId = "user_spam",
                username = "Spammer",
                userAvatar = null,
                isVerified = false,
                title = "Suspicious Ad",
                description = "Earn €1000 per day traveling! Click here!",
                images = emptyList(),
                // Utilise TravelLocation de data.model
                location = com.android.tripbook.posts.model.TravelLocation(
                    id = "loc_spam1",
                    name = "Internet",
                    latitude = 0.0,
                    longitude = 0.0,
                    description = null,
                    imageUrl = null
                ),
                tags = emptyList(),
                createdAt = Instant.now(),
                lastEditedAt = null,
                visibility = PostVisibility.PUBLIC,
                collaborators = null,
                isEphemeral = false,
                ephemeralDurationMillis = null,
                likes = emptyList(),
                comments = emptyList<Comment>()
                // Utilise la classe Comment du module posts
            ),
            PostModel(
                id = "rp2",
                userId = "user_hate",
                username = "Hater",
                userAvatar = null,
                isVerified = false,
                title = "Inappropriate Content",
                description = "This post contains hateful remarks.",
                images = emptyList(),
                // Utilise TravelLocation de data.model
                location = com.android.tripbook.posts.model.TravelLocation(
                    id = "loc_hate1",
                    name = "Online",
                    latitude = 0.0,
                    longitude = 0.0,
                    description = null,
                    imageUrl = null
                ),
                tags = emptyList(),
                createdAt = Instant.now(),
                lastEditedAt = null,
                visibility = PostVisibility.PUBLIC,
                collaborators = null,
                isEphemeral = false,
                ephemeralDurationMillis = null,
                likes = emptyList(),
                comments = emptyList<Comment>()
                // Utilise la classe Comment du module posts
            )
        )
        ReportedPostsScreen(
            reportedPosts = sampleReportedPosts,
            onResolveReport = { post -> println("Resolve ${post.id}") },
            onDeletePost = { post -> println("Delete ${post.id}") },
            onBack = {}
        )
    }
}