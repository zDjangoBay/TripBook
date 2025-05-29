package com.android.tripbook.screens.content

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.tripbook.R
import com.android.tripbook.di.ServiceLocator
import com.android.tripbook.screens.discovery.TravelPost
import com.android.tripbook.screens.discovery.User
import com.android.tripbook.ui.theme.*
import com.android.tripbook.util.Resource
import com.android.tripbook.viewmodel.ContentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPostScreen(
    postId: String,
    onPostUpdated: () -> Unit,
    navigateBack: () -> Unit
) {
    // Get ViewModel using factory
    val factory = ServiceLocator.provideViewModelFactory()
    val viewModel: ContentViewModel = viewModel(factory = factory)
    
    // In a real app, we'd use the ViewModel to get the post data
    // For now, simulating fetching post data based on the postId
    val post = remember {
        TravelPost(
            id = postId,
            title = "Amazing Safari Experience",
            description = "My journey through the Serengeti was incredible. Saw the big five in just two days!",
            imageResId = R.drawable.app_logo,
            likes = 246,
            comments = 52,
            user = User("1", "John Doe", R.drawable.app_logo),
            destination = "Serengeti, Tanzania"
        )
    }
    
    var title by remember { mutableStateOf(post.title) }
    var description by remember { mutableStateOf(post.description) }
    var location by remember { mutableStateOf(post.destination) }
    var selectedImages by remember { mutableStateOf(listOf(post.imageResId)) }
    var selectedTags by remember { mutableStateOf(currentTags) } // Initialize with existing tags
    var showLocationPicker by remember { mutableStateOf(false) }
    var showDiscardDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    
    // Observe state changes for updates
    LaunchedEffect(viewModel.editPostState.value) {
        val state = viewModel.editPostState.value
        when(state) {
            is Resource.Success -> {
                isLoading = false
                if (state.data == true) {
                    // Post updated successfully
                    onPostUpdated()
                }
            }
            is Resource.Loading -> {
                isLoading = true
            }
            is Resource.Error -> {
                isLoading = false
                // In a real app, show error message
            }
            null -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Post") },
                navigationIcon = {
                    IconButton(onClick = { 
                        // Check if changes were made
                        if (title != post.title || description != post.description || location != post.destination) {
                            showDiscardDialog = true
                        } else {
                            navigateBack()
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },                actions = {
                    if (isLoading) {
                        // Show loading indicator
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(24.dp)
                                .padding(end = 16.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        // Save button in app bar
                        TextButton(
                            onClick = {
                                // Update post via ViewModel
                                viewModel.updatePost(
                                    postId = postId,
                                    title = title,
                                    description = description,
                                    location = location,
                                    images = listOf(), // In a real app we'd convert the selected image resources
                                    tags = selectedTags
                                )
                            },
                            enabled = title.isNotBlank() && description.isNotBlank() && !isLoading
                        ) {
                            Text(
                                text = "SAVE",
                                fontWeight = FontWeight.Bold,
                                color = if (title.isNotBlank() && description.isNotBlank() && !isLoading) 
                                        MaterialTheme.colorScheme.primary else Color.Gray
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Image display area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (selectedImages.isEmpty()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { /* Open image picker */ }
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
                    // Show selected images
                    Image(
                        painter = painterResource(id = selectedImages.first()), // Use the first image
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
                    
                    // Edit photos button
                    FloatingActionButton(
                        onClick = { /* Open image picker */ },
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
            
            // Title input
            TextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("Add a title") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                textStyle = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            
            // Description input
            TextField(
                value = description,
                onValueChange = { description = it },
                placeholder = { Text("Share your travel experience...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 200.dp),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
            
            Divider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
            
            // Location selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showLocationPicker = true }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = "Add Location",
                    tint = Primary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = if (location.isEmpty()) "Add Location" else location,
                    color = if (location.isEmpty()) TextSecondary else TextPrimary
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = "Select Location",
                    tint = TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }
            
            Divider(modifier = Modifier.padding(horizontal = 16.dp))
            
            // Tags 
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Tag,
                    contentDescription = "Add Tags",
                    tint = Primary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Add Tags",
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = "Select Tags",
                    tint = TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }
              // Currently associated tags
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 56.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(selectedTags) { tag ->
                    TagChip(
                        tag = tag, 
                        onRemove = { 
                            selectedTags = selectedTags - tag
                        }
                    )
                }
                
                // Add new tag option
                item {
                    SuggestTagButton {
                        // In a real app, show tag selector dialog
                        val newTag = "newtag"
                        if (newTag.isNotEmpty() && !selectedTags.contains(newTag)) {
                            selectedTags = selectedTags + newTag
                        }
                    }
                }
            }
            
            Divider(modifier = Modifier.padding(horizontal = 16.dp))
            
            // Agency association
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { /* Open agency selector */ }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Business,
                    contentDescription = "Associated Agency",
                    tint = Primary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Safari Adventures Ltd.", // Example of an associated agency
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = "Change Agency",
                    tint = TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }
            
            // Spacer at the bottom for better UX
            Spacer(modifier = Modifier.height(32.dp))
        }
        
        // Location picker dialog (simplified)
        if (showLocationPicker) {
            AlertDialog(
                onDismissRequest = { showLocationPicker = false },
                title = { Text("Select Location") },
                text = { 
                    TextField(
                        value = location,
                        onValueChange = { location = it },
                        placeholder = { Text("Search for a location...") },
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(onClick = { showLocationPicker = false }) {
                        Text("Done")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLocationPicker = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
        
        // Discard changes confirmation dialog
        if (showDiscardDialog) {
            AlertDialog(
                onDismissRequest = { showDiscardDialog = false },
                title = { Text("Discard Changes?") },
                text = { Text("You have unsaved changes. Are you sure you want to discard them?") },
                confirmButton = {
                    Button(onClick = navigateBack) {
                        Text("Discard")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDiscardDialog = false }) {
                        Text("Keep Editing")
                    }
                }
            )
        }
    }
}

@Composable
fun TagChip(tag: String, onRemove: () -> Unit) {
    Surface(
        modifier = Modifier.height(32.dp),
        shape = RoundedCornerShape(16.dp),
        color = Primary.copy(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier.padding(start = 12.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "#$tag",
                color = Primary,
                fontSize = 14.sp
            )
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Remove Tag",
                    modifier = Modifier.size(16.dp),
                    tint = Primary
                )
            }
        }
    }
}

@Composable
fun SuggestTagButton(onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .height(32.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = Color.Transparent,
        border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Add, 
                contentDescription = "Add Tag", 
                tint = Primary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Add",
                color = Primary,
                fontSize = 14.sp
            )
        }
    }
}

// Sample existing tags
val currentTags = listOf("safari", "wildlife", "adventure", "tanzania")

// Composable preview would go here in a real implementation
