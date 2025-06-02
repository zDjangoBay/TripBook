<a id="readme-top"></a>

# TripBook 🌍✈️

[![MIT License](https://img.shields.io/badge/License-MIT-green.svg)](https://choosealicense.com/licenses/mit/)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.8.0-purple.svg)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-Latest-blue.svg)](https://developer.android.com/jetpack/compose)

> A modern travel booking platform built with Jetpack Compose, focusing on seamless user experience and comprehensive travel management.

![App Banner](app/src/main/java/com/android/tripbook/screenshots/onboarding_1.png)

## 📱 Features

- **Intuitive Booking Flow**: Simple 5-step process for travel reservations
- **Smart Travel Options**: Customizable add-ons and packages
- **Agency Integration**: Connect with verified travel agencies
- **Secure Payments**: Protected transaction processing
- **Travel History**: Track and manage your bookings

## 🛠️ Tech Stack

- **Frontend**: Kotlin + Jetpack Compose
- **Design System**: Material 3
- **Navigation**: Navigation Compose
- **State Management**: ViewModel + StateFlow
- **Storage**: DataStore Preferences
- **Dependency Injection**: Hilt
- **Testing**: JUnit5 + Mockito

## 📸 Screenshots

| Booking Flow | Agency Selection | Trip Summary |
|:-----------:|:---------------:|:------------:|
| ![Booking](app/src/main/java/com/android/tripbook/screenshots/create_account.png) | ![Agency](app/src/main/java/com/android/tripbook/screenshots/travel_styles.png) | ![Summary](app/src/main/java/com/android/tripbook/screenshots/upload_picture.png) |

## 🚀 Getting Started

### Prerequisites

- Android Studio Arctic Fox or later
- JDK 11+
- Android SDK 31+

### Installation

1. Clone the repository
```bash
git clone https://github.com/yourusername/TripBook.git
```

2. Open in Android Studio

3. Run the app
```bash
./gradlew installDebug
```

## 📖 Architecture

The app follows Clean Architecture principles with MVVM pattern:

```
app/
├── data/         # Data sources, repositories
├── domain/       # Business logic, use cases
├── presentation/ # UI components, ViewModels
└── di/           # Dependency injection
```

## 📚 Core Modules

### 🧑‍🤝‍🧑 User Profile Module
The foundation of user interaction and personalization in TripBook. This module handles:
- 🔐 Secure authentication through multiple providers (Google, Facebook, Email)
- 👤 Comprehensive profile management with travel preferences and history
- 🌟 Dynamic achievement system rewarding travel milestones
- 🤝 Social networking features connecting like-minded travelers
- 🔒 Advanced privacy settings and data protection

### 🗺️ Trip Catalog Module
Your window to exploring destinations across Africa and beyond:
- 🌍 Extensive database of African destinations with local insights
- 📸 Immersive destination previews with photos and virtual tours
- 🎯 Smart destination matching based on user preferences
- 🌦️ Live weather updates and seasonal recommendations
- 📝 Detailed travel guides with local customs and tips

### 🏢 Company Catalog Module
Connecting travelers with trusted service providers:
- ✅ Carefully vetted travel agencies and tour operators
- 🏨 Diverse accommodation options from luxury to local stays
- 🚗 Comprehensive transportation booking system
- 🎯 Activity and experience marketplace
- ⭐ Transparent review and rating system

### 📅 Reservation Module
Streamlined booking management system:
- 💳 Multi-currency payment processing with local options
- 🎫 Digital ticket and reservation management
- 📱 Hassle-free mobile check-in process
- 📬 Smart notification system for updates
- ⚡ Quick booking modifications and cancellations

### ⏰ Trip Scheduling Module
Intelligent travel planning tools:
- 📋 Visual trip planner with timeline view
- 💰 Real-time budget calculator and tracker
- 🤝 Collaborative trip planning for groups
- 📲 Offline access to trip details
- 🗺️ Smart route optimization

### 📝 Posts & Comments Module
Share and engage with the travel community:
- 📸 Rich travel story creation with multi-media support
- 🌍 Location-based content discovery
- 💬 Interactive community discussions
- 🔍 Smart content categorization
- 📊 Engagement tracking and analytics

### 📊 Data Mining Module
Enhancing travel experiences through smart analytics:
- 🎯 AI-powered travel recommendations
- 📈 Travel trend analysis and predictions
- 💡 Sentiment analysis for better service
- 🔒 Privacy-first data processing
- 📊 Tourism impact assessment tools

## 🤝 Contributing

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📜 License

TripBook is licensed under the MIT License, a permissive open source license that allows for maximum reuse:

- ✅ Commercial use
- ✅ Modification
- ✅ Distribution
- ✅ Private use

```
Copyright (c) 2024 Daniel Moune

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software")...
```

For complete license terms, see [`LICENSE`](LICENSE) in the repository.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## 📫 Contact & Support

### Project Lead
- **Daniel Moune (Bergson)**
  - 🎓 ICT University, Class of 2025
  - 📧 [daniel.moune@example.com](mailto:daniel.moune@example.com)
  - 💼 [LinkedIn](https://linkedin.com/in/daniel-moune)
  - 🐦 [Twitter @bergsondev](https://twitter.com/bergsondev)

### Project Resources
- 📚 [Documentation](https://github.com/sas-bergson/TripBook/wiki)
- 🐛 [Issue Tracker](https://github.com/sas-bergson/TripBook/issues)
- 💡 [Feature Requests](https://github.com/sas-bergson/TripBook/issues/new?labels=enhancement)
- 🤝 [Contributing Guidelines](CONTRIBUTING.md)

### Repository
📁 [github.com/sas-bergson/TripBook](https://github.com/sas-bergson/TripBook)

<p align="right">(<a href="#readme-top">back to top</a>)</p>
