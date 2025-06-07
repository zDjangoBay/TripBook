package com.TripBook.postmodule

import android.net.Uri

/**
 * Sealed class representing all possible user events in the post creation flow.
 * This ensures type safety and exhaustive handling of all post-related user interactions.
 *
 * Features:
 * - 17 comprehensive event types covering complete post workflow
 * - Travel-specific functionality for location, categories, and tags
 * - Form management with draft saving and clearing capabilities
 * - Error handling and user feedback system
 * - Privacy controls with visibility settings
 *
 * @author Feukoun Marel
 * @version 1.1
 * @since TripBook v1.0
 */
sealed class PostEvent {

    // ========== Text Input Events ==========

    /** Event triggered when user updates the post title */
    data class TitleChanged(val newTitle: String) : PostEvent()

    /** Event triggered when user updates the post description */
    data class DescriptionChanged(val newDescription: String) : PostEvent()

    // ========== Image Management Events ==========

    /** Event triggered when user adds an image to the post */
    data class ImageAdded(val imageUri: Uri) : PostEvent()

    /** Event triggered when user removes a specific image */
    data class ImageRemoved(val imageUri: Uri) : PostEvent()

    /** Event triggered when user clears all images */
    object ClearAllImages : PostEvent()

    // ========== Location Events ==========

    /** Event triggered when user adds location with coordinates and optional name */
    data class LocationAdded(val latitude: Double, val longitude: Double, val locationName: String?) : PostEvent()

    /** Event triggered when user clears location data */
    object ClearLocation : PostEvent()

    // ========== Travel Categorization Events ==========

    /** Event triggered when user selects a travel category */
    data class CategoryChanged(val category: String) : PostEvent()

    // ========== Tag Management Events ==========

    /** Event triggered when user adds a tag */
    data class TagAdded(val tag: String) : PostEvent()

    /** Event triggered when user removes a tag */
    data class TagRemoved(val tag: String) : PostEvent()

    // ========== Privacy Control Events ==========

    /** Event triggered when user changes post visibility */
    data class VisibilityChanged(val visibility: String) : PostEvent()

    // ========== Form Management Events ==========

    /** Event triggered when user submits the post */
    object SubmitPost : PostEvent()

    /** Event triggered when user clears the form */
    object ClearForm : PostEvent()

    /** Event triggered when user saves draft */
    object SaveDraft : PostEvent()

    // ========== User Feedback Events ==========

    /** Event triggered when an error needs to be shown to user */
    data class ShowError(val message: String) : PostEvent()

    /** Event triggered when user dismisses error message */
    object DismissError : PostEvent()

    // ========== Success Events ==========

    /** Event triggered when post is successfully created */
    data class PostCreated(val postId: String) : PostEvent()
}
