package com.TripBook.postmodule

import android.net.Uri

sealed class PostEvent {

    // Core text input events
    data class TitleChanged(val newTitle: String) : PostEvent()
    data class DescriptionChanged(val newDescription: String) : PostEvent()

    // Enhanced image management
    data class ImageAdded(val imageUri: Uri) : PostEvent()
    data class ImageRemoved(val imageUri: Uri) : PostEvent()
    object ClearAllImages : PostEvent()

    // Location support for travel posts
    data class LocationAdded(val latitude: Double, val longitude: Double, val locationName: String?) : PostEvent()
    object ClearLocation : PostEvent()

    // Form submission
    object SubmitPost : PostEvent()
}
