package com.android.tripbook.ui.media

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil.compose.rememberAsyncImagePainter

@Composable
fun MediaViewerScreen(mediaItem: MediaItem, onDismiss: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box(modifier = Modifier.fillMaxSize().clickable { onDismiss() }) {
            Image(
                painter = rememberAsyncImagePainter(mediaItem.uri),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
