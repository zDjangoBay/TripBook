// // CreatePostScreen.kt
// package com.android.tripbook.ui.screens

// import androidx.compose.foundation.layout.*
// import androidx.compose.foundation.rememberScrollState
// import androidx.compose.foundation.verticalScroll
// import androidx.compose.material3.*
// import androidx.compose.runtime.*
// import androidx.compose.ui.Modifier
// import androidx.compose.ui.unit.dp
// import androidx.lifecycle.compose.collectAsStateWithLifecycle
// import com.android.tripbook.data.state.PostEvent
// import com.android.tripbook.ui.components.*
// import com.android.tripbook.ui.viewmodel.PostViewModel
// import com.android.tripbook.utils.PostValidator

// @Composable
// fun CreatePostScreen(
//     viewModel: PostViewModel,
//     modifier: Modifier = Modifier,
//     onPostCreated: () -> Unit = {}
// ) {
//     val uiState by viewModel.uiState.collectAsStateWithLifecycle()
//     val validator = PostValidator()

//     LaunchedEffect(uiState.posts) {
//         if (uiState.title.isEmpty() && uiState.description.isEmpty()) {
//             // Post was successfully created and form was cleared
//             onPostCreated()
//         }
//     }

//     Column(
//         modifier = modifier
//             .fillMaxSize()
//             .padding(16.dp)
//             .verticalScroll(rememberScrollState()),
//         verticalArrangement = Arrangement.spacedBy(20.dp)
//     ) {
//         // Error Message
//         uiState.errorMessage?.let { error ->
//             Card(
//                 colors = CardDefaults.cardColors(
//                     containerColor = MaterialTheme.colorScheme.errorContainer
//                 )
//             ) {
//                 Text(
//                     text = error,
//                     color = MaterialTheme.colorScheme.onErrorContainer,
//                     modifier = Modifier.padding(16.dp),
//                     style = MaterialTheme.typography.bodyMedium
//                 )
//             }
//         }

//         // Title Input
//         PostTitleInput(
//             title = uiState.title,
//             onTitleChange = { viewModel.onEvent(PostEvent.TitleChanged(it)) },
//             isError = uiState.title.isNotEmpty() && validator.validateTitle(uiState.title) != null,
//             errorMessage = validator.validateTitle(uiState.title)
//         )

//         // Description Input
//         PostDescriptionInput(
//             description = uiState.description,
//             onDescriptionChange = { viewModel.onEvent(PostEvent.DescriptionChanged(it)) },
//             isError = uiState.description.isNotEmpty() && validator.validateDescription(uiState.description) != null,
//             errorMessage = validator.validateDescription(uiState.description)
//         )

//         // Image Upload
//         ImageUploadSection(
//             images = uiState.images,
//             onImageAdd = { viewModel.onEvent(PostEvent.ImageAdded(it)) },
//             onImageRemove = { viewModel.onEvent(PostEvent.ImageRemoved(it)) }
//         )

//         // Location Picker
//         LocationPicker(
//             location = uiState.location,
//             onLocationChange = { viewModel.onEvent(PostEvent.LocationChanged(it)) }
//         )

//         // Tag Selector
//         TagSelector(
//             availableTags = uiState.availableTags,
//             selectedTags = uiState.selectedTags,
//             onTagToggle = { viewModel.onEvent(PostEvent.TagToggled(it)) }
//         )

//         // Hashtag Input
//         HashtagInput(
//             hashtags = uiState.hashtags,
//             onHashtagsChange = { viewModel.onEvent(PostEvent.HashtagsChanged(it)) }
//         )

//         // Submit Button
//         SubmitPostButton(
//             onClick = { viewModel.onEvent(PostEvent.SubmitPost) },
//             isEnabled = uiState.isFormValid,
//             isLoading = uiState.isLoading
//         )

//         // Spacer for bottom navigation
//         Spacer(modifier = Modifier.height(80.dp))
//     }
// }

// CreatePostScreen.kt
package com.android.tripbook.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.android.tripbook.data.state.PostEvent
import com.android.tripbook.ui.components.*
import com.android.tripbook.ui.viewmodel.PostViewModel
import com.android.tripbook.utils.PostValidator

@Composable
fun CreatePostScreen(
    viewModel: PostViewModel,
    modifier: Modifier = Modifier,
    onPostCreated: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val validator = remember { PostValidator() }

    // Memoize validation errors
    val titleError = validator.validateTitle(uiState.title)
    val descriptionError = validator.validateDescription(uiState.description)

    // Handle successful post creation
    LaunchedEffect(uiState.isPostSuccessful) {
        if (uiState.isPostSuccessful) {
            onPostCreated()
            viewModel.resetPostCreationStatus()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Optional Loading Indicator
        if (uiState.isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        // Error Message
        uiState.errorMessage?.let { error ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Title Input
        PostTitleInput(
            title = uiState.title,
            onTitleChange = { viewModel.onEvent(PostEvent.TitleChanged(it)) },
            isError = titleError != null,
            errorMessage = titleError
        )

        // Description Input
        PostDescriptionInput(
            description = uiState.description,
            onDescriptionChange = { viewModel.onEvent(PostEvent.DescriptionChanged(it)) },
            isError = descriptionError != null,
            errorMessage = descriptionError
        )

        // Image Upload
        ImageUploadSection(
            images = uiState.images,
            onImageAdd = { viewModel.onEvent(PostEvent.ImageAdded(it)) },
            onImageRemove = { viewModel.onEvent(PostEvent.ImageRemoved(it)) }
        )

        // Location Picker
        LocationPicker(
            location = uiState.location,
            onLocationChange = { viewModel.onEvent(PostEvent.LocationChanged(it)) }
        )

        // Tag Selector
        TagSelector(
            availableTags = uiState.availableTags,
            selectedTags = uiState.selectedTags,
            onTagToggle = { viewModel.onEvent(PostEvent.TagToggled(it)) }
        )

        // Hashtag Input
        HashtagInput(
            hashtags = uiState.hashtags,
            onHashtagsChange = { viewModel.onEvent(PostEvent.HashtagsChanged(it)) }
        )

        // Submit Button
        SubmitPostButton(
            onClick = { viewModel.onEvent(PostEvent.SubmitPost) },
            isEnabled = uiState.isFormValid,
            isLoading = uiState.isLoading
        )

        // Spacer for bottom navigation
        Spacer(modifier = Modifier.height(80.dp))
    }
}
