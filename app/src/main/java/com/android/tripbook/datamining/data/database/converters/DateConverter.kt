package com.android.tripbook.datamining.data.database.converters

import androidx.room.TypeConverter
import java.util.Date

/**
 * Type converter for Room database to convert Date objects to/from Long values
 */
class DateConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}
