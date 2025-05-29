package com.android.tripbook.ui.components

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.android.tripbook.R
import com.android.tripbook.util.ImageHandler
import com.android.tripbook.util.ImagePickers
import com.android.tripbook.util.ImagePickers.rememberImagePicker

/**
 * A composable for selecting and displaying images
 */
@Composable
fun ImageSelectionComponent(
    selectedImages: List<String>,
    onImagesSelected: (List<String>) -> Unit,
    modifier: Modifier = Modifier,
    maxImages: Int = 5
) {
    val context = LocalContext.current
    val imageHandler = remember { ImageHandler(context) }
    
    // State for image picker dialog
    var showImagePickerDialog by remember { mutableStateOf(false) }
    
    // Image picker launcher
    val imagePicker = rememberImagePicker(context) { uris ->
        if (uris.isNotEmpty()) {
            val imagePaths = uris.map { imageHandler.getImagePathFromUri(it) }
            val updatedImages = selectedImages.toMutableList()
            updatedImages.addAll(imagePaths)
            
            // Make sure we don't exceed maxImages
            if (updatedImages.size > maxImages) {
                onImagesSelected(updatedImages.take(maxImages))
            } else {
                onImagesSelected(updatedImages)
            }
        }
    }
    
    // Dialog to choose between camera and gallery
    if (showImagePickerDialog) {
        Dialog(onDismissRequest = { showImagePickerDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Add Photos",
                        style = MaterialTheme.typography.titleLarge
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Gallery option
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable {
                                    imagePicker.pickFromGallery()
                                    showImagePickerDialog = false
                                }
                                .padding(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .background(
                                        MaterialTheme.colorScheme.primaryContainer,
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.AddPhotoAlternate,
                                    contentDescription = "Gallery",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(
                                text = "Gallery",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        
                        // Camera option
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable {
                                    imagePicker.takePhoto()
                                    showImagePickerDialog = false
                                }
                                .padding(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .background(
                                        MaterialTheme.colorScheme.primaryContainer,
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.CameraAlt,
                                    contentDescription = "Camera",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(
                                text = "Camera",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
    
    // UI for image display and selection
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        if (selectedImages.isEmpty()) {
            // Empty state - show add photos button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { showImagePickerDialog = true }
                    .padding(16.dp)
            ) {
                Icon(
                    Icons.Default.AddPhotoAlternate, 
                    contentDescription = "Add Photos",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Add Photos",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            // Images added - show image carousel
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                // Show the first image large
                val firstImage = selectedImages.first()
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(context)
                            .data(imageHandler.getDisplayableUri(firstImage))
                            .error(R.drawable.app_logo)
                            .placeholder(R.drawable.app_logo)
                            .build()
                    ),
                    contentDescription = "Selected Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                // Photo count indicator
                if (selectedImages.size > 1) {
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Black.copy(alpha = 0.6f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .align(Alignment.TopEnd)
                    ) {
                        Text(
                            text = "${selectedImages.size} photos",
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                }
                
                // Thumbnail row at the bottom
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(Color.Black.copy(alpha = 0.5f))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(selectedImages) { imagePath ->
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(4.dp))
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(
                                    ImageRequest.Builder(context)
                                        .data(imageHandler.getDisplayableUri(imagePath))
                                        .error(R.drawable.app_logo)
                                        .placeholder(R.drawable.app_logo)
                                        .build()
                                ),
                                contentDescription = "Thumbnail",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            
                            // Delete button on each thumbnail
                            IconButton(
                                onClick = {
                                    val updatedImages = selectedImages.toMutableList()
                                    updatedImages.remove(imagePath)
                                    onImagesSelected(updatedImages)
                                },
                                modifier = Modifier
                                    .size(24.dp)
                                    .align(Alignment.TopEnd)
                                    .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Remove Image",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                    
                    // Add more images button if under the limit
                    if (selectedImages.size < maxImages) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.White.copy(alpha = 0.2f))
                                .clickable { showImagePickerDialog = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.AddPhotoAlternate,
                                contentDescription = "Add More Photos",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
                
                // Edit button
                FloatingActionButton(
                    onClick = { showImagePickerDialog = true },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit Photos",
                        tint = Color.White
                    )
                }
            }
        }
    }
}
