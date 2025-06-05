# TripBook - Android App with Nodemailer Email System

A complete authentication system for Android with **real email functionality** using Nodemailer
backend.


## 🎯 Features

### ✅ Complete Auth System

- **User Registration** with email validation
- **Login/Logout** with secure session management
- **Password Recovery** with real email sending
- **Modern UI** with Material Design 3

### ✅ Real Email Functionality

- **Password Reset Emails** - Professional HTML templates
- **Welcome Emails** - Sent automatically on registration
- **Nodemailer Backend** - Node.js server with Express
- **Gmail Integration** - Secure SMTP email delivery

### ✅ Production Ready

- **Error Handling** - Graceful fallbacks for network issues
- **Professional Templates** - Beautiful, responsive emails
- **Scalable Architecture** - Easy to deploy and extend
- **Security** - App passwords and secure connections

## 🚀 Quick Start

### 1. Android App

```bash
# Build and install
./gradlew installDebug

# The app will work with in-memory auth
# Email functionality requires backend setup
```

### 2. Email Backend (Optional)

```bash
# Setup Nodemailer backend for real emails
cd backend
npm install
cp .env.example .env

# Configure your Gmail credentials in .env
# Follow NODEMAILER_SETUP.md for details

npm start
```

## 📁 Project Structure

```
TripBook/
├── app/                          # Android app
│   ├── src/main/java/com/android/tripbook/
│   │   ├── auth/                # Authentication system
│   │   │   ├── AuthViewModel.kt # Auth logic + API calls
│   │   │   ├── LoginScreen.kt   # Login interface
│   │   │   ├── RegisterScreen.kt# Registration interface
│   │   │   └── ...             # Other auth screens
│   │   └── MainActivity.kt      # Main app entry
│   └── build.gradle.kts         # Android dependencies
├── backend/                      # Node.js email server
│   ├── server.js               # Express + Nodemailer server
│   ├── package.json            # Node.js dependencies
│   └── .env.example            # Email configuration
├── NODEMAILER_SETUP.md          # Complete setup guide
└── README.md                    # This file
```

## 🎮 How to Use

### First Time Setup

1. **Register** - Create account with real email address
2. **Check Email** - Receive welcome email via Nodemailer
3. **Login** - Access your account
4. **Test Recovery** - Try "Forgot Password" feature

### With Backend Running

- **Real Emails** - Password reset emails sent to inbox
- **Welcome Messages** - Professional onboarding emails
- **Production Ready** - Scalable email infrastructure

### Without Backend

- **In-Memory Auth** - Registration and login work locally
- **Mock Emails** - Success messages without actual sending
- **Perfect for Testing** - Full UI flow available

## 📧 Email Examples

### Password Reset Email

```
Subject: Password Reset Request - TripBook

Professional email with:
✅ TripBook branding
✅ Reset button/link
✅ Security information
✅ 24-hour expiry notice
```

### Welcome Email

```
Subject: Welcome to TripBook! 🎉

Personalized email with:
✅ User's name
✅ Getting started checklist  
✅ Professional signature
✅ Beautiful HTML template
```

## 🛠️ Tech Stack

### Android App

- **Kotlin** - Modern Android development
- **Jetpack Compose** - Declarative UI framework
- **Material Design 3** - Latest design system
- **MVVM Architecture** - Clean separation of concerns
- **StateFlow** - Reactive state management

### Email Backend

- **Node.js** - JavaScript runtime
- **Express** - Web framework
- **Nodemailer** - Email sending library
- **Gmail SMTP** - Reliable email delivery
- **CORS** - Cross-origin request handling

## 📚 Documentation

- **[NODEMAILER_SETUP.md](NODEMAILER_SETUP.md)** - Complete email setup guide
- **[FIREBASE_EMAIL_SETUP.md](FIREBASE_EMAIL_SETUP.md)** - Alternative Firebase setup
- **[AUTH_SYSTEM_README.md](AUTH_SYSTEM_README.md)** - Original auth documentation

## 🔧 Configuration

### Android App

```kotlin
// In AuthViewModel.kt
private val apiBaseUrl = "http://10.0.2.2:3000" // Android emulator

// For real device testing:
private val apiBaseUrl = "http://192.168.1.100:3000" // Your computer's IP
```

### Backend Server

```env
# In backend/.env
EMAIL_USER=your-email@gmail.com
EMAIL_PASS=your-16-char-app-password
PORT=3000
```

## 🚀 Deployment

### Android App

```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease
```

### Backend Server

- **Heroku** - `git push heroku main`
- **Railway** - Connect GitHub repo
- **DigitalOcean** - VPS deployment
- **Local** - `npm start`

## 🐛 Troubleshooting

## 📊 Current Status

**✅ Android App:** Fully functional with beautiful UI
**✅ Authentication:** Complete login/register/recovery system  
**✅ Email Integration:** Nodemailer backend with real email sending
**✅ Documentation:** Comprehensive setup guides
**✅ Production Ready:** Deployable to app stores and cloud platforms

## 📱 Application Preview

### Authentication Flow

<div align="center">
  <img src="screenshots/Screenshot_2.png" width="1080" alt="Login Screen" />
  <img src="screenshots/Screenshot_3.png" width="1080" alt="Register Screen" />
  <img src="screenshots/Screenshot_4.png" width="1080" alt="Password Recovery" />
</div>


### Email Examples

<div align="center">
  <img src="screenshots/Screenshot_1.png" width="1440" alt="Password Reset Email" />
</div>