package com.android.tripbook.userprofilebradley.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.android.tripbook.userprofilebradley.data.PostData

class PostListViewModel : ViewModel() {

    private val _posts = mutableStateListOf<PostData>()
    val posts: List<PostData> = _posts

    fun addPost(post: PostData) {
        _posts.add(0, post) // Add to the beginning of the list
    }

    fun removePost(postId: String) {
        _posts.removeAll { it.id == postId }
    }

    fun getPost(postId: String): PostData? {
        return _posts.find { it.id == postId }
    }
}
