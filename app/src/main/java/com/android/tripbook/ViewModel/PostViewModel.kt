package com.android.tripbook.ViewModel


import androidx.lifecycle.ViewModel
import com.android.tripbook.ui.theme.state.PostUIState
import com.android.tripbook.ui.theme.events.PostEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class PostViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PostUIState())
    val uiState: StateFlow<PostUIState> = _uiState

    fun onEvent(event: PostEvent) {
        when (event) {
            is PostEvent.TitleChanged -> _uiState.update { it.copy(title = event.title) }
            is PostEvent.DescriptionChanged -> _uiState.update { it.copy(description = event.description) }
            is PostEvent.ImageAdded -> _uiState.update { it.copy(images = it.images + event.uri) }
            is PostEvent.ImageRemoved -> _uiState.update { it.copy(images = it.images - event.uri) }
            is PostEvent.LocationChanged -> _uiState.update { it.copy(location = event.location) }
            is PostEvent.TagsChanged -> _uiState.update { it.copy(tags = event.tags) }
            is PostEvent.SubmitPost -> {
                // Submission logic can be added later (validation, repository call, etc.)
            }
        }
    }
}

