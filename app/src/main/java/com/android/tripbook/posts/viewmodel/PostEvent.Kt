package com.android.tripbook.posts.viewmodel

import com.android.tripbook.posts.model.ImageModel
import com.android.tripbook.posts.model.Location
import com.android.tripbook.posts.model.TagModel

sealed class PostEvent {
    // Post list events
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
    
    // Post actions
    object SubmitPost : PostEvent()
    object ResetForm : PostEvent()
    data class DeletePost(val postId: String) : PostEvent()
    
    // Social interactions
    data class ToggleLike(val postId: String) : PostEvent()
    data class AddComment(val postId: String, val text: String) : PostEvent()
    data class AddReply(val postId: String, val commentId: String, val text: String) : PostEvent()
    
    // Location search
    data class SearchLocation(val query: String) : PostEvent()
    object ClearLocationSearch : PostEvent()
}