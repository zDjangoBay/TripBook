package com.android.tripbook.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.tripbook.data.CommentRepository
import com.android.tripbook.data.model.Comment
import com.android.tripbook.util.Resource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * ViewModel for comment-related operations
 */
class CommentViewModel(private val repository: CommentRepository) : ViewModel() {
    
    // Add comment state
    private val _addCommentState = MutableLiveData<Resource<Comment>>()
    val addCommentState: LiveData<Resource<Comment>> = _addCommentState
    
    // Comments for a post
    private val _commentsState = MutableLiveData<Resource<List<Comment>>>()
    val commentsState: LiveData<Resource<List<Comment>>> = _commentsState
    
    // Delete comment state
    private val _deleteCommentState = MutableLiveData<Resource<Boolean>>()
    val deleteCommentState: LiveData<Resource<Boolean>> = _deleteCommentState
    
    // Update comment state
    private val _updateCommentState = MutableLiveData<Resource<Boolean>>()
    val updateCommentState: LiveData<Resource<Boolean>> = _updateCommentState
    
    /**
     * Add a new comment to a post
     */
    fun addComment(postId: String, content: String) {
        _addCommentState.value = Resource.Loading()
        
        viewModelScope.launch {
            try {
                val comment = repository.addComment(postId, content)
                _addCommentState.value = Resource.Success(comment)
                
                // Refresh comments for the post
                loadCommentsForPost(postId)
            } catch (e: Exception) {
                _addCommentState.value = Resource.Error("Failed to add comment: ${e.message}")
            }
        }
    }
    
    /**
     * Load all comments for a post
     */
    fun loadCommentsForPost(postId: String) {
        _commentsState.value = Resource.Loading()
        
        viewModelScope.launch {
            try {
                repository.getCommentsForPost(postId).collectLatest { comments ->
                    _commentsState.value = Resource.Success(comments)
                }
            } catch (e: Exception) {
                _commentsState.value = Resource.Error("Failed to load comments: ${e.message}")
            }
        }
    }
    
    /**
     * Delete a comment
     */
    fun deleteComment(commentId: String) {
        _deleteCommentState.value = Resource.Loading()
        
        viewModelScope.launch {
            try {
                val success = repository.deleteComment(commentId)
                if (success) {
                    _deleteCommentState.value = Resource.Success(true)
                } else {
                    _deleteCommentState.value = Resource.Error("Failed to delete comment")
                }
            } catch (e: Exception) {
                _deleteCommentState.value = Resource.Error("Error deleting comment: ${e.message}")
            }
        }
    }
    
    /**
     * Update a comment
     */
    fun updateComment(commentId: String, newContent: String) {
        _updateCommentState.value = Resource.Loading()
        
        viewModelScope.launch {
            try {
                val success = repository.updateComment(commentId, newContent)
                if (success) {
                    _updateCommentState.value = Resource.Success(true)
                } else {
                    _updateCommentState.value = Resource.Error("Failed to update comment")
                }
            } catch (e: Exception) {
                _updateCommentState.value = Resource.Error("Error updating comment: ${e.message}")
            }
        }
    }
}
