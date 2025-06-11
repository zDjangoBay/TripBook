package com.tripbook.data.api

import com.tripbook.data.model.Post
import com.tripbook.data.model.ApiResponse
import retrofit2.Response
import retrofit2.http.*

/**
 * API service interface for Post-related network operations
 */
interface PostApiService {

    @GET("posts")
    suspend fun getPosts(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<List<Post>>>

    @GET("posts/{id}")
    suspend fun getPostById(
        @Path("id") postId: String
    ): Response<ApiResponse<Post>>

    @GET("posts/user/{userId}")
    suspend fun getPostsByUserId(
        @Path("userId") userId: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<List<Post>>>

    @GET("posts/search")
    suspend fun searchPosts(
        @Query("q") query: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<List<Post>>>

    @POST("posts")
    suspend fun createPost(
        @Body post: Post
    ): Response<ApiResponse<Post>>

    @PUT("posts/{id}")
    suspend fun updatePost(
        @Path("id") postId: String,
        @Body post: Post
    ): Response<ApiResponse<Post>>

    @DELETE("posts/{id}")
    suspend fun deletePost(
        @Path("id") postId: String
    ): Response<ApiResponse<Unit>>

    @POST("posts/{id}/like")
    suspend fun likePost(
        @Path("id") postId: String
    ): Response<ApiResponse<Unit>>

    @DELETE("posts/{id}/like")
    suspend fun unlikePost(
        @Path("id") postId: String
    ): Response<ApiResponse<Unit>>
}
