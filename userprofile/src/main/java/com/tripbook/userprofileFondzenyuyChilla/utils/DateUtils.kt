package com.example.tripbooktest.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {
    fun formatDate(date: Date, pattern: String = "dd MMM yyyy"): String {
        return SimpleDateFormat(pattern, Locale.getDefault()).format(date)
    }
}
