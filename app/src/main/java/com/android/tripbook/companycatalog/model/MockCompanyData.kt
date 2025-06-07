
/*
- This object allows the app to function and display realistic company information
  without needing actual backend integration.
- For UI testing purposes and ensure that it works well.
 */
package com.android.tripbook.companycatalog.model

object MockCompanyData {
    
    fun getCameroonCompanies(): List<Company> {
        return listOf(
            Company(
                id = "1",
                name = "Cameroon Safari Adventures",
                description = "Experience the wild beauty of Cameroon with our expert-guided safari tours through national parks and wildlife reserves.",
                category = "Safari Tours",
                location = "Yaoundé",
                rating = 4.8f,
                reviewCount = 142,
                imageUrl = "https://example.com/safari_cameroon.jpg",
                contact = CompanyContact(
                    phone = "+237 6XX XX XX XX",
                    email = "info@cameroon-safari.com",
                    address = "Avenue Kennedy, Quartier Bastos",
                    city = "Yaoundé",
                    region = "Centre Region",
                    whatsapp = "+237 6XX XX XX XX",
                    facebook = "CameroonSafariAdventures"
                ),
                services = listOf(
                    CompanyService("s1", "Waza National Park Safari", "3-day wildlife viewing expedition", "150,000 FCFA", "3 days", true),
                    CompanyService("s2", "Benoue Wildlife Tour", "Bird watching and game viewing", "80,000 FCFA", "2 days"),
                    CompanyService("s3", "Mount Cameroon Hiking", "Guided trek to Cameroon's highest peak", "200,000 FCFA", "4 days", true)
                ),
                priceRange = "₣₣₣",
                website = "www.cameroon-safari.com",
                established = 2010,
                specialties = listOf("Wildlife Photography", "Bird Watching", "Cultural Tours")
            ),
            
            Company(
                id = "2",
                name = "Douala City Tours",
                description = "Discover the economic capital of Cameroon with our comprehensive city tours and cultural experiences.",
                category = "City Tours",
                location = "Douala",
                rating = 4.3f,
                reviewCount = 89,
                imageUrl = "https://example.com/douala_tours.jpg",
                contact = CompanyContact(
                    phone = "+237 6XX XX XX XX",
                    email = "contact@douala-tours.cm",
                    address = "Boulevard de la Liberté, Akwa",
                    city = "Douala",
                    region = "Littoral Region",
                    whatsapp = "+237 6XX XX XX XX",
                    instagram = "douala_city_tours"
                ),
                services = listOf(
                    CompanyService("s4", "Historical Douala Walking Tour", "Explore colonial architecture and local markets", "25,000 FCFA", "4 hours"),
                    CompanyService("s5", "Wouri River Cruise", "Evening boat tour with dinner", "45,000 FCFA", "3 hours", true),
                    CompanyService("s6", "Bonapriso Art District Tour", "Visit galleries and meet local artists", "30,000 FCFA", "3 hours")
                ),
                priceRange = "₣₣",
                website = "www.douala-tours.cm",
                established = 2015,
                specialties = listOf("Urban Exploration", "Art & Culture", "River Tours")
            ),

            Company(
                id = "3",
                name = "Kribi Beach Resort",
                description = "Relax at Cameroon's most beautiful coastal destination with pristine beaches and luxury accommodations.",
                category = "Beach Resorts",
                location = "Kribi",
                rating = 4.6f,
                reviewCount = 203,
                imageUrl = "https://example.com/kribi_beach.jpg",
                contact = CompanyContact(
                    phone = "+237 6XX XX XX XX",
                    email = "reservations@kribi-resort.com",
                    address = "Plage de Grand Batanga",
                    city = "Kribi",
                    region = "South Region",
                    whatsapp = "+237 6XX XX XX XX",
                    facebook = "KribiBeachResort",
                    instagram = "kribi_beach_resort"
                ),
                services = listOf(
                    CompanyService("s7", "Deluxe Ocean View Suite", "Luxury accommodation with private beach access", "180,000 FCFA", "per night", true),
                    CompanyService("s8", "Lobe Waterfalls Excursion", "Visit the spectacular coastal waterfalls", "35,000 FCFA", "Half day"),
                    CompanyService("s9", "Deep Sea Fishing", "Professional fishing expedition", "120,000 FCFA", "Full day")
                ),
                priceRange = "₣₣₣₣",
                website = "www.kribi-resort.com",
                established = 2008,
                specialties = listOf("Beach Activities", "Water Sports", "Fine Dining")
            ),

            Company(
                id = "4",
                name = "Bamenda Highlands Trekking",
                description = "Explore the scenic Ring Road and highlands of Northwest Cameroon with expert local guides.",
                category = "Adventure Tours",
                location = "Bamenda",
                rating = 4.5f,
                reviewCount = 67,
                imageUrl = "https://example.com/bamenda_trek.jpg",
                contact = CompanyContact(
                    phone = "+237 6XX XX XX XX",
                    email = "info@bamenda-trekking.org",
                    address = "Commercial Avenue, Up Station",
                    city = "Bamenda",
                    region = "Northwest Region",
                    whatsapp = "+237 6XX XX XX XX"
                ),
                services = listOf(
                    CompanyService("s10", "Ring Road Cultural Circuit", "7-day tour of traditional kingdoms", "250,000 FCFA", "7 days", true),
                    CompanyService("s11", "Mount Oku Hiking", "Trek to Cameroon's second highest peak", "120,000 FCFA", "3 days"),
                    CompanyService("s12", "Lake Nyos Educational Tour", "Geological and cultural expedition", "80,000 FCFA", "2 days")
                ),
                priceRange = "₣₣",
                established = 2012,
                specialties = listOf("Mountain Hiking", "Cultural Immersion", "Traditional Architecture")
            ),

            Company(
                id = "5",
                name = "Maroua Cultural Center",
                description = "Immerse yourself in the rich culture of Far North Cameroon with traditional crafts and desert adventures.",
                category = "Cultural Tours",
                location = "Maroua",
                rating = 4.2f,
                reviewCount = 45,
                imageUrl = "https://example.com/maroua_culture.jpg",
                contact = CompanyContact(
                    phone = "+237 6XX XX XX XX",
                    email = "welcome@maroua-culture.cm",
                    address = "Quartier Domayo",
                    city = "Maroua",
                    region = "Far North Region",
                    whatsapp = "+237 6XX XX XX XX",
                    facebook = "MarouaCulturalCenter"
                ),
                services = listOf(
                    CompanyService("s13", "Traditional Pottery Workshop", "Learn from local Mousgoum artisans", "40,000 FCFA", "Full day"),
                    CompanyService("s14", "Mandara Mountains Tour", "Visit traditional villages and markets", "90,000 FCFA", "2 days", true),
                    CompanyService("s15", "Logone River Expedition", "Canoe trip and fishing experience", "60,000 FCFA", "Day trip")
                ),
                priceRange = "₣₣",
                established = 2018,
                specialties = listOf("Traditional Crafts", "Desert Culture", "Local Markets")
            ),

            Company(
                id = "6",
                name = "Limbe Botanical Gardens",
                description = "Discover Cameroon's biodiversity at our world-renowned botanical gardens and research center.",
                category = "Eco Tourism",
                location = "Limbe",
                rating = 4.4f,
                reviewCount = 156,
                imageUrl = "https://example.com/limbe_gardens.jpg",
                contact = CompanyContact(
                    phone = "+237 6XX XX XX XX",
                    email = "visit@limbe-gardens.org",
                    address = "Botanic Garden Road",
                    city = "Limbe",
                    region = "Southwest Region",
                    whatsapp = "+237 6XX XX XX XX",
                    instagram = "limbe_botanical"
                ),
                services = listOf(
                    CompanyService("s16", "Guided Garden Tour", "Educational walk through diverse ecosystems", "15,000 FCFA", "2 hours"),
                    CompanyService("s17", "Research Center Visit", "Learn about conservation efforts", "25,000 FCFA", "3 hours", true),
                    CompanyService("s18", "Night Forest Sounds Tour", "Evening wildlife listening experience", "30,000 FCFA", "2 hours")
                ),
                priceRange = "₣",
                website = "www.limbe-gardens.org",
                established = 1892,
                specialties = listOf("Biodiversity", "Conservation", "Research")
            ),

            Company(
                id = "7",
                name = "Garoua River Adventures",
                description = "Experience the mighty Benue River with fishing, boat tours, and wildlife watching in North Cameroon.",
                category = "River Tours",
                location = "Garoua",
                rating = 4.1f,
                reviewCount = 38,
                imageUrl = "https://example.com/garoua_river.jpg",
                contact = CompanyContact(
                    phone = "+237 6XX XX XX XX",
                    email = "adventures@garoua-river.com",
                    address = "Port de Garoua",
                    city = "Garoua",
                    region = "North Region",
                    whatsapp = "+237 6XX XX XX XX"
                ),
                services = listOf(
                    CompanyService("s19", "Benue River Fishing", "Traditional and sport fishing expeditions", "70,000 FCFA", "Full day"),
                    CompanyService("s20", "Hippo Watching Tour", "Safe observation of river wildlife", "50,000 FCFA", "Half day", true),
                    CompanyService("s21", "River Island Camping", "Overnight camping on secluded islands", "95,000 FCFA", "2 days")
                ),
                priceRange = "₣₣",
                established = 2016,
                specialties = listOf("River Fishing", "Wildlife Viewing", "Camping")
            ),

            Company(
                id = "8",
                name = "Foumban Royal Palace Tours",
                description = "Explore the rich history and royal heritage of the Bamoun Kingdom in Foumban.",
                category = "Historical Tours",
                location = "Foumban",
                rating = 4.7f,
                reviewCount = 94,
                imageUrl = "https://example.com/foumban_palace.jpg",
                contact = CompanyContact(
                    phone = "+237 6XX XX XX XX",
                    email = "tours@foumban-palace.cm",
                    address = "Near Royal Palace",
                    city = "Foumban",
                    region = "West Region",
                    whatsapp = "+237 6XX XX XX XX",
                    facebook = "FoumbanRoyalTours"
                ),
                services = listOf(
                    CompanyService("s22", "Royal Palace Museum Tour", "Guided tour of Bamoun royal artifacts", "20,000 FCFA", "2 hours", true),
                    CompanyService("s23", "Traditional Crafts Workshop", "Learn bronze casting and wood carving", "55,000 FCFA", "Full day"),
                    CompanyService("s24", "Sultan's Archive Visit", "Explore ancient manuscripts and history", "30,000 FCFA", "1.5 hours")
                ),
                priceRange = "₣₣",
                website = "www.foumban-heritage.cm",
                established = 2005,
                specialties = listOf("Royal History", "Traditional Arts", "Cultural Heritage")
            )
        )
    }
}
