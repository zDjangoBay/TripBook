# TripBook

TripBook is a comprehensive mobile application for managing travel reservations and trip planning. Built with modern Android development practices using Kotlin and Jetpack Compose.

## 🚀 Quick Start

### Prerequisites
- **Java 11+** - [Download from Adoptium](https://adoptium.net/)
- **Android Studio** - [Download from Google](https://developer.android.com/studio)
- **Android SDK** (comes with Android Studio)
- **Android Emulator or Physical Device**

### 🛠️ Setup & Run

#### Option 1: Flutter-Style Development (Recommended)
```bash
# 1. First-time setup (run once)
setup_dev_environment.bat

# 2. Start development with live logs
dev_runner.ps1
# OR
dev_runner.bat
```

#### Option 2: Simple Run
```bash
# Quick start with manual AVD selection
simple_run.bat
```

#### Option 3: Traditional Android Development
```bash
# Build and install manually
gradlew.bat installDebug

# Or use Android Studio's Run button
```

### 🔥 Hot Reload Development
```bash
# Make code changes, then run:
hot_reload.bat
```

## 📱 Features

### Reservation Module
- **Multi-step reservation flow**: Transport → Hotels → Activities → Payment
- **Real-time booking status tracking** with live notifications
- **Payment processing** with multiple payment methods (simulated)
- **Location-based search** with automatic current location detection
- **Tabbed reservation management**: Pending, Upcoming, Completed

### User Interface
- **Material 3 Design System** with purple-dominant theme
- **Trip icons** instead of images for faster loading
- **Location permissions** for current location search
- **Responsive layouts** for different screen sizes
- **Bottom navigation** with optimized short labels

### Core Features
- **Dashboard** with trip browsing and search
- **Location Services** with permission handling
- **Search functionality** by destination and current location
- **Trip categorization** with visual icons
- **Real-time development logs** (Flutter-style experience)

## 🎨 Design System

### Color Scheme
- **Primary**: #6A1B9A (Deep Purple)
- **Secondary**: #CE93D8 (Light Purple)
- **Accent**: #512DA8 (Dark Purple)

### Trip Categories & Icons
- **Business** → 💼 Business icon
- **Adventure** → 🧭 Explore icon
- **Cultural** → 📍 Place icon
- **Relaxation** → 🧘 Spa icon
- **Family** → 👥 Groups icon

## 🏗️ Project Structure

```
app/
├── src/main/java/com/android/tripbook/
│   ├── data/
│   │   ├── models/          # Data models (Trip, Reservation, etc.)
│   │   └── providers/       # Dummy data providers
│   ├── ui/
│   │   ├── components/      # Reusable UI components
│   │   ├── screens/         # Screen composables
│   │   │   ├── dashboard/   # Dashboard and trip browsing
│   │   │   ├── reservation/ # Multi-step reservation flow
│   │   │   ├── notifications/ # Notification management
│   │   │   └── profile/     # User profile
│   │   └── theme/           # Material 3 theme
│   └── MainActivity.kt      # App entry point
├── Development Scripts/
│   ├── dev_runner.ps1       # Flutter-style development (PowerShell)
│   ├── dev_runner.bat       # Flutter-style development (Batch)
│   ├── simple_run.bat       # Simple app runner
│   ├── hot_reload.bat       # Quick hot reload
│   └── setup_dev_environment.bat # First-time setup
└── DEVELOPMENT_GUIDE.md     # Detailed development guide
```

## 🔧 Development Environment

### Flutter-Style Experience
This project includes a **Flutter-like development environment** with:

- **🔥 Live logs** showing real-time app interactions
- **⚡ Hot reload** for instant code changes
- **🎨 Colored output** for easy debugging
- **📱 Automatic emulator management**
- **🔍 Real-time crash detection**

### Development Workflow
1. **Run development server**: `dev_runner.ps1`
2. **Make code changes** in Android Studio
3. **Hot reload**: `hot_reload.bat`
4. **See changes instantly** with live logs

### Available Scripts
- **`setup_dev_environment.bat`** - One-time environment setup
- **`dev_runner.ps1`** - Main development server (PowerShell)
- **`dev_runner.bat`** - Main development server (Batch)
- **`simple_run.bat`** - Simple app launcher
- **`hot_reload.bat`** - Quick code updates
- **`debug_logs.bat`** - Debug crash issues

## 🎯 Current Status

### ✅ Completed Features
- Complete reservation workflow with dummy data
- Material 3 theming with purple color scheme
- Location-based search functionality
- Trip categorization with visual icons
- Multi-step booking process (Transport → Hotels → Activities → Payment)
- Payment simulation with notifications
- Flutter-style development environment
- Real-time logging and debugging tools

### 🚧 In Development
- Backend API integration
- Real location services
- User authentication
- Data persistence with Room database
- Push notifications
- History and User preferences

## 🛠️ Technology Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM with Repository Pattern
- **Navigation**: Navigation Compose
- **State Management**: Compose State + DataStore
- **Design System**: Material 3
- **Image Loading**: Coil (replaced with icons for performance)
- **Development Tools**: Custom Flutter-style runners

## 📖 Documentation

- **[DEVELOPMENT_GUIDE.md](DEVELOPMENT_GUIDE.md)** - Comprehensive development guide
- **[LICENCE.txt](LICENCE.txt)** - MIT License details

## 🤝 Contributing

1. **Setup development environment**: Run `setup_dev_environment.bat`
2. **Start development server**: Run `dev_runner.ps1`
3. **Make changes** and test with hot reload
4. **Follow Material 3 design guidelines**
5. **Test on multiple screen sizes**

## 📄 License

This project is licensed under the MIT License - see the [LICENCE.txt](LICENCE.txt) file for details.

---

## 🎉 Ready to Start?

```bash
# Get started in 3 steps:
1. setup_dev_environment.bat
2. dev_runner.ps1
3. Start coding with live feedback!
```

**Experience Flutter-style development for Android!** 🚀