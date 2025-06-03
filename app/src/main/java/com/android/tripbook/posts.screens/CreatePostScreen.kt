package com.android.tripbook.posts.screens

import androidx.compose.runtime.* // For Composable, collectAsState, remember, mutableStateOf, LaunchedEffect
import com.android.tripbook.posts.viewmodel.PostViewModel // To recognize the PostViewModel type
import com.android.tripbook.posts.viewmodel.PostEvent     // For viewModel.handleEvent

// Other necessary imports that should already be there
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.tripbook.posts.ui.components.* // For your custom UI components
import com.android.tripbook.posts.utils.PostValidator


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    viewModel: PostViewModel, // <<<< 1. THIS IS CRITICAL: 'viewModel' is a PARAMETER
    onNavigateBack: () -> Unit,
    onNavigateToImagePicker: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // 2. VERY IMPORTANT: There should NOT be any line inside this function like:
    //    val viewModel: PostViewModel = viewModel(...)
    //    You use the 'viewModel' parameter that was passed into this function directly.

    val uiState by viewModel.uiState.collectAsState() // Uses the passed-in viewModel
    val scrollState = rememberScrollState()
    var submissionAttempted by remember { mutableStateOf(false) }

    // This LaunchedEffect uses the passed-in viewModel's uiState
    LaunchedEffect(uiState.isSubmitting, uiState.title, uiState.description, uiState.error) {
        if (submissionAttempted) {
            if (!uiState.isSubmitting) {
                if (uiState.error == null && uiState.title.isEmpty() && uiState.description.isEmpty()) {
                    onNavigateBack()
                }
                if (uiState.error != null) {
                    submissionAttempted = false
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Post") },
                navigationIcon = {
                    IconButton(onClick = {
                        submissionAttempted = false
                        viewModel.handleEvent(PostEvent.ResetForm) // Uses the passed-in viewModel
                        onNavigateBack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
                // ... (rest of TopAppBar if any)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(17.dp),
            verticalArrangement = Arrangement.spacedBy(25.dp)
        ) {
            // All calls to viewModel.handleEvent(...) below use the passed-in viewModel
            PostTitleInput(
                title = uiState.title,
                onTitleChange = { viewModel.handleEvent(PostEvent.TitleChanged(it)) },
                error = PostValidator().getTitleError(uiState.title)
            )

            PostDescriptionInput(
                description = uiState.description,
                onDescriptionChange = { viewModel.handleEvent(PostEvent.DescriptionChanged(it)) },
                error = PostValidator().getDescriptionError(uiState.description)
            )

            ImageUploadSection(
                images = uiState.selectedImages,
                onImageAdd = onNavigateToImagePicker,
                onImageRemove = { imageId ->
                    viewModel.handleEvent(PostEvent.ImageRemoved(imageId))
                },
                error = PostValidator().getImagesError(uiState.selectedImages)
            )

            LocationPicker(
                selectedLocation = uiState.selectedLocation,
                onLocationSelected = { location ->
                    viewModel.handleEvent(PostEvent.LocationSelected(location))
                },
                error = PostValidator().getLocationError(uiState.selectedLocation),
                viewModel = viewModel // <<<< 3. THIS 'viewModel' IS THE PARAMETER from CreatePostScreen's signature
            )

            TagSelector(
                availableTags = uiState.availableTags,
                selectedTags = uiState.selectedTags,
                onTagToggle = { tag ->
                    viewModel.handleEvent(PostEvent.TagToggled(tag))
                }
            )

            HashtagInput(
                hashtags = uiState.hashtags,
                onHashtagsChange = { hashtags ->
                    viewModel.handleEvent(PostEvent.HashtagsChanged(hashtags))
                }
            )

            if (uiState.error != null && submissionAttempted && !uiState.isSubmitting) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = uiState.error!!,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            SubmitPostButton(
                isEnabled = uiState.isFormValid,
                isLoading = uiState.isSubmitting,
                onSubmit = {
                    submissionAttempted = true
                    viewModel.handleEvent(PostEvent.SubmitPost)
                }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
