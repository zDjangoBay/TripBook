package com.android.tripbook.posts.utils

import android.content.Context
import android.net.Uri
import com.android.tripbook.posts.model.ImageModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

class ImageUploader(private val context: Context) {

    suspend fun uploadFromGallery(uri: Uri): ImageModel = withContext(Dispatchers.IO) {
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

    suspend fun uploadFromCamera(file: File): ImageModel = withContext(Dispatchers.IO) {
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

    private fun getRealPathFromUri(uri: Uri): String {
        // Implementation to get real path from URI
        return uri.path ?: ""
    }
}
