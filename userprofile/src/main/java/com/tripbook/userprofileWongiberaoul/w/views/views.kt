package com.tripbook.userprofileWongiberaoul.w.views

class views {
}

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




    private fun loadSampleData() {
        _mediaItems.value = listOf(
            MediaItem("1",
                "Yaounde",
                MediaType.PHOTO,
                "https://cdn.thecrazytourist.com/wp-content/uploads/2023/01/ccimage-shutterstock_1665757894.jpg",
                Date(),
                Date(),
                1024),
            MediaItem("2",
                "Dja Reserve",
                MediaType.PHOTO,
                "https://cdn.thecrazytourist.com/wp-content/uploads/2023/01/ccimage-shutterstock_695886031.jpg",
                Date(),
                Date(),
                5120),
            MediaItem(
                "3",
                "Mefou National Park",
                MediaType.PHOTO,
                "https://cdn.thecrazytourist.com/wp-content/uploads/2023/01/ccimage-shutterstock_1129631078.jpg",
                Date(),
                Date(),
                2048),
            MediaItem(
                "4",
                "Garoua",
                MediaType.PHOTO,
                "https://cdn.thecrazytourist.com/wp-content/uploads/2023/01/ccimage-shutterstock_2029421993.jpg",
                Date(),
                Date(),
                8192),
            MediaItem(
                "5",
                "Dschang",
                MediaType.PHOTO,
                "https://cdn.thecrazytourist.com/wp-content/uploads/2023/01/ccimage-shutterstock_1821119345.jpg",
                Date(),
                Date(),
                8172),
            MediaItem(
                "6",
                "The Bamileke People",
                MediaType.VIDEO,
                "https://youtu.be/F61EZ8OkGy0",
                Date(),
                Date(),
                8192),
            MediaItem(
                id = "7",
                name = "Korup National Park",
                type = MediaType.PHOTO,
                url = "https://cdn.thecrazytourist.com/wp-content/uploads/2023/01/ccimage-shutterstock_556229314.jpg", // or local file URI
                createdDate = Date(),
                modifiedDate = Date(),
                size = 1024
            ),

            )
    }
}