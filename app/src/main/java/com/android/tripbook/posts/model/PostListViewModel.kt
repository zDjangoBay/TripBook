package com.android.tripbook.posts.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.tripbook.posts.ui.state.PostListState
import com.android.tripbook.repository.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.compose.runtime.*


class PostListViewModel(private val repository: PostRepository) : ViewModel() {
    private val _state = MutableStateFlow(PostListState())
    val state: StateFlow<PostListState> = _state

    fun loadPosts() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                repository.getAllPosts().collect { posts ->
                    _state.value = _state.value.copy(posts = posts, isLoading = false)
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message, isLoading = false)
            }
        }
    }

    fun reactToPost(postId: String, reaction: String) {
        val updatedPosts = _state.value.posts.map { post ->
            if (post.id == postId) post.copy(userReaction = reaction) else post
        }
        _state.value = _state.value.copy(posts = updatedPosts)
    }
}
