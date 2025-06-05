 package com.android.tripbook.model.converters

import androidx.room.TypeConverter
import com.android.tripbook.model.TripStatus

object TripStatusConverter {
    @TypeConverter
    @JvmStatic
    fun fromStringToTripStatus(value: String?): TripStatus? {
        return value?.let { TripStatus.valueOf(it) }
    }

    @TypeConverter
    @JvmStatic
    fun fromTripStatusToString(status: TripStatus?): String? {
        return status?.name
    }
}