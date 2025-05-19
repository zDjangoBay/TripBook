package com.android.tripbook.domain.model

data class UserRegistrationData(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val profilePictureUri: String? = null,
    val birthDate: String = "",
    val favoriteDestinations: List<String> = emptyList(),
    val travelPreferences: List<String> = emptyList(),
    val bio: String = ""
)
