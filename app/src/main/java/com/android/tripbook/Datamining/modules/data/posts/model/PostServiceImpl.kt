package com.android.Tripbook.Datamining.modules.data.posts.model

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.eq
import org.litote.kmongo.setValue // Pour les mises à jour de champs spécifiques
import org.litote.kmongo.inc // Pour incrémenter les compteurs
import org.litote.kmongo.descendingSort // Pour trier
import redis.clients.jedis.Jedis
import java.time.LocalDateTime
import java.util.UUID

object PostCacheKeys {
    fun postById(postId: String) = "post:$postId"
    fun postsByAuthorId(authorId: String, page: Int, pageSize: Int) = "posts:author:$authorId:page:$page:size:$pageSize"
    fun postsByUsername(username: String, page: Int, pageSize: Int) = "posts:username:${username.lowercase()}:page:$page:size:$pageSize"
    fun feedForUser(userId: String, page: Int, pageSize: Int) = "feed:user:$userId:page:$page:size:$pageSize"
    fun repliesToPost(parentPostId: String, page: Int, pageSize: Int) = "post:$parentPostId:replies:page:$page:size:$pageSize"
    fun repostsOfPost(originalPostId: String, page: Int, pageSize: Int) = "post:$originalPostId:reposts:page:$page:size:$pageSize"
}

const val POST_CACHE_TTL_SECONDS: Long = 1800

class PostServiceImpl(
    private val mongoDb: CoroutineDatabase,
    private val redis: Jedis,
    private val jsonMapper: Json = Json { ignoreUnknownKeys = true; encodeDefaults = true; prettyPrint = false }
) : PostService {

    private val postsCollection: CoroutineCollection<Post> = mongoDb.getCollection("posts")

    override suspend fun createPost(
        authorId: String,
        authorUsername: String,
        authorProfileUrl: String?,
        request: CreatePostRequest
    ): Post? {
        val postId = UUID.randomUUID().toString()
        val newPost = Post(
            Post_id = postId,
            PostAuthor_id = authorId,
            PostAuthor_username = authorUsername,
            PostAuthorProfileUrl = authorProfileUrl,
            value = request.value,
            Post_mediasUrl = request.Post_mediasUrl ?: emptyList(),
            Localisation = request.Localisation,
            Posttype = request.Posttype,
            parentPostId = if (request.Posttype == PostType.REPLY) request.parentPostId else null,
            repostedPostId = if (request.Posttype == PostType.REPOST) request.repostedPostId else null,
            Hashtags = request.Hashtags ?: emptyList(),
            mention = request.mention ?: emptyList(),
            CreatedAt = LocalDateTime.now()
            visibility = request.visibility
        )

        try {

            when (newPost.Posttype) {
                PostType.REPLY -> {
                    if (newPost.parentPostId == null) return null // parentPostId is required for a response
                    postsCollection.updateOne(Post::Post_id eq newPost.parentPostId, inc(Post::repliesCount, 1))
                    redis.del(PostCacheKeys.postById(newPost.parentPostId)) // Invalidate the parent post cache
                }
                PostType.REPOST -> {
                    if (newPost.repostedPostId == null) return null
                    postsCollection.updateOne(Post::Post_id eq newPost.repostedPostId, inc(Post::repostCount, 1))
                    redis.del(PostCacheKeys.postById(newPost.repostedPostId))
                }

            }

            val insertResult = postsCollection.insertOne(newPost)
            if (!insertResult.wasAcknowledged()) {
                println("MongoDB insert failed for post by $authorUsername")
                return null
            }

            val postJson = jsonMapper.encodeToString(newPost)
            redis.setex(PostCacheKeys.postById(postId), POST_CACHE_TTL_SECONDS, postJson)

            redis.del(PostCacheKeys.postsByAuthorId(authorId, 1, 20))
            redis.del(PostCacheKeys.postsByUsername(authorUsername, 1, 20))


            return newPost
        } catch (e: Exception) {
            println("Error creating post by $authorUsername: ${e.message}")
            return null
        }
    }

    override suspend fun getPostById(postId: String): Post? {
        val cacheKey = PostCacheKeys.postById(postId)
        try {
            val cachedJson = redis.get(cacheKey)
            if (cachedJson != null) {
                val post = jsonMapper.decodeFromString<Post>(cachedJson)
                return post
            }
            val dbPost = postsCollection.findOne(Post::Post_id eq postId , Post::isDeleted eq false )
            if (dbPost != null) {
                redis.setex(cacheKey, POST_CACHE_TTL_SECONDS, jsonMapper.encodeToString(dbPost))
            }
            return dbPost
        } catch (e: Exception) {
            println("Error getting post by ID $postId: ${e.message}")
            return null
        }
    }

    override suspend fun updatePost(postId: String, authorId: String, request: UpdatePostRequest): Post? {
        try {

            val existingPost = postsCollection.findOne(Post::Post_id eq postId, Post::PostAuthor_id eq authorId)
            if (existingPost == null  || existingPost.isDeleted ) {
                return null
            }


            val updates = mutableListOf<org.bson.conversions.Bson>()
            request.value?.let { updates.add(setValue(Post::value, it)) }
            request.Post_mediasUrl?.let { updates.add(setValue(Post::Post_mediasUrl, it)) }
            request.Localisation?.let { updates.add(setValue(Post::Localisation, it)) }
            request.Hashtags?.let { updates.add(setValue(Post::Hashtags, it)) }
            request.mention?.let { updates.add(setValue(Post::mention, it)) }


            if (updates.isEmpty()) return existingPost

            val updateResult = postsCollection.updateOne(
                Post::Post_id eq postId,
                org.litote.kmongo.combine(updates)
            )

            if (updateResult.modifiedCount == 0L) {
                return postsCollection.findOne(Post::Post_id eq postId)
            }

            val updatedPost = postsCollection.findOne(Post::Post_id eq postId)
            if (updatedPost != null) {
                redis.setex(PostCacheKeys.postById(postId), POST_CACHE_TTL_SECONDS, jsonMapper.encodeToString(updatedPost))
                redis.del(PostCacheKeys.postsByAuthorId(updatedPost.PostAuthor_id, 1, 20))
                redis.del(PostCacheKeys.postsByUsername(updatedPost.PostAuthor_username, 1, 20))
            }
            return updatedPost
        } catch (e: Exception) {
            println("Error updating post $postId: ${e.message}")
            return null
        }
    }

    override suspend fun deletePost(postId: String, authorId: String): Boolean {
        try {
            val deleteResult = postsCollection.deleteOne(Post::Post_id eq postId, Post::PostAuthor_id eq authorId)
            if (deleteResult.deletedCount > 0) {
                redis.del(PostCacheKeys.postById(postId))

                return true
            }
            return false
        } catch (e: Exception) {
            println("Error deleting post $postId: ${e.message}")
            return false
        }
    }

    override suspend fun getPostsByAuthorId(authorId: String, page: Int, pageSize: Int): List<Post> {
        val cacheKey = PostCacheKeys.postsByAuthorId(authorId, page, pageSize)
        try {
            val cachedJson = redis.get(cacheKey)
            if (cachedJson != null) return jsonMapper.decodeFromString<List<Post>>(cachedJson)
            .filter { !it.isDeleted }

            val skip = (page - 1) * pageSize
            val dbPosts = postsCollection.find(Post::PostAuthor_id eq authorId , Post::isDeleted eq false )
                .descendingSort(Post::CreatedAt)
                .skip(skip)
                .limit(pageSize)
                .toList()

            if (dbPosts.isNotEmpty()) {
                redis.setex(cacheKey, POST_CACHE_TTL_SECONDS, jsonMapper.encodeToString(dbPosts))
            }
            return dbPosts
        } catch (e: Exception) {
            println("Error getting posts by author ID $authorId: ${e.message}")
            return emptyList()
        }
    }

    override suspend fun getPostsByAuthorUsername(username: String, page: Int, pageSize: Int): List<Post> {
        val cacheKey = PostCacheKeys.postsByUsername(username, page, pageSize)
        try {
            val cachedJson = redis.get(cacheKey)
            if (cachedJson != null) return jsonMapper.decodeFromString<List<Post>>(cachedJson)
            .filter { !it.isDeleted }

            val skip = (page - 1) * pageSize
            val dbPosts = postsCollection.find(Post::PostAuthor_username eq username , Post::isDeleted eq false )
                .descendingSort(Post::CreatedAt)
                .skip(skip)
                .limit(pageSize)
                .toList()

            if (dbPosts.isNotEmpty()) {
                redis.setex(cacheKey, POST_CACHE_TTL_SECONDS, jsonMapper.encodeToString(dbPosts))
            }
            return dbPosts
        } catch (e: Exception) {
            println("Error getting posts by username $username: ${e.message}")
            return emptyList()
        }
    }

    override suspend fun getFeedForUser(userId: String, page: Int, pageSize: Int): List<Post> {
        println("Feed generation for user $userId is conceptual. Returning user's own posts for now.")
        return getPostsByAuthorId(userId, page, pageSize)
    }


    private suspend fun handleLikeUnlike(postId: String, likingUserId: String, increment: Boolean): Post? {
        try {

            val updateValue = if (increment) 1 else -1
            val updateResult = postsCollection.updateOne(
                Post::Post_id eq postId,

                if (!increment) org.litote.kmongo.and(Post::likesCount gt 0) else org.litote.kmongo.EMPTY_BSON,
                inc(Post::likesCount, updateValue)
            )

            if (updateResult.modifiedCount > 0) {
                val updatedPost = postsCollection.findOne(Post::Post_id eq postId)
                if (updatedPost != null) {
                    redis.setex(PostCacheKeys.postById(postId), POST_CACHE_TTL_SECONDS, jsonMapper.encodeToString(updatedPost))

                    redis.del(PostCacheKeys.postsByAuthorId(updatedPost.PostAuthor_id, 1, 20))
                    redis.del(PostCacheKeys.postsByUsername(updatedPost.PostAuthor_username, 1, 20))
                }
                return updatedPost
            }

            return postsCollection.findOne(Post::Post_id eq postId)
        } catch (e: Exception) {
            println("Error liking/unliking post $postId: ${e.message}")
            return null
        }
    }

    override suspend fun likePost(postId: String, likingUserId: String): Post? {
        return handleLikeUnlike(postId, likingUserId, true)
    }

    override suspend fun unlikePost(postId: String, unlikingUserId: String): Post? {
        return handleLikeUnlike(postId, unlikingUserId, false)
    }

    override suspend fun getRepliesToPost(parentPostId: String, page: Int, pageSize: Int): List<Post> {
        val cacheKey = PostCacheKeys.repliesToPost(parentPostId, page, pageSize)
        try {
            val cachedJson = redis.get(cacheKey)
            if (cachedJson != null) return jsonMapper.decodeFromString<List<Post>>(cachedJson)
            .filter { !it.isDeleted }

            val skip = (page - 1) * pageSize
            val dbReplies = postsCollection.find(
                Post::parentPostId eq parentPostId,
                Post::Posttype eq PostType.REPLY
                , Post::isDeleted eq false
            )
                .descendingSort(Post::CreatedAt)
                .skip(skip)
                .limit(pageSize)
                .toList()

            if (dbReplies.isNotEmpty()) {
                redis.setex(cacheKey, POST_CACHE_TTL_SECONDS, jsonMapper.encodeToString(dbReplies))
            }
            return dbReplies
        } catch (e: Exception) {
            println("Error getting replies for post $parentPostId: ${e.message}")
            return emptyList()
        }
    }

    override suspend fun getRepostsOfPost(originalPostId: String, page: Int, pageSize: Int): List<Post> {
        val cacheKey = PostCacheKeys.repostsOfPost(originalPostId, page, pageSize)
        try {
            val cachedJson = redis.get(cacheKey)
            if (cachedJson != null) return jsonMapper.decodeFromString<List<Post>>(cachedJson)
             .filter { !it.isDeleted }

            val skip = (page - 1) * pageSize
            val dbReposts = postsCollection.find(
                Post::repostedPostId eq originalPostId,
                Post::Posttype eq PostType.REPOST
                , Post::isDeleted eq false
            )
                .descendingSort(Post::CreatedAt)
                .skip(skip)
                .limit(pageSize)
                .toList()

            if (dbReposts.isNotEmpty()) {
                redis.setex(cacheKey, POST_CACHE_TTL_SECONDS, jsonMapper.encodeToString(dbReposts))
            }
            return dbReposts
        } catch (e: Exception) {
            println("Error getting reposts for post $originalPostId: ${e.message}")
            return emptyList()
        }
    }


    override suspend fun reportPost(postId: String, reportingUserId: String): Post? {
        try {

            val updateResult = postsCollection.updateOne(
                Post::Post_id eq postId, Post::isDeleted eq false ,
                inc(Post::reportCount, 1)
            )
            if (updateResult.modifiedCount > 0) {
                val updatedPost = postsCollection.findOne(Post::Post_id eq postId)
                if (updatedPost != null) {
                    redis.setex(PostCacheKeys.postById(postId), POST_CACHE_TTL_SECONDS, jsonMapper.encodeToString(updatedPost))

                    redis.del(PostCacheKeys.postsByAuthorId(updatedPost.PostAuthor_id, 1, 20))
                    redis.del(PostCacheKeys.postsByUsername(updatedPost.PostAuthor_username, 1, 20))
                }
                return updatedPost
            }
            return postsCollection.findOne(Post::Post_id eq postId)
        } catch (e: Exception) {
            println("Error reporting post $postId: ${e.message}")
            return null
        }
    }
}