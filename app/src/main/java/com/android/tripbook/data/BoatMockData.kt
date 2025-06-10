package com.android.tripbook.data

import com.android.tripbook.model.BoatCompany
import com.android.tripbook.model.Destination
import com.android.tripbook.model.UserRating
import com.android.tripbook.R

object BoatMockData {

    // Store user ratings separately to track who has rated what
    private val userRatings = mutableListOf<UserRating>()

    // Keep original companies data
    private val originalBoatCompanies = listOf(
        BoatCompany(
            id = 1,
            name = "Atlantic Marine Services",
            description = "Premium ferry services connecting coastal cities",
            rating = 4.5,
            priceRange = "25,000 - 50,000 FCFA",
            startingPrice = "25,000",
            routes = listOf("Douala - Limbe", "Kribi - Campo", "Douala - Edea"),
            contact = "+237 233 456 789",
            logoRes = R.drawable.boat,
            totalTrips = 145,
            amenities = listOf("Life Jackets", "WiFi", "Refreshments", "AC", "Entertainment"),
            imageUrl = "https://media.licdn.com/dms/image/v2/C560BAQFSSPIoHz4QKg/company-logo_200_200/company-logo_200_200/0/1631425099523/atlantic_marine_services_egypt_logo?e=2147483647&v=beta&t=c2pIs_vO9CsUDKGGNQjEoQ9kA2QiKphkI0Q9q7Pc1QQ",
            totalRatings = 50, // Simulated initial ratings
            ratingSum = 225.0, // 4.5 * 50
            hasUserRated = false
        ),
        BoatCompany(
            id = 2,
            name = "Sea Express Transit",
            description = "Reliable river boat services for inland waterway travel",
            rating = 4.2,
            priceRange = "10,000 - 50,000 FCFA",
            startingPrice = "10,000",
            routes = listOf("Yaoundé - Mbalmayo", "Bertoua - Batouri", "Ngaoundéré - Garoua"),
            contact = "+237 222 334 567",
            logoRes = R.drawable.boat,
            totalTrips = 98,
            amenities = listOf("Life Jackets", "Snacks", "Charging", "Blanket"),
            imageUrl = "https://www.seaexpresstransit.com/assets/ico/favicon.ico",
            totalRatings = 35,
            ratingSum = 147.0, // 4.2 * 35
            hasUserRated = false
        ),
        BoatCompany(
            id = 3,
            name = "Coastal Express Lines",
            description = "Fast and comfortable coastal ferry services",
            rating = 4.7,
            priceRange = "30,000 - 100,000 FCFA",
            startingPrice = "30,000",
            routes = listOf("Douala - Kribi", "Limbe - Idenau", "Campo - Equatorial Guinea"),
            contact = "+237 243 567 890",
            logoRes = R.drawable.boat,
            totalTrips = 203,
            amenities = listOf("Life Jackets", "WiFi", "AC", "Entertainment", "Refreshments", "Charging"),
            imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQsIlyNdZOwCpNU-IMHUd0XbJhCDd4YvDQ9TMi_U5puqkiKftOWYEn8R3yjuoe-tkD0DtQ&usqp=CAU",
            totalRatings = 75,
            ratingSum = 352.5, // 4.7 * 75
            hasUserRated = false
        ),
        BoatCompany(
            id = 4,
            name = "Wouri River Cruises",
            description = "Scenic river cruises and transportation services",
            rating = 4.1,
            priceRange = "20,000 - 60,000 FCFA",
            startingPrice = "20,000",
            routes = listOf("Douala - Bonaberi", "Douala - Edea", "Yabassi - Dizangué"),
            contact = "+237 233 445 678",
            totalTrips = 156,
            amenities = listOf("Life Jackets", "Snacks", "Entertainment", "Charging"),
            imageUrl = "https://yengafrica.com/wp-content/uploads/2022/07/09a852eb-a4e6-4ed4-a017-644b256ca7f6.jpg",
            totalRatings = 40,
            ratingSum = 164.0, // 4.1 * 40
            hasUserRated = false
        ),
        BoatCompany(
            id = 5,
            name = "Cameroon Waterways",
            description = "Affordable river and coastal transportation",
            rating = 3.9,
            priceRange = "15,000 - 45,000 FCFA",
            startingPrice = "15,000",
            routes = listOf("Douala - Tiko", "Buea - Limbe", "Kribi - Lolodorf"),
            contact = "+237 244 556 789",
            logoRes = R.drawable.boat,
            totalTrips = 87,
            amenities = listOf("Life Jackets", "Snacks", "Charging"),
            imageUrl = null,
            totalRatings = 25,
            ratingSum = 97.5, // 3.9 * 25
            hasUserRated = false
        )
    )

    // Mutable list that can be updated with new ratings
    private val _boatCompanies = originalBoatCompanies.toMutableList()
    val boatCompanies: List<BoatCompany> get() = _boatCompanies.toList()

    val destinations = listOf(
        Destination(
            id = 1,
            name = "Limbe Beach Resort",
            description = "Beautiful black sand beaches with stunning views",
            priceFrom = "75,000 FCFA",
            duration = "2-3 hours",
            distance = "65",
            activities = listOf("Beach relaxation", "Volcano hiking", "Wildlife watching", "Local cuisine"),
            bestTimeToVisit = "November - March",
            imageRes = R.drawable.kribi_beach,
            imageUrl = "https://www.pilotguides.com/wp-content/uploads/2022/05/125938579_e9a59ee2ce_c-799x445.jpg"
        ),
        Destination(
            id = 2,
            name = "Kribi Ocean Paradise",
            description = "Pristine white sand beaches and famous Lobe Falls",
            priceFrom = "100,000 FCFA",
            duration = "3-4 hours",
            distance = "150",
            activities = listOf("Swimming", "Waterfall visit", "Fishing", "Seafood dining"),
            bestTimeToVisit = "December - April",
            imageRes = R.drawable.kribi_beach,
            imageUrl = "https://voyage.maresaonline.com/wp-content/uploads/2020/10/H%C3%B4tel-le-Cardinal-Kribi-Maresa-voyage-min.jpg"
        ),
        Destination(
            id = 3,
            name = "Wouri River Delta",
            description = "Explore mangrove forests and fishing villages",
            priceFrom = "30,000 FCFA",
            duration = "2-3 hours",
            distance = "45",
            activities = listOf("Mangrove tours", "Village visits", "Traditional fishing", "Cultural experience"),
            bestTimeToVisit = "Year-round",
            imageRes = R.drawable.kribi_beach,
            imageUrl = "https://media.istockphoto.com/id/1227369883/photo/boats-parked-at-the-banks-of-the-wouri-river.jpg?s=612x612&w=0&k=20&c=wXVk051dr_KjO2d27q2VpOhuXWvXzUCz7Oe4yElQbng="
        ),
        Destination(
            id = 4,
            name = "Edea Falls Adventure",
            description = "Spectacular waterfalls along the Sanaga River",
            priceFrom = "50,000 FCFA",
            duration = "3-4 hours",
            distance = "120",
            activities = listOf("Waterfall viewing", "Rapids navigation", "Photography", "Picnicking"),
            bestTimeToVisit = "October - March",
            imageRes = R.drawable.garoua_city,
            imageUrl = "https://yengafrica.com/wp-content/uploads/2014/11/WhatsApp-Image-2024-07-24-at-15.26.22.jpeg"
        ),
        Destination(
            id = 5,
            name = "Sanaga River Safari",
            description = "Wildlife river safari with hippos and crocodiles",
            priceFrom = "25,000 FCFA",
            duration = "4-5 hours",
            distance = "95",
            activities = listOf("Wildlife safari", "Bird watching", "Photography", "River exploration"),
            bestTimeToVisit = "November - April",
            imageRes = R.drawable.boat,
            imageUrl = "https://ayilaa.s3.eu-west-1.amazonaws.com/attraction/logos/666853a944b43_1718113193_Gorges%20de%20la%20Sanaga%20(1).jpg"
        )
    )

    /**
     * Check if a user has already rated a specific company
     */
    fun hasUserRatedCompany(userId: String, companyId: Int): Boolean {
        return userRatings.any { it.userId == userId && it.companyId == companyId }
    }

    /**
     * Add a new rating for a company
     * Returns the updated BoatCompany or null if user has already rated
     */
    fun rateCompany(userId: String, companyId: Int, rating: Float): BoatCompany? {
        // Check if user has already rated this company
        if (hasUserRatedCompany(userId, companyId)) {
            return null // User has already rated, return null
        }

        // Find the company
        val companyIndex = _boatCompanies.indexOfFirst { it.id == companyId }
        if (companyIndex == -1) return null

        val currentCompany = _boatCompanies[companyIndex]

        // Add the user rating to our tracking list
        userRatings.add(UserRating(userId, companyId, rating))

        // Calculate new rating
        val newTotalRatings = currentCompany.totalRatings + 1
        val newRatingSum = currentCompany.ratingSum + rating
        val newAverageRating = String.format("%.1f", newRatingSum / newTotalRatings).toDouble()

        // Create updated company
        val updatedCompany = currentCompany.copy(
            rating = newAverageRating,
            totalRatings = newTotalRatings,
            ratingSum = newRatingSum,
            hasUserRated = true
        )

        // Update the list
        _boatCompanies[companyIndex] = updatedCompany

        return updatedCompany
    }

    /**
     * Get companies with user rating status updated
     */
    fun getCompaniesForUser(userId: String): List<BoatCompany> {
        return _boatCompanies.map { company ->
            company.copy(hasUserRated = hasUserRatedCompany(userId, company.id))
        }
    }

    /**
     * Get a specific company with user rating status
     */
    fun getCompanyForUser(userId: String, companyId: Int): BoatCompany? {
        val company = _boatCompanies.find { it.id == companyId }
        return company?.copy(hasUserRated = hasUserRatedCompany(userId, companyId))
    }

    /**
     * Reset all ratings (for testing purposes)
     */
    fun resetRatings() {
        userRatings.clear()
        _boatCompanies.clear()
        _boatCompanies.addAll(originalBoatCompanies)
    }
}