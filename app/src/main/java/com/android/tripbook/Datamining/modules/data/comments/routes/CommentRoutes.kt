package com.android.comments.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Route.CommentRoutes(commentService: CommentService) {

    route("/comments") {


        /*
        * The algorithm of this request is :
        *  First it will create a comment object
        * After creating the object , if there is a parent_comment_id , it will go increase by 1 the number of repliesCount on the said parent_comment
        * Then the newly created comment object is then write in the cache -- So that the reading might be really fast
        * After beeing written in the cache the created comment will be stored in the Mongodb database
        * Then it update the Cached List -- all of this implementation has been done in the CommentServiceImpl
        * */
        post {
            try {
                val request = call.receive<CreateCommentRequest>()
                val newComment = commentService.createComment(request)
                if (newComment != null) {

                    call.respond(HttpStatusCode.Created, newComment)

                } else {

                    call.respond(HttpStatusCode.InternalServerError, "Failed to create comment")

                }
            } catch (e: Exception) {

                call.respond(HttpStatusCode.BadRequest, "Invalid comment data: ${e.message}")
            }
        }


        /*
        *
        * This route helps find a comment using its comment_id, its algorithm is as follow  :
        *   It will first go check the cache , and if found in the cache ensure that the isDeleted field of the comment is false meaning it is not considered deleted , it will be sent back
        *   If the comment is not the cache , then go and check the database and fetch the comment if isDeleted is false otherwise a not found is returned
        * */
        get("/{comment_id}") {
            val commentId = call.parameters["comment_id"]
            if (commentId == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing comment_id")
                return@get
            }

            val comment = commentService.getCommentById(commentId)
            if (comment != null && !comment.isDeleted) { //

                call.respond(HttpStatusCode.OK, comment)
            } else {

                    call.respond(HttpStatusCode.NotFound, "Comment not found or has been deleted")
            }
        }

        /*
        *This route is for retrieving the differents comments under a particuliar post , its algorithm goes as follow :
        *   First construct the cache key , then check redis for this particular key , if there , we will next use these id later
        *   Then if found it will fetch all the comments using the comments ids from redis , if it was not found it will go find the comments directly in the db ,
        *   Then store the list of id in the cache
        * */
        get("/post/{post_id}") {

            val postId = call.parameters["post_id"]

            val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1

            val pageSize = call.request.queryParameters["size"]?.toIntOrNull() ?: 20

            if (postId == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing post_id")
                return@get
            }
            val comments = commentService.getCommentsByPostId(postId, page, pageSize)


            call.respond(HttpStatusCode.OK, comments.filter { !it.isDeleted }) //
        }

        /*
        *By now you must have understood the whole mechanism of fetching from the cache then from the database ,
        * This route helps find all the comments of a particular user
        * */
        get("/user/{user_id}") {
            val userId = call.parameters["user_id"]

             val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1

                val pageSize = call.request.queryParameters["size"]?.toIntOrNull() ?: 20

            if (userId == null) {//propre n'est ce pas ?  😂

                call.respond(HttpStatusCode.BadRequest, "Missing user_id")

                return@get
            }

            val comments = commentService.getCommentsByUserId(userId, page, pageSize)

            call.respond(HttpStatusCode.OK, comments.filter { !it.isDeleted }) //
        }

        /*
        * This route is to find all the replies under a parent comment , to be displayed in the application , as done by every social media
        * */
        get("/{parent_comment_id}/replies") {
            val parentCommentId = call.parameters["parent_comment_id"]

            val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1

                val pageSize = call.request.queryParameters["size"]?.toIntOrNull() ?: 5

            if (parentCommentId == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing parent_comment_id")


                return@get
            }
            val replies = commentService.getRepliesForComment(parentCommentId, page, pageSize)

            call.respond(HttpStatusCode.OK, replies.filter { !it.isDeleted }) //
        }

        /*
        * Since all of the moderns social media kind of platforms allows people to modify their comment , it could be nice having it in the TripBook application , the algorithm is as  follow
        *   First it check if the the specified user is the proprietary of that comment ,
        *   Then it willl update the  comment in Mongodb
        *   After that it also update the coment in redis and invalidate the cachedLIst that contains that comment
        * */
        put("/{comment_id}") {
            val commentId = call.parameters["comment_id"]

            val currentUserId = call.parameters["User_id"]
            if (commentId == null || currentUserId == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing comment_id or Missing User_id")
                return@put
            }
            try {
                val request = call.receive<UpdateCommentRequest>()

                val updatedComment = commentService.updateComment(commentId,currentUserId, request)
                if (updatedComment != null) {

                    call.respond(HttpStatusCode.OK, updatedComment)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Comment not found or update failed ")
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Invalid update data: ${e.message}")
            }
        }


        delete("/{comment_id}") {

            val commentId = call.parameters["comment_id"]

            val currentUserId = call.parameter["User_id"]
            if (commentId == null || currentUserId == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing comment_id or authentication")
                return@delete
            }

            val success = commentService.deleteComment(commentId, currentUserId)

            if (success) {

                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.NotFound, "Comment not found or delete failed (e.g. permission denied)")
            }
        }

        /*
        *   This route is for the increaseing of likes number of a comment , we will of course do the same for posts
        *
        * */
        post("/{comment_id}/like") {
            val commentId = call.parameters["comment_id"]

                    val likingUserId = call.parameters["User_id"]

            if (commentId == null || likingUserId == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing comment_id or User_id")
                  return@post
            }

            val likedComment = commentService.likeComment(commentId,likingUserId )

            if (likedComment != null) {

                call.respond(HttpStatusCode.OK, likedComment)
            } else {

                call.respond(HttpStatusCode.NotFound, "Comment not found or like failed")
            }

        }
    }
}
