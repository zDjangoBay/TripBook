# Datamining - Posts Module

<p>This document provides a comprehensive overview of the Posts Module. This module is central to content creation and interaction within the TripBook application, allowing users to share text, media, create threads (replies), and repost content. It features a robust caching layer using Redis and persistent storage via MongoDB.</p>

## Table of Contents

1.  [Module Overview](#module-overview)
    * [Key Functionalities](#key-functionalities)
    * [Technologies Used](#technologies-used)
2.  [Data Models](#data-models)
    * [Post.kt](#postkt)
    * [PostType.kt](#posttypekt)
    * [PostVisibility.kt](#postvisibilitykt)
    * [CreatePostRequest DTO](#createpostrequest-dto)
   
3.  [Service Layer](#service-layer)
    * [PostServiceImpl (Implementation)](#postserviceimpl-implementation)
        * [Dependencies](#dependencies)
        * [MongoDB Collection](#mongodb-collection)
        * [Caching Strategy](#caching-strategy)
        * [Method Descriptions (Highlights)](#method-descriptions-highlights)
4.  [API Routes](#api-routes)

## 1. Module Overview

The Posts Module enables users to create, share, and interact with content. It supports various post types, including original posts, replies, and reposts, forming the backbone of user-generated content within the application.

### Key Functionalities

* **Post Creation:** Allows users to create posts with text, media, location, hashtags, and mentions. Supports different post types: `ORIGINAL`, `REPLY`, `REPOST`.
* **Post Retrieval:** Provides mechanisms to fetch individual posts or lists of posts (e.g., by author, username, replies to a post, reposts of a post).
* **Post Update:** Allows authors to update the content of their posts.
* **Post Deletion:** Allows authors to delete their posts (currently implemented as a hard delete).
* **Interactions:** Supports liking/unliking posts and reporting posts. Counters for likes, replies, reposts, and reports are maintained.
* **Caching:** Utilizes Redis for caching frequently accessed post data to improve performance.
* **Persistence:** Stores post data persistently in MongoDB.

### Technologies Used

* **Kotlin:** Primary programming language.
* **Ktor:** Framework for building the server and API routes.
* **MongoDB:** NoSQL database for persistent storage of posts.
* **Redis:** In-memory data store for caching post data.
* **Kotlinx Serialization:** For serializing and deserializing Kotlin objects to/from JSON.

## 2. Data Models


### `Post.kt`

This data class give the structure of a post in our database . 

```kotlin
@Serializable
data class Post(
    val Post_id: String = UUID.randomUUID().toString(),
    val PostAuthor_id: String,
    val PostAuthor_username: String,
    val PostAuthorProfileUrl: String?,
    val value: String?, // Texte du post (peut être null si media-only)
    val Post_mediasUrl: List<String> = emptyList(),
    val Localisation: String?,
    val Posttype: PostType = PostType.ORIGINAL,
    val parentPostId: String? = null, // ID du post parent si c'est une réponse
    val repostedPostId: String? = null, // ID du post original si c'est un repost
    var likesCount: Int = 0, // Mutable pour être incrémenté/décrémenté
    var repostCount: Int = 0, // Mutable
    var repliesCount: Int = 0, // Mutable
    val Hashtags: List<String> = emptyList(), // Corrigé de Hastags
    val mention: List<String> = emptyList(),
    var reportCount: Int = 0, // Mutable
    val CreatedAt: LocalDateTime,
    val visibility: PostVisibility = PostVisibility.PUBLIC,
    var isDeleted: Boolean = false,

)
```

### `PostType.kt`

This enumearation helps identify whether the post is an original post , a reply or a repost . The purpose of it is to have threaded posts

```kotlin
@Serializable
enum class PostType {// Here i want to make a distinction between the original post and a reply post and even a repost
    ORIGINAL,
    REPLY,
    REPOST
}
```

### `PostVisibility.kt`

```kotlin
@Serializable
enum class PostVisibility {
    PUBLIC,
    FOLLOWERS_ONLY
}
```

### `CreatePostRequest(DTO)`

This is a data transfer object that represent the payload a sent from the client to create a post record

```kotlin
@Serializable
data class CreatePostRequest(
    val PostAuthor_id:String,
    val PostAuthor_username:String?,
    val value: String?,
    val Post_mediasUrl: List<String>? = emptyList(),
    val Localisation: String?,
    val Posttype: PostType = PostType.ORIGINAL,// We assume by default that
    val parentPostId: String? = null, // Requis si Posttype est REPLY
    val repostedPostId: String? = null, // Requis si Posttype est REPOST
    val Hashtags: List<String>? = emptyList(),
    val mention: List<String>? = emptyList(),
    val visibility: PostVisibility = PostVisibility.PUBLIC
)
```


## Service Layer 
### `PostServiceImpl` (Implementation)

Implements `PostService` using MongoDB for storage and Redis for caching.

* **Dependencies:** MongoDB client (e.g., KMongo's `CoroutineDatabase`), Redis client (e.g., `Jedis`), JSON serializer (e.g., Kotlinx Serialization's `Json`).
* **MongoDB Collection:** Post data is stored in a collection named `"posts"`.
* **Caching Strategy:**
    * **Keys:** Managed by `PostCacheKeys` object (e.g., `post:<id>`, `posts:author:<id>:page:<p>:size:<s>`).
    * **TTL (Time-To-Live):** A default `POST_CACHE_TTL_SECONDS` (e.g., 30 minutes) is applied to cache entries.
    * **Read Operations:**
        1.  Attempt to retrieve data from Redis.
        2.  On cache hit, return deserialized data (filtering for `isDeleted eq false` if soft delete is active).
        3.  On cache miss, query MongoDB (filtering for `isDeleted eq false`).
        4.  Cache the MongoDB result in Redis before returning.
    * **Write Operations:**
        1.  Perform the primary operation in MongoDB.
        2.  Update or invalidate relevant Redis cache entries (e.g., individual post entries and potentially related list caches like the first page of an author's posts).
        3.  When creating replies/reposts or (un)liking, the cache for the affected parent/original post is also invalidated/updated to reflect new counter values.
* **Method Descriptions (Highlights):**
    * **`createPost(...)`**:
        
    * **`getPostById(postId: String)`**:
       
    * **`updatePost(postId: String, authorId: String, request: UpdatePostRequest)`**:
        
    * **`deletePost(postId: String, authorId: String)`**:
        
    * **`getPostsByAuthorId(...)` & `getPostsByAuthorUsername(...)`**:
        
    * **`likePost(...)` & `unlikePost(...)`**:
        
    * **`getRepliesToPost(...)` & `getRepostsOfPost(...)`**:
        
    * **`reportPost(...)`**:
       






## API Routes

The service methods are exposed via Ktor in `PostRoutes.kt`.

| Method | Route                          | Description                                         | Service Call                  |
| :----- | :----------------------------- | :-------------------------------------------------- | :---------------------------- |
| POST   | `/posts`                       | Create post                                         | `createPost()`                |
| GET    | `/posts/{post_id}`             | Get post by ID                                      | `getPostById()`               |
| GET    | `/posts/user/{user_id}`        | Get posts by author ID (paginated)                  | `getPostsByAuthorId()`        |
| GET    | `/posts/username/{username}`   | Get posts by author username (paginated)            | `getPostsByAuthorUsername()`  |
| PUT    | `/posts/{post_id}`             | Update post                                         | `updatePost()`                |
| DELETE | `/posts/{post_id}`             | Delete post                                         | `deletePost()`                |
| POST   | `/posts/{post_id}/like`        | Like post                                           | `likePost()`                  |
| DELETE | `/posts/{post_id}/like`        | Unlike post                                         | `unlikePost()`                |
| GET    | `/posts/{post_id}/replies`     | Get replies to post (paginated)                     | `getRepliesToPost()`          |
| GET    | `/posts/{post_id}/reposts`     | Get reposts of post (paginated)                     | `getRepostsOfPost()`          |
| POST   | `/posts/{post_id}/report`      | Report post                                         | `reportPost()`                |
| GET    | `/posts/feed`                  | Get user's feed (paginated, conceptual)             | `getFeedForUser()`            |
