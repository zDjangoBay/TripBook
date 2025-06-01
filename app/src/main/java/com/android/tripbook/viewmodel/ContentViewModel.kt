package com.android.tripbook.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.tripbook.data.PostRepository
import com.android.tripbook.data.model.Post
import com.android.tripbook.util.Resource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.UUID

class ContentViewModel(private val repository: PostRepository) : ViewModel() {
    
    // For CreatePost screen
    private val _createPostState = MutableLiveData<Resource<String>>()
    val createPostState: LiveData<Resource<String>> = _createPostState
    
    // For EditPost screen
    private val _editPostState = MutableLiveData<Resource<Boolean>>()
    val editPostState: LiveData<Resource<Boolean>> = _editPostState
    
    // For post details
    private val _postState = MutableLiveData<Resource<Post>>()
    val postState: LiveData<Resource<Post>> = _postState
      // For user posts
    private val _userPostsState = MutableLiveData<Resource<List<Post>>>()
    val userPostsState: LiveData<Resource<List<Post>>> = _userPostsState
    
    // For feed posts
    private val _feedPostsState = MutableLiveData<Resource<List<Post>>>()
    val feedPostsState: LiveData<Resource<List<Post>>> = _feedPostsState
    
    // For search results
    private val _searchResultsState = MutableLiveData<Resource<List<Post>>>()
    val searchResultsState: LiveData<Resource<List<Post>>> = _searchResultsState
    
    /**
     * Create a new post
     */
    fun createPost(title: String, description: String, location: String, images: List<String>, tags: List<String>, agencyId: String? = null) {
        _createPostState.value = Resource.Loading()
        
        viewModelScope.launch {
            try {
                val postId = repository.createPost(
                    title = title,
                    description = description,
                    location = location,
                    images = images,
                    tags = tags,
                    agencyId = agencyId
                )
                _createPostState.value = Resource.Success(postId)
            } catch (e: Exception) {
                _createPostState.value = Resource.Error("Failed to create post: ${e.message}")
            }
        }
    }
      /**
     * Update an existing post
     */
    fun updatePost(postId: String, title: String, description: String, location: String, images: List<String>, tags: List<String>, agencyId: String? = null) {
        _editPostState.value = Resource.Loading()
        
        viewModelScope.launch {
            try {
                val success = repository.updatePost(
                    postId = postId,
                    title = title,
                    description = description,
                    location = location,
                    images = images,
                    tags = tags,
                    agencyId = agencyId
                )
                _editPostState.value = Resource.Success(success)
            } catch (e: Exception) {
                _editPostState.value = Resource.Error("Failed to update post: ${e.message}")
            }
        }
    }
    
    /**
     * Delete a post
     */
    fun deletePost(postId: String) {
        viewModelScope.launch {
            try {
                // In a real app, this would communicate with the repository
                repository.deletePost(postId)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    /**
     * Get post details
     */
    fun getPost(postId: String) {
        _postState.value = Resource.Loading()
        
        viewModelScope.launch {
            try {
                val post = repository.getPost(postId)
                _postState.value = Resource.Success(post)
            } catch (e: Exception) {
                _postState.value = Resource.Error("Failed to load post: ${e.message}")
            }
        }
    }
    
    /**
     * Get current user's posts
     */
    fun getUserPosts() {
        _userPostsState.value = Resource.Loading()
        
        viewModelScope.launch {
            try {
                val posts = repository.getUserPosts()
                _userPostsState.value = Resource.Success(posts)
            } catch (e: Exception) {
                _userPostsState.value = Resource.Error("Failed to load posts: ${e.message}")
            }
        }
    }
    
    /**
     * Get posts for the feed
     */
    fun getFeedPosts(limit: Int = 20, offset: Int = 0) {
        _feedPostsState.value = Resource.Loading()
        
        viewModelScope.launch {
            try {
                repository.getFeedPosts(limit, offset).collectLatest { posts =>
                    _feedPostsState.value = Resource.Success(posts)
                }
            } catch (e: Exception) {
                _feedPostsState.value = Resource.Error("Failed to load feed: ${e.message}")
            }
        }
    }
    
    /**
     * Search for posts
     */
    fun searchPosts(query: String) {
        if (query.isEmpty()) {
            _searchResultsState.value = Resource.Success(emptyList())
            return
        }
        
        _searchResultsState.value = Resource.Loading()
        
        viewModelScope.launch {
            try {
                repository.searchPosts(query).collectLatest { posts =>
                    _searchResultsState.value = Resource.Success(posts)
                }
            } catch (e: Exception) {
                _searchResultsState.value = Resource.Error("Search failed: ${e.message}")
            }
        }
    }
}
