package com.android.tripbook.data

import com.android.tripbook.model.TrainCompany
import com.android.tripbook.model.TrainDestination

object TrainMockData {

    val trainCompanies = listOf(
        TrainCompany(
            id = 1,
            name = "Camrail",
            logoUrl = "https://weekinfos.com/wp-content/uploads/2023/08/5-50.png",
            description = "Cameroon's national railway company providing reliable train services across the country",
            rating = 4.2f,
            priceRange = "2,500 - 15,000 FCFA"
        )
    )

    val trainDestinations = listOf(
        TrainDestination(
            id = 1,
            name = "Douala",
            imageUrl = "https://sortiradouala.com/assets/uploads/article/QuestcequifaitdevousunhabitantdelavilledeDouala/image_1648914320085.webp",
            description = "Economic capital of Cameroon with bustling markets and modern infrastructure",
            duration = "4h 30min",
            price = "3,500 FCFA",
            distance = "285 km",
            popularTimes = "Morning & Evening"
        ),
        TrainDestination(
            id = 2,
            name = "Bafoussam",
            imageUrl = "https://static.wixstatic.com/media/318134_21478f6149af4c14a474d981d23adde8~mv2.jpg/v1/fill/w_3000,h_1564,al_c,q_90/Bafoussam-Cameroon_Africityshoot%20By%20Leandry%20JIEUTSA_edited.jpg",
            description = "Beautiful highland city known for its cool climate and coffee plantations",
            duration = "6h 15min",
            price = "4,200 FCFA",
            distance = "320 km",
            popularTimes = "Early Morning"
        ),
        TrainDestination(
            id = 3,
            name = "Ngaoundéré",
            imageUrl = "https://www.ccaa.aero/images/ART_NDERE_SITE_1.jpg",
            description = "Gateway to the northern regions with stunning savanna landscapes",
            duration = "12h 45min",
            price = "8,500 FCFA",
            distance = "622 km",
            popularTimes = "Night Service"
        ),
        TrainDestination(
            id = 4,
            name = "Belabo",
            imageUrl = "https://www.touristplaces.com.bd/images/pp/6/p120864nam.jpg",
            description = "Charming town surrounded by tropical forests and wildlife",
            duration = "8h 20min",
            price = "5,800 FCFA",
            distance = "425 km",
            popularTimes = "Morning"
        ),
        TrainDestination(
            id = 5,
            name = "Eseka",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/e/e9/Eseka%2C_statuette_de_Ruben_Um_Nyobe.jpg",
            description = "Historic railway junction town with colonial architecture",
            duration = "2h 45min",
            price = "2,500 FCFA",
            distance = "142 km",
            popularTimes = "All Day"
        )
    )
}