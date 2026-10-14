# Location Services Implementation

This implementation provides location services and permissions management for the TripBook Android
application.

## Features Implemented

### 🏠 Location Services (`LocationManager.kt`)
- **Permission Management**: Check and handle location permissions
- **Location Updates**: Real-time location tracking with configurable intervals
- **Provider Management**: Intelligent selection between GPS, Network, and Passive providers
- **Distance Calculations**: Calculate distances between coordinates using Haversine formula
- **Location Quality Assessment**: Determine the best location readings based on accuracy and time
- **Lifecycle Management**: Proper start/stop of location services

### 🔐 Permission Handling (`PermissionHandler.kt`)
- **Modern Permission System**: Support for both legacy and modern permission APIs
- **Permission Status Checking**: Individual and combined permission checks
- **Rationale Handling**: Show permission explanations when needed
- **Result Processing**: Handle permission grant/deny results
- **Compose Integration**: Works with modern Compose permission launchers

### 🎨 Compose Integration (`LocationComposables.kt`)
- **Compose Utilities**: Remember location managers with proper lifecycle
- **Lifecycle Awareness**: Automatic cleanup when composables are disposed
- **Context Integration**: Seamless integration with Android Compose

## File Structure

```
app/src/main/java/com/android/tripbook/utils/
├── LocationManager.kt           # Core location services
├── PermissionHandler.kt        # Permission management
└── LocationComposables.kt      # Compose integration
```

## Usage Examples

### Basic Location Tracking

```kotlin
@Composable
fun LocationScreen() {
    val locationManager = LocationComposables.rememberLifecycleAwareLocationManager()
    val permissionHandler = LocationComposables.rememberPermissionHandler()
    
    var currentLocation by remember { mutableStateOf<Location?>(null) }
    
    LaunchedEffect(Unit) {
        if (permissionHandler.hasLocationPermissions()) {
            locationManager.startLocationUpdates().collect { location ->
                currentLocation = location
            }
        }
    }
    
    currentLocation?.let { location ->
        Text("Current Location: ${location.latitude}, ${location.longitude}")
        Text("Accuracy: ${locationManager.getAccuracyDescription(location.accuracy)}")
    }
}
```

### Permission Handling

```kotlin
@Composable
fun PermissionScreen() {
    val permissionHandler = LocationComposables.rememberPermissionHandler()
    var hasPermissions by remember { mutableStateOf(false) }
    
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasPermissions = permissions.values.all { it }
    }
    
    LaunchedEffect(Unit) {
        hasPermissions = permissionHandler.hasLocationPermissions()
    }
    
    if (!hasPermissions) {
        Button(
            onClick = {
                permissionLauncher.launch(PermissionHandler.LOCATION_PERMISSIONS)
            }
        ) {
            Text("Grant Location Permission")
        }
    } else {
        Text("Location permissions granted!")
    }
}
```

### Distance Calculations

```kotlin
@Composable
fun DistanceCalculator() {
    val locationManager = LocationComposables.rememberLocationManager()
    
    val point1 = LatLng(37.7749, -122.4194) // San Francisco
    val point2 = LatLng(40.7128, -74.0060)  // New York
    
    val distance = locationManager.calculateDistance(
        point1.latitude, point1.longitude,
        point2.latitude, point2.longitude
    )
    
    Text("Distance: ${locationManager.formatDistance(distance)}")
}
```

## Dependencies Added

The implementation uses the following dependencies (already added to `build.gradle.kts`):

```kotlin
// Google Play Services for Location
implementation("com.google.android.gms:play-services-location:21.0.1")
```

## Permissions

The following permissions are configured in `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION" />
```

## Key Features

### Location Services
- ✅ **Permission Management**: Comprehensive permission checking and requesting
- ✅ **Real-time Updates**: Continuous location tracking with optimized intervals (5 seconds, 10
  meters)
- ✅ **Provider Intelligence**: Automatic selection of best location provider (GPS > Network >
  Passive)
- ✅ **Distance Calculations**: Haversine formula for accurate distance measurement
- ✅ **Accuracy Assessment**: Quality-based location filtering with time and accuracy considerations
- ✅ **Lifecycle Management**: Proper start/stop with coroutine-based Flow updates

### Permission Handling

- ✅ **Modern APIs**: Support for both ActivityCompat and modern Compose launchers
- ✅ **Granular Checking**: Individual checks for fine and coarse location permissions
- ✅ **Rationale Support**: Built-in shouldShowRequestPermissionRationale handling
- ✅ **Result Processing**: Clean callback-based result handling

### Compose Integration
- ✅ **Lifecycle Awareness**: Automatic cleanup when composables are disposed
- ✅ **Remember Functions**: Efficient state management with proper caching
- ✅ **Context Integration**: Seamless access to Android services
- ✅ **Modern Architecture**: Built for Jetpack Compose with coroutines and Flow

## Technical Implementation Notes

### LocationManager
- Uses Android's LocationManager with intelligent provider selection
- Implements location quality assessment using accuracy, timing, and provider information
- Provides Flow-based updates for reactive programming with coroutines
- Handles edge cases like provider availability, permissions, and lifecycle
- Includes utility functions for distance calculations and formatting

### PermissionHandler

- Supports both legacy (ActivityCompat) and modern (Compose) permission APIs
- Provides granular permission checking for fine and coarse location
- Handles rationale display requirements for better UX
- Clean separation of concerns with callback-based result handling

### LocationComposables

- Provides remember functions that integrate with Compose lifecycle
- Automatically handles cleanup when composables are disposed
- Lifecycle-aware location manager that stops updates when app is paused
- Efficient caching of manager instances to prevent recreation

## Usage in Trip Context

This location implementation can be integrated into trip functionality for:

- **Current Location**: Get user's current position for trip planning
- **Distance Calculations**: Calculate distances between trip destinations
- **Location Tracking**: Track user movement during active trips
- **Proximity Detection**: Detect when user is near trip destinations
- **Location Quality**: Provide feedback on location accuracy to users

The implementation provides a solid foundation for location-aware features in the TripBook
application, with proper error handling, lifecycle management, and modern Android development
practices.
