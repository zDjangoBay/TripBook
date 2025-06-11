package com.tripbook.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tripbook.data.model.NetworkResult
import com.tripbook.data.model.Post
import com.tripbook.domain.usecase.PostUseCase
import com.tripbook.domain.validator.PostValidator
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import android.net.Uri

/**
 * ViewModel for managing post-related UI state and operations
 */
class PostViewModel(
    private val postUseCase: PostUseCase
) : ViewModel() {

    // UI State for posts list
    private val _postsState = MutableStateFlow<NetworkResult<List<Post>>>(NetworkResult.Loading())
    val postsState: StateFlow<NetworkResult<List<Post>>> = _postsState.asStateFlow()

    // UI State for single post
    private val _postState = MutableStateFlow<NetworkResult<Post>?>(null)
    val postState: StateFlow<NetworkResult<Post>?> = _postState.asStateFlow()

    // UI State for post creation/update
    private val _postOperationState = MutableStateFlow<NetworkResult<Post>?>(null)
    val postOperationState: StateFlow<NetworkResult<Post>?> = _postOperationState.asStateFlow()

    // UI State for validation
    private val _validationState = MutableStateFlow<PostValidator.ValidationResult?>(null)
    val validationState: StateFlow<PostValidator.ValidationResult?> = _validationState.asStateFlow()

    // Current search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Search results
    private val _searchResults = MutableStateFlow<NetworkResult<List<Post>>?>(null)
    val searchResults: StateFlow<NetworkResult<List<Post>>?> = _searchResults.asStateFlow()

    // Pagination state
    private val _currentPage = MutableStateFlow(1)
    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()

    // Error state
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        // Load initial posts
        loadPosts()
    }

    /**
     * Loads posts with pagination support
     */
    fun loadPosts(refresh: Boolean = false) {
        viewModelScope.launch {
            if (refresh) {
                _currentPage.value = 1
                _postsState.value = NetworkResult.Loading("Refreshing posts...")
            }

            postUseCase.getPosts(_currentPage.value).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        if (refresh || _currentPage.value == 1) {
                            _postsState.value = result
                        } else {
                            // Append to existing posts for pagination
                            val currentPosts = (_postsState.value as? NetworkResult.Success)?.data.orEmpty()
                            val newPosts = currentPosts + result.data
                            _postsState.value = NetworkResult.Success(newPosts)
                        }
                        _isLoadingMore.value = false
                    }
                    is NetworkResult.Error -> {
                        if (_currentPage.value == 1) {
                            _postsState.value = result
                        } else {
                            _errorMessage.value = result.message
                        }
                        _isLoadingMore.value = false
                    }
                    is NetworkResult.Loading -> {
                        if (_currentPage.value == 1) {
                            _postsState.value = result
                        } else {
                            _isLoadingMore.value = true
                        }
                    }
                    is NetworkResult.Exception -> {
                        _postsState.value = NetworkResult.Error("An unexpected error occurred: ${result.exception.message}")
                        _isLoadingMore.value = false
                    }
                }
            }
        }
    }

    /**
     * Loads the next page of posts
     */
    fun loadMorePosts() {
        if (_isLoadingMore.value) return
        
        _currentPage.value += 1
        loadPosts()
    }

    /**
     * Loads a specific post by ID
     */
    fun loadPost(postId: String) {
        viewModelScope.launch {
            _postState.value = NetworkResult.Loading("Loading post...")
            
            val result = postUseCase.getPostById(postId)
            _postState.value = result
        }
    }

    /**
     * Creates a new post with images
     */
    fun createPost(post: Post, imageUris: List<Uri> = emptyList()) {
        viewModelScope.launch {
            postUseCase.createPost(post, imageUris).collect { result ->
                _postOperationState.value = result
                
                if (result is NetworkResult.Success) {
                    // Refresh posts list to include the new post
                    loadPosts(refresh = true)
                }
            }
        }
    }

    /**
     * Updates an existing post
     */
    fun updatePost(post: Post) {
        viewModelScope.launch {
            _postOperationState.value = NetworkResult.Loading("Updating post...")
            
            val result = postUseCase.updatePost(post)
            _postOperationState.value = result
            
            if (result is NetworkResult.Success) {
                // Refresh posts list
                loadPosts(refresh = true)
            }
        }
    }

    /**
     * Deletes a post
     */
    fun deletePost(postId: String, imageIds: List<String> = emptyList()) {
        viewModelScope.launch {
            val result = postUseCase.deletePost(postId, imageIds)
            
            when (result) {
                is NetworkResult.Success -> {
                    // Remove post from current list
                    val currentPosts = (_postsState.value as? NetworkResult.Success)?.data.orEmpty()
                    val updatedPosts = currentPosts.filter { it.id != postId }
                    _postsState.value = NetworkResult.Success(updatedPosts)
                }
                is NetworkResult.Error -> {
                    _errorMessage.value = result.message
                }
                else -> { /* Handle other cases */ }
            }
        }
    }

    /**
     * Searches for posts
     */
    fun searchPosts(query: String) {
        _searchQuery.value = query
        
        if (query.isBlank()) {
            _searchResults.value = null
            return
        }

        viewModelScope.launch {
            postUseCase.searchPosts(query).collect { result ->
                _searchResults.value = result
            }
        }
    }

    /**
     * Clears search results
     */
    fun clearSearch() {
        _searchQuery.value = ""
        _searchResults.value = null
    }

    /**
     * Loads posts by a specific user
     */
    fun loadUserPosts(userId: String) {
        viewModelScope.launch {
            _postsState.value = NetworkResult.Loading("Loading user posts...")
            
            postUseCase.getPostsByUser(userId).collect { result ->
                _postsState.value = result
            }
        }
    }

    /**
     * Validates a post and updates validation state
     */
    fun validatePost(post: Post) {
        val result = postUseCase.validatePost(post)
        _validationState.value = result
    }

    /**
     * Real-time field validation
     */
    fun validateField(field: String, value: String?): Boolean {
        return postUseCase.validateField(field, value)
    }

    /**
     * Likes a post
     */
    fun likePost(postId: String) {
        viewModelScope.launch {
            val result = postUseCase.likePost(postId)
            
            if (result is NetworkResult.Success) {
                // Update the post in the current list
                updatePostInList(postId) { post ->
                    post.copy(likes = post.likes + 1)
                }
            } else if (result is NetworkResult.Error) {
                _errorMessage.value = result.message
            }
        }
    }

    /**
     * Unlikes a post
     */
    fun unlikePost(postId: String) {
        viewModelScope.launch {
            val result = postUseCase.unlikePost(postId)
            
            if (result is NetworkResult.Success) {
                // Update the post in the current list
                updatePostInList(postId) { post ->
                    post.copy(likes = maxOf(0, post.likes - 1))
                }
            } else if (result is NetworkResult.Error) {
                _errorMessage.value = result.message
            }
        }
    }

    /**
     * Updates a specific post in the current posts list
     */
    private fun updatePostInList(postId: String, transform: (Post) -> Post) {
        val currentResult = _postsState.value
        if (currentResult is NetworkResult.Success) {
            val updatedPosts = currentResult.data.map { post ->
                if (post.id == postId) transform(post) else post
            }
            _postsState.value = NetworkResult.Success(updatedPosts)
        }
    }

    /**
     * Clears error message
     */
    fun clearError() {
        _errorMessage.value = null
    }

    /**
     * Clears post operation state
     */
    fun clearPostOperationState() {
        _postOperationState.value = null
    }

    /**
     * Clears validation state
     */
    fun clearValidationState() {
        _validationState.value = null
    }

    /**
     * Refreshes posts (pull-to-refresh)
     */
    fun refreshPosts() {
        loadPosts(refresh = true)
    }
}
