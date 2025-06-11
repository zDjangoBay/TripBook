package com.example.tripbook.profile

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore

object ProfileImageUtils {

    /**
     * Creates a content URI where a captured image can be saved.
     *
     * @param context The context used to access the content resolver.
     * @return Uri where the image will be saved, or null if creation fails.
     */
    fun createImageUri(context: Context): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "profile_image_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }

        return context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
    }
}
