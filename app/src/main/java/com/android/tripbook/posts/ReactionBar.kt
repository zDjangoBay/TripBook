package com.android.tripbook.posts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ReactionBar(
    currentReaction: String?,
    onReactionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val emojiList = listOf("👍", "❤️", "😂", "🔥", "😢")

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        emojiList.forEach { emoji ->
            Text(
                text = emoji,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .clickable { onReactionSelected(emoji) }
                    .then(if (emoji == currentReaction) Modifier else Modifier)
            )
        }
    }
}

// To be put in the PostDetailScreen class inside the post details section to make it work
// ReactionBar(
//    currentReaction = post.userReactions[currentUserId],
//    onReactionSelected = { selected ->
//        viewModel.handleEvent(PostEvent.ReactToPost(postId, selected))
//    },
//    modifier = Modifier.padding(vertical = 8.dp)
//)