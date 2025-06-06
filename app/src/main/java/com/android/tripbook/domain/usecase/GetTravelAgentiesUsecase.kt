package com.android.tripbook.domain.usecase

import com.andriod.tripbook.data.repository.TravelAgencyRepository
//import com.andriod.tripbook.util.Resource//import com.andriod.tripbook.data.model.Agency
import com.android.tripbook.data.model.Agency
import com.android.tripbook.data.model.BookingRequest
import com.android.tripbook.data.model.BookingResponse
import com.android.tripbook.util.Resource
import javax.inject.Inject

class GetTravelAgenciesUseCase @Inject constructor(
    private val repository: TravelAgencyRepository
) {
    suspend operator fun invoke(
        minRating: Double? = null,
        maxPrice: Double? = null,
        service: String? = null,
        searchQuery: String? = null
    ): Resource<List<Agency>> {
        return repository.getTravelAgencies(minRating, maxPrice, service, searchQuery)
    }
}

class BookServiceUseCase @Inject constructor(
    private val repository: TravelAgencyRepository
) {
    suspend operator fun invoke(bookingRequest: BookingRequest): Resource<BookingResponse> {
        return repository.bookService(bookingRequest)
    }
}