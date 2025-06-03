package com.android.tripbook.ui.model

enum class SortCriterion {
    DATE, RATING
}

enum class SortOrder {
    ASCENDING, DESCENDING
}

data class ReviewSortState(
    val criterion: SortCriterion = SortCriterion.DATE,
    val order: SortOrder = SortOrder.DESCENDING // Default: Newest first
)

fun SortCriterion.displayName(): String {
    return when (this) {
        SortCriterion.DATE -> "Date"
        SortCriterion.RATING -> "Rating"
    }
}