// UserProfileModel.kt
package com.android.Tripbook.Datamining.modules.data.userprofile.model

data class UserProfile(
    val id: String,
    val username: String,
    val email: String,
    val age: Int,
    val gender: String?,
    val location: String?,
    val tripsTaken: Int,
    val avgTripRating: Double,
    val preferredDestinations: List<String> = emptyList()
) {
    fun travelCategory(): String {
        return when {
            tripsTaken >= 20 -> "Globetrotter"
            tripsTaken >= 10 -> "Frequent Traveler"
            tripsTaken > 0 -> "Casual Explorer"
            else -> "Newbie"
        }
    }

    fun topRegion(): String {
        return preferredDestinations.groupingBy { it }.eachCount().maxByOrNull { it.value }?.key ?: "Unknown"
    }
}
