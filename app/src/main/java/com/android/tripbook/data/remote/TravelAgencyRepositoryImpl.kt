package com.android.tripbook.data.remote


import com.android.tripbook.data.model.Agency
import com.android.tripbook.util.Resource
import com.android.tripbook.data.repository.TravelAgencyRepository
import com.android.tripbook.data.model.BookingRequest
import com.android.tripbook.data.model.BookingResponse
import javax.inject.Inject

class TravelAgencyRepositoryImpl @Inject constructor(
    private val apiService: TravelAgencyApiService
) : TravelAgencyRepository {

    override suspend fun getTravelAgencies(
        minRating: Double?,
        maxPrice: Double?,
        service: String?,
        searchQuery: String?
    ): Resource<List<Agency>> {
        return try {
            val response = apiService.getTravelAgencies(minRating, maxPrice, service, searchQuery)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error("Failed to fetch travel agencies: ${e.localizedMessage}")
        }
    }

    override suspend fun getTravelAgencyDetails(agencyId: String): Resource<Agency> {
        return try {
            val response = apiService.getTravelAgencyDetails(agencyId)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error("Failed to fetch agency details: ${e.localizedMessage}")
        }
    }

    override suspend fun bookService(request: BookingRequest): Resource<BookingResponse> {
        return try {
            val response = apiService.createBooking(request)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error("Failed to book service: ${e.localizedMessage}")
        }
    }
}