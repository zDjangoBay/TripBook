package com.tripbook.userprofileManfoIngrid.presentation.media.components.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.tripbook.userprofileManfoIngrid.presentation.media.models.MediaItem

@Composable
fun MediaActionDialog(
    media: MediaItem,
    onDismiss: () -> Unit,
    onDelete: (MediaItem) -> Unit,
    onEdit: () -> Unit,
    onShare: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = media.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                ActionButton(
                    icon = Icons.Default.Edit,
                    text = "Modifier",
                    onClick = onEdit
                )

                Spacer(modifier = Modifier.height(12.dp))

                ActionButton(
                    icon = Icons.Default.Share,
                    text = "Partager",
                    onClick = onShare
                )

                Spacer(modifier = Modifier.height(12.dp))

                ActionButton(
                    icon = Icons.Default.Delete,
                    text = "Supprimer",
                    onClick = { onDelete(media) },
                    color = Color(0xFFE74C3C)
                )
            }
        }
    }
}

@Composable
fun ActionButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    color: Color = Color(0xFF6C63FF)
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = RoundedCornerShape(8.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text)
    }
}