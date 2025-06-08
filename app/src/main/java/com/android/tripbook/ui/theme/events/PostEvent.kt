package com.android.tripbook.ui.theme.events

sealed class PostEvent {
    data class TitleChanged(val title: String) : PostEvent()
    data class DescriptionChanged(val description: String) : PostEvent()
    data class ImageAdded(val uri: String) : PostEvent()
    data class ImageRemoved(val uri: String) : PostEvent()
    data class LocationChanged(val location: String) : PostEvent()
    data class TagsChanged(val tags: List<String>) : PostEvent()
    object SubmitPost : PostEvent()
}