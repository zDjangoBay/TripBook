package com.android.Tripbook.Datamining.modules.data.comments.model;




interface CommentService {
    suspend fun createComment(request: CreateCommentRequest): Comment?
    suspend fun getCommentById(commentId: String): Comment?
    suspend fun getCommentsByPostId(postId: String, page: Int = 1, pageSize: Int = 20): List<Comment>
    suspend fun getCommentsByUserId(userId: String, page: Int = 1, pageSize: Int = 20): List<Comment>
    suspend fun getRepliesForComment(parentCommentId: String, page: Int = 1, pageSize: Int = 20): List<Comment>
    suspend fun updateComment(commentId: String, userId: String, request: UpdateCommentRequest): Comment? // userId to verify ownership
    suspend fun deleteComment(commentId: String, userId: String): Boolean
    suspend fun likeComment(commentId: String, likingUserId: String): Comment?

}