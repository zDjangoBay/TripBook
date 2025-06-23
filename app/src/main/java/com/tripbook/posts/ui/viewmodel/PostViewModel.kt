package com.tripbook.posts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tripbook.posts.data.PostRepository
import com.tripbook.posts.ui.event.PostUiEvent
import com.tripbook.posts.ui.state.PostState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PostViewModel(
    private val repository: PostRepository = PostRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(PostState())
    val state: StateFlow<PostState> = _state

    fun onEvent(event: PostUiEvent, content: String) {
        when (event) {
            PostUiEvent.SubmitPost -> submitPost(content)
            else -> {}
        }
    }

    private fun submitPost(content: String) {
        _state.value = PostState(isLoading = true)
        viewModelScope.launch {
            try {
                val success = repository.submitPost(content)
                _state.value = if (success) {
                    PostState(isSuccess = true)
                } else {
                    PostState(error = "Post cannot be empty")
                }
            } catch (e: Exception) {
                _state.value = PostState(error = "Failed to submit: ${e.message}")
            }
        }
    }
}


