package com.TripBook.postmodule

import android.net.Uri

sealed class PostEvent {

    // Core text input events
    data class TitleChanged(val newTitle: String) : PostEvent()
    data class DescriptionChanged(val newDescription: String) : PostEvent()

    // Basic image support
    data class ImageAdded(val imageUri: Uri) : PostEvent()

    // Form submission
    object SubmitPost : PostEvent()
}
