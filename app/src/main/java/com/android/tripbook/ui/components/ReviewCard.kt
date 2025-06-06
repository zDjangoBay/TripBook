package com.android.tripbook.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.android.tripbook.Model.Review
import com.android.tripbook.ui.components.FullscreenGalleryDialog

@Composable
fun ReviewCard(
    review: Review,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onLikeClicked: (Int) -> Unit = {},
    onFlagClicked: (Int) -> Unit = {}
) {
    var showFullscreenGallery by remember { mutableStateOf(false) }
    var initialImageIndex by remember { mutableStateOf(0) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
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
                        .heightIn(max = 320.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    userScrollEnabled = false
                ) {
                    items(review.images) { imageUrl ->
                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    initialImageIndex = review.images.indexOf(imageUrl)
                                    showFullscreenGallery = true
                                }
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(imageUrl),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }

            // ✅ Reaction Buttons Row (inside the card)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { onLikeClicked(review.id) }) {
                    Icon(
                        imageVector = if (review.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (review.isLiked) Color.Red else Color.Gray
                    )
                }

                IconButton(onClick = { onFlagClicked(review.id) }) {
                    Icon(
                        imageVector = Icons.Default.Flag,
                        contentDescription = "Flag",
                        tint = if (review.isFlagged) Color.Red else Color.Gray
                    )
                }

                Text(
                    text = "${review.likeCount}",
                    color = if (review.isLiked) Color.Red else Color.Gray
                )
            }
        }
    }

    if (showFullscreenGallery) {
        FullscreenGalleryDialog(
            images = review.images,
            initialPage = initialImageIndex,
            onDismiss = { showFullscreenGallery = false }
        )
    }
}
