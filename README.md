

# TripBook 🌍✈️
# TripBook
TripBook: A mobile social network for travelers exploring Africa &amp; beyond. Share stories, photos, and tips, rate travel agencies, and connect with adventurers. Community-driven platform to discover hidden gems, promote tourism, and ensure safer journeys.  Contributions welcome! 🌍✨_

---

## 🚀 Principal Functionality

- **Share experiences**: Publish your travel stories, photos, and advice.
- **Discovery**: Explore new destinations through others' experiences.
- **Connection**: Meet travelers with similar interests and plan together.
- **Evaluation**: Rate travel agencies to help the community.
- **Security**: Access reliable information for peaceful journeys.
---

## 📱 Screenshots

| Home | Discover | Profile |
|:----:|:--------:|:-------:|
| ![Home](app/src/main/java/com/android/tripbook/screenshots/onboarding_1.png) | ![Discover](app/src/main/java/com/android/tripbook/screenshots/onboarding_2.png) | ![Profile](app/src/main/java/com/android/tripbook/screenshots/onboarding_3.png) |
| **Your Account** | **Your Style** | **Upload Picture** |
| ![Account](app/src/main/java/com/android/tripbook/screenshots/create_account.png) | ![Style](app/src/main/java/com/android/tripbook/screenshots/travel_styles.png) | ![Picture](app/src/main/java/com/android/tripbook/screenshots/upload_picture.png) |

---

## 🛠️ Stack technique

- **Android (Kotlin, Jetpack Compose)**
- **Navigation Compose**
- **DataStore Preferences**
- **Material 3**
- **Backend** : Node.js (API REST)
- **Géolocalisation** : APIs de localisation

---

## 📦 Installation

1. **Clone the repo :**
   ```bash
   git clone https://github.com/sas-bergson/TripBook.git

   Open in Android Studio
   Lancer l’application sur un émulateur ou un appareil physique

<hr></hr>
🤝 Contribute
Contributions are welcome! Please open an issue or a pull request for any suggestions or improvements.
<hr></hr>
👨‍💻 Author
Eng Daniel Moune (alias sas-bergson)   
Lecturer Android Mobile App,   
ICT University, Spring 2025 GitHub  <hr></hr>
🌍 Licence
Ce projet est sous licence MIT.


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
| ![Booking](![trep](https://github.com/user-attachments/assets/164bd002-4bc9-45ef-a920-f749b09c6d37)
![mieiei](https://github.com/user-attachments/assets/0e660d4a-e8eb-4cd8-8b28-ecfd465123f4)
![tripviw](https://github.com/user-attachments/assets/2cdd8dd9-e9c3-4712-9a7c-81d3ae29e084)
![mekdk](https://github.com/user-attachments/assets/23835478-1928-410c-8ecf-bfe0b05574ec)
![mejd](https://github.com/user-attachments/assets/182a0569-5bac-4f6e-aad5-0ca1be7d4fb1)
![meueu](https://github.com/user-attachments/assets/839dcca4-b134-4fdd-a8cc-f2478628c9a9)
![meejjj](https://github.com/user-attachments/assets/0212bed3-6000-4c58-8708-e6fa14acf6d5)
![medavid](https://github.com/user-attachments/assets/88918578-cacc-414c-8d08-433888bb891e)
) | ![Onboarding $ registration](![Uploading travel_styles.png…](![onboarding_2](https://github.com/user-attachments/assets/8b51a170-e9fe-42c0-a32e-c5a243164b62)
![onboarding_1](https://github.com/user-attachments/assets/d1bef56b-2fca-4b76-a1a4-d1a070802a48)
![create_account](https://github.com/user-attachments/assets/9b58c3fd-749a-48ac-80d3-ae91cd359f0e)
![upload_picture](https://github.com/user-attachments/assets/0e53d5f9-c0ec-44ca-8807-27a72fd77b4e)
)
![onboarding_3](https://github.com/user-attachments/assets/2c5b69d4-c16c-4c7d-9f52-504f830058c4)
) | ![Summary](app/src/main/java/com/android/tripbook/screenshots/upload_picture.png) |

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
