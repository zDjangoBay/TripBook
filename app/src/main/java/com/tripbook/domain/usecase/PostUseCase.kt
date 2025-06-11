package com.tripbook.domain.usecase

import com.tripbook.data.model.NetworkResult
import com.tripbook.data.model.Post
import com.tripbook.data.repository.PostRepository
import com.tripbook.domain.validator.PostValidator
import com.tripbook.utils.ImageUploader
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow

/**
 * Use case for handling post-related business logic
 * Orchestrates between repository, validator, and image uploader
 */
class PostUseCase(
    private val postRepository: PostRepository,
    private val postValidator: PostValidator,
    private val imageUploader: ImageUploader
) {

    /**
     * Creates a new post with validation and image upload
     * @param post The post to create
     * @param imageUris List of image URIs to upload (optional)
     * @return Flow of NetworkResult indicating the progress and result
     */
    fun createPost(post: Post, imageUris: List<android.net.Uri> = emptyList()): Flow<NetworkResult<Post>> = flow {
        emit(NetworkResult.Loading("Validating post..."))

        // Validate the post first
        val validationResult = postValidator.validate(post)
        if (!validationResult.isValid) {
            val errorMessage = validationResult.errors.values.joinToString(", ")
            emit(NetworkResult.Error("Validation failed: $errorMessage"))
            return@flow
        }

        // If there are warnings, we can still proceed but log them
        if (validationResult.warnings.isNotEmpty()) {
            emit(NetworkResult.Loading("Post has warnings but proceeding..."))
        }

        var finalPost = post

        // Upload images if provided
        if (imageUris.isNotEmpty()) {
            emit(NetworkResult.Loading("Uploading images..."))
            
            try {
                val uploadedImageUrls = mutableListOf<String>()
                
                imageUris.forEach { uri ->
                    imageUploader.uploadImage(uri).collect { uploadResult ->
                        when (uploadResult) {
                            is ImageUploader.UploadResult.Success -> {
                                uploadedImageUrls.add(uploadResult.imageUrl)
                            }
                            is ImageUploader.UploadResult.Error -> {
                                emit(NetworkResult.Error("Image upload failed: ${uploadResult.message}"))
                                return@flow
                            }
                            is ImageUploader.UploadResult.Progress -> {
                                emit(NetworkResult.Loading("Uploading images: ${uploadResult.percentage}%"))
                            }
                            else -> { /* Continue */ }
                        }
                    }
                }
                
                // Update post with uploaded image URLs
                finalPost = post.copy(images = uploadedImageUrls)
                
            } catch (e: Exception) {
                emit(NetworkResult.Error("Failed to upload images: ${e.message}"))
                return@flow
            }
        }

        // Save the post
        emit(NetworkResult.Loading("Saving post..."))
        val saveResult = postRepository.savePost(finalPost)
        when (saveResult) {
            is NetworkResult.Success -> emit(NetworkResult.Success(saveResult.data))
            is NetworkResult.Error -> emit(NetworkResult.Error(saveResult.message))
            is NetworkResult.Exception -> emit(NetworkResult.Exception(saveResult.exception))
            else -> emit(NetworkResult.Error("Unknown error occurred"))
        }
    }

    /**
     * Updates an existing post with validation
     * @param post The post to update
     * @return NetworkResult indicating the result
     */
    suspend fun updatePost(post: Post): NetworkResult<Post> {
        // Validate the post
        val validationResult = postValidator.validate(post)
        if (!validationResult.isValid) {
            val errorMessage = validationResult.errors.values.joinToString(", ")
            return NetworkResult.Error("Validation failed: $errorMessage")
        }

        return postRepository.updatePost(post)
    }

    /**
     * Deletes a post and its associated images
     * @param postId The ID of the post to delete
     * @param imageIds List of image IDs to delete from storage
     * @return NetworkResult indicating the result
     */
    suspend fun deletePost(postId: String, imageIds: List<String> = emptyList()): NetworkResult<Unit> {
        // Delete images first
        imageIds.forEach { imageId ->
            try {
                imageUploader.deleteImage(imageId)
            } catch (e: Exception) {
                // Log but don't fail the entire operation
                // In a real app, you'd use proper logging
            }
        }

        // Delete the post
        return postRepository.deletePost(postId)
    }

    /**
     * Gets posts with pagination
     * @param page Page number (starting from 1)
     * @param limit Number of posts per page
     * @return Flow of NetworkResult with list of posts
     */
    suspend fun getPosts(page: Int = 1, limit: Int = 20): Flow<NetworkResult<List<Post>>> {
        return postRepository.getPosts(page, limit)
    }

    /**
     * Gets a specific post by ID
     * @param postId The ID of the post to fetch
     * @return NetworkResult with the post data
     */
    suspend fun getPostById(postId: String): NetworkResult<Post> {
        return postRepository.getPostById(postId)
    }

    /**
     * Gets posts by a specific user
     * @param userId The ID of the user whose posts to fetch
     * @param page Page number (starting from 1)
     * @param limit Number of posts per page
     * @return Flow of NetworkResult with list of posts
     */
    suspend fun getPostsByUser(userId: String, page: Int = 1, limit: Int = 20): Flow<NetworkResult<List<Post>>> {
        return postRepository.getPostsByUserId(userId, page, limit)
    }

    /**
     * Searches for posts based on a query
     * @param query The search query
     * @param page Page number (starting from 1)
     * @param limit Number of posts per page
     * @return Flow of NetworkResult with list of posts
     */
    suspend fun searchPosts(query: String, page: Int = 1, limit: Int = 20): Flow<NetworkResult<List<Post>>> {
        if (query.isBlank()) {
            return flow { emit(NetworkResult.Error("Search query cannot be empty")) }
        }
        
        return postRepository.searchPosts(query.trim(), page, limit)
    }

    /**
     * Likes a post
     * @param postId The ID of the post to like
     * @return NetworkResult indicating the result
     */
    suspend fun likePost(postId: String): NetworkResult<Unit> {
        return postRepository.likePost(postId)
    }

    /**
     * Unlikes a post
     * @param postId The ID of the post to unlike
     * @return NetworkResult indicating the result
     */
    suspend fun unlikePost(postId: String): NetworkResult<Unit> {
        return postRepository.unlikePost(postId)
    }

    /**
     * Validates a post without saving it
     * @param post The post to validate
     * @return ValidationResult with detailed feedback
     */
    fun validatePost(post: Post): PostValidator.ValidationResult {
        return postValidator.validate(post)
    }

    /**
     * Real-time field validation for UI
     * @param field The field name to validate
     * @param value The field value
     * @return True if the field is valid
     */
    fun validateField(field: String, value: String?): Boolean {
        return postValidator.validateField(field, value)
    }

    /**
     * Combines multiple data streams for comprehensive post listing
     * For example, combining posts with user preferences or trending data
     */
    fun getEnhancedPosts(page: Int = 1, limit: Int = 20): Flow<NetworkResult<List<Post>>> {
        // This could combine posts with additional metadata, user preferences, etc.
        return postRepository.getPosts(page, limit)
    }
}
