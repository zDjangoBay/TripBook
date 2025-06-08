
# Datamining - User Profile Module

<p>This document outlines the structure and implementation details of the User Profile Module in the TripBook application. This module manages user-related information, including personal details, user activity metadata, and profile management. The module integrates with a MongoDB backend and offers route endpoints via Ktor for CRUD operations and data retrieval.</p>

## Table of Contents

1. [Module Overview](#module-overview)  
   • [Key Functionalities](#key-functionalities)  
   • [Technologies Used](#technologies-used)  
2. [Data Models](#data-models)  
   • [UserProfile](#userprofile-kt)  
   • [CreateUserProfileRequest DTO](#createuserprofilerequest-dto)  
   • [UpdateUserProfileRequest DTO](#updateuserprofilerequest-dto)  
3. [Service Layer](#service-layer)  
   • [UserProfileService (Interface)](#userprofileservice-interface)  
   • [UserProfileServiceImpl (Implementation)](#userprofileserviceimpl-implementation)  
     • [MongoDB Collection](#mongodb-collection)  
     • [Method Descriptions](#method-descriptions)  
4. [API Routes](#api-routes)

---

## 1. Module Overview

The **User Profile Module** handles all user-related metadata in the TripBook system. It maintains detailed user information that supports personalization, analytics, and social features within the app.

### Key Functionalities

- **User Creation and Registration**
- **Profile Updates and Customization**
- **Fetching by User ID or Username**
- **Optional profile picture, bio, and contact info**
- **Efficient queries for analytics and mining user data**

### Technologies Used

- **Kotlin:** Main language used for model and service logic
- **Ktor:** HTTP routing and API layer
- **MongoDB:** Used to persist user profile data
- **Kotlinx Serialization:** For transforming objects to/from JSON

---

## 2. Data Models

### `UserProfile.kt`

Defines the structure of a user profile.

```kotlin
@Serializable
data class UserProfile(
    val userId: String = UUID.randomUUID().toString(),
    val username: String,
    val fullName: String,
    val email: String,
    val phoneNumber: String? = null,
    val bio: String? = null,
    val profilePictureUrl: String? = null,
    val registeredAt: Long = System.currentTimeMillis()
)
```

### `CreateUserProfileRequest.kt`

DTO for creating a new user profile.

```kotlin
@Serializable
data class CreateUserProfileRequest(
    val username: String,
    val fullName: String,
    val email: String,
    val phoneNumber: String? = null,
    val bio: String? = null,
    val profilePictureUrl: String? = null
)
```

### `UpdateUserProfileRequest.kt`

DTO for updating a user profile.

```kotlin
@Serializable
data class UpdateUserProfileRequest(
    val fullName: String? = null,
    val phoneNumber: String? = null,
    val bio: String? = null,
    val profilePictureUrl: String? = null
)
```

---

## 3. Service Layer

### `UserProfileService.kt` (Interface)

```kotlin
interface UserProfileService {
    suspend fun createUserProfile(request: CreateUserProfileRequest): UserProfile?
    suspend fun getUserProfileById(userId: String): UserProfile?
    suspend fun getUserProfileByUsername(username: String): UserProfile?
    suspend fun updateUserProfile(userId: String, request: UpdateUserProfileRequest): UserProfile?
    suspend fun deleteUserProfile(userId: String): Boolean
}
```

### `UserProfileServiceImpl.kt` (Implementation)

#### MongoDB Collection

The module uses a MongoDB collection named `"UserProfiles"` to store user profile documents.

#### Method Descriptions

- `createUserProfile(request)` → Inserts a new user profile document.
- `getUserProfileById(userId)` → Returns user profile by UUID.
- `getUserProfileByUsername(username)` → Returns user profile by unique username.
- `updateUserProfile(userId, request)` → Applies field updates to the specified user.
- `deleteUserProfile(userId)` → Removes a profile by ID.

---

## 4. API Routes

The following Ktor routes are implemented in `UserProfileRoutes.kt`:

| Route | Method | Description | Service Call |
|-------|--------|-------------|--------------|
| `/userprofiles` | POST | Create a new user profile | `createUserProfile()` |
| `/userprofiles/{user_id}` | GET | Get user profile by ID | `getUserProfileById()` |
| `/userprofiles/username/{username}` | GET | Get profile by username | `getUserProfileByUsername()` |
| `/userprofiles/{user_id}` | PUT | Update user profile | `updateUserProfile()` |
| `/userprofiles/{user_id}` | DELETE | Delete a user profile | `deleteUserProfile()` |

---

> ✅ This module ensures robust user profile management and integrates seamlessly with other parts of the TripBook application like Reservations, Comments, and Posts.
