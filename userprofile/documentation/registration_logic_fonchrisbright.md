# Registration Logic Documentation

## Overview
This document describes the implementation of the registration logic for the user profile module by fonchrisbright. The logic integrates with Firebase Authentication and uses the UI/screens created by ndedilan.

## Components
- **RegistrationViewModel**: Handles registration state and logic using StateFlow.
- **RegistrationRepository**: Interacts with the API service for user registration.
- **RegistrationApi**: Uses FirebaseAuth to register users.
- **RegistrationValidator**: Validates email and password.

## Integration
- The registration logic is connected to the existing registration UI. When the user clicks the "Create Account" button, the ViewModel triggers registration with Firebase.

## Usage
- Ensure `google-services.json` is present in the app module.
- The registration process uses Firebase Authentication for live user creation.

---

*Author: fonchrisbright*

