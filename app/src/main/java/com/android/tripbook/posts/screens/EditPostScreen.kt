package com.android.tripbook.posts.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

import com.android.tripbook.posts.model.PostModel
import com.android.tripbook.posts.model.PostVisibility
import com.android.tripbook.posts.model.UserMinimal // s

// Importations des composants UI spécifiques aux posts
// import com.android.tripbook.posts.ui.components.HashtagInput //
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
import com.android.tripbook.posts.viewmodel.PostViewModel
import com.android.tripbook.posts.model.ImageModel
import com.android.tripbook.posts.model.Location as LocationPickerLocation
import com.android.tripbook.data.model.TravelLocation
import com.android.tripbook.posts.model.TagModel
import com.android.tripbook.posts.model.Category
import com.android.tripbook.posts.model.Comment

import com.android.tripbook.ui.theme.TripBookTheme
import java.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPostScreen(
    post: PostModel,
    onPostUpdated: (PostModel) -> Unit,
    onCancel: () -> Unit,
    postViewModel: PostViewModel = viewModel()
) {
    // Initialisation des états avec les valeurs du post existant
    var title by remember { mutableStateOf(post.title) }
    var description by remember { mutableStateOf(post.description) }
    var selectedImages by remember { mutableStateOf(post.images) }
    var selectedLocationForPicker by remember {
        mutableStateOf<LocationPickerLocation?>(
            post.location.let {
                LocationPickerLocation(null.toString(), null.toString(), null.toString(),
                    null.toString(), null)
            }
        )
    }
    var selectedTags by remember { mutableStateOf(post.tags) }
    var selectedVisibility by remember { mutableStateOf(post.visibility) }
    var selectedCollaborators by remember { mutableStateOf(post.collaborators ?: emptyList()) }
    var recordedAudioPath by remember { mutableStateOf<String?>(null) }


    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val newImage = ImageModel(id = "new_img_${System.currentTimeMillis()}", uri = it)
            selectedImages = selectedImages + newImage
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Post") },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Cancel")
                    }
                },
                actions = {
                    Button(onClick = {
                        val updatedPost = post.copy(
                            title = title,
                            description = description,
                            images = selectedImages,
                            location = selectedLocationForPicker?.let { pickerLoc ->
                                com.android.tripbook.posts.model.TravelLocation(
                                    id = pickerLoc.id,
                                    name = pickerLoc.name,
                                    latitude = pickerLoc.coordinates?.latitude ?: 0.0,
                                    longitude = pickerLoc.coordinates?.longitude ?: 0.0,
                                    description = pickerLoc.city,
                                    imageUrl = null
                                )
                            } ?: post.location,
                            tags = selectedTags,
                            visibility = selectedVisibility,
                            collaborators = selectedCollaborators,
                            lastEditedAt = Instant.now()
                        )
                        onPostUpdated(updatedPost)
                    }) {
                        Text("Save")
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
                title = title,
                onTitleChange = { title = it },
                error = null,
                modifier = Modifier.fillMaxWidth()
            )
            PostDescriptionInput(
                description = description,
                onDescriptionChange = { description = it },
                error = null,
                modifier = Modifier.fillMaxWidth()
            )

            ImageUploadSection(
                images = selectedImages,
                onImageAdd = { imagePickerLauncher.launch("image/*") },
                onImageRemove = { id -> selectedImages = selectedImages.filter { it.id != id } },
                error = null,
                modifier = Modifier.fillMaxWidth()
            )

            LocationPicker(
                selectedLocation = selectedLocationForPicker,
                onLocationSelected = { loc ->
                    selectedLocationForPicker = loc
                },
                error = null,
                modifier = Modifier.fillMaxWidth(),
                viewModel = postViewModel
            )

            TagSelector(
                availableTags = postViewModel.availableTags,
                selectedTags = selectedTags,
                onTagToggle = { tag ->
                    selectedTags = if (selectedTags.contains(tag)) {
                        selectedTags - tag
                    } else {
                        selectedTags + tag
                    }
                },
                error = null,
                modifier = Modifier.fillMaxWidth()
            )

            // HashtagInput( ... ) // COMPOSANT SUPPRIMÉ

            PostVisibilitySelector(
                selectedVisibility = selectedVisibility,
                onVisibilitySelected = { visibility -> selectedVisibility = visibility }
            )

            PostCollaboratorsInput(
                selectedCollaborators = selectedCollaborators,
                onCollaboratorAdded = { user -> selectedCollaborators = selectedCollaborators + user },
                onCollaboratorRemoved = { user -> selectedCollaborators = selectedCollaborators - user },
                onSearchUsers = { query ->
                    listOf(UserMinimal("u1", "charlie_travels"), UserMinimal("u2", "diana_explorer"))
                        .filter { it.username.contains(query, ignoreCase = true) }
                }
            )

            AudioRecorder(onAudioRecorded = { path ->
                recordedAudioPath = path
            })
            recordedAudioPath?.let {
                Text("Audio guide recorded: $it", style = MaterialTheme.typography.bodySmall)
            }

            SubmitPostButton(
                isEnabled = true,
                isLoading = false,
                onSubmit = {
                    val updatedPost = post.copy(
                        title = title,
                        description = description,
                        images = selectedImages,
                        location = selectedLocationForPicker?.let { pickerLoc ->
                            com.android.tripbook.posts.model.TravelLocation(
                                id = pickerLoc.id,
                                name = pickerLoc.name,
                                latitude = pickerLoc.coordinates?.latitude ?: 0.0,
                                longitude = pickerLoc.coordinates?.longitude ?: 0.0,
                                description = pickerLoc.city,
                                imageUrl = null
                            )
                        } ?: post.location,
                        tags = selectedTags,
                        visibility = selectedVisibility,
                        collaborators = selectedCollaborators,
                        lastEditedAt = Instant.now()
                    )
                    onPostUpdated(updatedPost)
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    @Composable
    fun PreviewEditPostScreen() {
        TripBookTheme {
            val samplePost = PostModel(
                id = "1",
                userId = "test_user",
                username = "TestUser",
                userAvatar = null,
                isVerified = false,
                title = "My Test Trip",
                description = "This is a sample description for editing.",
                images = listOf(ImageModel("img1", Uri.parse("https://via.placeholder.com/150/FF0000/FFFFFF?text=Old+Image"))),
                location = com.android.tripbook.posts.model.TravelLocation(
                    "loc1",
                    "Old City",
                    10.0,
                    20.0,
                    "Old Description",
                    null
                ),
                tags = listOf(TagModel("t1", "Adventure", Category.ACTIVITY)),
                createdAt = Instant.now(),
                lastEditedAt = null,
                visibility = PostVisibility.PUBLIC,
                collaborators = listOf(UserMinimal("collab1", "Friend A")),
                isEphemeral = false,
                ephemeralDurationMillis = null,
                likes = emptyList(),
                comments = emptyList<Comment>()
            )
            val mockViewModel = remember { PostViewModel() }
            EditPostScreen(post = samplePost, onPostUpdated = {}, onCancel = {}, postViewModel = mockViewModel)
        }
    }
}