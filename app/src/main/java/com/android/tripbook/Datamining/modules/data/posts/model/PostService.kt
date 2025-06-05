package com.android.Tripbook.Datamining.modules.data.posts.model

interface PostService {

    suspend fun createPost(authorId: String, authorUsername: String, authorProfileUrl: String?, request: CreatePostRequest): Post?
    suspend fun getPostById(postId: String): Post?
    suspend fun updatePost(postId: String, authorId: String, request: UpdatePostRequest): Post? // it will likely be a function for the premium users 😁
    suspend fun deletePost(postId: String, authorId: String): Boolean // authorId pour vérification
    suspend fun getPostsByAuthorId(authorId: String, page: Int = 1, pageSize: Int = 20): List<Post>
    suspend fun getPostsByAuthorUsername(username: String, page: Int = 1, pageSize: Int = 20): List<Post>
    // suspend fun getFeedForUser(userId: String, page: Int = 1, pageSize: Int = 20): List<Post> // It is still at the thinking  level , it will be coordinated with the
    suspend fun likePost(postId: String, likingUserId: String): Post?
    suspend fun unlikePost(postId: String, unlikingUserId: String): Post?
    suspend fun getRepliesToPost(parentPostId: String, page: Int = 1, pageSize: Int = 20): List<Post>
    suspend fun getRepostsOfPost(originalPostId: String, page: Int = 1, pageSize: Int = 20): List<Post> // posts that are litteraly repost of original post_id

    suspend fun reportPost(postId: String, reportingUserId: String): Post? // That is in the case , someone post an inappropriate content
}