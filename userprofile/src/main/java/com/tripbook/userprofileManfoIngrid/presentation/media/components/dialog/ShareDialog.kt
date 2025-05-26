package com.tripbook.userprofileManfoIngrid.presentation.media.components.dialogs

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import com.tripbook.userprofileManfoIngrid.presentation.media.models.MediaItem
import com.tripbook.userprofileManfoIngrid.presentation.media.models.MediaType
import java.io.File

@Composable
fun ShareDialog(
    media: MediaItem,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    
    val shareOptions = listOf(
        "Share File" to Icons.Default.Share,
        "Share Link" to Icons.Default.Link,
        "Copy Link" to Icons.Default.ContentCopy
    )
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Partager ${media.name}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                shareOptions.forEach { (name, icon) ->
                    ShareOption(
                        name = name,
                        icon = icon,
                        onClick = {
                            when (name) {
                                "Share File" -> shareMediaFile(context, media)
                                "Share Link" -> shareMediaLink(context, media)
                                "Copy Link" -> copyMediaLink(context, media)
                            }
                            onDismiss()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ShareOption(
    name: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = name,
            modifier = Modifier.size(24.dp),
            tint = Color(0xFF6C63FF)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = name,
            fontSize = 16.sp
        )
    }
}

// Share functions
private fun shareMediaFile(context: Context, media: MediaItem) {
    try {
        // If you have an actual file path, use this approach
        if (media.url.isNotEmpty() && File(media.url).exists()) {
            val file = File(media.url)
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )
            
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = when (media.type) {
                    MediaType.PHOTO -> "image/*"
                    MediaType.VIDEO -> "video/*"
                }
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_TEXT, "Check out this ${media.type.name.lowercase()}: ${media.name}")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            
            val chooser = Intent.createChooser(intent, "Share ${media.name}")
            context.startActivity(chooser)
        } else {
            // If no file exists, share as text with media info
            shareMediaText(context, media)
        }
    } catch (e: Exception) {
        // Fallback to text sharing if file sharing fails
        shareMediaText(context, media)
    }
}

private fun shareMediaLink(context: Context, media: MediaItem) {
    val shareText = if (media.url.isNotEmpty()) {
        "Check out this ${media.type.name.lowercase()}: ${media.name}\n${media.url}"
    } else {
        "Check out this ${media.type.name.lowercase()}: ${media.name}"
    }
    
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, shareText)
        putExtra(Intent.EXTRA_SUBJECT, media.name)
    }
    
    val chooser = Intent.createChooser(intent, "Share link")
    context.startActivity(chooser)
}

private fun shareMediaText(context: Context, media: MediaItem) {
    val shareText = "Check out this ${media.type.name.lowercase()}: ${media.name}"
    
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, shareText)
        putExtra(Intent.EXTRA_SUBJECT, media.name)
    }
    
    val chooser = Intent.createChooser(intent, "Share ${media.name}")
    context.startActivity(chooser)
}

private fun copyMediaLink(context: Context, media: MediaItem) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
    val clip = android.content.ClipData.newPlainText("Media Link", media.url.ifEmpty { media.name })
    clipboard.setPrimaryClip(clip)
    
    // You might want to show a toast or snackbar here to confirm the copy action
//     Toast.makeText(context, "Link copied to clipboard", Toast.LENGTH_SHORT).show()
}