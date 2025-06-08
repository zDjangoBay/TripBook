package com.tripbook.userprofileManfoDjuiko.utils

import android.content.Context
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.net.Uri
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object MediaUtils {

    fun createVideoThumbnail(context: Context, videoUri: Uri): Uri? {
        return try {
            val bitmap = ThumbnailUtils.createVideoThumbnail(
                videoUri.path ?: return null,
                MediaStore.Video.Thumbnails.MINI_KIND
            )

            bitmap?.let { saveBitmapToFile(context, it, "video_thumb_${System.currentTimeMillis()}.jpg") }
        } catch (e: Exception) {
            null
        }
    }

    private fun saveBitmapToFile(context: Context, bitmap: Bitmap, fileName: String): Uri? {
        return try {
            val file = File(context.cacheDir, fileName)
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            outputStream.close()

            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: IOException) {
            null
        }
    }

    private fun getFileExtension(fileName: String): String {
        return fileName.substringAfterLast('.', "")
    }

    fun isImageFile(fileName: String): Boolean {
        val imageExtensions = setOf("jpg", "jpeg", "png", "gif", "bmp", "webp")
        return imageExtensions.contains(getFileExtension(fileName).lowercase())
    }

    fun isVideoFile(fileName: String): Boolean {
        val videoExtensions = setOf("mp4", "avi", "mov", "wmv", "flv", "webm", "mkv")
        return videoExtensions.contains(getFileExtension(fileName).lowercase())
    }
}