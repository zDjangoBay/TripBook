package com.tripbook.posts.ui.event

sealed class PostUiEvent {
    data object SubmitPost : PostUiEvent()
    data class Error(val message: String) : PostUiEvent()
    data object Success : PostUiEvent()
}


