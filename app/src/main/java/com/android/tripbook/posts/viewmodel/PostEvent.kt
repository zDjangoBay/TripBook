package com.android.tripbook.posts.viewmodel


import com.android.tripbook.posts.model.Location
import com.android.tripbook.posts.model.TagModel

sealed class PostEvent {
    data class UpdateTitle(val title: String) : PostEvent()
    data class UpdateDescription(val description: String) : PostEvent()
    data class AddImage(val imageUriString: String) : PostEvent()
    data class RemoveImage(val imageId: String) : PostEvent()
    data class SelectLocation(val location: Location) : PostEvent()
    data class SearchLocation(val query: String) : PostEvent()
    object ClearLocationSearch : PostEvent()
    data class ToggleTag(val tag: TagModel) : PostEvent()
    data class UpdateHashtags(val hashtagsInput: String) : PostEvent()
}