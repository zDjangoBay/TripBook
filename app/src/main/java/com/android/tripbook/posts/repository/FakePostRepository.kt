package com.android.tripbook.posts.repository

import com.android.tripbook.posts.model.ImageModel
import com.android.tripbook.posts.model.Post
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakePostRepository {
    private val posts = mutableListOf<Post>()
    private val _postListFlow = MutableStateFlow<List<Post>>(emptyList())
    val postListFlow: StateFlow<List<Post>> = _postListFlow

    fun addPost(post: Post) {
        posts.add(post)
        _postListFlow.value = posts.toList()
    }

    fun getPostById(id: String): Post? = posts.find { it.id == id }
}
