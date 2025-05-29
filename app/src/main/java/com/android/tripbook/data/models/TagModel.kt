package com.android.tripbook.data.models

data class TagModel(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val color: String = "#6650a4", // Default to primary color
    val isSelected: Boolean = false
) {
    companion object {
        fun getDefaultTags(): List<TagModel> = listOf(
            TagModel(name = "Adventure", color = "#FF6B35"),
            TagModel(name = "Nature", color = "#4CAF50"),
            TagModel(name = "City", color = "#2196F3"),
            TagModel(name = "Beach", color = "#00BCD4"),
            TagModel(name = "Mountain", color = "#795548"),
            TagModel(name = "Food", color = "#FF9800"),
            TagModel(name = "Culture", color = "#9C27B0"),
            TagModel(name = "Relaxation", color = "#607D8B")
        )
    }
}