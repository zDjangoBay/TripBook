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
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
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
    
    // Network Connectivity Management
    private lateinit var connectivityManager: ConnectivityManager
    private var isNetworkAvailable = false
    
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            isNetworkAvailable = true
            runOnUiThread {
                handleNetworkAvailable()
            }
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            isNetworkAvailable = false
            runOnUiThread {
                handleNetworkLost()
            }
        }

        override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
            super.onCapabilitiesChanged(network, networkCapabilities)
            val hasInternet = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            val hasValidated = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            
            runOnUiThread {
                handleNetworkCapabilityChange(hasInternet && hasValidated)
            }
        }
    }
    
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
                Manifest.permission.ACCESS_NETWORK_STATE -> handleNetworkPermission(permission.value)
            }
        }
    }

    // Single permission launcher (keeping for backward compatibility)
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            showToast("Permission granted!")
        } else {
            showToast("Permission denied. Some features may not work properly.")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize connectivity manager
        initializeNetworkMonitoring()
        
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

    // Network Connectivity Methods
    private fun initializeNetworkMonitoring() {
        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()
        
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
        
        // Check initial network state
        checkInitialNetworkState()
    }

    private fun checkInitialNetworkState() {
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        
        isNetworkAvailable = networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        
        if (isNetworkAvailable) {
            handleNetworkAvailable()
        } else {
            handleNetworkLost()
        }
    }

    private fun handleNetworkAvailable() {
        showToast("Network connected - Syncing data...")
        // Enable online features
        enableOnlineFeatures()
        // Sync offline posts
        syncOfflinePosts()
        // Enable real-time features
        enableRealTimeFeatures()
    }

    private fun handleNetworkLost() {
        showToast("Network disconnected - Offline mode enabled")
        // Enable offline mode
        enableOfflineMode()
        // Disable real-time features
        disableRealTimeFeatures()
        // Cache important data
        cacheEssentialData()
    }

    private fun handleNetworkCapabilityChange(hasValidInternet: Boolean) {
        if (hasValidInternet != isNetworkAvailable) {
            isNetworkAvailable = hasValidInternet
            if (hasValidInternet) {
                handleNetworkAvailable()
            } else {
                handleNetworkLost()
            }
        }
    }

    // Network-related feature methods
    private fun enableOnlineFeatures() {
        // Enable cloud sync
        // Enable social features
        // Enable real-time location sharing
        // Enable online backup
    }

    private fun enableOfflineMode() {
        // Enable local storage
        // Show offline indicators
        // Queue actions for later sync
        // Enable offline post creation
    }

    private fun syncOfflinePosts() {
        // Upload pending posts
        // Sync user data
        // Update cached content
        // Resolve conflicts
    }

    private fun enableRealTimeFeatures() {
        // Enable live updates
        // Enable push notifications
        // Enable real-time chat
    }

    private fun disableRealTimeFeatures() {
        // Disable live updates
        // Show cached data
        // Queue real-time actions
    }

    private fun cacheEssentialData() {
        // Cache user profile
        // Cache recent posts
        // Cache essential app data
    }

    // Permission Methods
    private fun checkAndRequestPermissions() {
        val permissionsToRequest = mutableListOf<String>()
        
        val permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.ACCESS_NETWORK_STATE
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
            enableCameraFeatures()
        } else {
            showToast("Camera permission denied - Photo capture disabled")
            disableCameraFeatures()
        }
    }

    private fun handleLocationPermission(isGranted: Boolean) {
        if (isGranted) {
            showToast("Location permission granted - Auto-tagging enabled!")
            enableLocationFeatures()
        } else {
            showToast("Location permission denied - Manual location entry required")
            disableLocationFeatures()
        }
    }

    private fun handleStoragePermission(isGranted: Boolean) {
        if (isGranted) {
            showToast("Storage permission granted - Gallery access enabled!")
            enableStorageFeatures()
        } else {
            showToast("Storage permission denied - Gallery access limited")
            disableStorageFeatures()
        }
    }

    private fun handleMediaPermission(isGranted: Boolean) {
        if (isGranted) {
            showToast("Media permission granted - Full media access enabled!")
            enableMediaFeatures()
        } else {
            showToast("Media permission denied - Limited media access")
            disableMediaFeatures()
        }
    }

    private fun handleNetworkPermission(isGranted: Boolean) {
        if (isGranted) {
            showToast("Network permission granted - Connection monitoring enabled!")
        } else {
            showToast("Network permission denied - Limited connectivity features")
        }
    }

    // Feature control methods
    private fun enableCameraFeatures() {
        // Enable camera-related UI components
    }

    private fun disableCameraFeatures() {
        // Disable camera-related UI components
    }

    private fun enableLocationFeatures() {
        // Enable GPS tracking
        // Enable auto-location tagging
    }

    private fun disableLocationFeatures() {
        // Disable automatic location services
    }

    private fun enableStorageFeatures() {
        // Enable gallery access
        // Enable file import/export
    }

    private fun disableStorageFeatures() {
        // Disable gallery features
    }

    private fun enableMediaFeatures() {
        // Enable full media library access
    }

    private fun disableMediaFeatures() {
        // Limit media access
    }

    // Utility methods
    fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

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

    fun requestSpecificPermission(permission: String) {
        requestPermissionLauncher.launch(permission)
    }

    fun requestAllPermissions() {
        checkAndRequestPermissions()
    }

    fun isNetworkConnected(): Boolean {
        return isNetworkAvailable
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister network callback to avoid memory leaks
        try {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        } catch (e: Exception) {
            // Handle exception if callback was not registered
        }
    }
}