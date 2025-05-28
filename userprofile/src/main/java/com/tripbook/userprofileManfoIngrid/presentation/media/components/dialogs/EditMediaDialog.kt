package com.tripbook.userprofileManfoIngrid.presentation.media.components.dialogs

import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.tripbook.userprofileManfoIngrid.presentation.media.models.MediaItem

@Composable
fun EditMediaDialog(
    media: MediaItem,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var name by remember { mutableStateOf(media.name) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Media") },
        text = {
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Media Name") }
            )
        },
        confirmButton = {
            TextButton(onClick = { onSave(name) }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
