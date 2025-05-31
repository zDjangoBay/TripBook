package com.android.tripbook.data

import com.android.tripbook.model.Trip

object SampleTripsWithLocation {
    fun get(): List<Trip> = listOf(
        Trip(
            id = 1,
            title = "Yaounde Capital",
            caption = "summer",
            description = "Explore the romantic capital of Yaounde with its iconic landmarks",
            imageUrl = listOf("https://media.gettyimages.com/id/803446314/fr/photo/this-photo-taken-on-june-16-2017-shows-the-city-of-bamenda-the-anglophone-capital-of-northwest.jpg?s=1024x1024&w=gi&k=20&c=x_r8SMF62ult9Xps8g4t8HQJwr6eZvgJH3HOBi8rk48=",
                "https://media.gettyimages.com/id/141863081/fr/photo/kribi-cameroon-africa.jpg?s=1024x1024&w=gi&k=20&c=Pf5obkANbwDIJfAnkEEGf99sv4ykjtrB9PiYHY2LJ4k="
            ),
            price = "899FCFA",
            rating = 4.8f,
            reviewCount = 245,
            duration = "4 days",
            latitude = 48.8566,
            longitude = 2.3522,
            city = "Yaounde",
            country = "Cameroon",
            region = "Centre"
        ),
        Trip(
            id = 2,
            title = "Buea Mountain",
            caption = "weekends",
            description = "Experience the vibrant culture and modern marvels of Buea's capital",
            imageUrl = listOf("https://media.gettyimages.com/id/141863081/fr/photo/kribi-cameroon-africa.jpg?s=1024x1024&w=gi&k=20&c=Pf5obkANbwDIJfAnkEEGf99sv4ykjtrB9PiYHY2LJ4k=",
                "https://media.gettyimages.com/id/953837336/photo/cameroon-politics-unrest-police.jpg?s=2048x2048&w=gi&k=20&c=FB3Qus6FKDhoijiBjUfBNKEoBNmmdjaDntYX2ZxRSlA=",
                "https://source.unsplash.com/800x600/?mountains"),
            price = "1,299FCFA",
            rating = 4.9f,
            reviewCount = 189,
            duration = "7 days",
            latitude = 35.6762,
            longitude = 139.6503,
            city = "Buea",
            country = "Cameroon",
            region = "Southwest"
        ),
        Trip(
            id = 3,
            title = "Kribi",
            caption = "business",
            description = "Witness breathtaking sunsets on the beautiful kribi island",
            imageUrl = listOf("https://media.gettyimages.com/id/803446314/fr/photo/this-photo-taken-on-june-16-2017-shows-the-city-of-bamenda-the-anglophone-capital-of-northwest.jpg?s=1024x1024&w=gi&k=20&c=x_r8SMF62ult9Xps8g4t8HQJwr6eZvgJH3HOBi8rk48=",
                "https://media.gettyimages.com/id/141863081/fr/photo/kribi-cameroon-africa.jpg?s=1024x1024&w=gi&k=20&c=Pf5obkANbwDIJfAnkEEGf99sv4ykjtrB9PiYHY2LJ4k="
            ),
            price = "1,199FCFA",
            rating = 4.7f,
            reviewCount = 156,
            duration = "5 days",
            latitude = 36.3932,
            longitude = 25.4615,
            city = "Kribi",
            country = "Cameroon",
            region = "Littoral"
        ),
        Trip(
            id = 4,
            title = "Bamenda Town",
            caption = "studies",
            description = "Discover the city that never sleeps with all its iconic attractions",
            imageUrl = listOf( "https://media.gettyimages.com/id/141863081/fr/photo/kribi-cameroon-africa.jpg?s=1024x1024&w=gi&k=20&c=Pf5obkANbwDIJfAnkEEGf99sv4ykjtrB9PiYHY2LJ4k=",
                "https://media.gettyimages.com/id/953837336/photo/cameroon-politics-unrest-police.jpg?s=2048x2048&w=gi&k=20&c=FB3Qus6FKDhoijiBjUfBNKEoBNmmdjaDntYX2ZxRSlA=",
                "https://source.unsplash.com/800x600/?mountains"
            ),
            price = "1,099FCFA",
            rating = 4.6f,
            reviewCount = 298,
            duration = "6 days",
            latitude = 40.7128,
            longitude = -74.0060,
            city = "Bamenda",
            country = "Cameroon",
            region = "Northwest"
        ),
    )
}