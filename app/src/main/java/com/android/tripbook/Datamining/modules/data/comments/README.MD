# Datamining - Comments Module

<p>This document provides a comprehensive overview of the Comment Module's interaction with the datamining module .  The module is designed to manage comments, including their creation, retrieval, update, deletion, and interaction features like likes and replies, with a robust caching layer using Redis and persistent storage via MongoDB.</p>


## Table of Content 

1.[Module Overview](#module-overview)
- [Key Functionalities](#key-functionalities)
- [Technologies Used](#technology-used)

2.[Data Models](#data-models)
- [Comment](###Comment)
- [CreateCommentRequest](#Create-comment-request)
- [UpdateCommentRequest](#update-comment-request)

3.[Service Layer](#service-layer)
- [CommentService(Interface)](#comment-service)
- [CommentServiceImpl](#comment-service-impl)

4.[API Routes](#api-routes)
- [`POST /comments`](#post-comment)
- [`GET /comments/{comment_id`](#get-comment-comment-id)
- [`GET /comments/post/{post_id}`](#get-comments-posts-post-id)
- [`GET /comments/user/{user_id}`](#get-comments-user-user-id)
- [`GET /comments/{parent_comment_id}/replies`](#get-comment-parent-id-replies)
- [`PUT /comments/{comment_id}`](#put-comment-comment-id)
- [`DELETE /comments/{comment_id}`](#delete-comments-comment-id)
- [`POST /comments/{comment_id}/like`](#post-comments-comment-id-like)



---

## Module Overview

The Comment Module is a core component responsible for handling all comment-related operations within the TripBook application. It enables users to engage with posts and other comments by providing a threaded commenting system somehow similar to X(Twitter).

#### Key Functionalities

* **C**reate, **R**ead, **U**pdate, **D**elete **(CRUD)** operations for comments.
* Support for threaded comments (replies to other comments).
* Like and unlike functionality for comments.
* Efficient data retrieval through caching with Redis.
* Persistent storage of comment data in MongoDB.
* Soft deletion of comments, retaining data while marking it as inactive.


### Technologies Used

* `Ktor`: Framework for building the server and API routes.
* `MongoDB`: NoSQL database for persistent storage of comments.
* `Redis`: In-memory data store for caching comments and related data to improve performance.
* `Kotlinx Serialization`: For serializing and deserializing Kotlin objects to/from JSON.


## Data Models

The module utilizes several data classes to represent comments and related request payloads.

### Comment 

This data class defines the structure of a comment object as stored in the database and used within the application.

```kotlin
@Serializable
data class Comment(
    val Comment_Id: String = UUID.randomUUID().toString(),
    val Post_id: String, // The ID of the post the comment belongs to
    val User_id: String, // The ID of the user who made the comment
    @Contextual // Requires a custom serializer for kotlinx.datetime.LocalDateTime
    val PostedAt: LocalDateTime, // Timestamp of when the comment was created
    val value: String, // The actual text content of the comment
    val Parent_Comment_id: String? = null, // ID of the parent comment if this is a reply
    val likesCount: Int = 0, // Number of likes the comment has received
    val repliesCount: Int = 0, // Number of direct replies to this comment
    val isDeleted: Boolean = false, // Flag for soft deletion
)
```


### Create Comment Request

This is a DTO (Data Transfer Object). It  is used to represent the payload when a client requests to create a new comment.

```kotlin
@kotlinx.serialization.Serializable
data class CreateCommentRequest(
    val Post_id: String,
    val User_id: String, // Authentication will be handled via Kotlin JWT
    val value: String,
    val Parent_Comment_id: String? = null
)
```



### Update Comment Request 

This DTO is used for requests to update an existing comment's content.

```kotlin
@kotlinx.serialization.Serializable
data class UpdateCommentRequest(
    val value: String
)
```

## Service Layer 

The service layer encapsulates the business logic for managing comments, interacting with the database, and handling caching.


### Comment Service (Interface)

This interface defines the contract for comment-related operations, abstracting the implementation details. 

```kotlin
interface CommentService {
    suspend fun createComment(request: CreateCommentRequest): Comment?
    suspend fun getCommentById(commentId: String): Comment?
    suspend fun getCommentsByPostId(postId: String, page: Int = 1, pageSize: Int = 20): List<Comment>
    suspend fun getCommentsByUserId(userId: String, page: Int = 1, pageSize: Int = 20): List<Comment>
    suspend fun getRepliesForComment(parentCommentId: String, page: Int = 1, pageSize: Int = 20): List<Comment>
    suspend fun updateComment(commentId: String, userId: String, request: UpdateCommentRequest): Comment? 
    suspend fun deleteComment(commentId: String, userId: String): Boolean
    suspend fun likeComment(commentId: String, likingUserId: String): Comment?
    
}
```


### Comment Service Impl (Implementation)

This class provides the concrete implementation of the CommentService interface. It handles the logic for interacting with MongoDB for data persistence and Redis for caching.

* **Dependencies:**

  - `CoroutineDatabase (MongoDB)`: An instance of the KMongo coroutine database for asynchronous database operations.
  - `Jedis (Redis)`: An instance of the Jedis client for interacting with the Redis cache.
  - `Json (Kotlinx Serialization)`: Used for serializing and deserializing Comment objects to/from JSON strings for storage in Redis.


* **MongoDB Collection:**

  - Comments are stored in a MongoDB collection named `comments`.



* **Caching Strategy:**

  - Cache Keys: Defined in the CommentCacheKeys object to ensure consistent key naming. Keys are typically namespaced (e.g., comment:<id>, post:<id>:comments:...).
  - Time-To-Live (TTL): A default TTL (CACHE_TTL_SECONDS) of 3600 seconds (1 hour) is used for cache entries.
    - Read Operations (e.g., getCommentById, getCommentsByPostId):
      - Attempt to fetch data from Redis using the appropriate cache key.
      - If a cache hit occurs and the data is valid (e.g., not soft-deleted), deserialize and return it.
      - If a cache miss occurs, fetch the data from MongoDB.
      - If data is found in MongoDB, serialize it to JSON and store it in Redis with the defined TTL before returning.
      - Soft-deleted comments are generally filtered out or handled appropriately.



* **Write Operations (createComment, updateComment, deleteComment, likeComment, unlikeComment):**
      
  1. Perform the primary operation in MongoDB.
  2. Update or invalidate relevant cache entries in Redis:
     - For individual comment changes, the specific comment:<id> cache entry is updated or deleted.
     - For operations affecting lists (e.g., new comment, deletion), relevant list caches are typically invalidated (e.g., by deleting the cache entry for the first page of a list) to ensure fresh data on subsequent reads. More sophisticated list cache management may be employed for higher efficiency.
     - When a reply is added or deleted, the repliesCount on the parent comment is updated in both MongoDB and its corresponding Redis cache entry is invalidated.


* **Detailed Method Descriptions (Highlights):**


  * `createComment(request: CreateCommentRequest)`:

    - Generates a new `Comment_Id` and sets `PostedAt`.
    - If `Parent_Comment_id` is provided, increments repliesCount on the parent comment in MongoDB and invalidates the parent's cache entry in Redis.
    - Inserts the new Comment into MongoDB.
    - If successful, stores the new Comment as a JSON string in Redis.
    - Invalidates the cache for the first page of comments for the associated `Post_id` to reflect the new addition.


* `getCommentById(commentId: String)`:

  - Checks Redis for comment:<commentId>.
  - If not found or if the comment is marked isDeleted in cache, fetches from MongoDB.
  - If found in MongoDB and not deleted, caches it in Redis.
  - Returns the comment only if it's not soft-deleted.


* `getCommentsByPostId(postId: String, page: Int, pageSize: Int) (and similar list retrieval methods)`:

  - Uses a cache key incorporating postId, page, and pageSize.
  - On cache miss, queries MongoDB for non-deleted comments, applying pagination and sorting (e.g., by PostedAt descending).
  - Caches the retrieved list as a JSON string in Redis.
  
* `updateComment(commentId: String, userId: String, request: UpdateCommentRequest)`:

    - Verifies that the userId owns the comment and that the comment is not deleted before proceeding.
    - Updates the value and an implicit UpdatedAt timestamp (note: UpdatedAt needs to be added to Comment.kt) in MongoDB.
    - Fetches the updated comment from MongoDB and updates the comment:<commentId> entry in Redis.
    - Invalidates caches for lists that might contain this comment (e.g., first page of comments for the post and by the user).



* `deleteComment(commentId: String, userId: String)`:

    - Verifies ownership and that the comment is not already deleted.
    - Sets isDeleted = true and an implicit DeletedAt timestamp (note: DeletedAt needs to be added to Comment.kt) in MongoDB.
    - Deletes the comment:<commentId> entry from Redis.
    - If the deleted comment was a reply, decrements repliesCount on the parent comment in MongoDB and invalidates the parent's cache.
    - Invalidates various list caches that might have included this comment.



* `likeComment(commentId: String, likingUserId: String)`:

    - Increments likesCount for the specified non-deleted comment in MongoDB.
    - Fetches the updated comment and updates its entry in Redis.
    - Invalidates the first page of the post's comment list cache.


## API Routes 

- `POST /comments` -> `commentService.createComment(...)`
- `GET /comments/{comment_id}` ->` commentService.getCommentById(...)`
- `GET /comments/post/{post_id}` -> `commentService.getCommentsByPostId(...)`
- `GET /comments/user/{user_id}` -> `commentService.getCommentsByUserId(...)`
- `GET /comments/{parent_comment_id}/replies` -> `commentService.getRepliesForComment(...)`
- `PUT /comments/{comment_id}` -> `commentService.updateComment(...)`
- `DELETE /comments/{comment_id}` -> `commentService.deleteComment(...)`
- `POST /comments/{comment_id}/like` -> `commentService.likeComment(...)`
- `POST /comments/{comment_id}/unlike` -> `commentService.unlikeComment(...)`


