package com.tripbook.userprofile.data.models

data class UserProfile(
    val userId: String,
    val fullName: String,
    val email: String,
    val phoneNumber: String?,
    val profileImageUrl: String?,
    val dateJoined: Long
)

package com.android.tripbook.userprofile.data.models

data class UserProfile(
    val userId: String,
    val name: String,
    val email: String,
    val phoneNumber: String,
    val address: UserAddress,
    val preferences: UserPreferences,
    val stats: UserStats,
    val emergencyContact: EmergencyContact,
    val settings: UserSettings
    val auth: AuthToken,
    val update: ProfileUpdateRequest
)
