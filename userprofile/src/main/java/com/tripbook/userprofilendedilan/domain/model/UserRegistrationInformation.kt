package com.tripbook.userprofilendedilan.domain.model

data class UserRegistrationData(
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val profilePictureUri: String? = null,
    val birthDate: String = "",
    val favoriteDestinations: List<String> = emptyList(),
    val travelPreferences: List<String> = emptyList(),
    val bio: String = ""
)
