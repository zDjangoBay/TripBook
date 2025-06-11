package com.tripbook.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import com.tripbook.data.api.ImageUploadApiService
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Enhanced ImageUploader that handles image upload from gallery or camera with compression and error handling
 */
class ImageUploader(
    private val context: Context,
    private val imageUploadApiService: ImageUploadApiService
) {

    companion object {
        private const val MAX_IMAGE_SIZE = 1024 * 1024 * 5 // 5MB
        private const val COMPRESSION_QUALITY = 80
        private const val MAX_WIDTH = 1920
        private const val MAX_HEIGHT = 1080
        private const val AUTHORITY = "com.android.tripbook.fileprovider"
    }

    /**
     * Upload result that contains either the uploaded image URL or an error
     */
    sealed class UploadResult {
        data class Success(val imageUrl: String, val imageId: String) : UploadResult()
        data class Error(val message: String, val exception: Exception? = null) : UploadResult()
        data class Progress(val percentage: Int, val message: String = "Uploading...") : UploadResult()
        object Loading : UploadResult()
    }

    /**
     * Uploads an image from a local URI to the storage service
     * @param imageUri The URI of the image to upload
     * @param description Optional description for the image
     * @return Flow of UploadResult indicating the progress and result of the upload
     */
    fun uploadImage(imageUri: Uri, description: String? = null): Flow<UploadResult> = flow {
        emit(UploadResult.Loading)
        
        try {
            emit(UploadResult.Progress(10, "Preparing image..."))
            
            // Convert URI to File
            val imageFile = uriToFile(imageUri)
                ?: throw IOException("Could not convert URI to file")
            
            emit(UploadResult.Progress(30, "Compressing image..."))
            
            // Compress the image
            val compressedFile = compressImage(imageFile)
            
            emit(UploadResult.Progress(50, "Uploading to server..."))
            
            // Create multipart body
            val requestFile = compressedFile.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("image", compressedFile.name, requestFile)
            
            // Upload to server
            val response = imageUploadApiService.uploadImage(body, description)
            
            emit(UploadResult.Progress(90, "Processing response..."))
            
            if (response.isSuccessful) {
                val uploadResponse = response.body()
                if (uploadResponse?.success == true) {
                    emit(UploadResult.Success(uploadResponse.imageUrl, uploadResponse.imageId))
                } else {
                    emit(UploadResult.Error(uploadResponse?.message ?: "Upload failed"))
                }
            } else {
                emit(UploadResult.Error("HTTP ${response.code()}: ${response.message()}"))
            }
            
            // Clean up temporary file
            compressedFile.delete()
            
        } catch (e: IOException) {
            emit(UploadResult.Error("File error: ${e.message}", e))
        } catch (e: Exception) {
            emit(UploadResult.Error("Upload failed: ${e.message}", e))
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Uploads multiple images simultaneously
     * @param imageUris List of image URIs to upload
     * @param description Optional description for the images
     * @return Flow of UploadResult for the batch upload
     */
    fun uploadMultipleImages(imageUris: List<Uri>, description: String? = null): Flow<UploadResult> = flow {
        emit(UploadResult.Loading)
        
        try {
            emit(UploadResult.Progress(10, "Preparing ${imageUris.size} images..."))
            
            val imageParts = mutableListOf<MultipartBody.Part>()
            
            imageUris.forEachIndexed { index, uri ->
                emit(UploadResult.Progress(
                    10 + (40 * (index + 1) / imageUris.size),
                    "Processing image ${index + 1}/${imageUris.size}..."
                ))
                
                val imageFile = uriToFile(uri)
                    ?: throw IOException("Could not convert URI to file for image ${index + 1}")
                
                val compressedFile = compressImage(imageFile)
                val requestFile = compressedFile.asRequestBody("image/*".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("images", compressedFile.name, requestFile)
                imageParts.add(body)
            }
            
            emit(UploadResult.Progress(60, "Uploading ${imageUris.size} images..."))
            
            val response = imageUploadApiService.uploadMultipleImages(imageParts, description)
            
            emit(UploadResult.Progress(90, "Processing response..."))
            
            if (response.isSuccessful) {
                val uploadResponse = response.body()
                if (uploadResponse?.success == true && uploadResponse.imageUrls.isNotEmpty()) {
                    // For multiple images, we'll return the first URL as primary
                    emit(UploadResult.Success(uploadResponse.imageUrls.first(), uploadResponse.imageIds.first()))
                } else {
                    emit(UploadResult.Error(uploadResponse?.message ?: "Upload failed"))
                }
            } else {
                emit(UploadResult.Error("HTTP ${response.code()}: ${response.message()}"))
            }
            
        } catch (e: IOException) {
            emit(UploadResult.Error("File error: ${e.message}", e))
        } catch (e: Exception) {
            emit(UploadResult.Error("Upload failed: ${e.message}", e))
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Compresses an image file to reduce size while maintaining quality
     * @param imageFile The file to compress
     * @return The compressed file
     */
    suspend fun compressImage(imageFile: File): File = withContext(Dispatchers.IO) {
        try {
            Compressor.compress(context, imageFile) {
                resolution(MAX_WIDTH, MAX_HEIGHT)
                quality(COMPRESSION_QUALITY)
                format(Bitmap.CompressFormat.JPEG)
            }
        } catch (e: Exception) {
            // If compression fails, try manual compression
            manualCompress(imageFile)
        }
    }

    /**
     * Manual image compression fallback
     */
    private suspend fun manualCompress(imageFile: File): File = withContext(Dispatchers.IO) {
        val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
        val outputFile = createTempImageFile()
        
        val outputStream = FileOutputStream(outputFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_QUALITY, outputStream)
        outputStream.close()
        
        outputFile
    }

    /**
     * Converts URI to File
     */
    private suspend fun uriToFile(uri: Uri): File? = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val tempFile = createTempImageFile()
            
            inputStream?.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            
            tempFile
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Creates a temporary file for camera captures or processing
     * @return The created file
     */
    fun createTempImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "TRIPBOOK_${timeStamp}_"
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }

    /**
     * Creates a URI for camera capture
     * @return URI for the image file
     */
    fun createCameraImageUri(): Uri {
        val imageFile = createTempImageFile()
        return FileProvider.getUriForFile(context, AUTHORITY, imageFile)
    }

    /**
     * Validates if the image file meets the requirements
     * @param file The image file to validate
     * @return Validation result with error message if invalid
     */
    fun validateImageFile(file: File): ValidationResult {
        return when {
            !file.exists() -> ValidationResult(false, "File does not exist")
            file.length() > MAX_IMAGE_SIZE -> ValidationResult(false, "File size exceeds 5MB limit")
            !isValidImageFormat(file) -> ValidationResult(false, "Invalid image format. Only JPEG, PNG, and WebP are supported")
            else -> ValidationResult(true)
        }
    }

    /**
     * Checks if the file is a valid image format
     */
    private fun isValidImageFormat(file: File): Boolean {
        return try {
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeFile(file.absolutePath, options)
            options.outMimeType in listOf("image/jpeg", "image/png", "image/webp")
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Deletes an image from the server
     * @param imageId The ID of the image to delete
     * @return True if deletion was successful
     */
    suspend fun deleteImage(imageId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = imageUploadApiService.deleteImage(imageId)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Data class for validation results
     */
    data class ValidationResult(
        val isValid: Boolean,
        val errorMessage: String? = null
    )
}
