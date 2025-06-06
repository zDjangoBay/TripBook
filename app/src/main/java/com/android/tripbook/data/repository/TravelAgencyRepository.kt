package com.andriod.tripbook.data.repository

import com.andriod.tripbook.data.model.Agency
import com.andriod.tripbook.data.model.BookingRequest
import com.andriod.tripbook.data.model.BookingResponse
import com.andriod.tripbook.util.Resource

interface TravelAgencyRepository {
    suspend fun getTravelAgencies(
        minRating: Double? = null,
        maxPrice: Double? = null,
        service: String? = null,
        searchQuery: String? = null
    ): Resource<List<Agency>>

    suspend fun getTravelAgencyDetails(agencyId: String): Resource<Agency>

    suspend fun bookService(request: BookingRequest): Resource<BookingResponse>
}