package com.android.tripbook.data.state

import android.net.Uri
import com.android.tripbook.data.models.TagModel

sealed class PostEvent {
    data class TitleChanged(val title: String) : PostEvent()
    data class DescriptionChanged(val description: String) : PostEvent()
    data class LocationChanged(val location: String) : PostEvent()
    data class HashtagsChanged(val hashtags: String) : PostEvent()
    data class ImageAdded(val uri: Uri) : PostEvent()
    data class ImageRemoved(val imageId: String) : PostEvent()
    data class TagToggled(val tag: TagModel) : PostEvent()
    object SubmitPost : PostEvent()
    object ClearForm : PostEvent()
    object LoadPosts : PostEvent()
    data class DeletePost(val postId: String) : PostEvent()
}