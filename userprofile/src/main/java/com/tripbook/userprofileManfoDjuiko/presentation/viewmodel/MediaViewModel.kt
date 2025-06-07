package com.tripbook.userprofileManfoDjuiko.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.tripbook.userprofileManfoDjuiko.data.model.MediaItem
import com.tripbook.userprofileManfoDjuiko.data.model.MediaType
import com.tripbook.userprofileManfoDjuiko.data.repository.MediaRepository

data class MediaUiState(
    val images: List<MediaItem> = emptyList(),
    val videos: List<MediaItem> = emptyList(),
    val selectedItems: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentTab: MediaType = MediaType.IMAGE
)

class MediaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MediaRepository(application)

    private val _uiState = MutableStateFlow(MediaUiState())
    val uiState: StateFlow<MediaUiState> = _uiState.asStateFlow()

    init {
        loadMedia()
    }

    fun loadMedia() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val images = repository.getImages()
                val videos = repository.getVideos()

                _uiState.value = _uiState.value.copy(
                    images = images,
                    videos = videos,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun selectTab(tab: MediaType) {
        _uiState.value = _uiState.value.copy(currentTab = tab)
    }

    fun toggleItemSelection(itemId: String) {
        val currentSelection = _uiState.value.selectedItems
        val newSelection = if (currentSelection.contains(itemId)) {
            currentSelection - itemId
        } else {
            currentSelection + itemId
        }
        _uiState.value = _uiState.value.copy(selectedItems = newSelection)
    }

    fun clearSelection() {
        _uiState.value = _uiState.value.copy(selectedItems = emptySet())
    }

    fun addMediaItems(uris: Any) {

    }
}
