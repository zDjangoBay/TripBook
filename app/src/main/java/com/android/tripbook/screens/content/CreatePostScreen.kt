package com.android.tripbook.screens.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.tripbook.di.ServiceLocator
import com.android.tripbook.ui.components.ImageSelectionComponent
import com.android.tripbook.ui.components.LocationSelectionComponent
import com.android.tripbook.ui.components.TagSelectionComponent
import com.android.tripbook.ui.theme.*
import com.android.tripbook.util.Resource
import com.android.tripbook.viewmodel.ContentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    onPostCreated: (String) -> Unit,
    navigateBack: () -> Unit
) {
    // Get ViewModel using factory
    val factory = ServiceLocator.provideViewModelFactory()
    val viewModel: ContentViewModel = viewModel(factory = factory)
    val context = LocalContext.current
    
    // State
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var selectedImagePaths by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedTags by remember { mutableStateOf<List<String>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf<String?>(null) }
    
    // Handle ViewModel state changes
    LaunchedEffect(viewModel.createPostState.value) {
        val state = viewModel.createPostState.value
        when(state) {
            is Resource.Success -> {
                isLoading = false
                // When post is created successfully, navigate to post detail
                state.data?.let { postId ->
                    onPostCreated(postId)
                }
            }
            is Resource.Loading -> {
                isLoading = true
                showError = null
            }
            is Resource.Error -> {
                isLoading = false
                showError = state.message
            }
            null -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Post") },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (isLoading) {
                        // Show loading indicator
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(24.dp)
                                .padding(end = 16.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        // Publish button
                        TextButton(
                            onClick = { 
                                viewModel.createPost(
                                    title = title,
                                    description = description,
                                    location = location,
                                    images = selectedImagePaths,
                                    tags = selectedTags
                                )
                            },
                            enabled = title.isNotBlank() && 
                                     description.isNotBlank() && 
                                     selectedImagePaths.isNotEmpty() &&
                                     !isLoading,
                        ) {
                            Text(
                                text = "PUBLISH",
                                fontWeight = FontWeight.Bold,
                                color = if (title.isNotBlank() && 
                                           description.isNotBlank() && 
                                           selectedImagePaths.isNotEmpty() &&
                                           !isLoading) 
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
            // Error message if present
            if (showError != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = showError ?: "An error occurred",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
              // Image selection component
            ImageSelectionComponent(
                selectedImages = selectedImagePaths,
                onImagesSelected = { selectedImagePaths = it }
            )
            
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
            LocationSelectionComponent(
                currentLocation = location,
                onLocationSelected = { location = it },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Divider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp))
            
            // Tags component
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                TagSelectionComponent(
                    selectedTags = selectedTags,
                    onTagsUpdated = { selectedTags = it },
                    modifier = Modifier.padding(16.dp)
                )
            }
            
            // Agency association (simplified for now)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { /* Open agency selection (to be implemented) */ }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Business,
                        contentDescription = "Associate with Agency",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Column {
                        Text(
                            text = "Associate with Travel Agency",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = "Let travelers know which agency helped you",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                        )
                    }
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    Icon(
                        Icons.Default.ArrowForward,
                        contentDescription = "Select Agency",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            
            // Spacer at the bottom for better UX
            Spacer(modifier = Modifier.height(32.dp))
            
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
            
            // Tags suggestion
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
              // Suggested tags
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 56.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(suggestedTags) { tag ->
                    SuggestedTagChip(
                        tag = tag,
                        selected = selectedTags.contains(tag),
                        onTagSelected = { selected ->
                            selectedTags = if (selected) {
                                selectedTags + tag
                            } else {
                                selectedTags - tag
                            }
                        }
                    )
                }
            }
            
            Divider(modifier = Modifier.padding(horizontal = 16.dp))
            
            // Associate with agency option
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { /* Open agency selector */ }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Business,
                    contentDescription = "Associate with Agency",
                    tint = Primary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Associate with Travel Agency",
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = "Select Agency",
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
    }
}

@Composable
fun SuggestedTagChip(
    tag: String,
    selected: Boolean = false,
    onTagSelected: (Boolean) -> Unit
) {
    val backgroundColor = if (selected) Primary else Color.Transparent
    val textColor = if (selected) Color.White else Primary
    val borderColor = Primary
    
    Surface(
        modifier = Modifier
            .height(32.dp)
            .clickable { onTagSelected(!selected) },
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "#$tag",
                color = textColor,
                fontSize = 14.sp
            )
        }
    }
}

// Sample suggested tags
val suggestedTags = listOf("safari", "wildlife", "adventure", "nature", "beach", "mountains", "roadtrip")

// Composable preview would go here in a real implementation
