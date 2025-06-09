package com.android.tripbook.posts.controller

import FakePostRepository
import com.android.tripbook.posts.model.PostModel
import kotlinx.coroutines.flow.*

data class PostListUiState(
    val posts: List<PostModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class PostListController(
    private val repository: FakePostRepository
) {
    private val _uiState = MutableStateFlow(PostListUiState())
    val uiState: StateFlow<PostListUiState> = _uiState

    init {
        loadPosts()
    }

    fun refresh() {
        loadPosts()
    }

    private fun loadPosts() {
        // Load posts from repository and update _uiState
    }
}
