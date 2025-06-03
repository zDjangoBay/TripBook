# TripBook Development Guide

## 🚀 Quick Start

This guide will help you set up a Flutter-like development experience for the TripBook Android app with hot reload capabilities.

## 📋 Prerequisites

1. **Java 11+** - [Download from Adoptium](https://adoptium.net/)
2. **Android Studio** - [Download from Google](https://developer.android.com/studio)
3. **Android SDK** (comes with Android Studio)
4. **Android Emulator or Physical Device**

## 🛠️ Setup

### 1. First-time Setup
```bash
# Run this once to set up your development environment
setup_dev_environment.bat
```

### 2. Create an Android Virtual Device (AVD)
1. Open Android Studio
2. Go to **Tools > AVD Manager**
3. Click **Create Virtual Device**
4. Choose a device (recommended: Pixel 4 or newer)
5. Select a system image (recommended: API 30+)
6. Finish setup

## 🏃‍♂️ Running the App

### Option 1: Full Development Mode (Recommended)
```bash
# Starts emulator, builds, installs, and launches the app
run_tripbook_dev.bat
```

This script will:
- ✅ Check your development environment
- ✅ Start the Android emulator automatically
- ✅ Build and install the TripBook app
- ✅ Launch the app on the emulator
- ✅ Provide a development menu for common tasks

### Option 2: Quick Hot Reload
```bash
# For quick updates during development
hot_reload.bat
```

Use this when you make code changes and want to see them immediately.

## 🔥 Hot Reload Workflow

### Method 1: Using Batch Files (Flutter-like experience)
1. Make changes to your Kotlin/Compose code
2. Run `hot_reload.bat`
3. See changes instantly on the emulator

### Method 2: Using Android Studio (Faster)
1. Make changes in Android Studio
2. Press **Ctrl + F10** (Apply Changes and Restart Activity)
3. Or press **Ctrl + F9** (Build) then **Ctrl + F10**

### Method 3: Gradle Commands
```bash
# Build and install
gradlew.bat installDebug

# Just build
gradlew.bat assembleDebug

# Clean build
gradlew.bat clean assembleDebug
```

## 📱 Development Menu

When you run `run_tripbook_dev.bat`, you get access to a development menu with these options:

1. **Rebuild and reinstall app** - Full rebuild and install
2. **Launch app** - Launch if the app was closed
3. **View app logs** - See real-time logs from the app
4. **Uninstall app** - Remove the app from emulator
5. **Clean build** - Clean and rebuild everything
6. **Exit** - Close the development environment

## 🎯 TripBook Features

Your app includes:

### 🏨 Reservation Module
- **Multi-step booking flow**: Transport → Hotels → Activities → Payment
- **Purple Material 3 theme**: #6A1B9A primary, #CE93D8 secondary
- **Dummy data providers**: No backend needed for development
- **Payment simulation**: 2-second mock payment processing
- **Notification system**: In-app notifications for booking updates

### 📱 Navigation Structure
- **Dashboard**: Overview with quick actions
- **My Reservations**: Tabbed view (Pending, Upcoming, Completed)
- **Notifications**: Real-time booking updates
- **Profile**: User settings and preferences

### 🎨 UI Components
- **Material 3 Design**: Modern Android design patterns
- **Compose UI**: Declarative UI framework
- **Purple theme**: Consistent branding throughout
- **Responsive layouts**: Works on different screen sizes

## 🐛 Troubleshooting

### Emulator won't start
```bash
# Check available AVDs
emulator -list-avds

# Start specific AVD
emulator -avd YOUR_AVD_NAME
```

### Build fails
```bash
# Clean and rebuild
gradlew.bat clean
gradlew.bat assembleDebug
```

### App won't install
```bash
# Uninstall first
adb uninstall com.android.tripbook

# Then reinstall
gradlew.bat installDebug
```

### Can't find Android SDK
Add to your system PATH:
- `C:\Users\%USERNAME%\AppData\Local\Android\Sdk\platform-tools`
- `C:\Users\%USERNAME%\AppData\Local\Android\Sdk\emulator`

## 📝 Development Tips

1. **Use Android Studio** for the best development experience
2. **Enable hot reload** with Ctrl+F10 for instant updates
3. **Use the batch files** for automated workflows
4. **Check logs** regularly using the development menu
5. **Test on different screen sizes** using emulator controls

## 🔧 Customization

### Changing Colors
Edit `app/src/main/java/com/android/tripbook/ui/theme/Color.kt`:
```kotlin
val Purple80 = Color(0xFF6A1B9A)  // Primary
val PurpleGrey80 = Color(0xFFCE93D8)  // Secondary
val Pink80 = Color(0xFF512DA8)  // Accent
```

### Adding New Screens
1. Create new Composable in `ui/screens/`
2. Add navigation in `MainActivity.kt`
3. Update bottom navigation if needed

### Modifying Dummy Data
Edit providers in `data/providers/`:
- `DummyTripDataProvider.kt` - Trip listings
- `DummyHotelProvider.kt` - Hotel options
- `DummyActivityProvider.kt` - Activities

## 🎉 Happy Coding!

You now have a Flutter-like development experience for your Android TripBook app. Make changes, run `hot_reload.bat`, and see them instantly!

For questions or issues, check the troubleshooting section above.
