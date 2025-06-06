package com.android.tripbook.ui.screens.feed

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
fun HomeFeedScreen(
    posts: List<PostModel>,
    onPostClick: (PostModel) -> Unit,
    onNavigateToCreatePost: () -> Unit = {},
    // Ajoute de nouveaux callbacks pour la navigation vers les autres écrans
    onNavigateToSavedPosts: () -> Unit = {},
    onNavigateToPinnedPosts: () -> Unit = {},
    onNavigateToGroupFeed: (String) -> Unit = {},
    onNavigateToReportedPosts: () -> Unit = {},
    onNavigateToLocationFeed: () -> Unit = {},
    onNavigateToTripBooking: (String) -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("TripBook") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreatePost) {
                Icon(Icons.Filled.Add, "Create New Post")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Zone pour les boutons de test des autres écrans
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Test Other Screens:", style = MaterialTheme.typography.titleMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Button(onClick = onNavigateToSavedPosts) { Text("Saved") }
                        Button(onClick = onNavigateToPinnedPosts) { Text("Pinned") }
                        Button(onClick = { onNavigateToGroupFeed("Adventure Seekers") }) { Text("Group") }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Button(onClick = onNavigateToReportedPosts) { Text("Reported") }
                        Button(onClick = onNavigateToLocationFeed) { Text("Locations") }
                        Button(onClick = { onNavigateToTripBooking("loc1") }) { Text("Book Trip") } // Utilise un ID de localisation mock
                    }
                }
                HorizontalDivider() // <-- Renommé
                Spacer(modifier = Modifier.height(16.dp))
            }

            items(posts, key = { it.id }) { post ->
                PostCard(
                    post = post,
                    onCardClick = { onPostClick(post) },
                    onLikeClick = { println("Like clicked for post ${post.id}") },
                    onCommentClick = { onPostClick(post) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHomeFeedScreen() {
    TripBookTheme {
        val samplePosts = listOf(
            PostModel(
                id = "1",
                userId = "user_cam",
                username = "AfricanExplorer",
                userAvatar = null,
                isVerified = true,
                title = "Adventure in Cameroon's Rainforest!",
                description = "Explored the dense rainforest, met local communities, and witnessed incredible wildlife. A truly humbling experience.",
                images = listOf(ImageModel("img1_cam", Uri.parse("https://via.placeholder.com/300/FF5733/FFFFFF?text=Rainforest"))),
                // MODIFICATION ICI: Utilise TravelLocation du module data.model avec les bons paramètres
                location = TravelLocation(id = "loc_cam", name = "Dja Faunal Reserve", latitude = 3.0, longitude = 13.0, description = "East Region, Cameroon", imageUrl = null),
                tags = emptyList(),
                createdAt = Instant.now().minusSeconds(86400 * 2),
                lastEditedAt = null,
                visibility = PostVisibility.PUBLIC,
                collaborators = emptyList(),
                isEphemeral = false,
                ephemeralDurationMillis = null,
                likes = listOf("user_x", "user_y"),
                comments = listOf(Comment("c1_cam", "1", "user_z", "Commenter", null, "Wow!", Instant.now())) // Utilise Comment canonique
            ),
            PostModel(
                id = "2",
                userId = "user_sen",
                username = "BeachLover",
                userAvatar = null,
                isVerified = false,
                title = "Relaxing on the Beaches of Senegal",
                description = "Miles of golden sand and stunning sunsets. Perfect for unwinding and soaking up the sun.",
                images = listOf(ImageModel("img1_sen", Uri.parse("https://via.placeholder.com/300/33FF57/FFFFFF?text=Beach"))),
                // MODIFICATION ICI: Utilise TravelLocation du module data.model avec les bons paramètres
                location = TravelLocation("loc_sen", "Saly Portudal", latitude = 14.4173, longitude = -17.0396, description = "Thies Region, Senegal", imageUrl = null),
                tags = emptyList(),
                createdAt = Instant.now().minusSeconds(86400 * 5),
                lastEditedAt = null,
                visibility = PostVisibility.PUBLIC,
                collaborators = emptyList(),
                isEphemeral = false,
                ephemeralDurationMillis = null,
                likes = listOf("user_a"),
                comments = emptyList<Comment>() // Utilise Comment canonique
            )
        )
        HomeFeedScreen(posts = samplePosts, onPostClick = {})
    }
}
