package com.tripbook.userprofileManfoIngrid.presentation.media.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tripbook.userprofileManfoIngrid.presentation.media.models.MediaItem
import com.tripbook.userprofileManfoIngrid.presentation.media.models.MediaType

@Composable
fun MediaGrid(
    mediaItems: List<MediaItem>,
    onMediaClick: (MediaItem) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(mediaItems) { media ->
            MediaItemCard(
                media = media,
                onClick = { onMediaClick(media) }
            )
        }
    }
}

@Composable
fun MediaItemCard(
    media: MediaItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Placeholder for media content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFECF0F1))
            ) {
                Icon(
                    if (media.type == MediaType.PHOTO) Icons.Default.Image else Icons.Default.VideoLibrary,
                    contentDescription = media.name,
                    modifier = Modifier
                        .size(40.dp)
                        .align(Alignment.Center),
                    tint = Color(0xFF6C63FF)
                )
            }

            // Media type indicator
            Icon(
                if (media.type == MediaType.VIDEO) Icons.Default.PlayArrow else Icons.Default.Image,
                contentDescription = null,
                modifier = Modifier
                    .padding(8.dp)
                    .size(20.dp)
                    .align(Alignment.TopEnd),
                tint = Color.White
            )

            // Media name
            Text(
                text = media.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color.Black.copy(alpha = 0.7f),
                        RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
                    )
                    .padding(8.dp)
                    .align(Alignment.BottomCenter),
                color = Color.White,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}