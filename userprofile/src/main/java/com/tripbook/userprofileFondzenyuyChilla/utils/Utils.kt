package com.example.tripbooktest.utils

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

object Utils {

    // Format a date to "dd MMM yyyy"
    fun formatDate(dateString: String, pattern: String = "yyyy-MM-dd'T'HH:mm:ss"): String {
        return try {
            val parser = SimpleDateFormat(pattern, Locale.getDefault())
            val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val date = parser.parse(dateString)
            formatter.format(date!!)
        } catch (e: Exception) {
            "Invalid date"
        }
    }

    // Wrap a suspending API call in try/catch
    suspend fun <T> safeApiCall(apiCall: suspend () -> T): Result<T> {
        return withContext(Dispatchers.IO) {
            try {
                Result.success(apiCall())
            } catch (e: Exception) {
                Log.e("Utils", "API call failed: ${e.localizedMessage}")
                Result.failure(e)
            }
        }
    }

    // Capitalize the first letter of a string
    fun capitalizeFirstLetter(input: String): String {
        return input.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }

}
