package com.android.tripbook.posts.utils

import android.content.Context
import android.net.Uri
import com.android.tripbook.posts.model.ImageModel
import kotlin.Result
import android.provider.OpenableColumns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

/**
 * Utility class for uploading images from gallery or camera.
 * @param context The application context.
 */
class ImageUploader(private val context: Context) {
    /**
     * Uploads an image from the gallery.
     * @param uri The URI of the image.
     * @return Result containing the ImageModel or an error.
     */
    // Note: uploadUrl is a placeholder; replace with actual endpoint in production
    suspend fun uploadFromGallery(uri: Uri): Result<ImageModel> = withContext(Dispatchers.IO) {
        try {
            if (uri.scheme.isNullOrEmpty()) {
                return@withContext Result.failure(Exception("Invalid URI scheme"))
            }
            // Simulate upload process
            delay(1000)
            Result.success(
                ImageModel(
                    id = UUID.randomUUID().toString(),
                    uri = uri.toString(),
                    path = getRealPathFromUri(uri),
                    isUploaded = true,
                    uploadUrl = "https://example.com/uploads/${UUID.randomUUID()}"
                )
            )
        } catch (e: Exception) {
            // Retry once
            try {
                delay(1000)
                Result.success(
                    ImageModel(
                        id = UUID.randomUUID().toString(),
                        uri = uri.toString(),
                        path = getRealPathFromUri(uri),
                        isUploaded = true,
                        uploadUrl = "https://example.com/uploads/${UUID.randomUUID()}"
                    )
                )
            } catch (e2: Exception) {
                Result.failure(Exception("Upload failed after retry: ${e2.message}"))
            }
        }
        }
    }
        // Simulate upload process
        kotlinx.coroutines.delay(1000)

        ImageModel(
            id = UUID.randomUUID().toString(),
            uri = uri.toString(),
            path = getRealPathFromUri(uri),
            isUploaded = true,
            uploadUrl = "https://example.com/uploads/${UUID.randomUUID()}"
        )
    }
/**
 * Uploads an image from the camera.
 * @param file The file containing the image.
 * @return Result containing the ImageModel or an error.
 */
// Note: uploadUrl is a placeholder; replace with actual endpoint in production
suspend fun uploadFromCamera(file: File): Result<ImageModel> = withContext(Dispatchers.IO) {
    try {
        if (!file.exists()) {
            return@withContext Result.failure(Exception("Camera file does not exist"))
        }
        if (file.length() > 10 * 1024 * 1024) { // 10MB
            return@withContext Result.failure(Exception("File size exceeds 10MB"))
        }
        // Simulate upload process
        delay(1500)
        Result.success(
            ImageModel(
                id = UUID.randomUUID().toString(),
                uri = Uri.fromFile(file).toString(),
                path = file.absolutePath,
                isUploaded = true,
                uploadUrl = "https://example.com/uploads/${UUID.randomUUID()}"
            )
        )
    } catch (e: Exception) {
        Result.failure(Exception("Failed to upload image from camera: ${e.message}"))
    }
}
        // Simulate upload process
        kotlinx.coroutines.delay(1500)

        ImageModel(
            id = UUID.randomUUID().toString(),
            uri = Uri.fromFile(file).toString(),
            path = file.absolutePath,
            isUploaded = true,
            uploadUrl = "https://example.com/uploads/${UUID.randomUUID()}"
        )
    }
/**
 * Resolves the real file path from a URI.
 * @param uri The URI of the content.
 * @return The file path or empty string if resolution fails.
 */
// Note: This is a simplified implementation; real path resolution may vary based on Android version
private fun getRealPathFromUri(uri: Uri): String {
    return context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        cursor.moveToFirst()
        val fileName = cursor.getString(nameIndex)
        File(context.cacheDir, fileName).absolutePath
    } ?: uri.path ?: ""
}