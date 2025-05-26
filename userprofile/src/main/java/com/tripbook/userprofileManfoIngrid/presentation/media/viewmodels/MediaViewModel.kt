package com.tripbook.userprofileManfoIngrid.presentation.media.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import java.util.Date
import com.tripbook.userprofileManfoIngrid.presentation.media.models.*

class MediaViewModel : ViewModel() {
    private val _mediaItems = MutableStateFlow<List<MediaItem>>(emptyList())
    val mediaItems: StateFlow<List<MediaItem>> = _mediaItems.asStateFlow()

    private val _currentFilter = MutableStateFlow(MediaFilter.ALL)
    val currentFilter: StateFlow<MediaFilter> = _currentFilter.asStateFlow()

    private val _sortOption = MutableStateFlow(SortOption.CREATION_DATE)
    val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()

    val filteredAndSortedMedia: StateFlow<List<MediaItem>> = combine(
        _mediaItems,
        _currentFilter,
        _sortOption
    ) { items, filter, sort ->
        getFilteredAndSortedMedia(items, filter, sort)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        loadSampleData()
    }

    fun setFilter(filter: MediaFilter) {
        _currentFilter.value = filter
    }

    fun setSortOption(option: SortOption) {
        _sortOption.value = option
    }

    private fun getFilteredAndSortedMedia(
        items: List<MediaItem>,
        filter: MediaFilter,
        sort: SortOption
    ): List<MediaItem> {
        val filtered = when (filter) {
            MediaFilter.ALL -> items
            MediaFilter.PHOTOS -> items.filter { it.type == MediaType.PHOTO }
            MediaFilter.VIDEOS -> items.filter { it.type == MediaType.VIDEO }
        }

        return when (sort) {
            SortOption.CREATION_DATE -> filtered.sortedByDescending { it.createdDate }
            SortOption.MODIFICATION_DATE -> filtered.sortedByDescending { it.modifiedDate }
            SortOption.NAME -> filtered.sortedBy { it.name }
        }
    }

    fun deleteMedia(mediaId: String) {
        _mediaItems.value = _mediaItems.value.filter { it.id != mediaId }
    }

    fun updateMediaName(mediaId: String, newName: String) {
        _mediaItems.value = _mediaItems.value.map { media ->
            if (media.id == mediaId) {
                media.copy(name = newName, modifiedDate = Date())
            } else {
                media
            }
        }
    }

    private fun loadSampleData() {
        _mediaItems.value = listOf(
            MediaItem("1", "Vacation Photo 1", MediaType.PHOTO, "", Date(), Date(), 1024),
            MediaItem("2", "Trip Video", MediaType.VIDEO, "", Date(), Date(), 5120),
            MediaItem("3", "Beach Photo", MediaType.PHOTO, "", Date(), Date(), 2048),
            MediaItem("4", "Mountain Hike", MediaType.VIDEO, "", Date(), Date(), 8192),
            MediaItem("5", "wave video", MediaType.VIDEO, "", Date(), Date(), 8172),
            MediaItem("6", "Mountain Hike", MediaType.VIDEO, "", Date(), Date(), 8192),
        )
    }
}