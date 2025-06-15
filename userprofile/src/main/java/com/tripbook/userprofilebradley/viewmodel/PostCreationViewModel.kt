package com.tripbook.userprofilebradley.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.tripbook.userprofilebradley.data.LocationData
import com.tripbook.userprofilebradley.data.PostData
import com.tripbook.userprofilebradley.data.PostType

class PostCreationViewModel : ViewModel() {

    var uiState by mutableStateOf(PostCreationUiState())
        private set

    fun updatePostType(type: PostType) {
        uiState = uiState.copy(selectedType = type)
    }

    fun updateTitle(title: String) {
        uiState = uiState.copy(title = title)
    }

    fun updateContent(content: String) {
        uiState = uiState.copy(content = content)
    }

    fun updateImageUri(uri: String?) {
        uiState = uiState.copy(imageUri = uri)
    }

    fun updateLocation(location: LocationData?) {
        uiState = uiState.copy(location = location)
    }

    fun publishPost(
        onSuccess: (PostData) -> Unit,
        onError: (String) -> Unit
    ) {
        if (!validatePost()) {
            onError("Please fill in all required fields")
            return
        }

        uiState = uiState.copy(isPublishing = true)

        // Simulate API call
        try {
            val postData = PostData(
                id = generatePostId(),
                type = uiState.selectedType,
                title = uiState.title,
                content = uiState.content,
                imageUri = uiState.imageUri,
                location = uiState.location,
                authorId = "current_user_id", // In real app, get from auth
                authorName = "Bradley" // In real app, get from user profile
            )

            // Simulate network delay
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                uiState = uiState.copy(isPublishing = false)
                onSuccess(postData)
                resetForm()
            }, 1500)

        } catch (e: Exception) {
            uiState = uiState.copy(isPublishing = false)
            onError("Failed to publish post: ${e.message}")
        }
    }

    private fun validatePost(): Boolean {
        return uiState.title.isNotBlank() &&
                uiState.content.isNotBlank() &&
                when (uiState.selectedType) {
                    PostType.MEDIA -> uiState.imageUri != null
                    PostType.LOCATION -> uiState.location != null
                    PostType.TEXT -> true
                }
    }

    private fun generatePostId(): String {
        return "post_${System.currentTimeMillis()}_${(1000..9999).random()}"
    }

    private fun resetForm() {
        uiState = PostCreationUiState()
    }
}

data class PostCreationUiState(
    val selectedType: PostType = PostType.TEXT,
    val title: String = "",
    val content: String = "",
    val imageUri: String? = null,
    val location: LocationData? = null,
    val isPublishing: Boolean = false
)
