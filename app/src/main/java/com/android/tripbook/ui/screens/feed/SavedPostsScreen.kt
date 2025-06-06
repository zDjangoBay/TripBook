package com.android.tripbook.ui.screens.feed

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
// Import des modèles canoniques
import com.android.tripbook.data.model.TravelLocation
import com.android.tripbook.data.model.Comment
import com.android.tripbook.posts.model.ImageModel
import com.android.tripbook.posts.model.PostVisibility
import com.android.tripbook.posts.ui.components.PostCard
import com.android.tripbook.ui.theme.TripBookTheme
import java.time.Instant
import android.net.Uri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedPostsScreen(savedPosts: List<PostModel>, onPostClick: (PostModel) -> Unit, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Saved Posts") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (savedPosts.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text("No posts saved yet.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(savedPosts, key = { it.id }) { post ->
                    PostCard(
                        post = post,
                        onCardClick = { onPostClick(post) },
                        onLikeClick = { println("Like clicked for saved post ${post.id}") },
                        onCommentClick = { onPostClick(post) }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSavedPostsScreen() {
    TripBookTheme {
        val sampleSavedPosts = listOf(
            PostModel(
                id = "3",
                userId = "user_secret",
                username = "MysteryTraveller",
                userAvatar = null,
                isVerified = false,
                title = "Top 5 Secret Destinations You Must Visit",
                description = "Incredible hidden gems far from the tourist crowds. Don't tell anyone!",
                images = listOf(ImageModel("img1_secret", Uri.parse("https://via.placeholder.com/300/000000/FFFFFF?text=Secret+Spot"))),
                location = TravelLocation(id = "loc_secret", name = "Hidden Valley", latitude = 0.0, longitude = 0.0, description = "Unknown, Secret Country", imageUrl = null),
                tags = emptyList(),
                createdAt = Instant.now().minusSeconds(86400 * 10),
                lastEditedAt = null,
                visibility = PostVisibility.PRIVATE,
                collaborators = emptyList(),
                isEphemeral = false,
                ephemeralDurationMillis = null,
                likes = emptyList(),
                comments = emptyList<Comment>() // Utilise la classe Comment canonique
            )
        )
        SavedPostsScreen(savedPosts = sampleSavedPosts, onPostClick = {}, onBack = {})
    }
}
