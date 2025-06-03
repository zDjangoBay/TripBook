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

    // Travel categorization
    data class CategoryChanged(val category: String) : PostEvent()

    // Tag management for flexible content labeling
    data class TagAdded(val tag: String) : PostEvent()
    data class TagRemoved(val tag: String) : PostEvent()

    // Privacy and visibility controls
    data class VisibilityChanged(val visibility: String) : PostEvent()

    // Enhanced form management
    object SubmitPost : PostEvent()
    object ClearForm : PostEvent()
    object SaveDraft : PostEvent()
}
