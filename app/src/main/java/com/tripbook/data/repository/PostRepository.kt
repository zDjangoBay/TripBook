package com.tripbook.data.repository

import com.tripbook.data.api.PostApiService
import com.tripbook.data.model.NetworkResult
import com.tripbook.data.model.Post
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.catch
import retrofit2.HttpException
import java.io.IOException

/**
 * Repository interface for handling post data operations.
 * This class interfaces with backend (SQLite, Firebase, REST API, etc.)
 */
interface PostRepository {
    suspend fun getPosts(page: Int = 1, limit: Int = 20): Flow<NetworkResult<List<Post>>>
    suspend fun getPostById(postId: String): NetworkResult<Post>
    suspend fun savePost(post: Post): NetworkResult<Post>
    suspend fun updatePost(post: Post): NetworkResult<Post>
    suspend fun deletePost(postId: String): NetworkResult<Unit>
    suspend fun getPostsByUserId(userId: String, page: Int = 1, limit: Int = 20): Flow<NetworkResult<List<Post>>>
    suspend fun searchPosts(query: String, page: Int = 1, limit: Int = 20): Flow<NetworkResult<List<Post>>>
    suspend fun likePost(postId: String): NetworkResult<Unit>
    suspend fun unlikePost(postId: String): NetworkResult<Unit>
}

/**
 * Implementation of PostRepository that handles communication with backend services
 */
class PostRepositoryImpl(
    private val postApiService: PostApiService
) : PostRepository {

    override suspend fun getPosts(page: Int, limit: Int): Flow<NetworkResult<List<Post>>> = flow {
        emit(NetworkResult.Loading("Fetching posts..."))
        try {
            val response = postApiService.getPosts(page, limit)
            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse?.success == true && apiResponse.data != null) {
                    emit(NetworkResult.Success(apiResponse.data))
                } else {
                    emit(NetworkResult.Error(apiResponse?.message ?: "Failed to fetch posts"))
                }
            } else {
                emit(NetworkResult.Error("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: HttpException) {
            emit(NetworkResult.Error("Network error: ${e.message()}", e.code()))
        } catch (e: IOException) {
            emit(NetworkResult.Error("Connection error: ${e.message}"))
        } catch (e: Exception) {
            emit(NetworkResult.Exception(e))
        }
    }.catch { e ->
        emit(NetworkResult.Exception(e))
    }

    override suspend fun getPostById(postId: String): NetworkResult<Post> {
        return try {
            val response = postApiService.getPostById(postId)
            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse?.success == true && apiResponse.data != null) {
                    NetworkResult.Success(apiResponse.data)
                } else {
                    NetworkResult.Error(apiResponse?.message ?: "Post not found")
                }
            } else {
                NetworkResult.Error("HTTP ${response.code()}: ${response.message()}")
            }
        } catch (e: HttpException) {
            NetworkResult.Error("Network error: ${e.message()}", e.code())
        } catch (e: IOException) {
            NetworkResult.Error("Connection error: ${e.message}")
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun savePost(post: Post): NetworkResult<Post> {
        return try {
            val response = postApiService.createPost(post)
            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse?.success == true && apiResponse.data != null) {
                    NetworkResult.Success(apiResponse.data)
                } else {
                    NetworkResult.Error(apiResponse?.message ?: "Failed to create post")
                }
            } else {
                NetworkResult.Error("HTTP ${response.code()}: ${response.message()}")
            }
        } catch (e: HttpException) {
            NetworkResult.Error("Network error: ${e.message()}", e.code())
        } catch (e: IOException) {
            NetworkResult.Error("Connection error: ${e.message}")
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun updatePost(post: Post): NetworkResult<Post> {
        return try {
            val response = postApiService.updatePost(post.id, post)
            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse?.success == true && apiResponse.data != null) {
                    NetworkResult.Success(apiResponse.data)
                } else {
                    NetworkResult.Error(apiResponse?.message ?: "Failed to update post")
                }
            } else {
                NetworkResult.Error("HTTP ${response.code()}: ${response.message()}")
            }
        } catch (e: HttpException) {
            NetworkResult.Error("Network error: ${e.message()}", e.code())
        } catch (e: IOException) {
            NetworkResult.Error("Connection error: ${e.message}")
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun deletePost(postId: String): NetworkResult<Unit> {
        return try {
            val response = postApiService.deletePost(postId)
            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse?.success == true) {
                    NetworkResult.Success(Unit)
                } else {
                    NetworkResult.Error(apiResponse?.message ?: "Failed to delete post")
                }
            } else {
                NetworkResult.Error("HTTP ${response.code()}: ${response.message()}")
            }
        } catch (e: HttpException) {
            NetworkResult.Error("Network error: ${e.message()}", e.code())
        } catch (e: IOException) {
            NetworkResult.Error("Connection error: ${e.message}")
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun getPostsByUserId(userId: String, page: Int, limit: Int): Flow<NetworkResult<List<Post>>> = flow {
        emit(NetworkResult.Loading("Fetching user posts..."))
        try {
            val response = postApiService.getPostsByUserId(userId, page, limit)
            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse?.success == true && apiResponse.data != null) {
                    emit(NetworkResult.Success(apiResponse.data))
                } else {
                    emit(NetworkResult.Error(apiResponse?.message ?: "Failed to fetch user posts"))
                }
            } else {
                emit(NetworkResult.Error("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: HttpException) {
            emit(NetworkResult.Error("Network error: ${e.message()}", e.code()))
        } catch (e: IOException) {
            emit(NetworkResult.Error("Connection error: ${e.message}"))
        } catch (e: Exception) {
            emit(NetworkResult.Exception(e))
        }
    }.catch { e ->
        emit(NetworkResult.Exception(e))
    }

    override suspend fun searchPosts(query: String, page: Int, limit: Int): Flow<NetworkResult<List<Post>>> = flow {
        emit(NetworkResult.Loading("Searching posts..."))
        try {
            val response = postApiService.searchPosts(query, page, limit)
            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse?.success == true && apiResponse.data != null) {
                    emit(NetworkResult.Success(apiResponse.data))
                } else {
                    emit(NetworkResult.Error(apiResponse?.message ?: "No posts found"))
                }
            } else {
                emit(NetworkResult.Error("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: HttpException) {
            emit(NetworkResult.Error("Network error: ${e.message()}", e.code()))
        } catch (e: IOException) {
            emit(NetworkResult.Error("Connection error: ${e.message}"))
        } catch (e: Exception) {
            emit(NetworkResult.Exception(e))
        }
    }.catch { e ->
        emit(NetworkResult.Exception(e))
    }

    override suspend fun likePost(postId: String): NetworkResult<Unit> {
        return try {
            val response = postApiService.likePost(postId)
            if (response.isSuccessful) {
                NetworkResult.Success(Unit)
            } else {
                NetworkResult.Error("HTTP ${response.code()}: ${response.message()}")
            }
        } catch (e: HttpException) {
            NetworkResult.Error("Network error: ${e.message()}", e.code())
        } catch (e: IOException) {
            NetworkResult.Error("Connection error: ${e.message}")
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun unlikePost(postId: String): NetworkResult<Unit> {
        return try {
            val response = postApiService.unlikePost(postId)
            if (response.isSuccessful) {
                NetworkResult.Success(Unit)
            } else {
                NetworkResult.Error("HTTP ${response.code()}: ${response.message()}")
            }
        } catch (e: HttpException) {
            NetworkResult.Error("Network error: ${e.message()}", e.code())
        } catch (e: IOException) {
            NetworkResult.Error("Connection error: ${e.message}")
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }
}
