package com.tripbook.userprofileManfoIngrid.presentation.media

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import java.util.Date

// Data classes
data class MediaItem(
    val id: String,
    val name: String,
    val type: MediaType,
    val url: String,
    val createdDate: Date,
    val modifiedDate: Date,
    val size: Long
)

enum class MediaType {
    PHOTO, VIDEO
}

enum class MediaFilter {
    ALL, PHOTOS, VIDEOS
}

enum class SortOption {
    CREATION_DATE, MODIFICATION_DATE, NAME
}

// ViewModel
class MediaViewModel : androidx.lifecycle.ViewModel() {
    private val _mediaItems = mutableStateListOf<MediaItem>()
    val mediaItems: List<MediaItem> = _mediaItems
    
    private val _currentFilter = mutableStateOf(MediaFilter.ALL)
    val currentFilter: State<MediaFilter> = _currentFilter
    
    private val _sortOption = mutableStateOf(SortOption.CREATION_DATE)
    val sortOption: State<SortOption> = _sortOption
    
    fun setFilter(filter: MediaFilter) {
        _currentFilter.value = filter
    }
    
    fun setSortOption(option: SortOption) {
        _sortOption.value = option
    }
    
    fun getFilteredAndSortedMedia(): List<MediaItem> {
        val filtered = when (_currentFilter.value) {
            MediaFilter.ALL -> _mediaItems
            MediaFilter.PHOTOS -> _mediaItems.filter { it.type == MediaType.PHOTO }
            MediaFilter.VIDEOS -> _mediaItems.filter { it.type == MediaType.VIDEO }
        }
        
        return when (_sortOption.value) {
            SortOption.CREATION_DATE -> filtered.sortedByDescending { it.createdDate }
            SortOption.MODIFICATION_DATE -> filtered.sortedByDescending { it.modifiedDate }
            SortOption.NAME -> filtered.sortedBy { it.name }
        }
    }
    
    fun deleteMedia(mediaId: String) {
        _mediaItems.removeAll { it.id == mediaId }
    }
    
    fun updateMediaName(mediaId: String, newName: String) {
        val index = _mediaItems.indexOfFirst { it.id == mediaId }
        if (index != -1) {
            _mediaItems[index] = _mediaItems[index].copy(
                name = newName,
                modifiedDate = Date()
            )
        }
    }
    
    // Sample data initialization
    init {
        loadSampleData()
    }
    
    private fun loadSampleData() {
        _mediaItems.addAll(
            listOf(
                MediaItem("1", "Vacation Photo 1", MediaType.PHOTO, "", Date(), Date(), 1024),
                MediaItem("2", "Trip Video", MediaType.VIDEO, "", Date(), Date(), 5120),
                MediaItem("3", "Beach Photo", MediaType.PHOTO, "", Date(), Date(), 2048),
                MediaItem("4", "Mountain Hike", MediaType.VIDEO, "", Date(), Date(), 8192)
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaOrganizationScreen(
    viewModel: MediaViewModel = viewModel()
) {
    var showActionDialog by remember { mutableStateOf(false) }
    var selectedMedia by remember { mutableStateOf<MediaItem?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showShareDialog by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // Profile Header
        ProfileHeader()
        
        // Filter Tabs
        FilterTabs(
            currentFilter = viewModel.currentFilter.value,
            onFilterChange = { viewModel.setFilter(it) }
        )
        
        // Media Grid
        MediaGrid(
            mediaItems = viewModel.getFilteredAndSortedMedia(),
            onMediaClick = { media ->
                selectedMedia = media
                showActionDialog = true
            }
        )
    }
    
    // Action Dialog
    if (showActionDialog && selectedMedia != null) {
        MediaActionDialog(
            media = selectedMedia!!,
            onDismiss = { 
                showActionDialog = false
                selectedMedia = null
            },
            onDelete = { media ->
                viewModel.deleteMedia(media.id)
                showActionDialog = false
                selectedMedia = null
            },
            onEdit = {
                showActionDialog = false
                showEditDialog = true
            },
            onShare = {
                showActionDialog = false
                showShareDialog = true
            }
        )
    }
    
    // Edit Dialog
    if (showEditDialog && selectedMedia != null) {
        EditMediaDialog(
            media = selectedMedia!!,
            onDismiss = { 
                showEditDialog = false
                selectedMedia = null
            },
            onSave = { newName ->
                viewModel.updateMediaName(selectedMedia!!.id, newName)
                showEditDialog = false
                selectedMedia = null
            }
        )
    }
    
    // Share Dialog
    if (showShareDialog && selectedMedia != null) {
        ShareDialog(
            media = selectedMedia!!,
            onDismiss = { 
                showShareDialog = false
                selectedMedia = null
            }
        )
    }
}

@Composable
fun ProfileHeader() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Picture
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF6C63FF))
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Profile",
                    modifier = Modifier
                        .size(50.dp)
                        .align(Alignment.Center),
                    tint = Color.White
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // User Name
            Text(
                text = "John Doe",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2C3E50)
            )
            
            Text(
                text = "Mes Médias",
                fontSize = 14.sp,
                color = Color(0xFF7F8C8D)
            )
        }
    }
}

@Composable
fun FilterTabs(
    currentFilter: MediaFilter,
    onFilterChange: (MediaFilter) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        FilterTab(
            text = "Tous",
            isSelected = currentFilter == MediaFilter.ALL,
            onClick = { onFilterChange(MediaFilter.ALL) }
        )
        FilterTab(
            text = "Photos",
            isSelected = currentFilter == MediaFilter.PHOTOS,
            onClick = { onFilterChange(MediaFilter.PHOTOS) }
        )
        FilterTab(
            text = "Vidéos",
            isSelected = currentFilter == MediaFilter.VIDEOS,
            onClick = { onFilterChange(MediaFilter.VIDEOS) }
        )
    }
}

@Composable
fun FilterTab(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .clickable { onClick() }
            .padding(4.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF6C63FF) else Color.White
        )
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            color = if (isSelected) Color.White else Color(0xFF2C3E50),
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun MediaGrid(
    mediaItems: List<MediaItem>,
    onMediaClick: (MediaItem) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(mediaItems) { media ->
            MediaItemCard(
                media = media,
                onClick = { onMediaClick(media) }
            )
        }
    }
}

@Composable
fun MediaItemCard(
    media: MediaItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Placeholder for media content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFECF0F1))
            ) {
                Icon(
                    if (media.type == MediaType.PHOTO) Icons.Default.Image else Icons.Default.VideoLibrary,
                    contentDescription = media.name,
                    modifier = Modifier
                        .size(40.dp)
                        .align(Alignment.Center),
                    tint = Color(0xFF6C63FF)
                )
            }
            
            // Media type indicator
            Icon(
                if (media.type == MediaType.VIDEO) Icons.Default.PlayArrow else Icons.Default.Image,
                contentDescription = null,
                modifier = Modifier
                    .padding(8.dp)
                    .size(20.dp)
                    .align(Alignment.TopEnd),
                tint = Color.White
            )
            
            // Media name
            Text(
                text = media.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color.Black.copy(alpha = 0.7f),
                        RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
                    )
                    .padding(8.dp)
                    .align(Alignment.BottomCenter),
                color = Color.White,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

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
    icon: androidx.compose.ui.graphics.vector.ImageVector,
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

@Composable
fun EditMediaDialog(
    media: MediaItem,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var newName by remember { mutableStateOf(media.name) }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Modifier le nom",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("Nouveau nom") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Annuler")
                    }
                    
                    Button(
                        onClick = { onSave(newName) },
                        enabled = newName.isNotBlank()
                    ) {
                        Text("OK")
                    }
                }
            }
        }
    }
}

@Composable
fun ShareDialog(
    media: MediaItem,
    onDismiss: () -> Unit
) {
    val shareOptions = listOf(
        "WhatsApp" to Icons.Default.Message,
        "Facebook" to Icons.Default.Share,
        "Instagram" to Icons.Default.CameraAlt,
        "Gmail" to Icons.Default.Email,
        "Bluetooth" to Icons.Default.Bluetooth
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
                    text = "Partager via",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                shareOptions.forEach { (name, icon) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { 
                                // Handle share action
                                onDismiss()
                            }
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
            }
        }
    }
}