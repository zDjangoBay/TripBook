package com.android.Tripbook.Datamining.modules.data.tripcatalog.models

import java.time.LocalDate
import kotlix.serialization.*

@Serializable
data class Booking(
                   val tripId: Int,
                    //they  need their custom serializer, the date objects i mean
                    @Serializable(with=LocalDateTimeSerializer::class)
                   val startDate: LocalDate? = null,
                 @Serializable(with=LocalDateTimeSerializer::class)
                   val endDate: LocalDate? = null,
                   val adultCount: Int = 1,
  
                   val childCount: Int = 0,
                   val contactName: String = "",
                   val contactEmail: String = "",
  
                   val contactPhone: String = "",
                   val specialRequirements: String = "",
  
                   val selectedOptions: List<TripOption> = emptyList()
                   


                    )
