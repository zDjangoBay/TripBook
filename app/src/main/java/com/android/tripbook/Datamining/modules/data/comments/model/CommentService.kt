package com.android.Tripbook.Datamining.modules.data.comments.model;



/*
*
* This right here i am defining the methods of interactions with my comment objects and how the interaction with the database is going to be done
* rather than harcoding it for every route concerned
* */

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