package com.android.tripbook.companycatalog.data

import java.util.Date

data class SearchHistoryItem(
    val query: String,
    val timestamp: Date = Date(),
    val resultCount: Int = 0
) 