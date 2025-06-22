// package com.android.tripbook

// import android.os.Bundle
// import androidx.activity.ComponentActivity
// import androidx.activity.compose.setContent
// import androidx.activity.result.contract.ActivityResultContracts
// import androidx.compose.foundation.layout.fillMaxSize
// import androidx.compose.material3.MaterialTheme
// import androidx.compose.material3.Surface
// import androidx.compose.ui.Modifier
// import androidx.lifecycle.viewmodel.compose.viewModel
// import com.android.tripbook.navigation.AppNavigation
// import com.android.tripbook.posts.repository.PostRepository
// import com.android.tripbook.posts.utils.PostValidator
// import com.android.tripbook.posts.viewmodel.PostViewModel
// import com.android.tripbook.posts.viewmodel.PostViewModelFactory
// import com.android.tripbook.ui.theme.TripBookTheme

// class MainActivity : ComponentActivity() {
    
//     private val requestPermissionLauncher = registerForActivityResult(
//         ActivityResultContracts.RequestPermission()
//     ) { isGranted: Boolean ->
//         if (isGranted) {
//             // Permission granted - handle camera/storage access
//         } else {
//             // Permission denied - show explanation
//         }
//     }

//     override fun onCreate(savedInstanceState: Bundle?) {
//         super.onCreate(savedInstanceState)
        
//         setContent {
//             TripBookTheme {
//                 Surface(
//                     modifier = Modifier.fillMaxSize(),
//                     color = MaterialTheme.colorScheme.background
//                 ) {
//                     val repository = PostRepository()
//                     val validator = PostValidator()
//                     val viewModelFactory = PostViewModelFactory(repository, validator)
//                     val postViewModel: PostViewModel = viewModel(factory = viewModelFactory)
                    
//                     AppNavigation(postViewModel = postViewModel)
//                 }
//             }
//         }
//     }
// }


package com.android.tripbook

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.tripbook.navigation.AppNavigation
import com.android.tripbook.posts.repository.PostRepository
import com.android.tripbook.posts.utils.PostValidator
import com.android.tripbook.posts.viewmodel.PostViewModel
import com.android.tripbook.posts.viewmodel.PostViewModelFactory
import com.android.tripbook.ui.theme.TripBookTheme

class MainActivity : ComponentActivity() {
    
    // Enhanced Permission Management
    private val requestMultiplePermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissions.entries.forEach { permission ->
            when (permission.key) {
                Manifest.permission.CAMERA -> handleCameraPermission(permission.value)
                Manifest.permission.ACCESS_FINE_LOCATION -> handleLocationPermission(permission.value)
                Manifest.permission.ACCESS_COARSE_LOCATION -> handleLocationPermission(permission.value)
                Manifest.permission.READ_EXTERNAL_STORAGE -> handleStoragePermission(permission.value)
                Manifest.permission.WRITE_EXTERNAL_STORAGE -> handleStoragePermission(permission.value)
                Manifest.permission.READ_MEDIA_IMAGES -> handleMediaPermission(permission.value)
                Manifest.permission.READ_MEDIA_VIDEO -> handleMediaPermission(permission.value)
            }
        }
    }

    // Single permission launcher (keeping for backward compatibility)
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted - handle camera/storage access
            showToast("Permission granted!")
        } else {
            // Permission denied - show explanation
            showToast("Permission denied. Some features may not work properly.")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Check and request permissions on app start
        checkAndRequestPermissions()
        
        setContent {
            TripBookTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val repository = PostRepository()
                    val validator = PostValidator()
                    val viewModelFactory = PostViewModelFactory(repository, validator)
                    val postViewModel: PostViewModel = viewModel(factory = viewModelFactory)
                    
                    AppNavigation(postViewModel = postViewModel)
                }
            }
        }
    }

    private fun checkAndRequestPermissions() {
        val permissionsToRequest = mutableListOf<String>()
        
        val permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO
        )
        
        permissions.forEach { permission ->
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission)
            }
        }
        
        if (permissionsToRequest.isNotEmpty()) {
            requestMultiplePermissions.launch(permissionsToRequest.toTypedArray())
        }
    }

    private fun handleCameraPermission(isGranted: Boolean) {
        if (isGranted) {
            showToast("Camera permission granted - You can now capture photos!")
            // Enable camera features
            enableCameraFeatures()
        } else {
            showToast("Camera permission denied - Photo capture disabled")
            // Disable camera features or show alternative
            disableCameraFeatures()
        }
    }

    private fun handleLocationPermission(isGranted: Boolean) {
        if (isGranted) {
            showToast("Location permission granted - Auto-tagging enabled!")
            // Enable location-based features
            enableLocationFeatures()
        } else {
            showToast("Location permission denied - Manual location entry required")
            // Disable location features or show manual input
            disableLocationFeatures()
        }
    }

    private fun handleStoragePermission(isGranted: Boolean) {
        if (isGranted) {
            showToast("Storage permission granted - Gallery access enabled!")
            // Enable gallery and file access
            enableStorageFeatures()
        } else {
            showToast("Storage permission denied - Gallery access limited")
            // Disable storage features
            disableStorageFeatures()
        }
    }

    private fun handleMediaPermission(isGranted: Boolean) {
        if (isGranted) {
            showToast("Media permission granted - Full media access enabled!")
            // Enable full media access
            enableMediaFeatures()
        } else {
            showToast("Media permission denied - Limited media access")
            // Disable media features
            disableMediaFeatures()
        }
    }

    // Feature control methods
    private fun enableCameraFeatures() {
        // Enable camera-related UI components
        // Set camera availability flag
    }

    private fun disableCameraFeatures() {
        // Disable camera-related UI components
        // Show alternative options
    }

    private fun enableLocationFeatures() {
        // Enable GPS tracking
        // Enable auto-location tagging
        // Enable nearby places suggestions
    }

    private fun disableLocationFeatures() {
        // Disable automatic location services
        // Show manual location input
    }

    private fun enableStorageFeatures() {
        // Enable gallery access
        // Enable file import/export
        // Enable photo/video selection
    }

    private fun disableStorageFeatures() {
        // Disable gallery features
        // Show camera-only options
    }

    private fun enableMediaFeatures() {
        // Enable full media library access
        // Enable media management features
    }

    private fun disableMediaFeatures() {
        // Limit media access
        // Show reduced functionality warning
    }

    // Utility method to check if specific permission is granted
    fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    // Utility method to check if all required permissions are granted
    fun hasAllRequiredPermissions(): Boolean {
        val requiredPermissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        
        return requiredPermissions.all { permission ->
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    // Method to request specific permission
    fun requestSpecificPermission(permission: String) {
        requestPermissionLauncher.launch(permission)
    }

    // Method to request all permissions again
    fun requestAllPermissions() {
        checkAndRequestPermissions()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
