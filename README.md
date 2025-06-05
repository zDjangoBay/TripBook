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
