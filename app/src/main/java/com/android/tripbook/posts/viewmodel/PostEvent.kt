package com.android.tripbook.posts.viewmodel

import com.android.tripbook.posts.model.ImageModel

sealed class PostEvent {
    object ResetForm : PostEvent()
    data class SelectPost(val postId: String) : PostEvent()
    data class ImageAdded(val image: ImageModel) : PostEvent()
    // Add more events as needed
}
