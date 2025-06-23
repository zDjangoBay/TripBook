package com.android.tripbook.posts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.tripbook.posts.repository.FakePostRepository
import com.android.tripbook.posts.utils.PostValidator

class PostViewModelFactory(
    private val repository: FakePostRepository,
    private val validator: PostValidator
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostViewModel::class.java)) {
            return PostViewModel(repository, validator) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
