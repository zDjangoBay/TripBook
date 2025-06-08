package com.tripbook.userprofileManfoDjuiko.presentation.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
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

    fun shareSelectedMedia(context: Context, launcher: ActivityResultLauncher<Intent>) {
        val selectedMediaUris = _uiState.value.selectedItems.mapNotNull { itemId ->
            (_uiState.value.images + _uiState.value.videos).find { it.id == itemId }?.uri
        }

        if (selectedMediaUris.isNotEmpty()) {
            val shareIntent: Intent = when {
                selectedMediaUris.size == 1 -> {
                    // Share a single item
                    Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_STREAM, selectedMediaUris.first())
                        type = getMimeType(context, selectedMediaUris.first()) ?: "*/*" // Determine MIME type
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                }
                else -> {
                    // Share multiple items
                    Intent().apply {
                        action = Intent.ACTION_SEND_MULTIPLE
                        putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(selectedMediaUris))
                        type = "*/*" // Multiple types, so use a general type
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                }
            }
            val chooserIntent = Intent.createChooser(shareIntent, "Share Media")
            launcher.launch(chooserIntent) // Use the launcher to start the activity
        }
    }

    // Helper function to get MIME type from URI
    private fun getMimeType(context: Context, uri: Uri): String? {
        val cr = context.contentResolver
        return cr.getType(uri)
    }

    fun updateItemName(){
       // TDOD
    }

    fun deleteSelectedItems() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                // Delete items from storage
                repository.deleteMediaItems(_uiState.value.selectedItems)

                // Update UI state by removing deleted items
                val newImages = _uiState.value.images.filter { !(_uiState.value.selectedItems.contains(it.id)) }
                val newVideos = _uiState.value.videos.filter { !(_uiState.value.selectedItems.contains(it.id)) }

                _uiState.value = _uiState.value.copy(
                    images = newImages,
                    videos = newVideos,
                    selectedItems = emptySet(),
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to delete items: ${e.message}"
                )
            }
        }
    }
}
