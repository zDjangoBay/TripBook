package com.android.tripbook.repository

import com.android.tripbook.data.LoginBody
import com.android.tripbook.data.RegisterBody
import com.android.tripbook.data.ValidateEmailBody
import com.android.tripbook.utils.APIConsumer
import com.android.tripbook.utils.RequestStatus
import com.android.tripbook.utils.SimplifiedMessage
import kotlinx.coroutines.flow.flow

class AuthRepository(private val consumer: APIConsumer) {
    fun validateEmailAddress(body: ValidateEmailBody) = flow {
        emit(RequestStatus.Waiting)
        val response = consumer.validateEmailAdress(body)
        if (response.isSuccessful) {

            emit((RequestStatus.Success(response.body()!!)))
        } else {
            emit(
                RequestStatus.Error(
                    SimplifiedMessage.get(
                        response.errorBody()!!.byteStream().reader().readText()
                    )
                )
            )
        }


    }

    fun registerUser(body: RegisterBody) = flow {
        emit(RequestStatus.Waiting)
        val response = consumer.registerUser(body)
        if (response.isSuccessful) {

            emit((RequestStatus.Success(response.body()!!)))
        } else {
            emit(
                RequestStatus.Error(
                    SimplifiedMessage.get(
                        response.errorBody()!!.byteStream().reader().readText()
                    )
                )
            )
        }
    }

    fun loginUser(body: LoginBody) = flow {
        emit(RequestStatus.Waiting)
        val response = consumer.loginUser(body)
        if (response.isSuccessful) {

            emit((RequestStatus.Success(response.body()!!)))
        } else {
            emit(
                RequestStatus.Error(
                    SimplifiedMessage.get(
                        response.errorBody()!!.byteStream().reader().readText()
                    )
                )
            )
        }
    }


}