package com.android.tripbook.userprofilebradley.data

data class PostData(
    val id: String = "",
    val type: PostType,
    val title: String = "",
    val content: String = "",
    val imageUri: String? = null,
    val location: LocationData? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val authorId: String = "",
    val authorName: String = ""
)

data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val address: String = "",
    val placeName: String = ""
)
