package com.android.tripbook.ui.components.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Face
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

enum class ReactionType(val icon: ImageVector, val description: String, val color: Color) {
    LIKE(Icons.Default.ThumbUp, "J'aime", Color.Blue),
    LOVE(Icons.Default.Favorite, "J'adore", Color.Red),
    HAHA(Icons.Default.Face, "Haha", Color.Yellow)
}

@Composable
fun ReactionPicker(onReactionSelected: (ReactionType) -> Unit) {
    Row(
        modifier = Modifier.wrapContentWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ReactionType.entries.forEach { reaction ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .clickable { onReactionSelected(reaction) }
            ) {
                Icon(imageVector = reaction.icon, contentDescription = reaction.description, tint = reaction.color)
                Text(text = reaction.description, style = androidx.compose.material3.MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewReactionPicker() {
    ReactionPicker(onReactionSelected = { reaction ->
        println("Selected reaction: ${reaction.description}")
    })
}