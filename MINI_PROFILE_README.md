# Mini Profile Embedding Component

A reusable user profile component for the TripBook Android application that can be embedded in trip
catalogs and other screens.

## 🎯 Features

- **Circular Avatar** with user initials as fallback
- **User Name & Destination** display with location icon
- **Three Size Options**: Small (28dp), Medium (40dp), Large (48dp)
- **Material Design 3** styling with dark theme support
- **Easy Integration** - just pass a User object

## 📱 Component Preview

The component displays:

- User initials in a circular colored background
- User's full name
- User's destination with location icon
- Responsive sizing for different contexts

## 🔧 Usage

### Basic Usage

```kotlin
UserProfileEmbedding(
    user = User(
        id = "1",
        name = "Marie Nkomo",  
        destination = "Yaoundé, Cameroon"
    )
)
```

### With Custom Size

```kotlin
UserProfileEmbedding(
    user = user,
    size = ProfileSize.Large  // Small, Medium, or Large
)
```

## 📦 Integration

### 1. Add the User data model:

```kotlin
data class User(
    val id: String,
    val name: String,
    val destination: String,
    val profileImageUrl: String? = null
)
```

### 2. Use in your trip cards:

```kotlin
// In any Compose screen
Column {
    Text("Trip organized by:")
    UserProfileEmbedding(user = tripOrganizer)
}
```

## 🏗️ Files Structure

```
📂 Mini Profile Component
├── 📄 User.kt                     (Data model)
├── 📄 UserProfileEmbedding.kt     (Main component)
├── 📄 SampleData.kt              (Sample users)
├── 📄 UserProfileDemoScreen.kt   (Demo screen)
└── 📄 MiniProfileDemoActivity.kt (Demo activity)
```

## 🚀 Demo

Run the `MiniProfileDemoActivity` to see the component in action with different sizes and sample
Cameroon users.

## 🎨 Customization

The component supports:

- **ProfileSize.Small** - For compact lists
- **ProfileSize.Medium** - For standard cards
- **ProfileSize.Large** - For detailed views

## 🌍 Sample Data

Includes 4 sample users from different Cameroon cities:

- Marie Nkomo (Yaoundé)
- Paul Essomba (Douala)
- Fatima Abba (Maroua)
- Jean Baptiste (Kribi)

## 📄 License

MIT License - Can be freely integrated into TripBook project.

---

**Author**: Will Brayan  
**Component**: Mini Profile Embedding  
**For**: TripBook Social Travel App