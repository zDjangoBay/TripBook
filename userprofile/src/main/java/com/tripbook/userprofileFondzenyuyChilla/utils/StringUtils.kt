package com.example.tripbooktest.utils

object StringUtils {
    fun capitalizeFirstLetter(text: String): String {
        return text.replaceFirstChar { it.uppercase() }
    }
}
