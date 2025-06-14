# User Profile Bradley Package

## Overview

This package contains a complete post creation and management system for the TripBook application,
specifically created for Bradley's user profile functionality.

## Features Implemented

### 1. Post Type Selection

- **Text Posts**: Basic text-only posts for sharing travel experiences
- **Media Posts**: Posts with photo attachments
- **Location Posts**: Posts with location information and coordinates

### 2. Post Creation UI

- Modern Material 3 design with card-based type selection
- Dynamic content editor that adapts based on selected post type
- Form validation to ensure required fields are filled
- Publishing status with loading indicators

### 3. Post Publishing Functionality

- Simulated API integration with loading states
- Error handling with user-friendly messages
- Automatic form reset after successful publishing
- Post data persistence in local state

### 4. Post Feed Display

- Clean card-based layout for displaying posts
- Post type indicators with color-coded chips
- Timestamp formatting for better readability
- Empty state with call-to-action for first post

## Package Structure

```
userprofilebradley/
├── data/
│   ├── PostType.kt           # Enum for post types
│   └── PostData.kt           # Data classes for posts and location
├── ui/
│   ├── compose/
│   │   ├── PostTypeSelector.kt    # Post type selection UI
│   │   ├── PostContentEditor.kt   # Content editing interface
│   │   └── PostList.kt            # Feed display components
│   └── screen/
│       ├── PostCreationScreen.kt  # Main creation screen
│       └── PostFeedScreen.kt      # Feed display screen
├── viewmodel/
│   ├── PostCreationViewModel.kt   # Creation logic
│   └── PostListViewModel.kt       # Feed management
├── UserProfileBradleyApp.kt       # Navigation setup
└── PostActivity.kt               # Demo activity
```

## Usage

### Launching the App

```kotlin
// Start the PostActivity to see the complete functionality
val intent = Intent(context, PostActivity::class.java)
context.startActivity(intent)
```

### Navigation Flow

1. **Post Feed Screen**: Main screen showing all posts with FAB for creating new posts
2. **Post Creation Screen**: Full-featured creation interface with type selection and content
   editing

### Creating Posts

1. Select post type (Text, Media, or Location)
2. Fill in title and content
3. Add type-specific content (image for media, location for location posts)
4. Tap "Publish" to create the post

## Technical Details

### State Management

- Uses Jetpack Compose's state management with ViewModels
- Reactive UI updates based on state changes
- Shared ViewModel for consistent data across screens

### UI/UX Features

- Material 3 design system implementation
- Responsive layout with proper spacing and typography
- Loading states and error handling
- Accessibility support with content descriptions

### Future Enhancements

- Real image picker integration
- GPS location services integration
- Backend API integration
- User authentication
- Post editing and deletion
- Social features (likes, comments)

## Dependencies

- Jetpack Compose
- Navigation Compose
- Material 3
- ViewModel and LiveData
- Activity Compose

---
**Author**: Bradley  
**Package**: com.android.tripbook.userprofilebradley  
**Created**: January 2025