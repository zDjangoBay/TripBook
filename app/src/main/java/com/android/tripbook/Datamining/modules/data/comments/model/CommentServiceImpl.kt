package com.android.comments.model

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.eq
import org.litote.kmongo.and
import org.litote.kmongo.setValue
import org.litote.kmongo.inc
import org.bson.conversions.Bson
import redis.clients.jedis.Jedis
import java.time.LocalDateTime
import java.util.UUID


object CommentCacheKeys {
    fun commentById(commentId: String) = "comment:$commentId"
    fun commentsByPostId(postId: String, page: Int, pageSize: Int) = "post:$postId:comments:page:$page:size:$pageSize"
    fun commentsByUserId(userId: String, page: Int, pageSize: Int) = "user:$userId:comments:page:$page:size:$pageSize"
    fun repliesForComment(parentCommentId: String, page: Int, pageSize: Int) = "comment:$parentCommentId:replies:page:$page:size:$pageSize"
}


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

            if (newComment.Parent_Comment_id != null) {
                val parentFilter = Comment::Comment_Id eq newComment.Parent_Comment_id
                val parentUpdate = inc(Comment::repliesCount, 1)
                
                val parentUpdated = commentsCollection.updateOne(parentFilter, parentUpdate)
                if (parentUpdated.wasAcknowledged()) {
                    redis.del(CommentCacheKeys.commentById(newComment.Parent_Comment_id))
                }
            }


            val insertOneResult = commentsCollection.insertOne(newComment)
            if (!insertOneResult.wasAcknowledged()) {
                return null
            }


            redis.setex(
                CommentCacheKeys.commentById(newComment.Comment_Id),
                CACHE_TTL_SECONDS,
                jsonMapper.encodeToString(newComment)
            )

            redis.del(CommentCacheKeys.commentsByPostId(newComment.Post_id, 1, 20))

            return newComment
        } catch (e: Exception) {
            println("Error creating comment: ${e.message}")
            return null
        }
    }

    override suspend fun getCommentById(commentId: String): Comment? {
        try {
            val cachedCommentJson = redis.get(CommentCacheKeys.commentById(commentId))
            if (cachedCommentJson != null) {
                val cachedComment = jsonMapper.decodeFromString<Comment>(cachedCommentJson)
                if (cachedComment.isDeleted) return null
                return cachedComment
            }

            val dbComment = commentsCollection.findOne(Comment::Comment_Id eq commentId)

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

            val cachedListJson = redis.get(cacheKey)
            if (cachedListJson != null) {
                return jsonMapper.decodeFromString<List<Comment>>(cachedListJson)
                    .filter { !it.isDeleted }
            }

            val skip = (page - 1) * pageSize
            val dbComments = commentsCollection.find(
                Comment::Post_id eq postId,
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

    override suspend fun updateComment(commentId: String, request: UpdateCommentRequest): Comment? {
        try {
            val existingComment = commentsCollection.findOne(
                Comment::Comment_Id eq request.comment_id
            )
            if (existingComment == null) {
                return null
            }

            // Create a simple update with just the value field
            val update = setValue(Comment::value, request.value)
            
            val updateResult = commentsCollection.updateOne(
                Comment::Comment_Id eq commentId,
                update
            )

            if (!updateResult.wasAcknowledged()) {
                return null
            }

            val updatedComment = commentsCollection.findOne(Comment::Comment_Id eq commentId)
            if (updatedComment != null) {
                redis.setex(
                    CommentCacheKeys.commentById(commentId),
                    CACHE_TTL_SECONDS,
                    jsonMapper.encodeToString(updatedComment)
                )

                redis.del(CommentCacheKeys.commentsByPostId(updatedComment.Post_id, 1, 20))
                redis.del(CommentCacheKeys.commentsByUserId(updatedComment.User_id, 1, 20))
            }
            return updatedComment
        } catch (e: Exception) {
            println("Error updating comment $commentId: ${e.message}")
            return null
        }
    }

    override suspend fun deleteComment(commentId: String): Boolean {
        try {
            val comment = commentsCollection.findOne(
                Comment::Comment_Id eq commentId,
                Comment::isDeleted eq false
            )
            if (comment == null) {
                return false
            }

            // Simple update to mark as deleted
            val update = setValue(Comment::isDeleted, true)
            
            val updateResult = commentsCollection.updateOne(
                Comment::Comment_Id eq commentId,
                update
            )

            if (!updateResult.wasAcknowledged()) return false

            redis.del(CommentCacheKeys.commentById(commentId))

            if (comment.Parent_Comment_id != null) {
                commentsCollection.updateOne(
                    Comment::Comment_Id eq comment.Parent_Comment_id,
                    inc(Comment::repliesCount, -1)
                )
                redis.del(CommentCacheKeys.commentById(comment.Parent_Comment_id))
            }


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
            val filter = and(
                Comment::Comment_Id eq commentId
            )

            val update = inc(Comment::likesCount, 1)
            
            val updateResult = commentsCollection.updateOne(filter, update)

            if (!updateResult.wasAcknowledged()) {
                return null
            }

            val updatedComment = commentsCollection.findOne(Comment::Comment_Id eq commentId)
            if (updatedComment != null) {
                redis.setex(
                    CommentCacheKeys.commentById(commentId),
                    CACHE_TTL_SECONDS,
                    jsonMapper.encodeToString(updatedComment)
                )

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
            // First check if likes > 0
            val comment = commentsCollection.findOne(
                Comment::Comment_Id eq commentId,
                Comment::isDeleted eq false
            )
            
            if (comment == null || comment.likesCount <= 0) {
                return null
            }
            
            val filter = Comment::Comment_Id eq commentId
            val update = inc(Comment::likesCount, -1)
            
            val updateResult = commentsCollection.updateOne(filter, update)

            if (!updateResult.wasAcknowledged()) {
                return null
            }

            val updatedComment = commentsCollection.findOne(Comment::Comment_Id eq commentId)
            if (updatedComment != null) {
                redis.setex(
                    CommentCacheKeys.commentById(commentId),
                    CACHE_TTL_SECONDS,
                    jsonMapper.encodeToString(updatedComment)
                )

                redis.del(CommentCacheKeys.commentsByPostId(updatedComment.Post_id, 1, 20))
            }
            return updatedComment
        } catch (e: Exception) {
            println("Error unliking comment $commentId: ${e.message}")
            return null
        }
    }
}