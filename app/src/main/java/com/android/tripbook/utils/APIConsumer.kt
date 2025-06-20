package com.android.tripbook.utils

import com.android.tripbook.data.RegisterBody
import com.android.tripbook.data.AuthResponse
import com.android.tripbook.data.LoginBody
import com.android.tripbook.data.UniqueEmailValidationResponse
import com.android.tripbook.data.ValidateEmailBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface APIConsumer {

    @POST("users/validate-unique-email")
    suspend fun validateEmailAdress(@Body body: ValidateEmailBody): Response<UniqueEmailValidationResponse>

    @POST("users/register")
    suspend fun registerUser(@Body body: RegisterBody): Response<AuthResponse>

    @POST("users/login")
    suspend fun loginUser(@Body body: LoginBody): Response<AuthResponse>

}