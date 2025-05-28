package com.tripbook.userprofileManfoIngrid.presentation.media.components.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.tripbook.userprofileManfoIngrid.presentation.media.models.MediaItem

@Composable
fun MediaActionDialog(
    media: MediaItem,
    onDismiss: () -> Unit,
    onDelete: (MediaItem) -> Unit,
    onEdit: () -> Unit,
    onShare: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Media Actions") },
        text = { Text("What would you like to do with ${media.name}?") },
        confirmButton = {
            Column {
                TextButton(onClick = onEdit) { Text("Edit") }
                TextButton(onClick = onShare) { Text("Share") }
                TextButton(onClick = { onDelete(media) }) { Text("Delete") }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
