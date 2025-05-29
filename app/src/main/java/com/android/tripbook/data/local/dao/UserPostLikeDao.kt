package com.android.tripbook.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.android.tripbook.data.local.entity.UserPostLikeEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for UserPostLike entity operations
 */
@Dao
interface UserPostLikeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserPostLike(userPostLike: UserPostLikeEntity)

    @Delete
    suspend fun deleteUserPostLike(userPostLike: UserPostLikeEntity): Int

    @Query("DELETE FROM user_post_likes WHERE userId = :userId AND postId = :postId")
    suspend fun deleteUserPostLike(userId: String, postId: String): Int

    @Query("SELECT EXISTS(SELECT 1 FROM user_post_likes WHERE userId = :userId AND postId = :postId)")
    suspend fun isPostLikedByUser(userId: String, postId: String): Boolean

    @Query("SELECT * FROM user_post_likes WHERE postId = :postId")
    fun getLikesByPostId(postId: String): Flow<List<UserPostLikeEntity>>

    @Query("SELECT COUNT(*) FROM user_post_likes WHERE postId = :postId")
    suspend fun getLikesCountForPost(postId: String): Int

    @Query("SELECT postId FROM user_post_likes WHERE userId = :userId")
    fun getLikedPostIdsByUser(userId: String): Flow<List<String>>
}
