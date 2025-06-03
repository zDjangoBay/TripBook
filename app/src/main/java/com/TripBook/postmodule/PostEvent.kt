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

    // Form submission
    object SubmitPost : PostEvent()
}
