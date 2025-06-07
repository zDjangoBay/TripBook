package com.tripbook.userprofileManfoDjuiko

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.tripbook.userprofileManfoDjuiko.data.model.MediaItem
import com.tripbook.userprofileManfoDjuiko.data.model.MediaType
import com.tripbook.userprofileManfoDjuiko.presentation.screens.MediaUploadScreen
import com.tripbook.userprofileManfoDjuiko.presentation.viewmodel.MediaViewModel
import com.tripbook.userprofileManfoDjuiko.utils.PermissionHandler
import com.tripbook.userprofileManfoDjuiko.utils.formatDuration

@Composable
fun MediaManagementModule() {
    val context = LocalContext.current
    var hasPermissions by remember { mutableStateOf(PermissionHandler.hasMediaPermissions(context)) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasPermissions = permissions.values.all { it }
    }

    if (hasPermissions) {
        MediaManagementScreen()
    } else {
        PermissionRequestScreen(
            onRequestPermissions = {
                permissionLauncher.launch(PermissionHandler.getRequiredPermissions())
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaManagementScreen(
    viewModel: MediaViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showUploadScreen by remember { mutableStateOf(false) } // State to control upload screen visibility

    if (showUploadScreen) {
        MediaUploadScreen(
            onNavigateBack = { showUploadScreen = false },
            onUploadComplete = { uris ->
                // Handle the uploaded URIs here.
                // In a real application, you'd likely pass these to your ViewModel
                // to process and add them to your media list.
                // For now, let's just close the upload screen.
                viewModel.addMediaItems(uris) // Assuming you have a function in your ViewModel to add media
                showUploadScreen = false
            }
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Top App Bar
            TopAppBar(
                title = {
                    Text(
                        text = "Tripbook Gallery",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    // Add media button
                    IconButton(onClick = { showUploadScreen = true }) { // Toggle upload screen visibility
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add Media",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Selection actions
                    if (uiState.selectedItems.isNotEmpty()) {
                        Text(
                            text = "${uiState.selectedItems.size}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        IconButton(onClick = { viewModel.clearSelection() }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear selection")
                        }
                        IconButton(onClick = { /* Handle delete */ }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                        IconButton(onClick = { /* Handle share */ }) {
                            Icon(Icons.Default.Share, contentDescription = "Share")
                        }
                    }
                }
            )

            // User Account Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // User Avatar Placeholder
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .clickable { /* Handle profile picture click */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "User Avatar",
                            tint = Color.White,
                            modifier = Modifier.size(64.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // User Name Placeholder
                    Text(
                        text = "John Doe", // Replace with actual user name
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // User Subtitle Placeholder
                    Text(
                        text = "Mes Médias", // Replace with actual subtitle
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }

            // Custom Tab Row with rounded design
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .background(
                        Color(0xFFF5F5F5),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Tous Tab
                CustomTab(
                    text = "Tous",
                    isSelected = uiState.currentTab == MediaType.ALL,
                    onClick = { viewModel.selectTab(MediaType.ALL) },
                    modifier = Modifier.weight(1f)
                )

                // Photos Tab
                CustomTab(
                    text = "Photos",
                    isSelected = uiState.currentTab == MediaType.IMAGE,
                    onClick = { viewModel.selectTab(MediaType.IMAGE) },
                    modifier = Modifier.weight(1f)
                )

                // Vidéos Tab
                CustomTab(
                    text = "Vidéos",
                    isSelected = uiState.currentTab == MediaType.VIDEO,
                    onClick = { viewModel.selectTab(MediaType.VIDEO) },
                    modifier = Modifier.weight(1f)
                )
            }

            // Content Area
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    uiState.isLoading -> {
                        LoadingScreen()
                    }

                    uiState.error != null -> {
                        ErrorScreen(
                            error = uiState.error!!,
                            onRetry = { viewModel.loadMedia() }
                        )
                    }

                    else -> {
                        val currentItems = when (uiState.currentTab) {
                            MediaType.ALL -> uiState.images + uiState.videos
                            MediaType.IMAGE -> uiState.images
                            MediaType.VIDEO -> uiState.videos
                        }

                        if (currentItems.isEmpty()) {
                            EmptyStateScreen(
                                mediaType = uiState.currentTab,
                                onAddMedia = { showUploadScreen = true } // Also connect this to the upload screen
                            )
                        } else {
                            MediaGrid(
                                items = currentItems,
                                selectedItems = uiState.selectedItems,
                                onItemClick = { item ->
                                    viewModel.toggleItemSelection(item.id)
                                },
                                onItemLongClick = { item ->
                                    // Handle long click for detail view
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CustomTab(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isSelected) Color(0xFF6366F1) else Color.Transparent
            )
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
            color = if (isSelected) Color.White else Color(0xFF6B7280)
        )
    }
}

@Composable
private fun MediaGrid(
    items: List<MediaItem>,
    selectedItems: Set<String>,
    onItemClick: (MediaItem) -> Unit,
    onItemLongClick: (MediaItem) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(items) { item ->
            MediaGridItem(
                item = item,
                isSelected = selectedItems.contains(item.id),
                onClick = { onItemClick(item) },
                onLongClick = { onItemLongClick(item) }
            )
        }
    }
}

@Composable
private fun MediaGridItem(
    item: MediaItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .then(
                if (isSelected) {
                    Modifier.border(
                        3.dp,
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(8.dp)
                    )
                } else {
                    Modifier
                }
            )
    ) {
        // Media Image/Thumbnail
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(item.uri)
                .crossfade(true)
                .build(),
            contentDescription = item.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Video-specific overlays
        if (item.type == MediaType.VIDEO) {
            // Play icon overlay
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(32.dp)
                    .background(
                        Color.Black.copy(alpha = 0.6f),
                        shape = androidx.compose.foundation.shape.CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Duration overlay
            if (item.duration != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(4.dp)
                        .background(
                            Color.Black.copy(alpha = 0.7f),
                            RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = formatDuration(item.duration),
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }

        // Selection indicator
        if (isSelected) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .size(24.dp)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        shape = androidx.compose.foundation.shape.CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // Gradient overlay for better text visibility
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .height(24.dp)
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.3f)
                        )
                    )
                )
        )
    }
}

@Composable
private fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Loading media...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ErrorScreen(
    error: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.padding(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.Error,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Error Loading Media",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onRetry,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Retry")
                }
            }
        }
    }
}

@Composable
private fun EmptyStateScreen(
    mediaType: MediaType,
    onAddMedia: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                when (mediaType) {
                    MediaType.ALL -> Icons.Default.Collections
                    MediaType.IMAGE -> Icons.Default.Image
                    MediaType.VIDEO -> Icons.Default.VideoLibrary
                },
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = when (mediaType) {
                    MediaType.ALL -> "No Media Found"
                    MediaType.IMAGE -> "No Images Found"
                    MediaType.VIDEO -> "No Videos Found"
                },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = when (mediaType) {
                    MediaType.ALL -> "Start by adding some photos or videos to your gallery"
                    MediaType.IMAGE -> "Start by adding some photos to your gallery"
                    MediaType.VIDEO -> "Start by adding some videos to your gallery"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onAddMedia,
                modifier = Modifier.fillMaxWidth(0.6f)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Media")
            }
        }
    }
}

@Composable
private fun PermissionRequestScreen(
    onRequestPermissions: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.padding(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.PhotoLibrary,
                    contentDescription = "Media Access",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Media Access Required",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "To display your photos and videos, we need access to your device's media storage.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onRequestPermissions,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Default.Security,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Grant Permission")
                }
            }
        }
    }
}
