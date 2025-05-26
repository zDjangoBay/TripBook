package com.android.tripbook.mockData

import com.android.tripbook.model.Trip
import com.google.android.gms.maps.model.LatLng

class sampleTrips {
    fun sampleTrips(): List<Trip> = listOf(
        Trip(
            id = 1,
            title = "Yaounde Capital",
            description = "Explore the capital city.",
            imageUrl = "https://media.gettyimages.com/id/803446314/fr/photo/this-photo-taken-on-june-16-2017-shows-the-city-of-bamenda-the-anglophone-capital-of-northwest.jpg?s=1024x1024&w=gi&k=20&c=x_r8SMF62ult9Xps8g4t8HQJwr6eZvgJH3HOBi8rk48=\", \"https://media.gettyimages.com/id/141863081/fr/photo/kribi-cameroon-africa.jpg?s=1024x1024&w=gi&k=20&c=Pf5obkANbwDIJfAnkEEGf99sv4ykjtrB9PiYHY2LJ4k=",
        ),
        Trip(
            id = 2,
            title = "Buea Mountain",
            description = "Climb the scenic Mount Cameroon.",
            imageUrl = "https://source.unsplash.com/400x300/?bali",
        ),
        Trip(
            id = 3,
            title = "Kribi",
            description = "Beach town with waterfalls.",
            imageUrl = "https://media.gettyimages.com/id/803446314/fr/photo/this-photo-taken-on-june-16-2017-shows-the-city-of-bamenda-the-anglophone-capital-of-northwest.jpg?s=1024x1024&w=gi&k=20&c=x_r8SMF62ult9Xps8g4t8HQJwr6eZvgJH3HOBi8rk48=\", \"https://media.gettyimages.com/id/141863081/fr/photo/kribi-cameroon-africa.jpg?s=1024x1024&w=gi&k=20&c=Pf5obkANbwDIJfAnkEEGf99sv4ykjtrB9PiYHY2LJ4k="
        ),
        Trip(
            id = 4,
            title = "Bamenda Town",
            description = "Highland city with great culture.",
            imageUrl = "https://source.unsplash.com/400x300/?bali"
        )
    )

}