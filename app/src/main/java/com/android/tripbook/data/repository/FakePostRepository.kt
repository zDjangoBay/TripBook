package com.android.tripbook.data.repository

import com.android.tripbook.model.PostModel
import kotlinx.coroutines.delay

class FakePostRepository : PostRepository {

    private val postList = mutableListOf<PostModel>()

    override suspend fun submitPost(post: PostModel): Result<Unit> {
        delay(500) // Simulate network latency
        postList.add(post)
        return Result.success(Unit)
    }

    override suspend fun fetchPosts(): List<PostModel> {
        delay(300) // Simulate network delay
        return postList
    }
}