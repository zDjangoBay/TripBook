package com.android.tripbook.posts.viewmodel

import com.android.tripbook.posts.model.ImageModel // Assuming ImageModel is in this package
import com.android.tripbook.posts.model.TagModel    // Assuming TagModel is in this package
import com.android.tripbook.posts.model.Location  // Assuming Location model is in this package

sealed class PostEvent {
    object LoadPosts : PostEvent()
    object RefreshPosts : PostEvent()
    data class SelectPost(val postId: String) : PostEvent()

    // Form events
    data class TitleChanged(val title: String) : PostEvent()
    data class DescriptionChanged(val description: String) : PostEvent()
    data class ImageAdded(val image: ImageModel) : PostEvent()
    data class ImageRemoved(val imageId: String) : PostEvent()
    data class LocationSelected(val location: Location) : PostEvent()
    data class TagToggled(val tag: TagModel) : PostEvent()
    data class HashtagsChanged(val hashtags: String) : PostEvent()
    object SubmitPost : PostEvent()
    object ResetForm : PostEvent()

    // Interaction events
    data class DeletePost(val postId: String) : PostEvent()
    data class ToggleLike(val postId: String) : PostEvent()
    data class AddComment(val postId: String, val text: String) : PostEvent()
    data class AddReply(val postId: String, val commentId: String, val text: String) : PostEvent()

    // Location Search Events - THESE MUST BE PRESENT
    data class SearchLocation(val query: String) : PostEvent() // Must have 'query' property
    object ClearLocationSearch : PostEvent()
}

