package com.android.tripbook.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.android.tripbook.data.local.entity.CommentEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Comment entity operations
 */
@Dao
interface CommentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity): Long

    @Update
    suspend fun updateComment(comment: CommentEntity): Int

    @Delete
    suspend fun deleteComment(comment: CommentEntity): Int

    @Query("DELETE FROM comments WHERE id = :commentId")
    suspend fun deleteCommentById(commentId: String): Int

    @Query("SELECT * FROM comments WHERE id = :commentId")
    suspend fun getCommentById(commentId: String): CommentEntity?

    @Query("SELECT * FROM comments WHERE postId = :postId ORDER BY createdAt DESC")
    fun getCommentsByPostId(postId: String): Flow<List<CommentEntity>>

    @Query("SELECT * FROM comments WHERE userId = :userId ORDER BY createdAt DESC")
    fun getCommentsByUserId(userId: String): Flow<List<CommentEntity>>

    @Query("SELECT COUNT(*) FROM comments WHERE postId = :postId")
    suspend fun getCommentsCountForPost(postId: String): Int
}
