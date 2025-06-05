 package com.android.tripbook.model.converters

import androidx.room.TypeConverter
import java.time.LocalDate

object DateConverter {
    @TypeConverter
    @JvmStatic // Ensure KSP can find this static method
    fun fromTimestamp(value: Long?): LocalDate? {
        return value?.let { LocalDate.ofEpochDay(it) }
    }

    @TypeConverter
    @JvmStatic // Ensure KSP can find this static method
    fun dateToTimestamp(date: LocalDate?): Long? {
        return date?.toEpochDay()
    }
}