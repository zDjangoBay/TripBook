package com.tripbook.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

/**
 * Utility class for handling permissions in the app
 */
object PermissionUtils {

    // Camera permission
    const val CAMERA_PERMISSION = Manifest.permission.CAMERA

    // Storage permissions (different for different Android versions)
    val STORAGE_PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
    } else {
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    // All required permissions for the app
    val ALL_REQUIRED_PERMISSIONS = arrayOf(CAMERA_PERMISSION) + STORAGE_PERMISSIONS

    /**
     * Checks if a specific permission is granted
     * @param context The context to check permission against
     * @param permission The permission to check
     * @return True if permission is granted, false otherwise
     */
    fun isPermissionGranted(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Checks if all required permissions are granted
     * @param context The context to check permissions against
     * @return True if all permissions are granted, false otherwise
     */
    fun areAllPermissionsGranted(context: Context): Boolean {
        return ALL_REQUIRED_PERMISSIONS.all { permission ->
            isPermissionGranted(context, permission)
        }
    }

    /**
     * Checks if camera permission is granted
     * @param context The context to check permission against
     * @return True if camera permission is granted, false otherwise
     */
    fun isCameraPermissionGranted(context: Context): Boolean {
        return isPermissionGranted(context, CAMERA_PERMISSION)
    }

    /**
     * Checks if storage permissions are granted
     * @param context The context to check permissions against
     * @return True if storage permissions are granted, false otherwise
     */
    fun areStoragePermissionsGranted(context: Context): Boolean {
        return STORAGE_PERMISSIONS.all { permission ->
            isPermissionGranted(context, permission)
        }
    }

    /**
     * Gets a list of permissions that are not yet granted
     * @param context The context to check permissions against
     * @return List of permissions that need to be requested
     */
    fun getMissingPermissions(context: Context): List<String> {
        return ALL_REQUIRED_PERMISSIONS.filter { permission ->
            !isPermissionGranted(context, permission)
        }
    }

    /**
     * Gets a user-friendly description of what each permission is used for
     * @param permission The permission to get description for
     * @return Human-readable description of the permission usage
     */
    fun getPermissionDescription(permission: String): String {
        return when (permission) {
            Manifest.permission.CAMERA -> "Camera access is needed to take photos for your posts"
            Manifest.permission.READ_EXTERNAL_STORAGE -> "Storage access is needed to select photos from your gallery"
            Manifest.permission.WRITE_EXTERNAL_STORAGE -> "Storage access is needed to save photos"
            Manifest.permission.READ_MEDIA_IMAGES -> "Media access is needed to select photos from your gallery"
            else -> "This permission is required for the app to function properly"
        }
    }

    /**
     * Request codes for different permission types
     */
    object RequestCodes {
        const val CAMERA_PERMISSION = 1001
        const val STORAGE_PERMISSIONS = 1002
        const val ALL_PERMISSIONS = 1003
    }
}
