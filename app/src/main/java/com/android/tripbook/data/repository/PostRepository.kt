package com.android.tripbook.data.repository

import com.android.tripbook.model.PostModel

interface PostRepository {
    suspend fun submitPost(post: PostModel): Result<Unit>
    suspend fun fetchPosts(): List<PostModel>
}