// model/SampleReviews.kt
package com.android.tripbook.model

object SampleReviews {
    val reviews = listOf(
        Review(
            username = "Jane Doe",
            comment = "Absolutely breathtaking!",
            images = listOf("https://media.gettyimages.com/id/803446314/fr/photo/this-photo-taken-on-june-16-2017-shows-the-city-of-bamenda-the-anglophone-capital-of-northwest.jpg?s=1024x1024&w=gi&k=20&c=x_r8SMF62ult9Xps8g4t8HQJwr6eZvgJH3HOBi8rk48=", "https://media.gettyimages.com/id/141863081/fr/photo/kribi-cameroon-africa.jpg?s=1024x1024&w=gi&k=20&c=Pf5obkANbwDIJfAnkEEGf99sv4ykjtrB9PiYHY2LJ4k=")
        ),
        Review(
            username = "Mark Smith",
            comment = "Loved every moment of it.",
            images = listOf("https://source.unsplash.com/400x300/?temple", "https://source.unsplash.com/400x300/?bali")
        )
    )
}
