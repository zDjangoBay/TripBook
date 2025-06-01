package com.tripbook.userprofileManfoIngrid.presentation.media.viewmodels

import androidx.lifecycle.ViewModel
import com.tripbook.userprofileManfoIngrid.presentation.media.models.MediaItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MediaViewModel : ViewModel() {
    private val _filteredAndSortedMedia = MutableStateFlow<List<MediaItem>>(emptyList())
    val filteredAndSortedMedia: StateFlow<List<MediaItem>> = _filteredAndSortedMedia.asStateFlow()

    private val _currentFilter = MutableStateFlow("All")
    val currentFilter: StateFlow<String> = _currentFilter.asStateFlow()

    fun setFilter(filter: String) {
        _currentFilter.value = filter
    }

    fun deleteMedia(id: String) {
        val currentList = _filteredAndSortedMedia.value.toMutableList()
        currentList.removeIf { it.id == id }
        _filteredAndSortedMedia.value = currentList
    }

    fun updateMediaName(id: String, newName: String) {
        val currentList = _filteredAndSortedMedia.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == id }
        if (index != -1) {
            val updatedItem = currentList[index].copy(name = newName)
            currentList[index] = updatedItem
            _filteredAndSortedMedia.value = currentList
        }
    }
}
