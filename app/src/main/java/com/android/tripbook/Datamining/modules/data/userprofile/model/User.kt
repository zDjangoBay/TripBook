package com.android.Tripbook.Datamining.modules.data.userprofile.model

import java.util.UUID

data class User(
    val User_id:String=UUID.randomUUID().toString(),
    val username:String?=null, // This is in the case someone wants a username different from the name he/she used to register himself/herself
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val profilePictureUri: String? = null,
    val birthDate: String = "",
    val favoriteDestinations: List<String> = emptyList(),
    val travelPreferences: List<String> = emptyList(),
    val bio: String = ""
)
