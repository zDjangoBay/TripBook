package com.TripBook.postmodule

import android.net.Uri

/**
 * Represents user-triggered events in the Post module.
 *
 * This sealed class ensures all possible events are known and handled.
 */
sealed class PostEvent {

    /**
     * Event triggered when the user updates the post title.
     *
     * @param newTitle The latest title entered by the user.
     */
    data class TitleChanged(val newTitle: String) : PostEvent()

    /**
     * Event triggered when the user updates the post description.
     *
     * @param newDescription The latest description entered by the user.
     */
    data class DescriptionChanged(val newDescription: String) : PostEvent()

    /**
     * Event triggered when the user selects or captures an image.
     *
     * @param imageUri The URI of the image added by the user.
     */
    data class ImageAdded(val imageUri: Uri) : PostEvent()

    /**
     * Event triggered when the user removes the currently selected image.
     */
    object ImageRemoved : PostEvent()

    /**
     * Event triggered when the user taps the "Submit" button to post.
     */
    object SubmitPost : PostEvent()

    /**
     * Event triggered when the post form needs to be reset or cleared.
     */
    object ResetForm : PostEvent()
}
