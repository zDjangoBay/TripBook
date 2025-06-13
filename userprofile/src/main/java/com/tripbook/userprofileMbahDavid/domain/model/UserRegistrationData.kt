package com.tripbook.userprofileMbahDavid.domain.model

data class UserRegistrationData(
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val bio: String = "",
    val profilePictureUri: String? = null,
    val birthDate: String = "",
    val travelPreferences: List<String> = emptyList(),
    val favoriteDestinations: List<String> = emptyList()
)