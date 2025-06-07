# PostEvent Module

A comprehensive event-driven architecture for post creation in the TripBook application.

## Overview

The PostEvent module provides a complete, production-ready system for handling all user interactions during post creation. Built with Kotlin sealed classes, it ensures type safety and exhaustive handling of all possible events.

## Architecture

### Core Components

1. **PostEvent.kt** - The heart of the module
   - 17 comprehensive event types
   - Travel-specific functionality
   - Type-safe sealed class design

2. **PostEventHandler.kt** - Event processing engine
   - Centralized state management
   - Coroutine-based processing
   - Validation integration

3. **PostValidationService.kt** - Input validation system
   - Field-by-field validation
   - Content filtering and security
   - Comprehensive error reporting

### Supporting Infrastructure

4. **PostConstants.kt** - Configuration management
5. **PostUtils.kt** - Utility functions and analytics
6. **PostExceptions.kt** - Error handling framework
7. **PostSecurityManager.kt** - Security and content filtering
8. **PostEventExtensions.kt** - Extension functions
9. **PostEventLogger.kt** - Comprehensive logging
10. **PostEventFactory.kt** - Safe event creation
11. **PostUIState.kt** - UI state management

### Testing

12. **PostEventTest.kt** - Unit tests
13. **PostEventBenchmark.kt** - Performance tests

## Event Types

### Text Input Events
- `TitleChanged(newTitle: String)`
- `DescriptionChanged(newDescription: String)`

### Image Management Events
- `ImageAdded(imageUri: Uri)`
- `ImageRemoved(imageUri: Uri)`
- `ClearAllImages`

### Location Events
- `LocationAdded(latitude: Double, longitude: Double, locationName: String?)`
- `ClearLocation`

### Travel Categorization Events
- `CategoryChanged(category: String)`

### Tag Management Events
- `TagAdded(tag: String)`
- `TagRemoved(tag: String)`

### Privacy Control Events
- `VisibilityChanged(visibility: String)`

### Form Management Events
- `SubmitPost`
- `ClearForm`
- `SaveDraft`

### User Feedback Events
- `ShowError(message: String)`
- `DismissError`
- `PostCreated(postId: String)`

## Usage Examples

### Basic Event Creation
```kotlin
val titleEvent = PostEvent.TitleChanged("My Amazing Trip")
val locationEvent = PostEvent.LocationAdded(40.7128, -74.0060, "New York")
val submitEvent = PostEvent.SubmitPost
```

### Using Event Handler
```kotlin
val handler = PostEventHandler()

// Process events
handler.handleEvent(PostEvent.TitleChanged("Trip Title"))
handler.handleEvent(PostEvent.DescriptionChanged("Amazing journey"))

// Check validation
val canSubmit = handler.canSubmit()
val validationStatus = handler.getValidationStatus()
```

### Using Validation Service
```kotlin
val validator = PostValidationService()

val titleValidation = validator.validateTitle("My Trip")
val locationValidation = validator.validateLocation(40.7128, -74.0060, "NYC")
val completeValidation = validator.validateCompletePost(...)
```

### Using Event Factory
```kotlin
val factory = PostEventFactoryCompanion.createDefault()

// Create events safely with validation
val titleResult = factory.createTitleChangedEvent("My Trip")
val locationResult = factory.createLocationAddedEvent(40.7128, -74.0060, "NYC")

if (titleResult.isSuccess) {
    val titleEvent = titleResult.getOrNull()
    // Process the event
}
```

## Features

### Type Safety
- Sealed class ensures exhaustive when expressions
- Compile-time safety for all event handling
- No runtime surprises

### Travel-Specific
- Location support with coordinates and names
- Travel categories and tagging system
- Privacy controls for social sharing

### Production-Ready
- Comprehensive error handling
- Performance optimization with caching
- Security features with content filtering
- Analytics and user behavior tracking

### Developer-Friendly
- Extensive documentation
- Comprehensive unit tests
- Performance benchmarks
- Extension functions for convenience

### Enterprise Features
- Event logging and debugging
- Serialization support
- Draft management with auto-save
- Analytics and metrics collection

## Performance

The module is optimized for performance with:
- Efficient event processing (< 1ms per event)
- Memory-efficient design (< 100MB for 50k events)
- Caching for frequently accessed data
- Lazy initialization where appropriate

## Security

Built-in security features include:
- Content filtering and profanity detection
- Location privacy protection
- Input validation and sanitization
- Personal information detection

## Testing

Comprehensive test coverage includes:
- Unit tests for all event types
- Performance benchmarks
- Edge case testing
- Integration testing

## Integration

The module is designed to integrate seamlessly with:
- MVVM/MVI architecture patterns
- Jetpack Compose UI
- Room database for persistence
- Retrofit for network operations

## Contributing

When extending the module:
1. Add new events to the PostEvent sealed class
2. Update the PostEventHandler to process new events
3. Add validation rules in PostValidationService
4. Include tests for new functionality
5. Update documentation

## Author

**Feukoun Marel**  
ICTU Student ID: 20223436  
TripBook Development Team

## Version History

- **v1.0** - Initial implementation with 17 event types
- **v1.1** - Enhanced documentation and extension functions

## License

Part of the TripBook application - Internal use only.
