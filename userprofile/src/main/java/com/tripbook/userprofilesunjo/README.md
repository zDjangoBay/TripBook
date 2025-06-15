# UserProfile Sunjo - Password Recovery System

This package contains a complete password recovery implementation following Clean Architecture
principles and modern Android development practices.

## Features

✅ **Complete Password Recovery Flow**

- Email verification for password reset
- OTP (One-Time Password) verification
- Secure password reset with validation
- Modern Material Design 3 UI

✅ **Architecture & Patterns**

- Clean Architecture (Domain, Data, Presentation layers)
- MVVM with Compose
- Repository Pattern
- Use Cases for business logic
- State management with Compose

✅ **User Experience**

- Intuitive step-by-step flow
- Loading states and error handling
- Input validation and feedback
- Responsive Material 3 design
- Dark/Light theme support

## Package Structure

```
com.tripbook.userprofilesunjo/
├── data/
│   └── repository/
│       └── PasswordRecoveryRepositoryImpl.kt
├── domain/
│   ├── model/
│   │   └── PasswordRecoveryModels.kt
│   ├── repository/
│   │   └── PasswordRecoveryRepository.kt
│   └── usecase/
│       ├── RequestPasswordResetUseCase.kt
│       ├── VerifyOtpUseCase.kt
│       └── ResetPasswordUseCase.kt
├── presentation/
│   ├── auth/screens/
│   │   ├── LoginScreen.kt
│   │   ├── RegisterScreen.kt
│   │   ├── ForgotPasswordScreen.kt
│   │   ├── VerifyOtpScreen.kt
│   │   └── ResetPasswordScreen.kt
│   ├── navigation/
│   │   └── Screen.kt
│   ├── splash/
│   │   └── SplashScreen.kt
│   ├── theme/
│   │   └── Theme.kt
│   └── viewmodel/
│       └── PasswordRecoveryViewModel.kt
└── UserProfileSunjoEntryPoint.kt
```

## How to Use

### Demo Credentials

- **Email**: Any valid email format
- **OTP Code**: `******`
- **Password**: Must meet requirements (8+ chars, uppercase, lowercase, number, special char)

### Flow Steps

1. **Login Screen** → Click "Forgot Password?"
2. **Email Input** → Enter email and click "Send Verification Code"
4. **Password Reset** → Set new password following requirements
5. **Success** → Redirected to login screen

### Integration

```kotlin
UserProfileSunjoEntryPoint()
```

## Technical Implementation

### State Management

The `PasswordRecoveryViewModel` manages the entire flow state:

```kotlin
data class PasswordRecoveryUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isEmailSent: Boolean = false,
    val isOtpVerified: Boolean = false,
    val isPasswordReset: Boolean = false,
    val email: String = "",
    val resetToken: String = ""
)
```

### Validation Rules

- **Email**: Must be valid email format
- **OTP**: Must be exactly 6 digits
- **Password**:
    - Minimum 8 characters
    - At least one uppercase letter
    - At least one lowercase letter
    - At least one number
    - At least one special character

### Error Handling

- Network connectivity issues
- Invalid input validation
- API response errors
- User-friendly error messages


🔧 **Technical Improvements**

- [ ] Hilt dependency injection
- [ ] Retrofit for network calls
- [ ] Unit and UI tests

## Dependencies Used

- Jetpack Compose (UI)
- Navigation Compose (Navigation)
- Lifecycle ViewModel (State Management)
- Material 3 (Design System)
- Coroutines (Async Operations)


---

**Created by**: Sunjo  
**Date**: 2024  
**Architecture**: Clean Architecture + MVVM + Compose