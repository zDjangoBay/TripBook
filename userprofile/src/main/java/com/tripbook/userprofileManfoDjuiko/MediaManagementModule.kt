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
import androidx.compose.ui.graphics.Brush
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
import com.tripbook.userprofileManfoDjuiko.presentation.viewmodel.MediaUiState
import com.tripbook.userprofileManfoDjuiko.presentation.viewmodel.MediaViewModel
import com.tripbook.userprofileManfoDjuiko.utils.PermissionHandler
import com.tripbook.userprofileManfoDjuiko.utils.formatDuration
import com.tripbook.userprofileManfoDjuiko.presentation.screens.ErrorScreen

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
    // Explicitly specify the type for uiState
    val uiState by viewModel.uiState.collectAsState(initial = MediaUiState())
    var showUploadScreen by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var itemToEdit by remember { mutableStateOf<MediaItem?>(null) }
    val context = LocalContext.current

    // Launcher for sharing content
    val shareLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        // You can handle the result of the share intent here if needed
    }

    // Edit Name Dialog
    if (showEditDialog && itemToEdit != null) {
        EditNameDialog(
            currentName = itemToEdit!!.name,
            onDismiss = {
                showEditDialog = false
                itemToEdit = null
            },
            onConfirm = { newName ->
                // TODO: Implement name change logic here
                // For now, just close the dialog
                showEditDialog = false
                itemToEdit = null
            }
        )
    }

    if (showUploadScreen) {
        MediaUploadScreen(
            onNavigateBack = { showUploadScreen = false },
            onUploadComplete = { uris ->
                viewModel.addMediaItems(uris)
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
                    IconButton(onClick = { showUploadScreen = true }) {
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
                        IconButton(onClick = { viewModel.deleteSelectedItems() }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                        IconButton(onClick = {
                            // Call the share function from the ViewModel
                            viewModel.shareSelectedMedia(context, shareLauncher)
                        }) {
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
                                onAddMedia = { showUploadScreen = true }
                            )
                        } else {
                            MediaGrid(
                                items = currentItems,
                                selectedItems = uiState.selectedItems,
                                onItemClick = { item ->
                                    viewModel.toggleItemSelection(item.id)
                                },
                                onItemLongClick = {
                                    // Handle long click for detail view
                                },
                                onEditClick = { item ->
                                    itemToEdit = item
                                    showEditDialog = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNameDialog(
    currentName: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var textValue by remember { mutableStateOf(currentName) }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Modifier le nom",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Saisissez un nouveau nom pour ce média :",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = textValue,
                    onValueChange = {
                        textValue = it
                        isError = it.isBlank()
                    },
                    label = {
                        Text(
                            "Nom du média",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    placeholder = {
                        Text(
                            "Entrez le nom...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    },
                    isError = isError,
                    supportingText = if (isError) {
                        {
                            Text(
                                "Le nom ne peut pas être vide",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    } else null,
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        errorBorderColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (textValue.isNotBlank()) {
                        onConfirm(textValue.trim())
                    } else {
                        isError = true
                    }
                },
                enabled = textValue.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    "Confirmer",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text(
                    "Annuler",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    )
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
    onItemLongClick: (MediaItem) -> Unit,
    onEditClick: (MediaItem) -> Unit // New lambda for edit functionality
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2), // Changed from 3 to 2 for larger grid items
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp), // Increased spacing
        verticalArrangement = Arrangement.spacedBy(8.dp), // Increased spacing
        modifier = Modifier.fillMaxSize()
    ) {
        items(items) { item ->
            MediaGridItem(
                item = item,
                isSelected = selectedItems.contains(item.id),
                onClick = { onItemClick(item) },
                onLongClick = { onItemLongClick(item) },
                onEditClick = { onEditClick(item) } // Pass the new lambda
            )
        }
    }
}

@Composable
private fun MediaGridItem(
    item: MediaItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onEditClick: () -> Unit // New lambda for edit icon click
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
            // .onLongClick { onLongClick() } // Added onLongClick modifier
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
                    .size(48.dp) // Increased size
                    .background(
                        Color.Black.copy(alpha = 0.6f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp) // Increased size
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
                    .size(28.dp) // Slightly increased size
                    .background(
                        MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp) // Slightly increased size
                )
            }
        }

        // Gradient overlay for better text visibility and label/edit icon
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.6f) // Increased alpha for better contrast
                        )
                    )
                )
                .padding(horizontal = 8.dp, vertical = 4.dp) // Added padding
        ) {
            // Item Name and Edit Icon
            if (item.name.isNotBlank()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween // Space out text and icon
                ) {
                    Text(
                        text = item.name,
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium, // Adjusted typography
                        maxLines = 1,
                        modifier = Modifier.weight(1f) // Allow text to take available space
                    )
                    IconButton(
                        onClick = onEditClick,
                        modifier = Modifier.size(24.dp) // Size of the icon button
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit Label",
                            tint = Color.White
                        )
                    }
                }
            }
        }
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
