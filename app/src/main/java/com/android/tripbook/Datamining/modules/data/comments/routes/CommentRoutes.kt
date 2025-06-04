package com.android.Tripbook.Datamining.modules.data.comments.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
// Assuming CommentService and DTOs are in scope

fun Route.CommentRoutes(commentService: CommentService) {

    route("/comments") {

        // --- Create a new comment ---
        post {
            try {
                val request = call.receive<CreateCommentRequest>()
                // In a real app, User_id might come from authenticated principal
                val newComment = commentService.createComment(request)
                if (newComment != null) {
                    // Logic:
                    // 1. Create Comment object (generate ID, set PostedAt).
                    // 2. If Parent_Comment_id exists, increment repliesCount on parent comment in DB & cache.
                    // 3. Write newComment to Redis (e.g., "comment:{Comment_Id}" -> JSON).
                    // 4. Write newComment to MongoDB.
                    // 5. Potentially update cached lists (e.g., comments for Post_id).
                    call.respond(HttpStatusCode.Created, newComment)
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Failed to create comment")
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Invalid comment data: ${e.message}")
            }
        }

        // --- Retrieve a specific comment by its ID ---
        get("/{comment_id}") {
            val commentId = call.parameters["comment_id"]
            if (commentId == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing comment_id")
                return@get
            }
            val comment = commentService.getCommentById(commentId)
            // Logic:
            // 1. Check Redis for "comment:{comment_id}".
            // 2. If found and not soft-deleted, return.
            // 3. If not in Redis, fetch from MongoDB.
            // 4. If found in MongoDB and not soft-deleted, store in Redis and return.
            // 5. If soft-deleted, might return a specific status or a "deleted" representation.
            if (comment != null && !comment.isDeleted) { //
                call.respond(HttpStatusCode.OK, comment)
            } else {
                call.respond(HttpStatusCode.NotFound, "Comment not found or has been deleted")
            }
        }

        // --- Retrieve comments for a specific Post ---
        // Example: GET /comments/post/some_post_id?page=1&size=10
        get("/post/{post_id}") {
            val postId = call.parameters["post_id"]
            val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
            val pageSize = call.request.queryParameters["size"]?.toIntOrNull() ?: 20

            if (postId == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing post_id")
                return@get
            }
            val comments = commentService.getCommentsByPostId(postId, page, pageSize)
            // Logic:
            // 1. Construct a cache key (e.g., "post:{post_id}:comments:page:{page}:size:{pageSize}").
            // 2. Check Redis for this key (could store list of IDs or full objects).
            // 3. If found, fetch full comments from Redis/DB if only IDs are stored, then return.
            // 4. If not in Redis, fetch paginated list from MongoDB (filtering out comment.isDeleted = true).
            // 5. Store the list (or IDs) in Redis with an appropriate TTL.
            // 6. Return the list.
            call.respond(HttpStatusCode.OK, comments.filter { !it.isDeleted }) //
        }

        // --- Retrieve (non-deleted) comments by a specific User ---
        // Example: GET /comments/user/some_user_id?page=1&size=10
        get("/user/{user_id}") {
            val userId = call.parameters["user_id"]
            val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
            val pageSize = call.request.queryParameters["size"]?.toIntOrNull() ?: 20

            if (userId == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing user_id")
                return@get
            }
            val comments = commentService.getCommentsByUserId(userId, page, pageSize)
            // Caching logic similar to /post/{post_id}, key like "user:{user_id}:comments:page..."
            call.respond(HttpStatusCode.OK, comments.filter { !it.isDeleted }) //
        }

        // --- Retrieve (non-deleted) replies to a specific Parent Comment ---
        // Example: GET /comments/parent_comment_123/replies?page=1&size=5
        get("/{parent_comment_id}/replies") {
            val parentCommentId = call.parameters["parent_comment_id"]
            val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
            val pageSize = call.request.queryParameters["size"]?.toIntOrNull() ?: 5

            if (parentCommentId == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing parent_comment_id")
                return@get
            }
            val replies = commentService.getRepliesForComment(parentCommentId, page, pageSize)
            // Caching logic similar, key like "comment:{parent_comment_id}:replies:page..."
            call.respond(HttpStatusCode.OK, replies.filter { !it.isDeleted }) //
        }

        // --- Update an existing comment (e.g., its text value) ---
        put("/{comment_id}") {
            val commentId = call.parameters["comment_id"]
            // val currentUserId = ... // Get from authenticated principal
            if (commentId == null /*|| currentUserId == null*/) {
                call.respond(HttpStatusCode.BadRequest, "Missing comment_id or authentication")
                return@put
            }
            try {
                val request = call.receive<UpdateCommentRequest>()
                // Pass currentUserId to service to verify ownership before update
                val updatedComment = commentService.updateComment(commentId, "TODO_Authenticated_User_Id", request)
                if (updatedComment != null) {
                    // Logic:
                    // 1. Service verifies user owns the comment.
                    // 2. Update comment in MongoDB.
                    // 3. Update/invalidate "comment:{comment_id}" in Redis.
                    // 4. Potentially invalidate cached lists containing this comment.
                    call.respond(HttpStatusCode.OK, updatedComment)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Comment not found or update failed (e.g. permission denied)")
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Invalid update data: ${e.message}")
            }
        }

        // --- Soft delete a comment ---
        delete("/{comment_id}") {
            val commentId = call.parameters["comment_id"]
            // val currentUserId = ... // Get from authenticated principal
            if (commentId == null /*|| currentUserId == null*/) {
                call.respond(HttpStatusCode.BadRequest, "Missing comment_id or authentication")
                return@delete
            }
            // Pass currentUserId to service to verify ownership
            val success = commentService.deleteComment(commentId, "TODO_Authenticated_User_Id")
            if (success) {
                // Logic:
                // 1. Service verifies user owns the comment.
                // 2. Set isDeleted = true in MongoDB.
                // 3. Update "comment:{comment_id}" in Redis (reflecting isDeleted=true) or remove it.
                // 4. Potentially invalidate/update cached lists.
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.NotFound, "Comment not found or delete failed (e.g. permission denied)")
            }
        }

        // --- Like a comment ---
        post("/{comment_id}/like") {
            val commentId = call.parameters["comment_id"]
            // val likingUserId = ... // Get from authenticated principal
            if (commentId == null /*|| likingUserId == null*/) {
                call.respond(HttpStatusCode.BadRequest, "Missing comment_id or authentication")
                return@post
            }

            val likedComment = commentService.likeComment(commentId, "TODO_Authenticated_Liking_User_Id")
            if (likedComment != null) {
                // Logic:
                // 1. Increment likesCount in MongoDB for the comment.
                // 2. Update "comment:{comment_id}" in Redis with new likesCount.
                // Optional: Store user's like to prevent double-liking.
                call.respond(HttpStatusCode.OK, likedComment)
            } else {
                call.respond(HttpStatusCode.NotFound, "Comment not found or like failed")
            }
        }
    }
}