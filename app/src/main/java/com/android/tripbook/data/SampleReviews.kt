// model/SampleReviews.kt
package com.android.tripbook.data

import com.android.tripbook.model.Review

object SampleReviews {
    fun get(): List<Review>  = listOf(
        Review(
            id = 6,
            tripId = 2,
            rating = 3,
            username = "Briyand",
            comment = "Absolutely breathtaking!",
            images = listOf("https://media.gettyimages.com/id/803446314/fr/photo/this-photo-taken-on-june-16-2017-shows-the-city-of-bamenda-the-anglophone-capital-of-northwest.jpg?s=1024x1024&w=gi&k=20&c=x_r8SMF62ult9Xps8g4t8HQJwr6eZvgJH3HOBi8rk48=", "https://media.gettyimages.com/id/141863081/fr/photo/kribi-cameroon-africa.jpg?s=1024x1024&w=gi&k=20&c=Pf5obkANbwDIJfAnkEEGf99sv4ykjtrB9PiYHY2LJ4k=")
        ),
        Review(
            id = 9,
            tripId = 2,
            rating = 3,
            username = "Carole",
            comment = "Absolutely breathtaking!",
            images = listOf("https://th.bing.com/th/id/OIP.tyIEwOIscevzwLnKw3XuGwHaE7?w=291&h=194&c=7&r=0&o=5&dpr=1.5&pid=1.7", "https://th.bing.com/th/id/OIP.r1tWienW6NOcnZwPQeV8_AHaE8?w=266&h=180&c=7&r=0&o=5&dpr=1.5&pid=1.7")
        ),
        Review(
            id = 8,
            tripId = 2,
            rating = 3,
            username = "Emmanuel",
            comment = "Absolutely breathtaking!",
            images = listOf("https://media.gettyimages.com/id/803446314/fr/photo/this-photo-taken-on-june-16-2017-shows-the-city-of-bamenda-the-anglophone-capital-of-northwest.jpg?s=1024x1024&w=gi&k=20&c=x_r8SMF62ult9Xps8g4t8HQJwr6eZvgJH3HOBi8rk48=", "https://media.gettyimages.com/id/141863081/fr/photo/kribi-cameroon-africa.jpg?s=1024x1024&w=gi&k=20&c=Pf5obkANbwDIJfAnkEEGf99sv4ykjtrB9PiYHY2LJ4k=")
        ),
        Review(
            id = 5,
            tripId = 2,
            rating = 1,
            username = "Marie",
            comment = "Absolutely breathtaking!",
            images = listOf("https://media.gettyimages.com/id/803446314/fr/photo/this-photo-taken-on-june-16-2017-shows-the-city-of-bamenda-the-anglophone-capital-of-northwest.jpg?s=1024x1024&w=gi&k=20&c=x_r8SMF62ult9Xps8g4t8HQJwr6eZvgJH3HOBi8rk48=", "https://media.gettyimages.com/id/141863081/fr/photo/kribi-cameroon-africa.jpg?s=1024x1024&w=gi&k=20&c=Pf5obkANbwDIJfAnkEEGf99sv4ykjtrB9PiYHY2LJ4k=")
        ),
        Review(
            id = 3,
            tripId = 1,
            rating = 4,
            username = "Brian",
            comment = "Stepping into this historic quarter is like entering a living museum where centuries of human stories continue to unfold on every corner. The architectural diversity is breathtaking Each narrow alley and hidden courtyard reveals new details that speak to the layered history of this remarkable place.\n\nThe guided tours here are exceptional, led by passionate local historians who bring the past to life with engaging storytelling and little-known anecdotes that you won't find in any guidebook. I learned fascinating details about the daily lives of past residents, the evolution of local industries, and how major historical events shaped the character of these neighborhoods.\n\nThe artisan quarter is particularly enchanting, where traditional workshops still operate using time-honored techniques. Watching a master glassblower shape molten glass into delicate sculptures or observing a weaver create intricate textiles on century-old looms provides a profound appreciation for human skill and patience. The local markets offer authentic handcrafted goods that carry the essence of this place's creative spirit, making them meaningful souvenirs that connect you to the cultural heritage long after your visit ends.",
            images = listOf("https://source.unsplash.com/400x300/?temple", "https://source.unsplash.com/400x300/?bali")
        ),
        Review(
            id = 4,
            tripId = 2,
            username = "CulturalBuff",
            rating = 4,
            comment = "Stepping into this historic quarter is like entering a living museum where centuries of human stories continue to unfold on every corner. The architectural diversity is breathtaking - from medieval stone structures with their weathered facades telling tales of ancient craftsmanship, to elegant Victorian buildings that showcase the prosperity and artistic sensibilities of bygone eras. Each narrow alley and hidden courtyard reveals new details that speak to the layered history of this remarkable place.\n\nThe guided tours here are exceptional, led by passionate local historians who bring the past to life with engaging storytelling and little-known anecdotes that you won't find in any guidebook. I learned fascinating details about the daily lives of past residents, the evolution of local industries, and how major historical events shaped the character of these neighborhoods.\n\nThe artisan quarter is particularly enchanting, where traditional workshops still operate using time-honored techniques. Watching a master glassblower shape molten glass into delicate sculptures or observing a weaver create intricate textiles on century-old looms provides a profound appreciation for human skill and patience. The local markets offer authentic handcrafted goods that carry the essence of this place's creative spirit, making them meaningful souvenirs that connect you to the cultural heritage long after your visit ends.",
            images = listOf("https://picsum.photos/400/300?random=4")
        ),
        Review(
            id = 1,
            tripId = 3,
            username = "Martine",
            rating = 4,
            comment = "Amazing trip!",
            images = listOf("https://picsum.photos/300/200?random=1",
                "https://picsum.photos/300/200?random=2"
            )
        ),
        Review(
            id = 2,
            tripId = 2,
            username = "Anthony",
            rating = 5,
            comment = "Loved every moment!",
            images = listOf(
                "https://picsum.photos/300/200?random=3"
            )
        )
    )
}
