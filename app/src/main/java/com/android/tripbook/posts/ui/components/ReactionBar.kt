package com.android.tripbook.posts.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ReactionBar(
    onReact: (String) -> Unit
) {
    val emojis = listOf("👍", "❤️", "😂", "😮", "🎉")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        emojis.forEach { emoji ->
            Text(
                text = emoji,
                modifier = Modifier
                    .clickable { onReact(emoji) }
                    .padding(4.dp)
            )
        }
    }
}
