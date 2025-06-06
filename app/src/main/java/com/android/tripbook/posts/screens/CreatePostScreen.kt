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

import com.android.tripbook.posts.model.PostModel
import com.android.tripbook.posts.model.PostVisibility
import com.android.tripbook.posts.model.UserMinimal // For collaborators

// Importations des composants UI spécifiques aux posts
import com.android.tripbook.posts.ui.components.PostCollaboratorsInput
import com.android.tripbook.posts.ui.components.PostDescriptionInput
import com.android.tripbook.posts.ui.components.PostTitleInput
import com.android.tripbook.posts.ui.components.PostVisibilitySelector
import com.android.tripbook.posts.ui.components.TagSelector
import com.android.tripbook.posts.ui.components.ImageUploadSection
import com.android.tripbook.posts.ui.components.LocationPicker
import com.android.tripbook.ui.components.common.AudioRecorder
import com.android.tripbook.posts.ui.components.SubmitPostButton

// Importations des ViewModels et modèles spécifiques aux posts
import com.android.tripbook.posts.viewmodel.PostEvent
import com.android.tripbook.posts.viewmodel.PostViewModel
import com.android.tripbook.ui.theme.TripBookTheme
import com.android.tripbook.data.model.TravelLocation // <-- NOUVEL IMPORT (pour le PostModel final)

import java.time.Instant // Pour l'Instant.now() lors de la création du PostModel
import java.util.UUID // Pour générer l'ID du post mocké

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
                selectedLocation = uiState.selectedLocation, // uiState.selectedLocation est déjà du bon type (LocationSearchItem)
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

            // HashtagInput( ... ) // <-- REMOVED si tu ne veux plus la fonctionnalité hashtags

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
                    listOf(UserMinimal("u1", "charlie_travels"), UserMinimal("u2", "diana_explorer"))
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
                        // Construire le PostModel complet ici avant d'appeler onPostCreated()
                        // Ceci est une étape de maquette pour montrer comment les données seraient assemblées.
                        val newPostId = UUID.randomUUID().toString()
                        val currentTimestamp = Instant.now()
                        val newPost = PostModel(
                            id = newPostId,
                            userId = "mock_user_id_creator", // Remplacer par l'ID utilisateur réel
                            username = "MockCreator", // Remplacer par le nom d'utilisateur réel
                            userAvatar = null,
                            isVerified = false,
                            title = uiState.title,
                            description = uiState.description,
                            images = uiState.images,
                            location = uiState.selectedLocation?.let { pickerLoc ->
                                TravelLocation(
                                    id = pickerLoc.id,
                                    name = pickerLoc.name,
                                    latitude = pickerLoc.coordinates?.latitude ?: 0.0,
                                    longitude = pickerLoc.coordinates?.longitude ?: 0.0,
                                    description = pickerLoc.city,
                                    imageUrl = pickerLoc.country
                                )
                            } ?: TravelLocation(UUID.randomUUID().toString(), "Unknown Location", 0.0, 0.0, null, null), // Fallback si aucune localisation sélectionnée
                            tags = uiState.selectedTags,
                            hashtags = emptyList(), 
                            createdAt = currentTimestamp,
                            lastEditedAt = null,
                            visibility = selectedVisibility,
                            collaborators = selectedCollaborators,
                            isEphemeral = false, // Par défaut pour la création
                            ephemeralDurationMillis = null,
                            likes = emptyList(),
                            comments = emptyList()
                        )
                        println("Mock Post Created: $newPost")
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
