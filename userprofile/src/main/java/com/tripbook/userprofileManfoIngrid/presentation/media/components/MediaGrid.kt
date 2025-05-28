package com.tripbook.userprofileManfoIngrid.presentation.media.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.tripbook.userprofileManfoIngrid.presentation.media.models.MediaItem
import com.tripbook.userprofileManfoIngrid.presentation.media.components.MediaGridItem

@Composable
fun MediaGrid(
    mediaItems: List<MediaItem>,
    onMediaClick: (MediaItem) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(mediaItems) { media ->
            MediaGridItem(
                media = media,
                onClick = { onMediaClick(media) }
            )
        }
    }
}
