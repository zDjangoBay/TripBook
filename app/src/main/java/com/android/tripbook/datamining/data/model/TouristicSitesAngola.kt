package com.android.tripbook.datamining.data.model

/**
 * Data class representing touristic sites in Angola
 */
data class AngolaTouristicSite(
    val id: Long,
    val name: String,
    val description: String,
    val location: String,
    val region: String,
    val coordinates: Coordinates,
    val category: List<String>,
    val images: List<String>,
    val entryFee: String,
    val openingHours: String,
    val bestTimeToVisit: String,
    val durationOfVisit: String,
    val accessibilityInfo: String,
    val localTips: String,
    val nearbyAttractions: List<String>,
    val historicalSignificance: String,
    val culturalRelevance: String,
    val websiteUrl: String? = null,
    val contactInfo: String? = null,
    val rating: Float = 0.0f,
    val reviewCount: Int = 0,
    val tags: List<String> = emptyList()
)

/**
 * Repository of touristic sites in Angola
 */
object AngolaTouristicSites {
    val sites = listOf(
        AngolaTouristicSite(
            id = 1,
            name = "Kissama National Park",
            description = "Kissama (also spelled Quiçama) National Park is a vast wildlife reserve located south of Luanda. Once devastated by the civil war, the park has been revitalized through 'Operation Noah's Ark,' which reintroduced elephants, antelopes, and other wildlife from neighboring countries. The park covers approximately 9,960 square kilometers of diverse landscapes, including savannah, woodland, and coastal areas along the Atlantic Ocean. Visitors can observe elephants, giraffes, zebras, ostriches, and various antelope species in their natural habitat. The park also features the Kwanza River, which forms its northern boundary and offers opportunities for boat trips and fishing.",
            location = "Bengo Province, Angola",
            region = "Northern Angola",
            coordinates = Coordinates(-9.3333, 13.5000),
            category = listOf("Wildlife", "National Park", "Natural"),
            images = listOf(
                "https://images.unsplash.com/photo-1518709268805-4e9042af9f23",
                "https://images.unsplash.com/photo-1516426122078-c23e76319801",
                "https://images.unsplash.com/photo-1549366021-9f761d450615"
            ),
            entryFee = "Approximately 5,000 Kwanza (15 USD) for foreigners",
            openingHours = "7:00 AM - 5:00 PM daily",
            bestTimeToVisit = "May to October (dry season) for better wildlife viewing and road conditions.",
            durationOfVisit = "Full day to multiple days",
            accessibilityInfo = "4x4 vehicles recommended for exploring the park. Some areas may be inaccessible during the rainy season.",
            localTips = "Hire a local guide for better wildlife spotting. Bring plenty of water, food, and fuel as facilities are limited. Consider staying at Muxima Camp for overnight visits.",
            nearbyAttractions = listOf("Kwanza River", "Muxima Fortress", "Atlantic Beaches", "Cabo Ledo"),
            historicalSignificance = "The park was established in 1938 but suffered during Angola's civil war (1975-2002). The 'Operation Noah's Ark' project in 2001 was one of Africa's largest wildlife relocation initiatives, bringing animals from South Africa and Botswana to repopulate the park.",
            culturalRelevance = "The park represents Angola's commitment to conservation and ecological restoration following decades of conflict. It is named after the Kissama (Quiçama) people who traditionally inhabited the area.",
            websiteUrl = null,
            contactInfo = null,
            rating = 4.3f,
            reviewCount = 245,
            tags = listOf("wildlife", "safari", "conservation", "elephants", "nature", "post-war recovery")
        ),
        
        AngolaTouristicSite(
            id = 2,
            name = "Luanda Bay (Baía de Luanda)",
            description = "Luanda Bay is a natural harbor and the heart of Angola's capital city. Following extensive redevelopment, the bay now features a beautiful 3.5 km promenade (Marginal) lined with palm trees, restaurants, bars, and recreational areas. The crescent-shaped bay offers stunning views of the Atlantic Ocean and the Ilha do Cabo (Luanda Island) across the water. The area has become a popular spot for locals and tourists to stroll, exercise, socialize, and enjoy the sea breeze. The redeveloped waterfront showcases modern Angola while preserving historical elements, including colonial-era buildings and monuments.",
            location = "Luanda, Angola",
            region = "Northern Angola",
            coordinates = Coordinates(-8.8147, 13.2346),
            category = listOf("Urban", "Waterfront", "Cultural"),
            images = listOf(
                "https://images.unsplash.com/photo-1580060839134-75a5edca2e99",
                "https://images.unsplash.com/photo-1564868456298-7d4a1ae1a1a8",
                "https://images.unsplash.com/photo-1578774929812-c11262c7b8f1"
            ),
            entryFee = "Free",
            openingHours = "24/7, but best visited during daylight hours and early evening",
            bestTimeToVisit = "May to October for pleasant temperatures and less humidity. Sunset offers particularly beautiful views.",
            durationOfVisit = "2-3 hours",
            accessibilityInfo = "The promenade is wheelchair accessible with smooth pavements and ramps.",
            localTips = "Visit in the early morning or evening to avoid the midday heat. The area is generally safe but remain vigilant with personal belongings. Try local cuisine at one of the waterfront restaurants.",
            nearbyAttractions = listOf("Agostinho Neto Mausoleum", "Fortaleza de São Miguel", "National Museum of Anthropology", "Ilha do Cabo"),
            historicalSignificance = "Luanda Bay has been the center of the city since Portuguese colonization in the 16th century. The bay served as a major port for the slave trade and later for Angola's exports. The recent redevelopment (completed in 2012) transformed the area from a deteriorated waterfront to a modern urban space.",
            culturalRelevance = "The bay area represents the modernization and revitalization of Angola following the civil war. It has become a symbol of Luanda's transformation and Angola's economic growth, while also serving as an important social gathering place for Angolans.",
            websiteUrl = null,
            contactInfo = null,
            rating = 4.5f,
            reviewCount = 1250,
            tags = listOf("waterfront", "promenade", "urban renewal", "city views", "relaxation", "sunset")
        ),
        
        AngolaTouristicSite(
            id = 3,
            name = "Fortaleza de São Miguel",
            description = "Fortaleza de São Miguel (St. Michael Fortress) is a historic fortress located on a hill overlooking Luanda Bay. Built by the Portuguese in 1576, it is one of the oldest colonial structures in Angola and served as the administrative center of the colony for centuries. The fortress has been converted into a museum (Museum of the Armed Forces) that chronicles Angola's military history from the early colonial period through the struggle for independence and the civil war. The site features well-preserved walls, old cannons, a chapel, and various military artifacts. The fortress offers panoramic views of Luanda and its bay.",
            location = "Luanda, Angola",
            region = "Northern Angola",
            coordinates = Coordinates(-8.8136, 13.2342),
            category = listOf("Historical", "Museum", "Colonial"),
            images = listOf(
                "https://images.unsplash.com/photo-1564868456298-7d4a1ae1a1a8",
                "https://images.unsplash.com/photo-1580060839134-75a5edca2e99",
                "https://images.unsplash.com/photo-1578774929812-c11262c7b8f1"
            ),
            entryFee = "Approximately 1,000 Kwanza (3 USD)",
            openingHours = "9:00 AM - 4:30 PM, Tuesday to Sunday (Closed on Mondays)",
            bestTimeToVisit = "Year-round, but early morning offers cooler temperatures and better lighting for photography.",
            durationOfVisit = "1-2 hours",
            accessibilityInfo = "The fortress involves steps and uneven surfaces, which may be challenging for those with mobility issues.",
            localTips = "Hire a guide for historical context. Photography is allowed but may require a permit for professional equipment. The museum has limited English signage, so a Portuguese-speaking guide is helpful.",
            nearbyAttractions = listOf("Luanda Bay", "National Museum of Anthropology", "Agostinho Neto Mausoleum", "Ilha do Cabo"),
            historicalSignificance = "The fortress was the first Portuguese military stronghold in Angola and played a crucial role in the colonization process. It served as the governor's residence and administrative headquarters. During the slave trade era, the fortress was used to hold enslaved Africans before they were shipped to the Americas.",
            culturalRelevance = "The fortress represents Angola's complex colonial history and the beginning of Portuguese influence that shaped the country's culture, language, and architecture. Today, it serves as an educational site for Angolans and visitors to understand the country's past.",
            websiteUrl = null,
            contactInfo = "+244 222 310 225",
            rating = 4.2f,
            reviewCount = 520,
            tags = listOf("fortress", "colonial history", "museum", "military", "portuguese", "historical site", "architecture")
        ),
        
        AngolaTouristicSite(
            id = 4,
            name = "Miradouro da Lua (Moon Viewpoint)",
            description = "Miradouro da Lua (Moon Viewpoint) is a striking natural landscape located south of Luanda along the road to Kissama National Park. The site features unusual clay formations that have been eroded by rain and wind over centuries, creating a lunar-like landscape with various shapes and colors ranging from yellow to dark red. The formations change appearance throughout the day as the sun casts different shadows, with sunset offering particularly spectacular views. The viewpoint overlooks the Atlantic Ocean, adding to the scenic beauty of this natural wonder.",
            location = "Bengo Province, Angola",
            region = "Northern Angola",
            coordinates = Coordinates(-9.2167, 13.2333),
            category = listOf("Natural", "Geological", "Scenic"),
            images = listOf(
                "https://images.unsplash.com/photo-1518709268805-4e9042af9f23",
                "https://images.unsplash.com/photo-1516426122078-c23e76319801",
                "https://images.unsplash.com/photo-1549366021-9f761d450615"
            ),
            entryFee = "Free",
            openingHours = "24/7, but best visited during daylight hours",
            bestTimeToVisit = "Late afternoon for the best lighting and most dramatic shadows. May to October (dry season) for clearer views.",
            durationOfVisit = "30 minutes to 1 hour",
            accessibilityInfo = "The main viewpoint is accessible by car, but exploring the formations requires walking on uneven terrain.",
            localTips = "Visit on the way to or from Kissama National Park. Bring a camera with wide-angle lens for the best photos. Be careful near cliff edges as there are no safety barriers.",
            nearbyAttractions = listOf("Kissama National Park", "Cabo Ledo Beach", "Mussulo Peninsula"),
            historicalSignificance = "The area has been a landmark for travelers along the coastal route for centuries. The unusual formations have inspired local legends and stories.",
            culturalRelevance = "The site has become an iconic natural landmark of Angola, featured in tourism promotions and photography. It represents the country's diverse and unique landscapes beyond the more well-known wildlife areas.",
            websiteUrl = null,
            contactInfo = null,
            rating = 4.6f,
            reviewCount = 380,
            tags = listOf("geological formation", "lunar landscape", "natural wonder", "sunset", "photography", "scenic view")
        ),
        
        AngolaTouristicSite(
            id = 5,
            name = "Ilha do Cabo (Luanda Island)",
            description = "Ilha do Cabo, commonly known as Luanda Island or simply 'The Island,' is a narrow, 5-kilometer-long sandbar that forms a natural barrier between Luanda Bay and the Atlantic Ocean. Once a simple fishing community, the island has developed into a vibrant entertainment and leisure destination with beaches, restaurants, bars, and nightclubs. The island features a mix of upscale establishments and local venues, offering everything from traditional Angolan cuisine to international dining. The beaches on the ocean side are popular for swimming, sunbathing, and water sports, while the bay side offers calmer waters and views of Luanda's skyline.",
            location = "Luanda, Angola",
            region = "Northern Angola",
            coordinates = Coordinates(-8.7833, 13.2333),
            category = listOf("Beach", "Entertainment", "Coastal"),
            images = listOf(
                "https://images.unsplash.com/photo-1580060839134-75a5edca2e99",
                "https://images.unsplash.com/photo-1564868456298-7d4a1ae1a1a8",
                "https://images.unsplash.com/photo-1578774929812-c11262c7b8f1"
            ),
            entryFee = "Free to access the island, individual establishments have their own prices",
            openingHours = "24/7, but most establishments operate from around 10:00 AM until late evening",
            bestTimeToVisit = "Weekends for the vibrant atmosphere or weekdays for fewer crowds. December to March for the warmest beach weather.",
            durationOfVisit = "Half-day to full-day",
            accessibilityInfo = "The main areas are accessible, but beaches may present challenges for those with mobility issues.",
            localTips = "Visit in the late afternoon to enjoy the beach and then dinner with sunset views. Try local seafood dishes like calulu de peixe. Be prepared for premium prices at upscale establishments.",
            nearbyAttractions = listOf("Luanda Bay", "Fortaleza de São Miguel", "Agostinho Neto Mausoleum", "Luanda's Central Market"),
            historicalSignificance = "The island has transformed from a simple fishing community to a symbol of Angola's post-war development and growing tourism industry. Historically, it served as a natural protection for Luanda's harbor.",
            culturalRelevance = "The island represents the leisure and entertainment side of contemporary Angolan culture. It showcases the country's coastal lifestyle and has become a social hub where different classes of Angolan society mix with expatriates and tourists.",
            websiteUrl = null,
            contactInfo = null,
            rating = 4.4f,
            reviewCount = 950,
            tags = listOf("beach", "nightlife", "restaurants", "seafood", "relaxation", "water sports", "island")
        ),
        
        AngolaTouristicSite(
            id = 6,
            name = "Kalandula Falls",
            description = "Kalandula Falls (formerly known as Duque de Bragança Falls) are one of Africa's largest waterfalls, with a width of about 400 meters and a drop of 105 meters. Located on the Lucala River, the falls create a spectacular horseshoe-shaped cascade that is particularly impressive during the rainy season. The surrounding area features lush vegetation and offers hiking opportunities with views of the falls from different angles. The mist created by the falling water produces frequent rainbows, adding to the scenic beauty. Despite being one of Angola's most magnificent natural attractions, the falls remain relatively undeveloped for tourism, offering a more authentic experience.",
            location = "Malanje Province, Angola",
            region = "Northern Angola",
            coordinates = Coordinates(-9.1067, 15.9967),
            category = listOf("Natural", "Waterfall", "Scenic"),
            images = listOf(
                "https://images.unsplash.com/photo-1518709268805-4e9042af9f23",
                "https://images.unsplash.com/photo-1516426122078-c23e76319801",
                "https://images.unsplash.com/photo-1549366021-9f761d450615"
            ),
            entryFee = "Approximately 2,000 Kwanza (6 USD)",
            openingHours = "Daylight hours (no formal opening times)",
            bestTimeToVisit = "March to May, after the rainy season, when the falls are at their most powerful but access roads are becoming passable.",
            durationOfVisit = "2-3 hours",
            accessibilityInfo = "Access requires walking on uneven, sometimes slippery paths. Not suitable for those with mobility issues.",
            localTips = "A 4x4 vehicle is recommended for reaching the falls. Bring water and snacks as there are limited facilities. Wear sturdy shoes and be prepared to get wet from the mist.",
            nearbyAttractions = listOf("Black Rocks of Pungo Andongo", "Cangandala National Park", "Malanje City"),
            historicalSignificance = "The falls were renamed after independence from Portugal, changing from Duque de Bragança (named after a Portuguese royal title) to Kalandula, reflecting the local indigenous name.",
            culturalRelevance = "The falls hold spiritual significance for local communities and represent Angola's natural heritage. They are increasingly becoming a symbol of Angola's tourism potential beyond the capital city.",
            websiteUrl = null,
            contactInfo = null,
            rating = 4.8f,
            reviewCount = 320,
            tags = listOf("waterfall", "natural wonder", "hiking", "photography", "rainbow", "river", "landscape")
        ),
        
        AngolaTouristicSite(
            id = 7,
            name = "Black Rocks of Pungo Andongo",
            description = "The Black Rocks of Pungo Andongo (Pedras Negras de Pungo Andongo) are a series of mysterious, massive black rock formations rising dramatically from the surrounding plains. These gigantic geological formations, some reaching over 100 meters in height, are spread across an area of about 200 square kilometers. The rocks are primarily composed of granite and gneiss, with their dark coloration resulting from mineral composition and weathering. Local legends claim that some rocks bear the footprints of Queen Ginga, a powerful 17th-century ruler who resisted Portuguese colonization. The area offers hiking opportunities, cave explorations, and spectacular views from the top of accessible formations.",
            location = "Malanje Province, Angola",
            region = "Northern Angola",
            coordinates = Coordinates(-9.6667, 15.5833),
            category = listOf("Natural", "Geological", "Historical"),
            images = listOf(
                "https://images.unsplash.com/photo-1516426122078-c23e76319801",
                "https://images.unsplash.com/photo-1518709268805-4e9042af9f23",
                "https://images.unsplash.com/photo-1549366021-9f761d450615"
            ),
            entryFee = "Approximately 1,500 Kwanza (4.50 USD)",
            openingHours = "Daylight hours (no formal opening times)",
            bestTimeToVisit = "May to October (dry season) for easier access and hiking conditions.",
            durationOfVisit = "2-4 hours",
            accessibilityInfo = "Exploring the rocks requires moderate fitness for climbing and walking on uneven terrain. Not suitable for those with mobility issues.",
            localTips = "Hire a local guide to learn about the historical and cultural significance of the rocks. Bring plenty of water and sun protection. Some areas may require permission from local authorities to visit.",
            nearbyAttractions = listOf("Kalandula Falls", "Cangandala National Park", "Malanje City"),
            historicalSignificance = "The area was a stronghold for Queen Ginga (Nzinga Mbande) during her resistance against Portuguese colonization in the 17th century. The rocks also served as a refuge during Angola's civil war.",
            culturalRelevance = "The rocks feature in many Angolan legends and are considered sacred by some local communities. They represent the intersection of Angola's natural beauty, cultural heritage, and resistance to colonization.",
            websiteUrl = null,
            contactInfo = null,
            rating = 4.5f,
            reviewCount = 210,
            tags = listOf("rock formations", "geology", "queen ginga", "hiking", "caves", "cultural heritage", "natural monument")
        ),
        
        AngolaTouristicSite(
            id = 8,
            name = "Tundavala Gap",
            description = "The Tundavala Gap is a dramatic geological formation located in the Serra da Leba mountain range, offering one of Angola's most breathtaking viewpoints. The site features a massive natural crevice where the Huíla plateau suddenly drops about 1,200 meters (nearly 4,000 feet) to the plains below. From the viewpoint, visitors can see an expansive panorama stretching seemingly endlessly toward the horizon, with the provinces of Huíla and Namibe visible on clear days. The area surrounding the gap features unique flora adapted to the high-altitude conditions, as well as interesting rock formations shaped by erosion over millions of years.",
            location = "Huíla Province, Angola",
            region = "Southern Angola",
            coordinates = Coordinates(-14.8167, 13.4000),
            category = listOf("Natural", "Geological", "Scenic"),
            images = listOf(
                "https://images.unsplash.com/photo-1549366021-9f761d450615",
                "https://images.unsplash.com/photo-1518709268805-4e9042af9f23",
                "https://images.unsplash.com/photo-1516426122078-c23e76319801"
            ),
            entryFee = "Free",
            openingHours = "24/7, but best visited during daylight hours",
            bestTimeToVisit = "Early morning for the best visibility and photography conditions. May to October (dry season) for clearer views.",
            durationOfVisit = "1-3 hours",
            accessibilityInfo = "The main viewpoint can be reached by car, but exploring the area requires walking on uneven terrain. Caution is needed near the cliff edges as there are few safety barriers.",
            localTips = "Visit on a clear day for the best views. Bring warm clothing as it can be windy and cool at the high elevation. Early morning offers the best lighting for photography and fewer clouds.",
            nearbyAttractions = listOf("Lubango City", "Christ the King statue (Cristo Rei)", "Serra da Leba Pass", "Bicuar National Park"),
            historicalSignificance = "The area has been considered sacred by local indigenous groups for centuries. The name 'Tundavala' comes from the local Nhaneca-Humbe language.",
            culturalRelevance = "The gap represents the natural beauty of Angola's highlands and has become an iconic symbol of Huíla Province. It features prominently in Angolan tourism promotions and is increasingly drawing domestic and international visitors.",
            websiteUrl = null,
            contactInfo = null,
            rating = 4.9f,
            reviewCount = 280,
            tags = listOf("viewpoint", "cliff", "panorama", "mountains", "geological formation", "photography", "hiking")
        ),
        
        AngolaTouristicSite(
            id = 9,
            name = "Benguela Railway Museum",
            description = "The Benguela Railway Museum (Museu do Caminho de Ferro de Benguela) is dedicated to preserving and showcasing the history of the Benguela Railway, one of Africa's most important colonial-era infrastructure projects. The museum is housed in a beautifully restored railway station building and features vintage locomotives, carriages, equipment, photographs, maps, and documents that tell the story of this engineering marvel. The Benguela Railway, completed in 1929, connected Angola's Atlantic coast to the copper mines of the Belgian Congo (now Democratic Republic of Congo) and Zambia, spanning over 1,300 kilometers. The museum highlights the technical challenges overcome during construction, the railway's economic importance, and its role in regional development.",
            location = "Benguela, Angola",
            region = "Western Angola",
            coordinates = Coordinates(-12.5833, 13.4167),
            category = listOf("Museum", "Historical", "Cultural"),
            images = listOf(
                "https://images.unsplash.com/photo-1564868456298-7d4a1ae1a1a8",
                "https://images.unsplash.com/photo-1580060839134-75a5edca2e99",
                "https://images.unsplash.com/photo-1578774929812-c11262c7b8f1"
            ),
            entryFee = "Approximately 1,000 Kwanza (3 USD)",
            openingHours = "9:00 AM - 4:00 PM, Tuesday to Sunday (Closed on Mondays)",
            bestTimeToVisit = "Year-round, weekday mornings for fewer visitors.",
            durationOfVisit = "1-2 hours",
            accessibilityInfo = "The main exhibition areas are accessible, though some historic train carriages may have steps for entry.",
            localTips = "Combine with a visit to Benguela's colonial architecture and beaches. Some exhibits have limited English descriptions, so a Portuguese-speaking guide can be helpful.",
            nearbyAttractions = listOf("Benguela City Center", "Baía Azul Beach", "Catumbela", "São Filipe Fortress"),
            historicalSignificance = "The Benguela Railway was one of colonial Africa's most ambitious infrastructure projects, built between 1903 and 1929. It played a crucial role in the economic development of central Africa, facilitating mineral exports and connecting inland regions to the coast. The railway suffered extensive damage during Angola's civil war but has been rehabilitated in recent years.",
            culturalRelevance = "The railway represents both colonial exploitation and engineering achievement. It shaped settlement patterns and economic development in Angola and continues to be an important transportation link. The museum preserves this complex legacy for future generations.",
            websiteUrl = null,
            contactInfo = "+244 923 456 789",
            rating = 4.3f,
            reviewCount = 175,
            tags = listOf("railway", "colonial history", "transportation", "engineering", "museum", "locomotives", "industrial heritage")
        ),
        
        AngolaTouristicSite(
            id = 10,
            name = "Serra da Leba Pass",
            description = "Serra da Leba Pass is a spectacular mountain pass featuring a winding road with hairpin turns that dramatically descends from the Huíla plateau to the lower plains of Namibe Province. The road, built during the Portuguese colonial era in the 1970s, is considered an engineering marvel and one of Africa's most scenic drives. The pass offers breathtaking views of the surrounding mountains and valleys, with dramatic changes in vegetation as the altitude decreases. The most iconic feature is a horseshoe-shaped bend in the road that has become one of Angola's most photographed landmarks. The pass connects the cooler highland city of Lubango with the arid coastal regions, showcasing Angola's diverse landscapes in a single journey.",
            location = "Between Huíla and Namibe Provinces, Angola",
            region = "Southern Angola",
            coordinates = Coordinates(-15.0833, 13.2500),
            category = listOf("Scenic", "Road Trip", "Engineering"),
            images = listOf(
                "https://images.unsplash.com/photo-1518709268805-4e9042af9f23",
                "https://images.unsplash.com/photo-1516426122078-c23e76319801",
                "https://images.unsplash.com/photo-1549366021-9f761d450615"
            ),
            entryFee = "Free",
            openingHours = "24/7, but best traveled during daylight hours for safety and views",
            bestTimeToVisit = "Early morning for the best lighting and photography. May to October (dry season) for clearer views and safer driving conditions.",
            durationOfVisit = "1-2 hours for stops and photography along the pass",
            accessibilityInfo = "Accessible by vehicle, but the road is steep and winding. Viewpoints can be reached by car.",
            localTips = "Drive carefully as the road has sharp turns and steep drops. Stop at designated viewpoints for photos. Be aware of changing weather conditions, as fog can develop quickly at higher elevations.",
            nearbyAttractions = listOf("Lubango City", "Tundavala Gap", "Christ the King statue (Cristo Rei)", "Namibe Desert"),
            historicalSignificance = "The pass was constructed in the early 1970s during the late Portuguese colonial period as part of efforts to improve transportation between the highlands and the coast. The engineering feat was remarkable for its time, especially considering the challenging terrain.",
            culturalRelevance = "The Serra da Leba Pass has become an iconic symbol of Angola's natural beauty and is featured on postcards, in tourism promotions, and even on the country's currency. It represents the connection between Angola's diverse geographical regions and ecosystems.",
            websiteUrl = null,
            contactInfo = null,
            rating = 4.7f,
            reviewCount = 310,
            tags = listOf("mountain pass", "scenic drive", "hairpin turns", "engineering", "photography", "road trip", "viewpoint")
        )
    )
}
