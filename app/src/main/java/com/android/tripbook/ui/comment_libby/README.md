# Comment Popup Component - by Libby

A beautiful, reusable comment popup component for the TripBook Android application. This component provides a slide-up popup interface for displaying and adding comments to trips.

## Features

- **Material 3 Design**: Modern UI following Material Design 3 principles
- **Slide-up Animation**: Smooth popup animation from bottom
- **Interactive Comments**: Like/unlike functionality for comments
- **Comment Input**: Text field with send button for new comments
- **Dummy Data**: Pre-populated with sample comments for demonstration
- **Trip ID Support**: Easy integration with trip-specific data
- **Responsive Layout**: Adapts to different screen sizes
- **Easy Integration**: Simple API for existing screens

## Files Structure

```
comment_libby/
├── CommentPopup.kt          # Main popup component
├── CommentItem.kt           # Individual comment item component
├── CommentDemoScreen.kt     # Demo screen showing component usage
├── model/
│   └── Comment.kt           # Comment data model
└── example/
    └── TripListExample.kt   # Integration example
```

## Usage

### Basic Usage

```kotlin
import com.android.tripbook.ui.components.comment_libby.CommentPopup

@Composable
fun YourScreen() {
    var showComments by remember { mutableStateOf(false) }
    val tripId = "your_trip_id"

    // Your existing UI...

    Button(
        onClick = { showComments = true }
    ) {
        Text("Show Comments")
    }

    // Add the comment popup
    CommentPopup(
        isVisible = showComments,
        tripId = tripId,
        onDismiss = { showComments = false },
        onCommentSubmit = { comment ->
            // Handle new comment submission
            println("New comment: $comment")
        }
    )
}
```

### Advanced Integration

See `TripListExample.kt` for a complete example of how to integrate the component into a trip listing screen.

## Component Parameters

### CommentPopup

| Parameter | Type | Description |
|-----------|------|-------------|
| `isVisible` | Boolean | Controls popup visibility |
| `tripId` | String | ID of the trip to show comments for |
| `onDismiss` | () -> Unit | Callback when popup is closed |
| `onCommentSubmit` | (String) -> Unit | Callback when new comment is submitted |

## Data Model

### Comment

```kotlin
data class Comment(
    val id: String,           // Unique comment identifier
    val userId: String,       // User who wrote the comment
    val userName: String,     // Display name of the user
    val userAvatar: String,   // User avatar URL (placeholder for now)
    val text: String,         // Comment content
    val timestamp: String,    // When the comment was posted
    val likes: Int = 0,       // Number of likes
    val replies: List<Comment> = emptyList() // Nested replies (future feature)
)
```

## Demo

Run the `CommentDemoScreen` to see the component in action:

1. Shows different trip IDs
2. Demonstrates popup functionality
3. Interactive comment features
4. Material 3 theming

## Customization

The component uses Material 3 theming and will automatically adapt to your app's color scheme. You can customize:

- Colors through MaterialTheme
- Typography through Material 3 typography
- Animations by modifying the Dialog properties
- Layout by adjusting padding and spacing values

## Future Enhancements

- **Real API Integration**: Replace dummy data with actual API calls
- **Reply System**: Support for nested comment replies
- **Image Support**: Add image attachments to comments
- **Emoji Reactions**: Quick reaction buttons
- **Comment Moderation**: Report and delete functionality
- **Offline Support**: Local caching of comments
- **Push Notifications**: Real-time comment notifications

## Integration Notes

1. **Theme Compatibility**: Component follows your app's Material 3 theme
2. **State Management**: Uses Compose state for UI interactions
3. **Performance**: Lazy loading for comment lists
4. **Accessibility**: Proper content descriptions for screen readers
5. **Platform**: Designed for Android with Jetpack Compose

## Author

Created by **Libby** for the TripBook project.

---

For questions or improvements, please contact the development team.
