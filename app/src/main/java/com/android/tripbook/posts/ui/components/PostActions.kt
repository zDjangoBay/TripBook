package com.android.tripbook.posts.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.ChatBubbleOutline // <-- Icône Comment Outlined
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.android.tripbook.posts.model.PostModel // Utilise PostModel
import com.android.tripbook.posts.model.PostVisibility
import com.android.tripbook.ui.components.common.ReactionPicker // Utilise le composant commun
import com.android.tripbook.data.model.TravelLocation // <-- Import canonique pour TravelLocation
import com.android.tripbook.data.model.Comment // <-- Import canonique pour Comment
import java.time.Instant

@Composable
fun PostActions(post: PostModel, onCommentClick: (PostModel) -> Unit = {}, onShareClick: (PostModel) -> Unit = {}, onSaveClick: (PostModel) -> Unit = {}) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        ReactionPicker(onReactionSelected = { reactionType ->
            println("Reaction selected: $reactionType for post ${post.id}")
        })

        IconButton(onClick = { onCommentClick(post) }) {
            Icon(Icons.Outlined.ChatBubbleOutline, contentDescription = "Comment") // Icône Outlined
        }
        IconButton(onClick = { onShareClick(post) }) {
            Icon(Icons.Default.Share, contentDescription = "Share")
        }
        IconButton(onClick = { onSaveClick(post) }) {
            Icon(Icons.Default.Bookmark, contentDescription = "Save") // Icône Bookmark non dépréciée
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPostActions() {
    // Mock PostModel for preview
    val mockPost = PostModel(
        id = "mock1",
        userId = "u1",
        username = "MockUser",
        userAvatar = null,
        isVerified = false,
        title = "Mock Post",
        description = "This is a mock post for preview.",
        images = emptyList(),
        location = TravelLocation(id = "mock_loc1", name = "Mock City", latitude = 0.0, longitude = 0.0, description = "Mock Location Description", imageUrl = null),
        tags = emptyList(),
        createdAt = Instant.now(),
        lastEditedAt = null,
        visibility = PostVisibility.PUBLIC,
        collaborators = emptyList(),
        isEphemeral = false,
        ephemeralDurationMillis = null,
        likes = emptyList(),
        comments = emptyList<Comment>() // Utilise Comment canonique
    )
    PostActions(post = mockPost)
}
