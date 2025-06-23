package com.android.tripbook.posts.viewmodel

import androidx.lifecycle.ViewModel
import com.android.tripbook.posts.repository.FakePostRepository
import com.android.tripbook.posts.utils.PostValidator
import com.android.tripbook.posts.model.ImageModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PostViewModel(
    private val repository: FakePostRepository,
    private val validator: PostValidator
) : ViewModel() {

    private val _selectedPostId = MutableStateFlow<String?>(null)
    val selectedPostId: StateFlow<String?> = _selectedPostId

    private val _images = MutableStateFlow<List<ImageModel>>(emptyList())
    val images: StateFlow<List<ImageModel>> = _images

    fun handleEvent(event: PostEvent) {
        when (event) {
            is PostEvent.ResetForm -> {
                _selectedPostId.value = null
                _images.value = emptyList()
            }
            is PostEvent.SelectPost -> {
                _selectedPostId.value = event.postId
            }
            is PostEvent.ImageAdded -> {
                _images.value = _images.value + event.image
            }
        }
    }
}
