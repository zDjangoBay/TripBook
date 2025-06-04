package com.android.Tripbook.Datamining.modules.data.comments.model;

import com.android.Tripbook.Datamining.modules.data.comments.model.Comment
import com.android.Tripbook.Datamining.modules.data.comments.model.CreateCommentRequest
import com.android.Tripbook.Datamining.modules.data.comments.model.UpdateCommentRequest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.eq
import org.litote.kmongo.setValue
import org.litote.kmongo.inc
import redis.clients.jedis.Jedis
import java.time.LocalDateTime
import java.util.UUID

// This will help us define cache keys to be stored in redis and also define the TTL
object CommentCacheKeys {
    fun commentById(commentId: String) = "comment:$commentId"
    fun commentsByPostId(postId: String, page: Int, pageSize: Int) = "post:$postId:comments:page:$page:size:$pageSize"
    fun commentsByUserId(userId: String, page: Int, pageSize: Int) = "user:$userId:comments:page:$page:size:$pageSize"
    fun repliesForComment(parentCommentId: String, page: Int, pageSize: Int) = "comment:$parentCommentId:replies:page:$page:size:$pageSize"
}

// Let's set enough time for the cached data to sit around in the caching database
const val CACHE_TTL_SECONDS: Long = 3600

class CommentServiceImpl(
    private val mongoDb: CoroutineDatabase,
    private val redis: Jedis,
    private val jsonMapper: Json = Json { ignoreUnknownKeys = true; encodeDefaults = true }
) : CommentService {

    private val commentsCollection: CoroutineCollection<Comment> = mongoDb.getCollection("comments")

    override suspend fun createComment(request: CreateCommentRequest): Comment? {
        val newComment = Comment(
            Comment_Id = UUID.randomUUID().toString(),
            Post_id = request.Post_id,
            User_id = request.User_id,
            value = request.value,
            Parent_Comment_id = request.Parent_Comment_id,
            PostedAt = LocalDateTime.now(),
            isDeleted = false,
            likesCount = 0,
            repliesCount = 0
        )

        try {
            // If it's a reply, update parent comment's repliesCount
            if (newComment.Parent_Comment_id != null) {
                val parentUpdated = commentsCollection.updateOne(
                    Comment::Comment_Id eq newComment.Parent_Comment_id,
                    inc(Comment::repliesCount, 1)
                )
                if (parentUpdated.modifiedCount > 0) {
                    // Invalidate/update parent comment in Redis
                    redis.del(CommentCacheKeys.commentById(newComment.Parent_Comment_id))
                }
            }

            // 1. Write to Redis first (cache-aside for writes, or write-through)
            // For simplicity, let's do write-through: write to DB then update cache.
            // More robust: write to DB, if successful, write to cache.

            // 2. Write to MongoDB
            val insertOneResult = commentsCollection.insertOne(newComment)
            if (!insertOneResult.wasAcknowledged()) {
                return null // Failed to insert into DB
            }

            // 3. Write the newly created comment to Redis
            redis.setex(
                CommentCacheKeys.commentById(newComment.Comment_Id),
                CACHE_TTL_SECONDS,
                jsonMapper.encodeToString(newComment)
            )

            // 4. Invalidate relevant list caches (e.g., for the post)
            // This is complex as lists are paginated. A common strategy is to invalidate
            // the first page or use more sophisticated versioning/event-driven cache updates.
            // For simplicity, we might just clear the first page cache for that post.
            redis.del(CommentCacheKeys.commentsByPostId(newComment.Post_id, 1, 20)) // Example invalidation

            return newComment
        } catch (e: Exception) {
            // Log error (e.g., using Ktor's application.log)
            println("Error creating comment: ${e.message}")
            return null
        }
    }

    override suspend fun getCommentById(commentId: String): Comment? {
        try {
            // 1. Check Redis
            val cachedCommentJson = redis.get(CommentCacheKeys.commentById(commentId))
            if (cachedCommentJson != null) {
                val cachedComment = jsonMapper.decodeFromString<Comment>(cachedCommentJson)
                // Optionally check isDeleted here if soft-deleted items are kept in cache
                if (cachedComment.isDeleted) return null
                return cachedComment
            }

            // 2. If not in Redis, fetch from MongoDB
            val dbComment = commentsCollection.findOne(Comment::Comment_Id eq commentId)

            // 3. If found in MongoDB, store in Redis
            if (dbComment != null && !dbComment.isDeleted) {
                redis.setex(
                    CommentCacheKeys.commentById(commentId),
                    CACHE_TTL_SECONDS,
                    jsonMapper.encodeToString(dbComment)
                )
            }
            return dbComment?.takeIf { !it.isDeleted }
        } catch (e: Exception) {
            println("Error getting comment by ID $commentId: ${e.message}")
            return null
        }
    }

    override suspend fun getCommentsByPostId(postId: String, page: Int, pageSize: Int): List<Comment> {
        val cacheKey = CommentCacheKeys.commentsByPostId(postId, page, pageSize)
        try {
            // 1. Check Redis for the list
            val cachedListJson = redis.get(cacheKey)
            if (cachedListJson != null) {
                return jsonMapper.decodeFromString<List<Comment>>(cachedListJson)
                    .filter { !it.isDeleted }
            }

            // 2. If not in Redis, fetch from MongoDB
            val skip = (page - 1) * pageSize
            val dbComments = commentsCollection.find(
                Comment::Post_id eq postId,
                Comment::isDeleted eq false
            )
                .skip(skip)
                .limit(pageSize)
                .descendingSort(Comment::PostedAt)
                .toList()

            // 3. Store the list in Redis
            if (dbComments.isNotEmpty()) {
                redis.setex(
                    cacheKey,
                    CACHE_TTL_SECONDS,
                    jsonMapper.encodeToString(dbComments)
                )
            }
            return dbComments
        } catch (e: Exception) {
            println("Error getting comments for post $postId: ${e.message}")
            return emptyList()
        }
    }

    override suspend fun getCommentsByUserId(userId: String, page: Int, pageSize: Int): List<Comment> {
        val cacheKey = CommentCacheKeys.commentsByUserId(userId, page, pageSize)
        try {
            val cachedListJson = redis.get(cacheKey)
            if (cachedListJson != null) {
                return jsonMapper.decodeFromString<List<Comment>>(cachedListJson)
                    .filter { !it.isDeleted }
            }

            val skip = (page - 1) * pageSize
            val dbComments = commentsCollection.find(
                Comment::User_id eq userId,
                Comment::isDeleted eq false
            )
                .skip(skip)
                .limit(pageSize)
                .descendingSort(Comment::PostedAt)
                .toList()

            if (dbComments.isNotEmpty()) {
                redis.setex(
                    cacheKey,
                    CACHE_TTL_SECONDS,
                    jsonMapper.encodeToString(dbComments)
                )
            }
            return dbComments
        } catch (e: Exception) {
            println("Error getting comments for user $userId: ${e.message}")
            return emptyList()
        }
    }

    override suspend fun getRepliesForComment(parentCommentId: String, page: Int, pageSize: Int): List<Comment> {
        val cacheKey = CommentCacheKeys.repliesForComment(parentCommentId, page, pageSize)
        try {
            val cachedListJson = redis.get(cacheKey)
            if (cachedListJson != null) {
                return jsonMapper.decodeFromString<List<Comment>>(cachedListJson)
                    .filter { !it.isDeleted }
            }

            val skip = (page - 1) * pageSize
            val dbComments = commentsCollection.find(
                Comment::Parent_Comment_id eq parentCommentId,
                Comment::isDeleted eq false
            )
                .skip(skip)
                .limit(pageSize)
                .ascendingSort(Comment::PostedAt)
                .toList()

            if (dbComments.isNotEmpty()) {
                redis.setex(
                    cacheKey,
                    CACHE_TTL_SECONDS,
                    jsonMapper.encodeToString(dbComments)
                )
            }
            return dbComments
        } catch (e: Exception) {
            println("Error getting replies for comment $parentCommentId: ${e.message}")
            return emptyList()
        }
    }

    override suspend fun updateComment(commentId: String, userId: String, request: UpdateCommentRequest): Comment? {
        try {
            // 1. Verify ownership
            val existingComment = commentsCollection.findOne(
                Comment::Comment_Id eq commentId,
                Comment::User_id eq userId,
                Comment::isDeleted eq false
            )
            if (existingComment == null) {
                return null
            }

            // 2. Update in MongoDB
            val updateResult = commentsCollection.updateOne(
                Comment::Comment_Id eq commentId,
                setValue(Comment::value, request.value),
                setValue(Comment::UpdatedAt, LocalDateTime.now())
            )

            if (updateResult.modifiedCount == 0L) {
                return null
            }

            // 3. Update cache
            val updatedComment = commentsCollection.findOne(Comment::Comment_Id eq commentId)
            if (updatedComment != null) {
                redis.setex(
                    CommentCacheKeys.commentById(commentId),
                    CACHE_TTL_SECONDS,
                    jsonMapper.encodeToString(updatedComment)
                )
                // Invalidate relevant list caches
                redis.del(CommentCacheKeys.commentsByPostId(updatedComment.Post_id, 1, 20))
                redis.del(CommentCacheKeys.commentsByUserId(updatedComment.User_id, 1, 20))
            }
            return updatedComment
        } catch (e: Exception) {
            println("Error updating comment $commentId: ${e.message}")
            return null
        }
    }

    override suspend fun deleteComment(commentId: String, userId: String): Boolean {
        try {
            // 1. Verify ownership
            val comment = commentsCollection.findOne(
                Comment::Comment_Id eq commentId,
                Comment::User_id eq userId,
                Comment::isDeleted eq false
            )
            if (comment == null) {
                return false
            }

            // 2. Soft delete in MongoDB
            val updateResult = commentsCollection.updateOne(
                Comment::Comment_Id eq commentId,
                setValue(Comment::isDeleted, true),
                setValue(Comment::DeletedAt, LocalDateTime.now())
            )

            if (updateResult.modifiedCount == 0L) return false

            // 3. Update cache
            redis.del(CommentCacheKeys.commentById(commentId))

            // 4. If it was a reply, decrement parent's repliesCount
            if (comment.Parent_Comment_id != null) {
                commentsCollection.updateOne(
                    Comment::Comment_Id eq comment.Parent_Comment_id,
                    inc(Comment::repliesCount, -1)
                )
                redis.del(CommentCacheKeys.commentById(comment.Parent_Comment_id))
            }

            // 5. Invalidate list caches
            redis.del(CommentCacheKeys.commentsByPostId(comment.Post_id, 1, 20))
            redis.del(CommentCacheKeys.commentsByUserId(comment.User_id, 1, 20))
            redis.del(CommentCacheKeys.repliesForComment(comment.Parent_Comment_id ?: "", 1, 20))

            return true
        } catch (e: Exception) {
            println("Error deleting comment $commentId: ${e.message}")
            return false
        }
    }

    override suspend fun likeComment(commentId: String, likingUserId: String): Comment? {
        try {
            // First check if user already liked this comment
            // This would typically involve checking a separate collection
            // For simplicity, we'll just increment the count

            val updateResult = commentsCollection.updateOne(
                Comment::Comment_Id eq commentId,
                Comment::isDeleted eq false,
                inc(Comment::likesCount, 1)
            )

            if (updateResult.modifiedCount == 0L) {
                return null
            }

            // Fetch updated comment
            val updatedComment = commentsCollection.findOne(Comment::Comment_Id eq commentId)
            if (updatedComment != null) {
                redis.setex(
                    CommentCacheKeys.commentById(commentId),
                    CACHE_TTL_SECONDS,
                    jsonMapper.encodeToString(updatedComment)
                )
                // Invalidate relevant list caches
                redis.del(CommentCacheKeys.commentsByPostId(updatedComment.Post_id, 1, 20))
            }
            return updatedComment
        } catch (e: Exception) {
            println("Error liking comment $commentId: ${e.message}")
            return null
        }
    }

    override suspend fun unlikeComment(commentId: String, unlikingUserId: String): Comment? {
        try {
            // First verify the user had previously liked this comment
            // For simplicity, we'll just decrement the count

            val updateResult = commentsCollection.updateOne(
                Comment::Comment_Id eq commentId,
                Comment::isDeleted eq false,
                Comment::likesCount gt 0,
                inc(Comment::likesCount, -1)
            )

            if (updateResult.modifiedCount == 0L) {
                return null
            }

            // Fetch updated comment
            val updatedComment = commentsCollection.findOne(Comment::Comment_Id eq commentId)
            if (updatedComment != null) {
                redis.setex(
                    CommentCacheKeys.commentById(commentId),
                    CACHE_TTL_SECONDS,
                    jsonMapper.encodeToString(updatedComment)
                )
                // Invalidate relevant list caches
                redis.del(CommentCacheKeys.commentsByPostId(updatedComment.Post_id, 1, 20))
            }
            return updatedComment
        } catch (e: Exception) {
            println("Error unliking comment $commentId: ${e.message}")
            return null
        }
    }
}