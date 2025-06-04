package com.android.Tripbook.Datamining.modules.data.posts.model

import java.time.LocalDateTime
import java.util.UUID

data class Post(
    val Post_id:String=UUID.randomUUID().toString(),
    val PostAuthor_id:String,// It is going to be an uuid string , and will hold the id of the user who obvioulsy created it
    val PostAuthor_username:String,// The obvious username of the one who created the post
    val PostAuthorProfileUrl:String?,

    val value:String?, // The text of the post , as the post can only contain images and that is it
    val Post_mediasUrl:List<String> = emptyList<String>(),// This is going to be the link of all the medias used in the post
    val Localisation:String?,// The localisation of the user when doing the post or if he/she wants to mention a particular place

    val Posttype: PostType = PostType.ORIGINAL, // should i say by default , it is going to be an Original post
    val parentPostId:String?=null, // If this post was a reply of another post
    val reportedPostId:String?=null, // This is in the case the this post is the repost of another post .


    val likesCount:Int=0,// This corresponds to the number of likes of the post
    val repostCount:Int=0, // This refers to the number of repost of this. post
    val repliesCount:Int=0,// This refers to the number of replies under a particular post

    val Hastags:List<String> = emptyList<String>(),// This corresponds to the list of hashtags of a post
    val mention:List<String> = emptyList<String>(), // This corresponds to the list of username that was mentioned in the post


    val reportCount:Int=0,//In the case where the post shared false informations , it can be reported
    val CreatedAt: LocalDateTime,// The post creation date
)
