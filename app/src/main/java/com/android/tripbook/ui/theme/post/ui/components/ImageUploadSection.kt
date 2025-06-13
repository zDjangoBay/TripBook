package com.android.tripbook.posts.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.android.tripbook.posts.model.ImageModel

private val IMAGE_SIZE = 100.dp
private val IMAGE_CORNER_RADIUS = 12.dp
private val CLOSE_BUTTON_SIZE = 24.dp
private val CLOSE_ICON_SIZE = 16.dp

/**
 * Displays a section for uploading and managing images in a post.
 *
 * @param images List of images to display
 * @param onImageAdd Callback for when the add image button is clicked
 * @param onImageRemove Callback when an image is removed
 * @param error Optional error message to display
 * @param modifier Modifier for styling
 */
@Composable
fun ImageUploadSection(
    images: List<ImageModel>,
    onImageAdd: () -> Unit,
    onImageRemove: (String) -> Unit,
    error: String? = null,
    modifier: Modifier = Modifier
) {
    // Main container for the image upload section
    Column(modifier = modifier) {
        // Section title
        Text(
            text = "Photos",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Horizontal list for add button and selected images
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Add image button
            item {
                Box(
                    modifier = Modifier
                        .size(IMAGE_SIZE)
                        .clip(RoundedCornerShape(IMAGE_CORNER_RADIUS))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(
                            width = 2.dp,
                            color = if (error != null) MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(IMAGE_CORNER_RADIUS)
                        )
                        .clickable { onImageAdd() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Image",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Display selected images
            items(images) { image ->
                Box(
                    modifier = Modifier
                        .size(IMAGE_SIZE)
                        .clip(RoundedCornerShape(IMAGE_CORNER_RADIUS))
                ) {
                    AsyncImage(
                        model = image.uri ?: "",
                        contentDescription = "Selected Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    IconButton(
                        onClick = { onImageRemove(image.id) },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(CLOSE_BUTTON_SIZE)
                            .background(
                                Color.Black.copy(alpha = 0.6f),
                                RoundedCornerShape(IMAGE_CORNER_RADIUS)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove Image",
                            tint = Color.White,
                            modifier = Modifier.size(CLOSE_ICON_SIZE)
                        )
                    }
                }
            }
        }

        // Show error message if present
        if (error != null) {
            Text(
                text = error ?: "Please select at least one image to continue.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Show image count
        Text(
            text = "${images.size}/10 images",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

// TODO: Support drag-and-drop reordering of images in the future
