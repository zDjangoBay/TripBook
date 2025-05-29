package com.android.tripbook.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.android.tripbook.data.local.entity.PostEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Post entity operations
 */
@Dao
interface PostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity): Long

    @Update
    suspend fun updatePost(post: PostEntity): Int

    @Delete
    suspend fun deletePost(post: PostEntity): Int

    @Query("DELETE FROM posts WHERE id = :postId")
    suspend fun deletePostById(postId: String): Int

    @Query("SELECT * FROM posts WHERE id = :postId")
    suspend fun getPostById(postId: String): PostEntity?

    @Query("SELECT * FROM posts ORDER BY createdAt DESC")
    fun getAllPosts(): Flow<List<PostEntity>>
    
    @Query("SELECT * FROM posts ORDER BY createdAt DESC LIMIT :limit OFFSET :offset")
    fun getFeedPosts(limit: Int, offset: Int): Flow<List<PostEntity>>
    
    @Query("SELECT * FROM posts WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' OR location LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    fun searchPostsByText(query: String): Flow<List<PostEntity>>
    
    @Query("SELECT * FROM posts WHERE userId = :userId ORDER BY createdAt DESC")
    fun getPostsByUser(userId: String): Flow<List<PostEntity>>

    @Query("UPDATE posts SET likes = likes + :increment WHERE id = :postId")
    suspend fun updateLikesCount(postId: String, increment: Int): Int

    @Query("UPDATE posts SET comments = comments + :increment WHERE id = :postId")
    suspend fun updateCommentsCount(postId: String, increment: Int): Int
}
