package com.android.tripbook.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.android.tripbook.model.Review

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Flag
import androidx.compose.ui.graphics.Color

@Composable
fun ReviewCard(
    review: Review,
    modifier: Modifier = Modifier,
    onLikeClicked: () -> Unit = {},
    onFlagClicked: () -> Unit = {}
)
 {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = review.username, style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = review.comment, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))

            if (review.images.isNotEmpty()) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 320.dp), // max height, adjust if needed
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    userScrollEnabled = false // prevent nested scroll in LazyColumn
                ) {
                    items(review.images) { imageUrl ->
                        Image(
                            painter = rememberAsyncImagePainter(imageUrl),
                            contentDescription = null,
                            modifier = Modifier
                                .aspectRatio(1f) // make images square
                                .clip(RoundedCornerShape(8.dp))
                                .fillMaxWidth()
                        )
                    }
                }
            }

            // 🔽 Reaction Buttons Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onLikeClicked) {
                    Icon(
                        imageVector = if (review.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (review.isLiked) Color.Red else Color.Gray
                    )
                }

                IconButton(onClick = onFlagClicked) {
                    Icon(
                        imageVector = Icons.Default.Flag,
                        contentDescription = "Flag",
                        tint = if (review.isFlagged) Color.Red else Color.Gray
                    )
                }
            }
        }
    }
}