package com.android.tripbook.data

import com.android.tripbook.Model.BusCompany
import com.android.tripbook.Model.PopularDestination
import com.android.tripbook.R

object BusMockData {

    val busCompanies = listOf(
        BusCompany(
            id = 1,
            name = "Général Voyage",
            logoRes = R.drawable.general_voyage_logo,
            rating = 4.5,
            totalTrips = 250,
            startingPrice = 3500,
            amenities = listOf("AC", "WiFi", "Charging", "Entertainment"),
            description = "Leading bus company in Cameroon offering comfortable and reliable transportation services across major cities.",
            routes = listOf("Yaoundé - Douala", "Yaoundé - Bafoussam", "Yaoundé - Ngaoundéré")
        ),
        BusCompany(
            id = 2,
            name = "Guaranti Express",
            logoRes = R.drawable.guaranti_express_logo,
            rating = 4.2,
            totalTrips = 180,
            startingPrice = 3200,
            amenities = listOf("AC", "Charging", "Snacks"),
            description = "Affordable and punctual bus service connecting all major towns in Cameroon.",
            routes = listOf("Yaoundé - Douala", "Yaoundé - Bamenda", "Douala - Buea")
        ),
        BusCompany(
            id = 3,
            name = "Musango Voyage",
            logoRes = R.drawable.musango_voyage_logo,
            rating = 4.3,
            totalTrips = 320,
            startingPrice = 3800,
            amenities = listOf("AC", "WiFi", "Entertainment", "Blanket"),
            description = "Premium bus service with luxury coaches and excellent customer service.",
            routes = listOf("Yaoundé - Douala", "Yaoundé - Garoua", "Yaoundé - Maroua")
        ),
        BusCompany(
            id = 4,
            name = "Binam Voyage",
            logoRes = R.drawable.binam_voyage_logo,
            rating = 4.1,
            totalTrips = 150,
            startingPrice = 2800,
            amenities = listOf("AC", "Charging"),
            description = "Economic bus service providing affordable transportation to various destinations.",
            routes = listOf("Yaoundé - Bamenda", "Yaoundé - Bertoua", "Douala - Kribi")
        ),
        BusCompany(
            id = 5,
            name = "Cam Transit",
            logoRes = R.drawable.cam_transit_logo,
            rating = 4.4,
            totalTrips = 200,
            startingPrice = 3600,
            amenities = listOf("AC", "WiFi", "Charging", "Snacks", "Entertainment"),
            description = "Modern fleet with state-of-the-art buses and professional drivers.",
            routes = listOf("Yaoundé - Douala", "Yaoundé - Ebolowa", "Douala - Limbe")
        ),
        BusCompany(
            id = 6,
            name = "Central Voyage",
            logoRes = R.drawable.central_voyage_logo,
            rating = 3.9,
            totalTrips = 120,
            startingPrice = 2500,
            amenities = listOf("AC", "Charging"),
            description = "Budget-friendly bus service with regular schedules to popular destinations.",
            routes = listOf("Yaoundé - Sangmélima", "Yaoundé - Mbalmayo", "Douala - Edéa")
        )
    )

    val popularDestinations = listOf(
        PopularDestination(
            id = 1,
            name = "Douala",
            imageRes = R.drawable.douala_city,
            distance = 250,
            description = "Economic capital of Cameroon with beautiful beaches and vibrant nightlife.",
            averagePrice = 3500,
            estimatedTime = "4h 30min"
        ),
        PopularDestination(
            id = 2,
            name = "Bafoussam",
            imageRes = R.drawable.bafoussam_city,
            distance = 290,
            description = "Cultural hub of the West Region with rich traditional heritage.",
            averagePrice = 4200,
            estimatedTime = "5h 15min"
        ),
        PopularDestination(
            id = 3,
            name = "Bamenda",
            imageRes = R.drawable.bamenda_city,
            distance = 370,
            description = "Capital of the Northwest Region known for its cool climate and hills.",
            averagePrice = 5000,
            estimatedTime = "6h 45min"
        ),
        PopularDestination(
            id = 4,
            name = "Ngaoundéré",
            imageRes = R.drawable.ngaoundere_city,
            distance = 470,
            description = "Gateway to the north with unique Fulani culture and architecture.",
            averagePrice = 6500,
            estimatedTime = "8h 30min"
        ),
        PopularDestination(
            id = 5,
            name = "Bertoua",
            imageRes = R.drawable.bertoua_city,
            distance = 350,
            description = "Capital of the East Region surrounded by tropical rainforest.",
            averagePrice = 4800,
            estimatedTime = "6h 00min"
        ),
        PopularDestination(
            id = 6,
            name = "Garoua",
            imageRes = R.drawable.garoua_city,
            distance = 620,
            description = "Major city in the North Region with diverse cultural attractions.",
            averagePrice = 8000,
            estimatedTime = "10h 15min"
        ),
        PopularDestination(
            id = 7,
            name = "Ebolowa",
            imageRes = R.drawable.ebolowa_city,
            distance = 180,
            description = "Capital of the South Region known for its cocoa plantations.",
            averagePrice = 2800,
            estimatedTime = "3h 20min"
        ),
        PopularDestination(
            id = 8,
            name = "Kribi",
            imageRes = R.drawable.kribi_beach,
            distance = 200,
            description = "Beautiful coastal town with pristine beaches and waterfalls.",
            averagePrice = 3200,
            estimatedTime = "4h 00min"
        )
    )
}