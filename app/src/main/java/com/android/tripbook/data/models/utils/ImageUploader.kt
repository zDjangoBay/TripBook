package com.android.tripbook.utils

import android.content.Context
import android.net.Uri
import com.android.tripbook.data.models.ImageModel
import kotlinx.coroutines.delay

class ImageUploader(private val context: Context) {
    
    suspend fun uploadImage(uri: Uri): ImageModel {
        // Simulate upload process
        delay(2000)
        
        return ImageModel(
            uri = uri,
            path = uri.toString(),
            isUploaded = true
        )
    }

    suspend fun uploadImages(uris: List<Uri>): List<ImageModel> {
        return uris.map { uploadImage(it) }
    }

    fun validateImage(uri: Uri): Boolean {
        // Basic validation - check if URI is valid
        return try {
            context.contentResolver.openInputStream(uri)?.use { true } ?: false
        } catch (e: Exception) {
            false
        }
    }

    fun getImageSize(uri: Uri): Long {
        return try {
            context.contentResolver.openInputStream(uri)?.available()?.toLong() ?: 0L
        } catch (e: Exception) {
            0L
        }
    }
}