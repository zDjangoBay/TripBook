// PostViewModel.kt
package com.android.tripbook.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.tripbook.data.models.ImageModel
import com.android.tripbook.data.models.PostModel
import com.android.tripbook.data.models.TagModel
import com.android.tripbook.data.repository.PostRepository
import com.android.tripbook.data.state.PostEvent
import com.android.tripbook.data.state.PostUIState
import com.android.tripbook.utils.PostValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PostViewModel : ViewModel() {
    private val repository = PostRepository()
    private val validator = PostValidator()

    private val _uiState = MutableStateFlow(PostUIState())
    val uiState: StateFlow<PostUIState> = _uiState.asStateFlow()

    init {
        loadPosts()
    }

    fun onEvent(event: PostEvent) {
        when (event) {
            is PostEvent.TitleChanged -> {
                _uiState.value = _uiState.value.copy(
                    title = event.title,
                    isFormValid = validator.isValid(
                        _uiState.value.copy(title = event.title)
                    )
                )
            }
            is PostEvent.DescriptionChanged -> {
                _uiState.value = _uiState.value.copy(
                    description = event.description,
                    isFormValid = validator.isValid(
                        _uiState.value.copy(description = event.description)
                    )
                )
            }
            is PostEvent.LocationChanged -> {
                _uiState.value = _uiState.value.copy(location = event.location)
            }
            is PostEvent.HashtagsChanged -> {
                _uiState.value = _uiState.value.copy(hashtags = event.hashtags)
            }
            is PostEvent.ImageAdded -> {
                val newImage = ImageModel(uri = event.uri)
                val updatedImages = _uiState.value.images + newImage
                _uiState.value = _uiState.value.copy(images = updatedImages)
            }
            is PostEvent.ImageRemoved -> {
                val updatedImages = _uiState.value.images.filter { it.id != event.imageId }
                _uiState.value = _uiState.value.copy(images = updatedImages)
            }
            is PostEvent.TagToggled -> {
                val currentTags = _uiState.value.selectedTags.toMutableList()
                if (currentTags.any { it.id == event.tag.id }) {
                    currentTags.removeAll { it.id == event.tag.id }
                } else {
                    currentTags.add(event.tag)
                }
                _uiState.value = _uiState.value.copy(selectedTags = currentTags)
            }
            is PostEvent.SubmitPost -> {
                submitPost()
            }
            is PostEvent.ClearForm -> {
                clearForm()
            }
            is PostEvent.LoadPosts -> {
                loadPosts()
            }
            is PostEvent.DeletePost -> {
                deletePost(event.postId)
            }
        }
    }

    private fun submitPost() {
        if (!_uiState.value.isFormValid) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                val post = PostModel(
                    title = _uiState.value.title,
                    description = _uiState.value.description,
                    images = _uiState.value.images,
                    location = _uiState.value.location,
                    tags = _uiState.value.selectedTags,
                    hashtags = _uiState.value.hashtagsList
                )
                
                repository.createPost(post)
                clearForm()
                loadPosts()
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to create post: ${e.message}"
                )
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    private fun clearForm() {
        _uiState.value = _uiState.value.copy(
            title = "",
            description = "",
            images = emptyList(),
            location = "",
            selectedTags = emptyList(),
            hashtags = "",
            isFormValid = false,
            errorMessage = null
        )
    }

    private fun loadPosts() {
        viewModelScope.launch {
            try {
                val posts = repository.getAllPosts()
                _uiState.value = _uiState.value.copy(posts = posts)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to load posts: ${e.message}"
                )
            }
        }
    }

    private fun deletePost(postId: String) {
        viewModelScope.launch {
            try {
                repository.deletePost(postId)
                loadPosts()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to delete post: ${e.message}"
                )
            }
        }
    }
}