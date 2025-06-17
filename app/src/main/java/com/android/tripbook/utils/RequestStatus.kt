package com.android.tripbook.utils

sealed class RequestStatus<out T> {

    object Waiting : RequestStatus<Nothing>()
    data class Success<out T>(val data: T) : RequestStatus<T>()
    data class Error(val message: HashMap<String, String>) : RequestStatus<Nothing>()


}