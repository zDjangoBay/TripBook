package com.android.tripbook.ui.screens.feed

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
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
fun GroupFeedScreen(groupName: String, groupPosts: List<PostModel>, onPostClick: (PostModel) -> Unit, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Group: $groupName") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Group Options")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (groupPosts.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text("This group has no posts yet.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(groupPosts, key = { it.id }) { post ->
                    PostCard(
                        post = post,
                        onCardClick = { onPostClick(post) },
                        onLikeClick = { println("Like clicked for group post ${post.id}") },
                        onCommentClick = { onPostClick(post) }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewGroupFeedScreen() {
    TripBookTheme {
        val sampleGroupPosts = listOf(
            PostModel(
                id = "group_p1",
                userId = "group_member1",
                username = "FamilyExplorer",
                userAvatar = null,
                isVerified = false,
                title = "Family Trip to Seychelles",
                description = "An amazing family vacation with the kids. Crystal clear waters and stunning beaches!",
                images = listOf(ImageModel("img1_fam", Uri.parse("https://via.placeholder.com/300/FF00FF/FFFFFF?text=Seychelles"))),
                location = Location("loc_sey", "Seychelles", "Mahe", "Seychelles", null),
                tags = emptyList(),
                createdAt = Instant.now().minusSeconds(86400 * 7),
                lastEditedAt = null,
                visibility = PostVisibility.FRIENDS,
                collaborators = emptyList(),
                isEphemeral = false,
                ephemeralDurationMillis = null,
                likes = emptyList(),
                comments = emptyList()
            ),
            PostModel(
                id = "group_p2",
                userId = "group_member2",
                username = "BudgetBuddy",
                userAvatar = null,
                isVerified = false,
                title = "Tips for Budget Travel",
                description = "How to save money while exploring the world. Share your own tips!",
                images = emptyList(),
                location = Location("loc_virtual", "Anywhere", "", "Global", null),
                tags = emptyList(),
                createdAt = Instant.now().minusSeconds(86400 * 12),
                lastEditedAt = null,
                visibility = PostVisibility.PUBLIC,
                collaborators = emptyList(),
                isEphemeral = false,
                ephemeralDurationMillis = null,
                likes = emptyList(),
                comments = emptyList()
            )
        )
        GroupFeedScreen(groupName = "Adventure Seekers", groupPosts = sampleGroupPosts, onPostClick = {}, onBack = {})
    }
}