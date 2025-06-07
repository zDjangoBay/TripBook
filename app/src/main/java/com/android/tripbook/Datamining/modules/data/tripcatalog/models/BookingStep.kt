package com.android.Tripbook.Datamining.modules.data.tripcatalog.models
import kotlinx.serialization.*

@Serializable
enum class BookingStep {
    DATE_SELECTION,
    TRAVELER_INFO,
    ADDITIONAL_OPTIONS,
    SUMMARY
}
