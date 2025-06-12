package com.android.Tripbook.Datamining.modules.data.tripcatalog.models

import java.time.LocalDate

data class Booking(
                   val tripId: Int,
                   val startDate: LocalDate? = null,
                   val endDate: LocalDate? = null,
                   val adultCount: Int = 1,
                   val childCount: Int = 0,
                   val contactName: String = "",
                   val contactEmail: String = "",
                   val contactPhone: String = "",
                   val specialRequirements: String = "",
                   val selectedOptions: List<TripOption> = emptyList(),
                   val agreedToTerms: Boolean = false
                    )
