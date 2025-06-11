# Location Picker System

This document describes the improved location picker system for the TripBook app.

## Overview

The location picker system has been redesigned and moved to a better organized structure to avoid merge conflicts and improve maintainability. The new system is located in the `shared` package and provides a clean, reusable component for location selection.

## Directory Structure

```
app/src/main/java/com/android/tripbook/shared/
├── model/
│   └── Location.kt                 # Location data model with coordinates
├── service/
│   ├── LocationSearchService.kt    # Interface for location search services
│   └── MockLocationSearchService.kt # Mock implementation for testing
├── viewmodel/
│   └── LocationPickerViewModel.kt  # ViewModel for location picker state
└── ui/
    ├── components/
    │   └── LocationPicker.kt       # Main location picker component
    └── examples/
        └── LocationPickerExample.kt # Usage example
```

## Key Features

### 1. Improved Location Model
- **Serializable**: Uses kotlinx.serialization for easy data persistence
- **Coordinates**: Supports latitude/longitude coordinates
- **Validation**: Built-in coordinate validation
- **Display Names**: Smart formatting for location display

### 2. Service Layer Architecture
- **Interface-based**: `LocationSearchService` interface for different implementations
- **Mock Service**: `MockLocationSearchService` with sample data for testing
- **Extensible**: Easy to add Google Places API, Nominatim, or other services

### 3. Modern UI Components
- **Material 3 Design**: Uses latest Material Design components
- **Error Handling**: Proper error states and validation
- **Search Functionality**: Debounced search with loading states
- **Accessibility**: Proper content descriptions and semantics

### 4. ViewModel Integration
- **State Management**: Centralized state using StateFlow
- **Lifecycle Aware**: Properly handles coroutines and cancellation
- **Testable**: Easy to unit test with dependency injection

## Usage

### Basic Usage

```kotlin
@Composable
fun MyScreen() {
    var selectedLocation by remember { mutableStateOf<Location?>(null) }
    
    LocationPicker(
        selectedLocation = selectedLocation,
        onLocationSelected = { location ->
            selectedLocation = location
        },
        error = if (selectedLocation == null) "Please select a location" else null,
        placeholder = "Choose your destination"
    )
}
```

### Advanced Usage with Custom ViewModel

```kotlin
@Composable
fun MyScreen(
    viewModel: LocationPickerViewModel = viewModel()
) {
    var selectedLocation by remember { mutableStateOf<Location?>(null) }
    
    LocationPicker(
        selectedLocation = selectedLocation,
        onLocationSelected = { location ->
            selectedLocation = location
        },
        viewModel = viewModel,
        error = null,
        placeholder = "Search for places..."
    )
}
```

## Dependencies

The location picker system requires the following dependencies (already added to `libs.versions.toml`):

- `kotlinx-serialization-json`: For data serialization
- `play-services-location`: For future Google Play Services integration
- `play-services-maps`: For future maps integration

## Migration Guide

### From Old LocationPicker

If you were using the old LocationPicker from `com.android.tripbook.posts.ui.components`, update your imports:

**Old:**
```kotlin
import com.android.tripbook.posts.ui.components.LocationPicker
import com.android.tripbook.posts.model.Location
```

**New:**
```kotlin
import com.android.tripbook.shared.ui.components.LocationPicker
import com.android.tripbook.shared.model.Location
```

### API Changes

The new LocationPicker has a simplified API:

**Old:**
```kotlin
LocationPicker(
    selectedLocation = location,
    onLocationSelected = { },
    onLocationSearch = { query -> viewModel.search(query) },
    searchResults = viewModel.results,
    isSearching = viewModel.isLoading,
    searchError = viewModel.error,
    // ... other parameters
)
```

**New:**
```kotlin
LocationPicker(
    selectedLocation = location,
    onLocationSelected = { },
    // ViewModel handles search automatically
    viewModel = viewModel() // Optional, creates default if not provided
)
```

## Future Enhancements

1. **Google Places Integration**: Replace MockLocationSearchService with real Google Places API
2. **Offline Support**: Cache frequently searched locations
3. **Current Location**: Add "Use Current Location" functionality
4. **Favorite Locations**: Allow users to save favorite locations
5. **Maps Integration**: Show selected location on a map
6. **Address Autocomplete**: Enhanced address completion

## Testing

The system includes comprehensive testing support:

- Use `MockLocationSearchService` for unit tests
- `LocationPickerViewModel` is easily testable with dependency injection
- Location model includes validation methods for testing coordinates

## Troubleshooting

### Common Issues

1. **Import Errors**: Make sure to use the new import paths under `shared`
2. **Merge Conflicts**: The old LocationPicker has been removed to prevent conflicts
3. **Dependencies**: Ensure kotlinx-serialization plugin is enabled in your module

### Performance

- Search queries are debounced (300ms) to avoid excessive API calls
- Results are cached in the ViewModel during the session
- LazyColumn is used for efficient list rendering

## Contributing

When adding new features to the location picker system:

1. Follow the existing architecture patterns
2. Add proper error handling and loading states
3. Include unit tests for new functionality
4. Update this documentation
5. Ensure backwards compatibility when possible
