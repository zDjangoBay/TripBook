// UpdateUserProfileRequest.kt
package com.android.tripbook.userprofile.model
import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserProfileRequest(
    val fullName: String? = null,
    val phoneNumber: String? = null,
    val age: Int? = null,
    val gender: String? = null,
    val profileImageUrl: String? = null
)