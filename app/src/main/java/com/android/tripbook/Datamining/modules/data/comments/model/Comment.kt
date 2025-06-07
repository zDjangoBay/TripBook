package com.android.comments.model

import com.android.comments.serialization.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.UUID


/*
*
* This class represent the skeleton of a comment in our database
* */
@Serializable
data class Comment(
    val Comment_Id: String = UUID.randomUUID().toString(),
    val Post_id: String, // The id of the post under which was done the comment , it will also be an uuid by the way
    val User_id: String, // the identifiant of the one who commented

    @Serializable(with = LocalDateTimeSerializer::class)
    val PostedAt: LocalDateTime, // The date at which the post was published

    val value: String, // This is the actual comment
    val Parent_Comment_id: String?, // In the case the comment is a response to another comment

    val likesCount: Int = 0, // This will help display the number of likes under a particular comment
    val repliesCount: Int = 0, // This is as the variable name implies the number of replies one received under his/her comment
    val isDeleted: Boolean = false, // This right here will help to know if the actual comment on the ui should be erased , but we are going to have the comment in the database
)






