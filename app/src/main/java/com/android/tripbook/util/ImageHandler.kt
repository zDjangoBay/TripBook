package com.android.tripbook.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Utility class for handling image operations in TripBook
 */
class ImageHandler(private val context: Context) {

    /**
     * Create a temporary file for capturing an image with the camera
     */
    fun createImageFile(): Pair<File, Uri> {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(imageFileName, ".jpg", storageDir)
        
        val uri = FileProvider.getUriForFile(
            context,
            "com.android.tripbook.fileprovider",
            image
        )
        
        return Pair(image, uri)
    }
    
    /**
     * Save an image to the MediaStore
     */
    suspend fun saveImageToGallery(bitmap: Bitmap, displayName: String): Uri? {
        return try {
            val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }
            
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "$displayName.jpg")
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }
            }
            
            val contentResolver = context.contentResolver
            val uri = contentResolver.insert(collection, contentValues)
            
            uri?.let {
                contentResolver.openOutputStream(it)?.use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)
                }
                
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.clear()
                    contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                    contentResolver.update(it, contentValues, null, null)
                }
            }
            
            uri
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Convert Uri to a file path string representation (for storage)
     */
    fun getImagePathFromUri(uri: Uri): String {
        // For now, just return the URI string for simplicity
        // In a real app, we would handle different URIs differently
        return uri.toString()
    }
    
    /**
     * Utility method to get a displayable URI for images
     */
    fun getDisplayableUri(path: String): Uri {
        return try {
            Uri.parse(path)
        } catch (e: Exception) {
            Uri.EMPTY
        }
    }
}

/**
 * Composable functions for image selection
 */
object ImagePickers {
    
    /**
     * Composable for picking images from gallery
     */
    @Composable
    fun rememberGalleryLauncher(onImageSelected: (Uri?) -> Unit) = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        onImageSelected(uris.firstOrNull())
    }
    
    /**
     * Composable for picking multiple images from gallery
     */
    @Composable
    fun rememberMultipleGalleryLauncher(onImagesSelected: (List<Uri>) -> Unit) = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        onImagesSelected(uris)
    }
    
    /**
     * Composable for taking pictures with camera
     */
    @Composable
    fun rememberCameraLauncher(onImageCaptured: (Boolean) -> Unit) = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        onImageCaptured(success)
    }
    
    /**
     * Combined picker for both gallery and camera
     */
    @Composable
    fun rememberImagePicker(
        context: Context,
        onImagesPicked: (List<Uri>) -> Unit
    ): ImagePickerLauncher {
        val imageHandler = remember { ImageHandler(context) }
        
        // Temporary URI for camera capture
        val tempImageUri = remember { mutableListOf<Uri>() }
        
        val cameraLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.TakePicture()
        ) { success ->
            if (success && tempImageUri.isNotEmpty()) {
                onImagesPicked(tempImageUri)
            }
        }
        
        val galleryLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.GetMultipleContents()
        ) { uris ->
            if (uris.isNotEmpty()) {
                onImagesPicked(uris)
            }
        }
        
        return remember {
            ImagePickerLauncher(
                pickFromGallery = {
                    galleryLauncher.launch("image/*")
                },
                takePhoto = {
                    try {
                        val (_, uri) = imageHandler.createImageFile()
                        tempImageUri.clear()
                        tempImageUri.add(uri)
                        cameraLauncher.launch(uri)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            )
        }
    }
}

/**
 * Launcher class for image picking operations
 */
class ImagePickerLauncher(
    val pickFromGallery: () -> Unit,
    val takePhoto: () -> Unit
)
