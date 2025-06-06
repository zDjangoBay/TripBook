
package com.android.tripbook.data

import com.android.tripbook.model.Trip

object SampleTrips {
    private val internalTrips = mutableListOf<Trip>()

    init {
        internalTrips.addAll(listOf(
            Trip(
                id = 1,
                title = "Yaounde Capital",
                caption = "Discover the heart of Cameroon.",
                description = """
                    Yaoundé, the political and administrative capital of Cameroon...
                """.trimIndent(),
                imageUrl = listOf(
                    "https://media.gettyimages.com/id/803446314/fr/photo/this-photo-taken-on-june-16-2017-shows-the-city-of-bamenda-the-anglophone-capital-of-northwest.jpg?s=1024x1024&w=gi&k=20&c=x_r8SMF62ult9Xps8g4t8HQJwr6eZvgJH3HOBi8rk48=",
                    "https://media.gettyimages.com/id/141863081/fr/photo/kribi-cameroon-africa.jpg?s=1024x1024&w=gi&k=20&c=Pf5obkANbwDIJfAnkEEGf99sv4ykjtrB9PiYHY2LJ4k="
                ),

                latitude = 3.8480,
                longitude = 11.5021,
                city = "Yaoundé",
                country = "Cameroon",
                region = "Centre"
            ),
            Trip(
                id = 2,
                title = "Buea Mountain",
                caption = "Climb the legendary Mount Cameroon.",
                description = """
                    Nestled at the foot of Mount Cameroon, Buea offers an unmatched blend...
                """.trimIndent(),
                imageUrl = listOf(
                    "https://media.gettyimages.com/id/141863081/fr/photo/kribi-cameroon-africa.jpg?s=1024x1024&w=gi&k=20&c=Pf5obkANbwDIJfAnkEEGf99sv4ykjtrB9PiYHY2LJ4k=",
                    "https://media.gettyimages.com/id/953837336/photo/cameroon-politics-unrest-police.jpg?s=2048x2048&w=gi&k=20&c=FB3Qus6FKDhoijiBjUfBNKEoBNmmdjaDntYX2ZxRSlA=",
                    "https://source.unsplash.com/800x600/?mountains"
                ),

                latitude = 4.1651,
                longitude = 9.2921,
                city = "Buea",
                country = "Cameroon",
                region = "Southwest"
            ),
            Trip(
                id = 3,
                title = "Kribi",
                caption = "Soak in paradise by the sea.",
                description = """
                    Kribi is Cameroon’s coastal gem, renowned for its golden beaches...
                """.trimIndent(),
                imageUrl = listOf(
                    "https://media.gettyimages.com/id/803446314/fr/photo/this-photo-taken-on-june-16-2017-shows-the-city-of-bamenda-the-anglophone-capital-of-northwest.jpg?s=1024x1024&w=gi&k=20&c=x_r8SMF62ult9Xps8g4t8HQJwr6eZvgJH3HOBi8rk48=",
                    "https://media.gettyimages.com/id/141863081/fr/photo/kribi-cameroon-africa.jpg?s=1024x1024&w=gi&k=20&c=Pf5obkANbwDIJfAnkEEGf99sv4ykjtrB9PiYHY2LJ4k="
                ),
                latitude = 2.9407,
                longitude = 9.9098,
                city = "Kribi",
                country = "Cameroon",
                region = "South"
            ),
            Trip(
                id = 4,
                title = "Bamenda Town",
                caption = "Experience the soul of the highlands.",
                description = """
                    Set amidst rolling hills and cool highland air, Bamenda offers a unique blend...
                """.trimIndent(),
                imageUrl = listOf(
                    "https://media.gettyimages.com/id/141863081/fr/photo/kribi-cameroon-africa.jpg?s=1024x1024&w=gi&k=20&c=Pf5obkANbwDIJfAnkEEGf99sv4ykjtrB9PiYHY2LJ4k=",
                    "https://media.gettyimages.com/id/953837336/photo/cameroon-politics-unrest-police.jpg?s=2048x2048&w=gi&k=20&c=FB3Qus6FKDhoijiBjUfBNKEoBNmmdjaDntYX2ZxRSlA=",
                    "https://source.unsplash.com/800x600/?mountains"
                ),

                latitude = 5.9639,
                longitude = 10.1591,
                city = "Bamenda",
                country = "Cameroon",
                region = "Northwest"
            )
        ))
    }

    fun get(): List<Trip> = internalTrips.toList()

    fun addTrip(trip: Trip) {
        internalTrips.add(trip)
    }

    fun generateId(): Int {
        return (internalTrips.maxOfOrNull { it.id } ?: 0) + 1
    }
}