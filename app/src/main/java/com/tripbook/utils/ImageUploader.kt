package com.tripbook.utils

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File

/**
 * Handles image upload from gallery or camera
 */
class ImageUploader(private val context: Context) {

    /**
     * Upload result that contains either the uploaded image URL or an error
     */
    sealed class UploadResult {
        data class Success(val imageUrl: String) : UploadResult()
        data class Error(val exception: Exception) : UploadResult()
        object Loading : UploadResult()
    }

    /**
     * Uploads an image from a local URI to the storage service
     * @param imageUri The URI of the image to upload
     * @return Flow of UploadResult indicating the progress and result of the upload
     */
    fun uploadImage(imageUri: Uri): Flow<UploadResult> = flow {
        emit(UploadResult.Loading)
        try {
            // Here you would implement the actual upload logic
            // For example, using Firebase Storage:
            // val storageRef = FirebaseStorage.getInstance().reference
            // val imageRef = storageRef.child("images/${UUID.randomUUID()}")
            // val uploadTask = imageRef.putFile(imageUri)

            // Simulate upload delay and success for now
            kotlinx.coroutines.delay(2000)

            // Return a mock URL for now
            emit(UploadResult.Success("https://firebasestorage.example.com/image123.jpg"))
        } catch (e: Exception) {
            emit(UploadResult.Error(e))
        }
    }

    /**
     * Compresses an image file before uploading
     * @param imageFile The file to compress
     * @return The compressed file
     */
    fun compressImage(imageFile: File): File {
        // Implement image compression logic
        // This would typically use a library like Compressor
        return imageFile // Return original for now
    }

    /**
     * Creates a temporary file for camera captures
     * @return The created file
     */
    fun createTempImageFile(): File {
        val timestamp = System.currentTimeMillis().toString()
        val storageDir = context.getExternalFilesDir("TripBookImages")
        return File.createTempFile("JPEG_${timestamp}_", ".jpg", storageDir)
    }
}
