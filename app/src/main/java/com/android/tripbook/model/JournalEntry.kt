package com.android.tripbook.model

import java.time.LocalDate

data class JournalEntry(
    val id: String,
    val date: LocalDate,
    val title: String,
    val content: String,
    val tags: List<String> = emptyList(),
    val location: String = "",
    val photos: List<String> = emptyList()
)