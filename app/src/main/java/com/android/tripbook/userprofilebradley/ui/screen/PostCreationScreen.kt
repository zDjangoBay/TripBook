package com.android.tripbook.userprofilebradley.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.tripbook.userprofilebradley.data.LocationData
import com.android.tripbook.userprofilebradley.ui.compose.PostContentEditor
import com.android.tripbook.userprofilebradley.ui.compose.PostTypeSelector
import com.android.tripbook.userprofilebradley.viewmodel.PostCreationViewModel
import com.android.tripbook.userprofilebradley.viewmodel.PostListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostCreationScreen(
    onNavigateBack: () -> Unit,
    onPostPublished: () -> Unit,
    postListViewModel: PostListViewModel? = null,
    viewModel: PostCreationViewModel = viewModel()
) {
    val uiState = viewModel.uiState
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(showSnackbar) {
        if (showSnackbar) {
            snackbarHostState.showSnackbar(snackbarMessage)
            showSnackbar = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Post") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            viewModel.publishPost(
                                onSuccess = { postData ->
                                    postListViewModel?.addPost(postData)
                                    onPostPublished()
                                },
                                onError = { error ->
                                    snackbarMessage = error
                                    showSnackbar = true
                                }
                            )
                        },
                        enabled = !uiState.isPublishing
                    ) {
                        if (uiState.isPublishing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Publish")
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Post Type Selection
            PostTypeSelector(
                selectedType = uiState.selectedType,
                onTypeSelected = viewModel::updatePostType
            )

            Divider()

            // Post Content Editor
            PostContentEditor(
                postType = uiState.selectedType,
                title = uiState.title,
                content = uiState.content,
                imageUri = uiState.imageUri,
                location = uiState.location,
                onTitleChange = viewModel::updateTitle,
                onContentChange = viewModel::updateContent,
                onImageSelect = {
                    // In a real app, this would launch image picker
                    viewModel.updateImageUri("sample_image_uri")
                },
                onLocationSelect = {
                    // In a real app, this would launch location picker
                    viewModel.updateLocation(
                        LocationData(
                            latitude = 6.2088,
                            longitude = 1.2536,
                            address = "Lomé, Togo",
                            placeName = "Independence Monument"
                        )
                    )
                }
            )

            // Publishing Status
            if (uiState.isPublishing) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Publishing your post...",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}
