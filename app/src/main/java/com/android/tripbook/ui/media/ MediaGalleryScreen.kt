package com.android.tripbook.ui.media

import android.content.Context
import android.provider.MediaStore
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun MediaGalleryScreen(
    context: Context,
    onMediaClick: (MediaItem) -> Unit
) {
    var mediaItems by remember { mutableStateOf(loadMediaFromDevice(context)) }
    var isGridView by remember { mutableStateOf(true) }
    var filterType by remember { mutableStateOf("All") }
    var isRefreshing by remember { mutableStateOf(false) }

    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing)

    Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = { isGridView = !isGridView }) {
                Text(if (isGridView) "Switch to List" else "Switch to Grid")
            }
            Row {
                FilterButton("All", filterType) { filterType = it }
                FilterButton("Images", filterType) { filterType = it }
                FilterButton("Videos", filterType) { filterType = it }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = {
                isRefreshing = true
                mediaItems = loadMediaFromDevice(context)
                isRefreshing = false
            }
        ) {
            val filteredItems = when (filterType) {
                "Images" -> mediaItems.filter { it.mimeType?.startsWith("image") == true }
                "Videos" -> mediaItems.filter { it.mimeType?.startsWith("video") == true }
                else -> mediaItems
            }

            if (isGridView) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(4.dp)
                ) {
                    items(filteredItems) { media ->
                        Image(
                            painter = rememberAsyncImagePainter(media.uri),
                            contentDescription = null,
                            modifier = Modifier
                                .aspectRatio(1f)
                                .padding(2.dp)
                                .clickable { onMediaClick(media) }
                        )
                    }
                }
            } else {
                LazyColumn {
                    items(filteredItems) { media ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable { onMediaClick(media) },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(media.uri),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(72.dp)
                                    .padding(end = 8.dp)
                            )
                            Text(text = media.displayName ?: "Unknown", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FilterButton(text: String, selected: String, onClick: (String) -> Unit) {
    OutlinedButton(
        onClick = { onClick(text) },
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (text == selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            else MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier.padding(start = 4.dp)
    ) {
        Text(text)
    }
}

fun loadMediaFromDevice(context: Context): List<MediaItem> {
    val mediaList = mutableListOf<MediaItem>()
    val projection = arrayOf(
        MediaStore.MediaColumns._ID,
        MediaStore.MediaColumns.DISPLAY_NAME,
        MediaStore.MediaColumns.MIME_TYPE,
        MediaStore.MediaColumns.DATE_ADDED
    )
    val sortOrder = "${MediaStore.MediaColumns.DATE_ADDED} DESC"

    val uri = MediaStore.Files.getContentUri("external")
    context.contentResolver.query(uri, projection, null, null, sortOrder)?.use { cursor ->
        val idCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
        val nameCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
        val mimeCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE)
        val dateCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_ADDED)

        while (cursor.moveToNext()) {
            val id = cursor.getLong(idCol)
            val name = cursor.getString(nameCol)
            val mime = cursor.getString(mimeCol)
            val date = cursor.getLong(dateCol)
            val contentUri = MediaStore.Files.getContentUri("external", id)
            mediaList += MediaItem(contentUri, name, mime, date)
        }
    }
    return mediaList
}
