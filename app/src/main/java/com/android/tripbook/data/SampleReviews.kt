// model/SampleReviews.kt
package com.android.tripbook.data

import com.android.tripbook.model.Review

object SampleReviews {
    fun get(): List<Review>  = listOf(
        Review(
            id = 6,
            tripId = 2,
            rating = 3,
            username = "Jane Doe",
            comment = "Absolutely breathtaking!",
            images = listOf("https://media.gettyimages.com/id/803446314/fr/photo/this-photo-taken-on-june-16-2017-shows-the-city-of-bamenda-the-anglophone-capital-of-northwest.jpg?s=1024x1024&w=gi&k=20&c=x_r8SMF62ult9Xps8g4t8HQJwr6eZvgJH3HOBi8rk48=", "https://media.gettyimages.com/id/141863081/fr/photo/kribi-cameroon-africa.jpg?s=1024x1024&w=gi&k=20&c=Pf5obkANbwDIJfAnkEEGf99sv4ykjtrB9PiYHY2LJ4k=")
        ),
        Review(
            id = 9,
            tripId = 2,
            rating = 3,
            username = "Jane Doe",
            comment = "Absolutely breathtaking!",
            images = listOf("https://media.gettyimages.com/id/803446314/fr/photo/this-photo-taken-on-june-16-2017-shows-the-city-of-bamenda-the-anglophone-capital-of-northwest.jpg?s=1024x1024&w=gi&k=20&c=x_r8SMF62ult9Xps8g4t8HQJwr6eZvgJH3HOBi8rk48=", "https://media.gettyimages.com/id/141863081/fr/photo/kribi-cameroon-africa.jpg?s=1024x1024&w=gi&k=20&c=Pf5obkANbwDIJfAnkEEGf99sv4ykjtrB9PiYHY2LJ4k=")
        ),
        Review(
            id = 8,
            tripId = 2,
            rating = 3,
            username = "Jane Doe",
            comment = "Absolutely breathtaking!",
            images = listOf("https://media.gettyimages.com/id/803446314/fr/photo/this-photo-taken-on-june-16-2017-shows-the-city-of-bamenda-the-anglophone-capital-of-northwest.jpg?s=1024x1024&w=gi&k=20&c=x_r8SMF62ult9Xps8g4t8HQJwr6eZvgJH3HOBi8rk48=", "https://media.gettyimages.com/id/141863081/fr/photo/kribi-cameroon-africa.jpg?s=1024x1024&w=gi&k=20&c=Pf5obkANbwDIJfAnkEEGf99sv4ykjtrB9PiYHY2LJ4k=")
        ),
        Review(
            id = 5,
            tripId = 2,
            rating = 1,
            username = "Jane Doe",
            comment = "Absolutely breathtaking!",
            images = listOf("https://media.gettyimages.com/id/803446314/fr/photo/this-photo-taken-on-june-16-2017-shows-the-city-of-bamenda-the-anglophone-capital-of-northwest.jpg?s=1024x1024&w=gi&k=20&c=x_r8SMF62ult9Xps8g4t8HQJwr6eZvgJH3HOBi8rk48=", "https://media.gettyimages.com/id/141863081/fr/photo/kribi-cameroon-africa.jpg?s=1024x1024&w=gi&k=20&c=Pf5obkANbwDIJfAnkEEGf99sv4ykjtrB9PiYHY2LJ4k=")
        ),
        Review(
            id = 3,
            tripId = 1,
            rating = 4,
            username = "Mark Smith",
            comment = "Loved every moment of it.",
            images = listOf("https://source.unsplash.com/400x300/?temple", "https://source.unsplash.com/400x300/?bali")
        ),
        Review(
            id = 1,
            tripId = 101,
            username = "Martine",
            rating = 4,
            comment = "Amazing trip!",
            images = listOf(
                "https://picsum.photos/300/200?random=1",
                "https://picsum.photos/300/200?random=2"
            )
        ),
        Review(
            id = 2,
            tripId = 102,
            username = "Carole",
            rating = 5,
            comment = "Loved every moment!",
            images = listOf(
                "https://picsum.photos/300/200?random=3"
            )
        )
    )
}
