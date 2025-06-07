package com.android.tripbook.data

import com.android.tripbook.model.BoatCompany
import com.android.tripbook.model.Destination
import com.android.tripbook.R

object BoatMockData {

    val boatCompanies = listOf(
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
            imageUrl = "https://media.licdn.com/dms/image/v2/C560BAQFSSPIoHz4QKg/company-logo_200_200/company-logo_200_200/0/1631425099523/atlantic_marine_services_egypt_logo?e=2147483647&v=beta&t=c2pIs_vO9CsUDKGGNQjEoQ9kA2QiKphkI0Q9q7Pc1QQ"
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
            imageUrl = "https://www.seaexpresstransit.com/assets/ico/favicon.ico"
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
            imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQsIlyNdZOwCpNU-IMHUd0XbJhCDd4YvDQ9TMi_U5puqkiKftOWYEn8R3yjuoe-tkD0DtQ&usqp=CAU"
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
            //logoRes = R.drawable.boat,
            totalTrips = 156,
            amenities = listOf("Life Jackets", "Snacks", "Entertainment", "Charging"),
            imageUrl = "https://yengafrica.com/wp-content/uploads/2022/07/09a852eb-a4e6-4ed4-a017-644b256ca7f6.jpg"
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
            imageUrl = null
        )
    )

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
}