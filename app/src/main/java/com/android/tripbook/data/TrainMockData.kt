package com.android.tripbook.data

import com.android.tripbook.model.TrainCompany
import com.android.tripbook.model.TrainDestination

// Extension functions for TrainDestination
fun TrainDestination.addRating(newRating: Float): TrainDestination {
    val totalScore = this.rating * this.totalRatings + newRating
    val newTotalRatings = this.totalRatings + 1
    val newAverageRating = totalScore / newTotalRatings

    return this.copy(
        rating = newAverageRating,
        totalRatings = newTotalRatings
    )
}

fun TrainDestination.getDisplayRating(): String {
    return if (totalRatings > 0) {
        String.format("%.1f (%d)", rating, totalRatings)
    } else {
        "No ratings yet"
    }
}

// Extension functions for TrainCompany
fun TrainCompany.addRating(newRating: Float): TrainCompany {
    val totalScore = this.rating * this.totalRatings + newRating
    val newTotalRatings = this.totalRatings + 1
    val newAverageRating = totalScore / newTotalRatings

    return this.copy(
        rating = newAverageRating,
        totalRatings = newTotalRatings
    )
}

fun TrainCompany.getDisplayRating(): String {
    return if (totalRatings > 0) {
        String.format("%.1f (%d)", rating, totalRatings)
    } else {
        "No ratings yet"
    }
}

object TrainMockData {

    // Made mutable to allow rating updates
    val trainCompanies = mutableListOf(
        TrainCompany(
            id = 1,
            name = "Camrail",
            logoUrl = "https://weekinfos.com/wp-content/uploads/2023/08/5-50.png",
            description = "Cameroon's national railway company providing reliable train services across the country",
            rating = 4.2f,
            totalRatings = 248,
            priceRange = "2,500 - 15,000 FCFA"
        )
    )

    // Made mutable to allow rating updates
    val trainDestinations = mutableListOf(
        TrainDestination(
            id = 1,
            name = "Douala",
            imageUrl = "https://sortiradouala.com/assets/uploads/article/QuestcequifaitdevousunhabitantdelavilledeDouala/image_1648914320085.webp",
            description = "Economic capital of Cameroon with bustling markets and modern infrastructure",
            duration = "4h 30min",
            price = "3,500 FCFA",
            distance = "285 km",
            popularTimes = "Morning & Evening",
            rating = 4.3f,
            totalRatings = 156
        ),
        TrainDestination(
            id = 2,
            name = "Bafoussam",
            imageUrl = "https://static.wixstatic.com/media/318134_21478f6149af4c14a474d981d23adde8~mv2.jpg/v1/fill/w_3000,h_1564,al_c,q_90/Bafoussam-Cameroon_Africityshoot%20By%20Leandry%20JIEUTSA_edited.jpg",
            description = "Beautiful highland city known for its cool climate and coffee plantations",
            duration = "6h 15min",
            price = "4,200 FCFA",
            distance = "320 km",
            popularTimes = "Early Morning",
            rating = 4.1f,
            totalRatings = 89
        ),
        TrainDestination(
            id = 3,
            name = "Ngaoundéré",
            imageUrl = "https://www.ccaa.aero/images/ART_NDERE_SITE_1.jpg",
            description = "Gateway to the northern regions with stunning savanna landscapes",
            duration = "12h 45min",
            price = "8,500 FCFA",
            distance = "622 km",
            popularTimes = "Night Service",
            rating = 3.8f,
            totalRatings = 134
        ),
        TrainDestination(
            id = 4,
            name = "Belabo",
            imageUrl = "https://www.touristplaces.com.bd/images/pp/6/p120864nam.jpg",
            description = "Charming town surrounded by tropical forests and wildlife",
            duration = "8h 20min",
            price = "5,800 FCFA",
            distance = "425 km",
            popularTimes = "Morning",
            rating = 4.0f,
            totalRatings = 67
        ),
        TrainDestination(
            id = 5,
            name = "Eseka",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/e/e9/Eseka%2C_statuette_de_Ruben_Um_Nyobe.jpg",
            description = "Historic railway junction town with colonial architecture",
            duration = "2h 45min",
            price = "2,500 FCFA",
            distance = "142 km",
            popularTimes = "All Day",
            rating = 4.4f,
            totalRatings = 203
        )
    )

    // Function to update destination rating
    fun updateDestinationRating(destinationId: Int, newRating: Float) {
        val destinationIndex = trainDestinations.indexOfFirst { it.id == destinationId }
        if (destinationIndex != -1) {
            val currentDestination = trainDestinations[destinationIndex]
            val updatedDestination = currentDestination.addRating(newRating)
            trainDestinations[destinationIndex] = updatedDestination
        }
    }

    // Function to update company rating
    fun updateCompanyRating(companyId: Int, newRating: Float) {
        val companyIndex = trainCompanies.indexOfFirst { it.id == companyId }
        if (companyIndex != -1) {
            val currentCompany = trainCompanies[companyIndex]
            val updatedCompany = currentCompany.addRating(newRating)
            trainCompanies[companyIndex] = updatedCompany
        }
    }

    // Function to get destination by ID
    fun getDestinationById(destinationId: Int): TrainDestination? {
        return trainDestinations.find { it.id == destinationId }
    }

    // Function to get company by ID
    fun getCompanyById(companyId: Int): TrainCompany? {
        return trainCompanies.find { it.id == companyId }
    }

    // Function to get destinations with rating filter
    fun getDestinationsByMinRating(minRating: Float): List<TrainDestination> {
        return trainDestinations.filter { it.rating >= minRating }
    }

    // Function to get top rated destinations
    fun getTopRatedDestinations(limit: Int = 3): List<TrainDestination> {
        return trainDestinations.sortedByDescending { it.rating }.take(limit)
    }
}