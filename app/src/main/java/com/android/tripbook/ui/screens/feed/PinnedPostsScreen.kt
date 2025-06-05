package com.android.tripbook.ui.screens.feed

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.tripbook.posts.model.PostModel
import com.android.tripbook.posts.model.Location
import com.android.tripbook.posts.model.ImageModel
import com.android.tripbook.posts.model.PostVisibility
import com.android.tripbook.posts.ui.components.PostCard
import com.android.tripbook.ui.theme.TripBookTheme
import java.time.Instant
import android.net.Uri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PinnedPostsScreen(pinnedPosts: List<PostModel>, onPostClick: (PostModel) -> Unit, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pinned Posts") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (pinnedPosts.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text("No posts pinned yet.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(pinnedPosts, key = { it.id }) { post ->
                    PostCard(
                        post = post,
                        onCardClick = { onPostClick(post) },
                        onLikeClick = { println("Like clicked for pinned post ${post.id}") },
                        onCommentClick = { onPostClick(post) }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPinnedPostsScreen() {
    TripBookTheme {
        val samplePinnedPosts = listOf(
            PostModel(
                id = "pinned1",
                userId = "user_planner",
                username = "TravelPlanner",
                userAvatar = null,
                isVerified = true,
                title = "My Dream Itinerary for Europe!",
                description = "A comprehensive guide covering 10 countries in 30 days. Tips, tricks, and hidden gems included!",
                images = listOf(ImageModel("img1_euro", Uri.parse("https://via.placeholder.com/300/FFFF00/000000?text=Europe"))),
                location = Location("loc_euro", "Europe", "Multiple", "Europe", null),
                tags = emptyList(),
                createdAt = Instant.now().minusSeconds(86400 * 30),
                lastEditedAt = Instant.now().minusSeconds(86400 * 5),
                visibility = PostVisibility.PUBLIC,
                collaborators = emptyList(),
                isEphemeral = false,
                ephemeralDurationMillis = null,
                likes = listOf("user_a", "user_b", "user_c"),
                comments = emptyList()
            )
        )
        PinnedPostsScreen(pinnedPosts = samplePinnedPosts, onPostClick = {}, onBack = {})
    }
}