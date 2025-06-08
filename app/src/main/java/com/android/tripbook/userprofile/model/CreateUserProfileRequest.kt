// CreateUserProfileRequest.kt
package com.android.tripbook.userprofile.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateUserProfileRequest(
    val fullName: String,
    val email: String,
    val phoneNumber: String,
    val age: Int?,
    val gender: String?,
    val profileImageUrl: String? = null
)