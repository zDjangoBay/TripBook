package com.android.tripbook.posts.screens


import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.tripbook.posts.model.ImageModel
import com.android.tripbook.posts.model.Location
import com.android.tripbook.posts.model.PostVisibility
import com.android.tripbook.posts.model.UserMinimal
import com.android.tripbook.ui.components.common.AudioRecorder
import com.android.tripbook.posts.ui.components.HashtagInput
import com.android.tripbook.posts.ui.components.ImageUploadSection
import com.android.tripbook.posts.ui.components.LocationPicker
import com.android.tripbook.posts.ui.components.PostCollaboratorsInput
import com.android.tripbook.posts.ui.components.PostDescriptionInput
import com.android.tripbook.posts.ui.components.PostTitleInput
import com.android.tripbook.posts.ui.components.PostVisibilitySelector
import com.android.tripbook.posts.ui.components.SubmitPostButton
import com.android.tripbook.posts.ui.components.TagSelector
import com.android.tripbook.posts.viewmodel.PostEvent
import com.android.tripbook.posts.viewmodel.PostViewModel
import com.android.tripbook.ui.theme.TripBookTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    postViewModel: PostViewModel = viewModel(),
    onPostCreated: () -> Unit,
    onBack: () -> Unit = {}
) {
    val uiState by postViewModel.uiState.collectAsState()
    val availableTags = postViewModel.availableTags

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            postViewModel.handleEvent(PostEvent.AddImage(it.toString()))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create New Post") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            PostTitleInput(
                title = uiState.title,
                onTitleChange = { postViewModel.handleEvent(PostEvent.UpdateTitle(it)) },
                error = uiState.titleError,
                modifier = Modifier.fillMaxWidth()
            )

            PostDescriptionInput(
                description = uiState.description,
                onDescriptionChange = { postViewModel.handleEvent(PostEvent.UpdateDescription(it)) },
                error = uiState.descriptionError,
                modifier = Modifier.fillMaxWidth()
            )

            ImageUploadSection(
                images = uiState.images,
                onImageAdd = { imagePickerLauncher.launch("image/*") },
                onImageRemove = { id -> postViewModel.handleEvent(PostEvent.RemoveImage(id)) },
                error = uiState.imagesError,
                modifier = Modifier.fillMaxWidth()
            )

            LocationPicker(
                selectedLocation = uiState.selectedLocation,
                onLocationSelected = { location -> postViewModel.handleEvent(PostEvent.SelectLocation(location)) },
                error = uiState.locationError,
                modifier = Modifier.fillMaxWidth(),
                viewModel = postViewModel
            )

            TagSelector(
                availableTags = availableTags,
                selectedTags = uiState.selectedTags,
                onTagToggle = { tag -> postViewModel.handleEvent(PostEvent.ToggleTag(tag)) },
                error = uiState.tagsError,
                modifier = Modifier.fillMaxWidth()
            )

            HashtagInput(
                hashtags = uiState.hashtagsInput,
                onHashtagsChange = { postViewModel.handleEvent(PostEvent.UpdateHashtags(it)) },
                modifier = Modifier.fillMaxWidth()
            )

            var selectedVisibility by remember { mutableStateOf(PostVisibility.PUBLIC) }
            PostVisibilitySelector(
                selectedVisibility = selectedVisibility,
                onVisibilitySelected = { visibility -> selectedVisibility = visibility }
            )

            var selectedCollaborators by remember { mutableStateOf<List<UserMinimal>>(emptyList()) }
            PostCollaboratorsInput(
                selectedCollaborators = selectedCollaborators,
                onCollaboratorAdded = { user -> selectedCollaborators = selectedCollaborators + user },
                onCollaboratorRemoved = { user -> selectedCollaborators = selectedCollaborators - user },
                onSearchUsers = { query ->
                    listOf(UserMinimal("u1", "alice_travels"), UserMinimal("u2", "bob_explores"))
                        .filter { it.username.contains(query, ignoreCase = true) }
                }
            )

            var recordedAudioPath by remember { mutableStateOf<String?>(null) }
            AudioRecorder(onAudioRecorded = { path ->
                recordedAudioPath = path
            })
            recordedAudioPath?.let {
                Text("Audio-guide enregistré: $it", style = MaterialTheme.typography.bodySmall)
            }

            SubmitPostButton(
                isEnabled = !uiState.isPublishing,
                isLoading = uiState.isPublishing,
                onSubmit = {
                    val success = postViewModel.validateAndPublishPost()
                    if (success) {
                        onPostCreated()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCreatePostScreen() {
    TripBookTheme {
        val mockViewModel = remember { PostViewModel() }
        CreatePostScreen(postViewModel = mockViewModel, onPostCreated = {})
    }
}