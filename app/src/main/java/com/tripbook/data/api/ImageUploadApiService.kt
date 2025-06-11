package com.tripbook.data.api

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

/**
 * API service interface for image upload operations
 */
interface ImageUploadApiService {

    @Multipart
    @POST("upload/image")
    suspend fun uploadImage(
        @Part image: MultipartBody.Part,
        @Part("description") description: String? = null
    ): Response<ImageUploadResponse>

    @Multipart
    @POST("upload/images")
    suspend fun uploadMultipleImages(
        @Part images: List<MultipartBody.Part>,
        @Part("description") description: String? = null
    ): Response<MultipleImageUploadResponse>

    @DELETE("images/{imageId}")
    suspend fun deleteImage(
        @Path("imageId") imageId: String
    ): Response<ResponseBody>
}

/**
 * Response model for single image upload
 */
data class ImageUploadResponse(
    val success: Boolean,
    val imageUrl: String,
    val imageId: String,
    val message: String? = null
)

/**
 * Response model for multiple image upload
 */
data class MultipleImageUploadResponse(
    val success: Boolean,
    val imageUrls: List<String>,
    val imageIds: List<String>,
    val message: String? = null
)
