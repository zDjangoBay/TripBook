package com.tripbook.userprofileManfoIngrid.presentation.media.components.dialogs

import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.tripbook.userprofileManfoIngrid.presentation.media.models.MediaItem

@Composable
fun ShareDialog(
    media: MediaItem,
    onDismiss: () -> Unit,
    onShare: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Share Media") },
        text = { Text("Share ${media.name}?") },
        confirmButton = {
            TextButton(onClick = onShare) {
                Text("Share")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
