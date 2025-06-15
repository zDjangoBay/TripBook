package com.tripbook.userprofilebradley.ui.compose

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.tripbook.userprofilebradley.data.LocationData
import com.tripbook.userprofilebradley.data.PostType

@Composable
fun PostContentEditor(
    postType: PostType,
    title: String,
    content: String,
    imageUri: String?,
    location: LocationData?,
    onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    onImageSelect: () -> Unit,
    onLocationSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title input
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            label = { Text("Post Title") },
            placeholder = { Text("Enter a catchy title...") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // Content input
        OutlinedTextField(
            value = content,
            onValueChange = onContentChange,
            label = { Text("Post Content") },
            placeholder = { Text("Share your travel experience...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            maxLines = 5
        )

        // Type-specific content
        when (postType) {
            PostType.MEDIA -> {
                MediaSelector(
                    imageUri = imageUri,
                    onImageSelect = onImageSelect
                )
            }

            PostType.LOCATION -> {
                LocationSelector(
                    location = location,
                    onLocationSelect = onLocationSelect
                )
            }

            PostType.TEXT -> {
                // Additional text-specific options could go here
            }
        }
    }
}

@Composable
private fun MediaSelector(
    imageUri: String?,
    onImageSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (imageUri != null) {
                Text(
                    text = "Image selected",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconButton(onClick = onImageSelect) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Select Image",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        text = "Add Photo",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun LocationSelector(
    location: LocationData?,
    onLocationSelect: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        if (location != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location",
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    if (location.placeName.isNotEmpty()) {
                        Text(
                            text = location.placeName,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    if (location.address.isNotEmpty()) {
                        Text(
                            text = location.address,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                TextButton(onClick = onLocationSelect) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Location")
                }
            }
        }
    }
}
