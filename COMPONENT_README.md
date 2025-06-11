# TripBook Components Documentation

This document describes the improved PostRepository, ImageUploader, and PostValidator components for the TripBook Android application.

## Overview

The components have been enhanced with comprehensive functionality including:

- **PostRepository**: Full REST API integration with error handling and state management
- **ImageUploader**: Advanced image processing with compression, validation, and progress tracking
- **PostValidator**: Comprehensive validation with quality scoring and real-time feedback

## Architecture

```
Presentation Layer (ViewModel)
    ↓
Domain Layer (UseCase + Validator)
    ↓
Data Layer (Repository + API Services)
    ↓
Network Layer (Retrofit + OkHttp)
```

## Components

### 1. PostRepository

**Location**: `com.tripbook.data.repository.PostRepository`

**Features**:
- REST API integration using Retrofit
- Comprehensive error handling with different error types
- Pagination support
- Search functionality
- Like/unlike operations
- Flow-based reactive programming

**Usage**:
```kotlin
val repository = PostRepositoryImpl(postApiService)

// Get posts with pagination
repository.getPosts(page = 1, limit = 20).collect { result ->
    when (result) {
        is NetworkResult.Success -> { /* Handle success */ }
        is NetworkResult.Error -> { /* Handle error */ }
        is NetworkResult.Loading -> { /* Show loading */ }
    }
}
```

### 2. ImageUploader

**Location**: `com.tripbook.utils.ImageUploader`

**Features**:
- Image compression with configurable quality
- Multiple image upload support
- Progress tracking
- Image validation (size, format)
- File provider integration for camera
- Automatic cleanup of temporary files

**Usage**:
```kotlin
val imageUploader = ImageUploader(context, imageUploadApiService)

imageUploader.uploadImage(imageUri).collect { result ->
    when (result) {
        is UploadResult.Progress -> { /* Update progress */ }
        is UploadResult.Success -> { /* Handle success */ }
        is UploadResult.Error -> { /* Handle error */ }
    }
}
```

### 3. PostValidator

**Location**: `com.tripbook.domain.validator.PostValidator`

**Features**:
- Comprehensive field validation
- Quality scoring system (EXCELLENT, GOOD, FAIR, POOR)
- Real-time validation for individual fields
- Detailed error messages and warnings
- Pattern-based validation for special fields

**Usage**:
```kotlin
val validator = PostValidator()
val result = validator.validate(post)

if (result.isValid) {
    // Proceed with post creation
} else {
    // Show validation errors
    result.errors.forEach { (field, message) ->
        showError(field, message)
    }
}
```

## Dependencies Added

The following dependencies have been added to support the enhanced functionality:

```kotlin
// Networking
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-gson:2.9.0")
implementation("com.squareup.okhttp3:okhttp:4.12.0")
implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

// Image handling
implementation("com.github.bumptech.glide:glide:4.16.0")
implementation("id.zelory:compressor:3.0.1")

// Architecture components
implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
implementation("androidx.navigation:navigation-compose:2.7.7")

// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
```

## Configuration

### 1. Network Configuration

Update the base URL in `NetworkModule.kt`:
```kotlin
private const val BASE_URL = "https://your-api-endpoint.com/"
```

### 2. Permissions

The following permissions have been added to `AndroidManifest.xml`:
- `INTERNET` - For network operations
- `ACCESS_NETWORK_STATE` - For network state checking
- `CAMERA` - For camera functionality
- `READ_EXTERNAL_STORAGE` - For accessing images
- `READ_MEDIA_IMAGES` - For Android 13+ image access

### 3. File Provider

File provider is configured for camera image capture with paths defined in `res/xml/file_paths.xml`.

## Usage Examples

### Complete Post Creation Flow

```kotlin
// 1. Create post object
val post = Post(
    userId = "user123",
    title = "My Amazing Trip",
    content = "Description of the trip...",
    location = "Paris, France",
    tags = listOf("travel", "paris")
)

// 2. Validate post
val validation = postValidator.validate(post)
if (!validation.isValid) {
    // Handle validation errors
    return
}

// 3. Create post with images
viewModel.createPost(post, imageUris)

// 4. Observe result
viewModel.postOperationState.collect { result ->
    when (result) {
        is NetworkResult.Success -> {
            // Post created successfully
        }
        is NetworkResult.Error -> {
            // Handle error
        }
    }
}
```

### Image Upload with Validation

```kotlin
// 1. Validate image
val validation = imageUploader.validateImageFile(imageFile)
if (!validation.isValid) {
    showError(validation.errorMessage)
    return
}

// 2. Upload with progress tracking
imageUploader.uploadImage(imageUri).collect { result ->
    when (result) {
        is UploadResult.Progress -> {
            updateProgress(result.percentage)
        }
        is UploadResult.Success -> {
            // Use result.imageUrl and result.imageId
        }
        is UploadResult.Error -> {
            showError(result.message)
        }
    }
}
```

### Real-time Validation

```kotlin
// Validate fields as user types
titleEditText.addTextChangedListener { text ->
    val isValid = postValidator.validateField("title", text.toString())
    titleLayout.error = if (isValid) null else "Invalid title"
}
```

## Error Handling

The components provide comprehensive error handling:

1. **Network Errors**: HTTP status codes, connection issues
2. **Validation Errors**: Field-specific validation messages
3. **File Errors**: Image processing, compression failures
4. **API Errors**: Server-side validation, business logic errors

## Testing

For testing, you can use the `TripBookComponentsExample` class which demonstrates all major functionality:

```kotlin
val example = TripBookComponentsExample(context)
example.createPostExample()
example.uploadImageExample(imageUri)
example.realTimeValidationExample()
```

## Best Practices

1. **Always validate posts** before attempting to create/update
2. **Compress images** before uploading to reduce bandwidth
3. **Handle all NetworkResult states** in your UI
4. **Use Flow collection** for reactive updates
5. **Clean up resources** (temporary files, etc.)
6. **Implement proper error handling** for better UX

## Future Enhancements

Potential improvements for future versions:

1. **Offline support** with Room database caching
2. **Image editing** capabilities (crop, rotate, filter)
3. **Batch operations** for multiple posts
4. **Advanced search** with filters and sorting
5. **Analytics integration** for usage tracking
6. **Performance optimization** with image lazy loading

## Troubleshooting

Common issues and solutions:

1. **Network errors**: Check internet connection and API endpoint
2. **Image upload failures**: Verify file permissions and size limits
3. **Validation errors**: Check field requirements and formats
4. **Memory issues**: Ensure image compression is working properly
5. **Permission denied**: Check manifest permissions and runtime permissions

For more detailed examples, refer to the `TripBookComponentsExample.kt` file.
