package com.android.tripbook.data.repository.comments

class CommentRepository {
    suspend fun searchUsers(query: String): List<String> {
        // TODO: Hit `/api/users/search?query=<input>`
        return listOf("JohnDoe", "JaneSmith", "TravelGuru")
    }
}
