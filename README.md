
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




This PR introduces a fully functional, Material Design 3-compliant login screen for the userprofileMbahDavid module of the TripBook Android app. It enhances user interaction, improves security, and establishes a scalable code structure using Jetpack Compose and MVVM architecture.
✨ Key Features

✅ Material Design 3 UI using material3 components

🔒 Secure password field with visibility toggle

💾 “Remember Me” support for session persistence

✅ Email & password input validation

🔄 Loading indicators for login feedback

↗️ Navigation to the registration screen

🎨 Consistent styling using centralized theme files
🗂️ Code Structure
userprofileMbahDavid/
├── domain/model/UserLoginInformation.kt
├── presentation/auth/
│ ├── screens/LoginScreen.kt
│ └── viewmodels/LoginViewModel.kt
└── theme/
├── Color.kt
├── Theme.kt
└── Type.kt
🧪 Testing Overview

Validated UI on various device sizes and orientations

Confirmed proper validation for empty and invalid input

Verified password visibility toggle functionality

Ensured smooth navigation between login and registration

Checked loading state transitions
![loginpage](https://github.com/user-attachments/assets/e1ecdfff-83fb-4852-b624-32b1916c0c44)


also 
