package com.android.tripbook.data

import com.android.tripbook.model.Trip

object SampleTrips {
    fun get(): List<Trip> = listOf(
        Trip(
            id = 1,
            title = "Yaounde Capital",
            caption = "Discover the heart of Cameroon.",
            description = """
                Yaoundé, the political and administrative capital of Cameroon, is a city that gracefully blends modernity with tradition. Set across lush hills, it offers a panoramic view of urban sprawl peppered with colonial-era buildings, bustling markets, and tree-lined boulevards. As you walk through the city, the rhythm of local life pulsates through every corner — from roadside vendors selling grilled fish and plantains to students debating politics in open cafés.

                The city also offers a rich cultural experience with attractions like the National Museum, the Reunification Monument, and the Mvog-Betsi Zoo. Whether you're exploring its historical sites, enjoying the diverse local cuisine, or simply soaking in the atmosphere, Yaoundé presents an authentic slice of Cameroon’s soul. It’s a dynamic entry point for any traveler wishing to dive deep into the country’s culture.
            """.trimIndent(),
            imageUrl = listOf(
                "https://media.gettyimages.com/id/803446314/fr/photo/this-photo-taken-on-june-16-2017-shows-the-city-of-bamenda-the-anglophone-capital-of-northwest.jpg?s=1024x1024&w=gi&k=20&c=x_r8SMF62ult9Xps8g4t8HQJwr6eZvgJH3HOBi8rk48=",
                "https://media.gettyimages.com/id/141863081/fr/photo/kribi-cameroon-africa.jpg?s=1024x1024&w=gi&k=20&c=Pf5obkANbwDIJfAnkEEGf99sv4ykjtrB9PiYHY2LJ4k="
            )
        ),
        Trip(
            id = 2,
            title = "Buea Mountain",
            caption = "Climb the legendary Mount Cameroon.",
            description = """
                Nestled at the foot of Mount Cameroon, Buea offers an unmatched blend of natural beauty and adventure. The mountain — the highest in West Africa — looms in the background, drawing hikers, researchers, and nature lovers alike. A trek up the slopes rewards adventurers with stunning views, volcanic landscapes, and encounters with rare flora and fauna. The cool, misty air is a refreshing break from the tropical lowlands.

                Beyond its mountain appeal, Buea is a historical town with colonial German architecture, vibrant university life, and friendly locals. It's the perfect base for exploring Cameroon's diverse ecosystems, from dense rainforests to lava flows. Whether you're here to conquer the peak or relax in its scenic ambiance, Buea is an unforgettable destination for explorers.
            """.trimIndent(),
            imageUrl = listOf(
                "https://media.gettyimages.com/id/141863081/fr/photo/kribi-cameroon-africa.jpg?s=1024x1024&w=gi&k=20&c=Pf5obkANbwDIJfAnkEEGf99sv4ykjtrB9PiYHY2LJ4k=",
                "https://media.gettyimages.com/id/953837336/photo/cameroon-politics-unrest-police.jpg?s=2048x2048&w=gi&k=20&c=FB3Qus6FKDhoijiBjUfBNKEoBNmmdjaDntYX2ZxRSlA=",
                "https://source.unsplash.com/800x600/?mountains"
            )
        ),
        Trip(
            id = 3,
            title = "Kribi",
            caption = "Soak in paradise by the sea.",
            description = """
                Kribi is Cameroon’s coastal gem, renowned for its golden beaches, crystal-clear waters, and laid-back charm. The town is a popular retreat for locals and tourists seeking sun, sea, and serenity. Among its highlights is the Lobe Waterfall — a rare natural wonder where the river plunges directly into the Atlantic Ocean, creating a surreal backdrop for boat rides and photo opportunities.

                Kribi is not just about scenic beauty; it's a place where culture and relaxation merge seamlessly. Fresh seafood is a local specialty, with grilled shrimp and fish served fresh by the beach. The friendly communities and tranquil atmosphere make it a perfect spot for romantic getaways, solo retreats, or family vacations. Kribi invites you to unplug and reconnect with nature in the most soothing way.
            """.trimIndent(),
            imageUrl = listOf(
                "https://media.gettyimages.com/id/803446314/fr/photo/this-photo-taken-on-june-16-2017-shows-the-city-of-bamenda-the-anglophone-capital-of-northwest.jpg?s=1024x1024&w=gi&k=20&c=x_r8SMF62ult9Xps8g4t8HQJwr6eZvgJH3HOBi8rk48=",
                "https://media.gettyimages.com/id/141863081/fr/photo/kribi-cameroon-africa.jpg?s=1024x1024&w=gi&k=20&c=Pf5obkANbwDIJfAnkEEGf99sv4ykjtrB9PiYHY2LJ4k="
            )
        ),
        Trip(
            id = 4,
            title = "Bamenda Town",
            caption = "Experience the soul of the highlands.",
            description = """
                Set amidst rolling hills and cool highland air, Bamenda offers a unique blend of tradition and progress. Known for its rich cultural heritage, the town is home to vibrant festivals, traditional dances, and bustling markets filled with colorful crafts and textiles. As the unofficial capital of the English-speaking northwest, Bamenda has a distinct identity that sets it apart from other Cameroonian cities.

                The landscape around Bamenda is equally captivating. Terraced hills, hidden waterfalls, and scenic drives through the countryside make it a haven for hikers and photographers. Despite recent social challenges, the spirit of the city remains welcoming and resilient. Visiting Bamenda provides not only an escape into nature but also a deep cultural immersion that leaves a lasting impression.
            """.trimIndent(),
            imageUrl = listOf(
                "https://media.gettyimages.com/id/141863081/fr/photo/kribi-cameroon-africa.jpg?s=1024x1024&w=gi&k=20&c=Pf5obkANbwDIJfAnkEEGf99sv4ykjtrB9PiYHY2LJ4k=",
                "https://media.gettyimages.com/id/953837336/photo/cameroon-politics-unrest-police.jpg?s=2048x2048&w=gi&k=20&c=FB3Qus6FKDhoijiBjUfBNKEoBNmmdjaDntYX2ZxRSlA=",
                "https://source.unsplash.com/800x600/?mountains"
            )
        ),
        Trip(
            id = 5,
            title = "Kribi Beach Resort",
            caption = "Luxury seaside escape with premium amenities.",
            description = """
                Experience Kribi like never before at this exclusive beachfront resort. With private beach access, world-class spa services, and gourmet dining options, this is the ultimate luxury getaway. The resort features infinity pools overlooking the Atlantic, private cabanas, and personalized concierge services that cater to your every need.

                Perfect for honeymooners, business executives seeking relaxation, or families wanting premium comfort. The resort's kids' club and family-friendly activities ensure everyone has an unforgettable experience. Indulge in fresh seafood prepared by internationally trained chefs while watching spectacular sunsets over the ocean.
            """.trimIndent(),
            imageUrl = listOf(
                "https://media.gettyimages.com/id/141863081/fr/photo/kribi-cameroon-africa.jpg?s=1024x1024&w=gi&k=20&c=Pf5obkANbwDIJfAnkEEGf99sv4ykjtrB9PiYHY2LJ4k=",
                "https://source.unsplash.com/800x600/?luxury,resort"
            ),
            tags = listOf("beach", "luxury", "family", "romantic")
        ),
        Trip(
            id = 6,
            title = "Mount Cameroon Adventure Trek",
            caption = "Challenge yourself on Africa's active volcano.",
            description = """
                Embark on the ultimate adventure with this challenging trek up Mount Cameroon. This guided expedition takes you through diverse ecosystems, from tropical rainforest to alpine meadows. Experience the thrill of climbing an active volcano while discovering unique wildlife and breathtaking panoramic views.

                This adventure is perfect for experienced hikers and thrill-seekers looking for their next challenge. The trek includes camping under the stars, encountering rare bird species, and reaching the summit for sunrise views that will leave you speechless. Professional guides ensure your safety while sharing fascinating geological and cultural insights.
            """.trimIndent(),
            imageUrl = listOf(
                "https://source.unsplash.com/800x600/?mountains,hiking",
                "https://source.unsplash.com/800x600/?volcano"
            ),
            tags = listOf("adventure", "hiking", "nature", "challenging")
        ),
        Trip(
            id = 7,
            title = "Yaoundé Cultural Discovery",
            caption = "Immerse yourself in Cameroon's rich heritage.",
            description = """
                Discover the cultural heart of Cameroon through this comprehensive city tour. Visit traditional markets, colonial architecture, local art galleries, and experience authentic Cameroonian cuisine. This cultural immersion includes visits to the National Museum, traditional dance performances, and interactions with local artisans.

                Perfect for culture enthusiasts and families wanting educational experiences. Learn about Cameroon's diverse ethnic groups, traditional crafts, and modern urban life. The tour includes cooking classes, craft workshops, and opportunities to purchase authentic souvenirs directly from local makers.
            """.trimIndent(),
            imageUrl = listOf(
                "https://media.gettyimages.com/id/803446314/fr/photo/this-photo-taken-on-june-16-2017-shows-the-city-of-bamenda-the-anglophone-capital-of-northwest.jpg?s=1024x1024&w=gi&k=20&c=x_r8SMF62ult9Xps8g4t8HQJwr6eZvgJH3HOBi8rk48=",
                "https://source.unsplash.com/800x600/?culture,africa"
            ),
            tags = listOf("culture", "family", "educational", "urban")
        ),
        Trip(
            id = 8,
            title = "Bamenda Highland Retreat",
            caption = "Peaceful escape in the cool mountains.",
            description = """
                Escape to the serene highlands of Bamenda for a peaceful retreat away from city life. This relaxing getaway features comfortable mountain lodges, guided nature walks, and traditional healing practices. The cool climate and stunning landscapes provide the perfect environment for rejuvenation and reflection.

                Ideal for solo travelers, couples seeking tranquility, or anyone needing to disconnect and recharge. Activities include meditation sessions, traditional massage therapy, organic farm visits, and scenic photography tours. Experience the slower pace of highland life while enjoying locally grown coffee and fresh mountain air.
            """.trimIndent(),
            imageUrl = listOf(
                "https://source.unsplash.com/800x600/?mountains,peace",
                "https://media.gettyimages.com/id/953837336/photo/cameroon-politics-unrest-police.jpg?s=2048x2048&w=gi&k=20&c=FB3Qus6FKDhoijiBjUfBNKEoBNmmdjaDntYX2ZxRSlA="
            ),
            tags = listOf("peaceful", "nature", "romantic", "wellness")
        ),
        Trip(
            id = 9,
            title = "Kribi Family Beach Holiday",
            caption = "Fun-filled beach vacation for the whole family.",
            description = """
                Create lasting memories with this family-friendly beach holiday in Kribi. Features safe swimming areas, beach games, boat tours to see the famous waterfalls, and kid-friendly accommodations. The package includes family rooms, children's activities, and babysitting services for parents who want some alone time.

                Enjoy building sandcastles, snorkeling in calm waters, and learning about marine life through educational boat tours. Evening entertainment includes local music, dance performances, and beach bonfires with storytelling. All meals are included with options for children's preferences and dietary requirements.
            """.trimIndent(),
            imageUrl = listOf(
                "https://media.gettyimages.com/id/141863081/fr/photo/kribi-cameroon-africa.jpg?s=1024x1024&w=gi&k=20&c=Pf5obkANbwDIJfAnkEEGf99sv4ykjtrB9PiYHY2LJ4k=",
                "https://source.unsplash.com/800x600/?family,beach"
            ),
            tags = listOf("beach", "family", "children", "safe")
        ),
        Trip(
            id = 10,
            title = "Luxury Safari & Beach Combo",
            caption = "Ultimate luxury experience combining wildlife and beach.",
            description = """
                Experience the best of both worlds with this exclusive luxury package combining wildlife safari and beach relaxation. Start with private game drives in Cameroon's national parks, then unwind at a premium beach resort in Kribi. This high-end experience includes private transportation, luxury accommodations, and personalized service throughout.

                Perfect for discerning travelers who want the ultimate African experience. Features include private chef services, helicopter transfers, exclusive wildlife encounters, and premium spa treatments. Every detail is carefully curated to provide an unforgettable luxury adventure.
            """.trimIndent(),
            imageUrl = listOf(
                "https://source.unsplash.com/800x600/?safari,luxury",
                "https://source.unsplash.com/800x600/?beach,luxury"
            ),
            tags = listOf("luxury", "safari", "beach", "exclusive", "adventure")
        )
    )
}