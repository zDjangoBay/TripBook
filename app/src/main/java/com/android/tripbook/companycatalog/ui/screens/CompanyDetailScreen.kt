package com.android.tripbook.companycatalog.ui.screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.tripbook.companycatalog.data.MockCompanyData
import com.android.tripbook.companycatalog.ui.components.*
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyDetailScreen(companyId: String, initialTab: Int = 0) {
    val company = MockCompanyData.companies.find { it.id == companyId }
    var selectedTab by remember { mutableStateOf(initialTab) }

    if (company == null) {
        Text("Company not found", modifier = Modifier.padding(16.dp))
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Company Header Card
            item {
                CompanyHeaderCard(company = company)
            }

            // Navigation Tabs
            item {
                CompanyDetailTabs(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )
            }

            // Tab Content
            item {
                when (selectedTab) {
                    0 -> CompanyOverviewSection(company = company)
                    1 -> CompanyServicesShowcase(
                        companyId = companyId,
                        services = getSampleServices(),
                        onServiceClick = { /* Handle service click */ },
                        onRequestQuote = { /* Handle quote request */ },
                        onContactForService = { /* Handle contact */ }
                    )
                    2 -> CompanyReviewsWidget(
                        companyId = companyId,
                        reviews = getSampleReviews(),
                        averageRating = company.averageRating,
                        totalReviews = company.numberOfReviews,
                        onWriteReview = { /* Handle write review */ },
                        onReviewHelpful = { /* Handle helpful */ },
                        onReportReview = { /* Handle report */ }
                    )
                    3 -> CompanyContactHub(
                        companyId = companyId,
                        companyName = company.name,
                        contactMethods = getSampleContactMethods(company),
                        businessHours = getSampleBusinessHours(),
                        contactPersons = getSampleContactPersons(),
                        onContactMethodClick = { /* Handle contact method */ },
                        onSendMessage = { _, _, _, _ -> /* Handle send message */ }
                    )
                    4 -> CompanyAnalyticsDashboard(
                        analytics = getSampleAnalytics(companyId),
                        onMetricClick = { /* Handle metric click */ },
                        onExportData = { /* Handle export */ }
                    )
                    5 -> CompanyPortfolioGallery(
                        companyId = companyId,
                        projects = getSamplePortfolioProjects(),
                        onProjectClick = { /* Handle project click */ },
                        onCategoryFilter = { /* Handle category filter */ },
                        onContactForProject = { /* Handle contact for project */ }
                    )
                    6 -> CompanyComparisonTool(
                        availableCompanies = getSampleComparisonCompanies(),
                        selectedCompanies = listOf(companyId),
                        onCompanySelect = { /* Handle company select */ },
                        onCompanyRemove = { /* Handle company remove */ },
                        onClearComparison = { /* Handle clear comparison */ },
                        onContactCompany = { /* Handle contact company */ }
                    )
                }
            }
        }
    }
}

@Composable
private fun CompanyHeaderCard(company: com.android.tripbook.companycatalog.data.CompanyData) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Company logo
            Image(
                painter = painterResource(id = company.logoResId),
                contentDescription = "${company.name} Logo",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(bottom = 12.dp),
                contentScale = ContentScale.Fit
            )

            // Company name
            Text(
                text = company.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Description
            Text(
                text = company.description,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Rating
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(5) { index ->
                    Icon(
                        imageVector = if (index < company.averageRating.toInt()) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = null,
                        tint = Color(0xFFFFB000),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = "${company.averageRating} (${company.numberOfReviews} reviews)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun CompanyDetailTabs(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val tabs = listOf("Overview", "Services", "Reviews", "Contact", "Analytics", "Portfolio", "Compare")

    ScrollableTabRow(
        selectedTabIndex = selectedTab,
        modifier = Modifier.fillMaxWidth()
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                text = { Text(title) }
            )
        }
    }
}

@Composable
private fun CompanyOverviewSection(company: com.android.tripbook.companycatalog.data.CompanyData) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Services Offered
            Text("Services Offered:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            company.servicesOffered.forEach {
                Text("• $it", style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Contact Info
            company.contactInfo.let { contacts ->
                Text("Contact Info:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                contacts.forEach { (type, value) ->
                    Text("$type: $value", style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Address
            Text("Address:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(company.address, style = MaterialTheme.typography.bodyMedium)

            // Social Media
            company.socialMediaLinks?.takeIf { it.isNotEmpty() }?.let { links ->
                Spacer(modifier = Modifier.height(16.dp))
                Text("Social Media:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                links.forEach { (platform, url) ->
                    Text("$platform: $url", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

// Sample data functions for the new UI components
private fun getSampleServices(): List<CompanyService> {
    return listOf(
        CompanyService(
            id = "service_1",
            name = "Guided City Tours",
            description = "Professional guided tours of the city's most famous landmarks, historical sites, and hidden gems",
            category = ServiceCategory.CONSULTING,
            price = ServicePrice(45.0, "USD", PriceUnit.HOURLY),
            duration = "2-4 hours",
            availability = ServiceAvailability.AVAILABLE,
            isPopular = true,
            rating = 4.8f,
            reviewCount = 342
        ),
        CompanyService(
            id = "service_2",
            name = "Adventure Activities",
            description = "Exciting outdoor adventures including hiking, rock climbing, zip-lining, and water sports",
            category = ServiceCategory.TRAINING,
            price = ServicePrice(85.0, "USD", PriceUnit.DAILY),
            duration = "Full day",
            availability = ServiceAvailability.AVAILABLE,
            rating = 4.9f,
            reviewCount = 228
        ),
        CompanyService(
            id = "service_3",
            name = "Cultural Experiences",
            description = "Immersive cultural activities including cooking classes, art workshops, and traditional performances",
            category = ServiceCategory.DESIGN,
            price = ServicePrice(65.0, "USD", PriceUnit.HOURLY),
            duration = "2-6 hours",
            availability = ServiceAvailability.LIMITED,
            rating = 4.7f,
            reviewCount = 185
        ),
        CompanyService(
            id = "service_4",
            name = "Transportation Services",
            description = "Convenient transportation options including airport transfers, car rentals, and private drivers",
            category = ServiceCategory.SUPPORT,
            price = ServicePrice(25.0, "USD", PriceUnit.HOURLY),
            duration = "As needed",
            availability = ServiceAvailability.AVAILABLE,
            rating = 4.6f,
            reviewCount = 156
        ),
        CompanyService(
            id = "service_5",
            name = "Accommodation Booking",
            description = "Wide selection of accommodations from budget hostels to luxury resorts with best price guarantees",
            category = ServiceCategory.SUPPORT,
            price = ServicePrice(15.0, "USD", PriceUnit.FIXED),
            duration = "Booking fee",
            availability = ServiceAvailability.AVAILABLE,
            rating = 4.5f,
            reviewCount = 423
        )
    )
}

private fun getSampleReviews(): List<CompanyReview> {
    return listOf(
        CompanyReview(
            id = "review_1",
            authorName = "Sarah Johnson",
            authorInitials = "SJ",
            rating = 5.0f,
            title = "Amazing Cultural Experience",
            content = "This destination exceeded all my expectations! The guided tours were incredibly informative and the local guides were passionate about sharing their culture. The food experiences were unforgettable and I learned so much about the local traditions.",
            date = Date(System.currentTimeMillis() - 86400000L * 3),
            isVerified = true,
            helpfulCount = 23,
            category = ReviewCategory.SERVICE_QUALITY
        ),
        CompanyReview(
            id = "review_2",
            authorName = "Michael Chen",
            authorInitials = "MC",
            rating = 4.7f,
            title = "Perfect Adventure Destination",
            content = "The adventure activities here are world-class! From hiking the scenic trails to the thrilling zip-line experiences, every moment was exciting. The safety standards were excellent and the staff was very professional.",
            date = Date(System.currentTimeMillis() - 86400000L * 7),
            isVerified = true,
            helpfulCount = 18,
            category = ReviewCategory.RELIABILITY
        ),
        CompanyReview(
            id = "review_3",
            authorName = "Emma Rodriguez",
            authorInitials = "ER",
            rating = 4.9f,
            title = "Great Value for Money",
            content = "This destination offers incredible value! The accommodation options are diverse and affordable, the food is amazing and reasonably priced, and there are so many free activities to enjoy. Perfect for budget travelers.",
            date = Date(System.currentTimeMillis() - 86400000L * 12),
            isVerified = true,
            helpfulCount = 31,
            category = ReviewCategory.VALUE_FOR_MONEY
        ),
        CompanyReview(
            id = "review_4",
            authorName = "David Thompson",
            authorInitials = "DT",
            rating = 4.6f,
            title = "Excellent Tourist Support",
            content = "The local tourism office and guides were incredibly helpful throughout our stay. They provided great recommendations, helped with transportation, and were always available to answer questions. Made our trip stress-free!",
            date = Date(System.currentTimeMillis() - 86400000L * 18),
            isVerified = false,
            helpfulCount = 14,
            category = ReviewCategory.CUSTOMER_SUPPORT
        ),
        CompanyReview(
            id = "review_5",
            authorName = "Lisa Martinez",
            authorInitials = "LM",
            rating = 4.8f,
            title = "Beautiful Scenic Location",
            content = "The natural beauty of this place is breathtaking! The landscapes are stunning, perfect for photography, and there are so many scenic viewpoints. The sunset views from the main lookout are absolutely magical.",
            date = Date(System.currentTimeMillis() - 86400000L * 25),
            isVerified = true,
            helpfulCount = 27,
            category = ReviewCategory.SERVICE_QUALITY
        )
    )
}

private fun getSampleContactMethods(company: com.android.tripbook.companycatalog.data.CompanyData): List<ContactMethod> {
    return listOf(
        ContactMethod(
            id = "contact_1",
            type = ContactType.PHONE,
            value = company.contactInfo["phone"] ?: "+1 (555) 847-2100",
            label = "Tourist Information Center",
            isPrimary = true,
            responseTime = "Daily 8 AM - 8 PM"
        ),
        ContactMethod(
            id = "contact_2",
            type = ContactType.EMAIL,
            value = company.contactInfo["email"] ?: "info@visitdestination.com",
            label = "Travel Inquiries",
            responseTime = "Within 24 hours"
        ),
        ContactMethod(
            id = "contact_3",
            type = ContactType.EMAIL,
            value = "bookings@visitdestination.com",
            label = "Booking & Reservations",
            responseTime = "Within 4 hours"
        ),
        ContactMethod(
            id = "contact_4",
            type = ContactType.WEBSITE,
            value = company.contactInfo["website"] ?: "https://www.visitdestination.com",
            label = "Official Tourism Website",
            responseTime = "24/7 access"
        ),
        ContactMethod(
            id = "contact_5",
            type = ContactType.FACEBOOK,
            value = "https://facebook.com/visitdestination",
            label = "Facebook Travel Page",
            responseTime = "Within 12 hours"
        ),
        ContactMethod(
            id = "contact_6",
            type = ContactType.ADDRESS,
            value = company.address,
            label = "Visitor Center Location",
            responseTime = "Open daily 9 AM - 6 PM"
        )
    )
}

private fun getSampleBusinessHours(): List<BusinessHours> {
    return listOf(
        BusinessHours("Monday", "9:00 AM", "6:00 PM"),
        BusinessHours("Tuesday", "9:00 AM", "6:00 PM"),
        BusinessHours("Wednesday", "9:00 AM", "6:00 PM"),
        BusinessHours("Thursday", "9:00 AM", "6:00 PM"),
        BusinessHours("Friday", "9:00 AM", "5:00 PM"),
        BusinessHours("Saturday", "10:00 AM", "2:00 PM"),
        BusinessHours("Sunday", "", "", false)
    )
}

private fun getSampleContactPersons(): List<ContactPerson> {
    return listOf(
        ContactPerson(
            id = "person_1",
            name = "Maria Santos",
            title = "Tourism Director",
            department = "Visitor Services",
            email = "maria.santos@visitdestination.com",
            phone = "+1 (555) 847-2101"
        ),
        ContactPerson(
            id = "person_2",
            name = "James Wilson",
            title = "Adventure Activities Coordinator",
            department = "Outdoor Recreation",
            email = "james.wilson@visitdestination.com",
            phone = "+1 (555) 847-2102"
        ),
        ContactPerson(
            id = "person_3",
            name = "Elena Rodriguez",
            title = "Cultural Events Manager",
            department = "Cultural Affairs",
            email = "elena.rodriguez@visitdestination.com",
            phone = "+1 (555) 847-2103"
        ),
        ContactPerson(
            id = "person_4",
            name = "David Kim",
            title = "Transportation Coordinator",
            department = "Travel Services",
            email = "david.kim@visitdestination.com",
            phone = "+1 (555) 847-2104"
        ),
        ContactPerson(
            id = "person_5",
            name = "Sophie Chen",
            title = "Accommodation Specialist",
            department = "Hospitality Services",
            email = "sophie.chen@visitdestination.com",
            phone = "+1 (555) 847-2105"
        )
    )
}

private fun getSampleAnalytics(companyId: String): CompanyAnalytics {
    return CompanyAnalytics(
        companyId = companyId,
        totalViews = 18750,
        totalContacts = 542,
        totalQuotes = 167,
        averageRating = 4.7f,
        totalReviews = 423,
        monthlyGrowth = 15.8f,
        popularServices = listOf(
            ServiceAnalytics("Guided City Tours", 4200, 89, 21.2f),
            ServiceAnalytics("Adventure Activities", 3850, 76, 19.7f),
            ServiceAnalytics("Cultural Experiences", 3100, 62, 20.0f),
            ServiceAnalytics("Transportation Services", 2800, 58, 20.7f),
            ServiceAnalytics("Accommodation Booking", 2500, 45, 18.0f)
        ),
        viewsOverTime = listOf(
            ViewsData("Mon", 2180),
            ViewsData("Tue", 2420),
            ViewsData("Wed", 2650),
            ViewsData("Thu", 2350),
            ViewsData("Fri", 3280),
            ViewsData("Sat", 3820),
            ViewsData("Sun", 3050)
        ),
        ratingDistribution = listOf(
            RatingData(5, 198),
            RatingData(4, 156),
            RatingData(3, 45),
            RatingData(2, 18),
            RatingData(1, 6)
        ),
        contactSources = listOf(
            ContactSourceData("Tourism Website", 217, 40.0f),
            ContactSourceData("Social Media", 162, 29.9f),
            ContactSourceData("Travel Blogs", 108, 19.9f),
            ContactSourceData("Word of Mouth", 55, 10.1f)
        )
    )
}

private fun getSamplePortfolioProjects(): List<PortfolioProject> {
    return listOf(
        PortfolioProject(
            id = "project_1",
            title = "Historic Downtown Walking Tour",
            description = "Comprehensive guided walking tour through the historic downtown district, featuring 15 landmark buildings, local history, and architectural highlights.",
            category = ProjectCategory.WEB_DEVELOPMENT,
            imageUrl = null,
            clientName = "Heritage Tourism Board",
            completionDate = Date(System.currentTimeMillis() - 86400000L * 45),
            duration = "3 hours",
            technologies = listOf("Audio Guide", "GPS Navigation", "Historical Maps", "Photo Points", "QR Codes"),
            projectValue = "Free with donation",
            isFeature = true,
            tags = listOf("History", "Architecture", "Culture"),
            testimonial = ProjectTestimonial(
                clientName = "Margaret Thompson",
                clientTitle = "Visitor from Boston",
                content = "The walking tour was absolutely fascinating! Our guide was incredibly knowledgeable and brought the city's history to life. Highly recommend for anyone interested in local culture.",
                rating = 5.0f
            ),
            metrics = ProjectMetrics(
                performanceImprovement = "+85% visitor satisfaction",
                userGrowth = "+45% repeat visitors",
                revenueIncrease = "+32% local business visits",
                timeToMarket = "Available year-round"
            )
        ),
        PortfolioProject(
            id = "project_2",
            title = "Mountain Adventure Experience",
            description = "Full-day mountain adventure including guided hiking, rock climbing, scenic viewpoints, and traditional mountain lunch with local specialties.",
            category = ProjectCategory.CONSULTING,
            imageUrl = null,
            clientName = "Adventure Tourism Collective",
            completionDate = Date(System.currentTimeMillis() - 86400000L * 75),
            duration = "8 hours",
            technologies = listOf("Safety Equipment", "Professional Guides", "GPS Tracking", "Emergency Communication", "Photography Service"),
            projectValue = "$120 per person",
            isFeature = true,
            tags = listOf("Adventure", "Hiking", "Nature"),
            testimonial = ProjectTestimonial(
                clientName = "Alex Johnson",
                clientTitle = "Adventure Traveler from Canada",
                content = "Incredible mountain experience! The guides were professional and safety-focused. The views were breathtaking and the lunch was delicious. Perfect for adventure seekers!",
                rating = 4.9f
            ),
            metrics = ProjectMetrics(
                performanceImprovement = "+90% safety record",
                userGrowth = "+65% return customers",
                revenueIncrease = "+45% group bookings",
                timeToMarket = "Seasonal availability"
            )
        ),
        PortfolioProject(
            id = "project_3",
            title = "Cultural Heritage Festival",
            description = "Annual cultural festival celebrating local traditions with live music, traditional dance performances, artisan crafts, and authentic regional cuisine.",
            category = ProjectCategory.WEB_DEVELOPMENT,
            imageUrl = null,
            clientName = "Cultural Heritage Foundation",
            completionDate = Date(System.currentTimeMillis() - 86400000L * 30),
            duration = "3 days",
            technologies = listOf("Live Performances", "Artisan Workshops", "Food Vendors", "Cultural Exhibits", "Interactive Displays"),
            projectValue = "$25 entry fee",
            isFeature = false,
            tags = listOf("Culture", "Festival", "Traditions"),
            testimonial = ProjectTestimonial(
                clientName = "Carmen Rodriguez",
                clientTitle = "Cultural Enthusiast from Spain",
                content = "The festival was a wonderful celebration of local culture! The performances were authentic and engaging, and I learned so much about the region's traditions. The food was exceptional!",
                rating = 4.8f
            )
        ),
        PortfolioProject(
            id = "project_4",
            title = "Coastal Wildlife Safari",
            description = "Guided wildlife watching experience along the coastal areas with opportunities to see dolphins, seabirds, and marine life in their natural habitat.",
            category = ProjectCategory.MOBILE_APP,
            imageUrl = null,
            clientName = "Marine Conservation Society",
            completionDate = Date(System.currentTimeMillis() - 86400000L * 60),
            duration = "4 hours",
            technologies = listOf("Binoculars", "Wildlife Guide", "Boat Transportation", "Photography Tips", "Educational Materials"),
            projectValue = "$85 per person",
            isFeature = false,
            tags = listOf("Wildlife", "Nature", "Marine"),
            metrics = ProjectMetrics(
                performanceImprovement = "+95% wildlife sightings",
                userGrowth = "+75% family bookings",
                revenueIncrease = "+60% seasonal revenue",
                timeToMarket = "Weather dependent"
            )
        ),
        PortfolioProject(
            id = "project_5",
            title = "Artisan Market & Food Tour",
            description = "Immersive culinary experience visiting local artisan markets, traditional food vendors, and family-run restaurants with tastings and cooking demonstrations.",
            category = ProjectCategory.WEB_DEVELOPMENT,
            imageUrl = null,
            clientName = "Culinary Tourism Board",
            completionDate = Date(System.currentTimeMillis() - 86400000L * 90),
            duration = "5 hours",
            technologies = listOf("Food Tastings", "Cooking Demos", "Market Tours", "Recipe Cards", "Cultural Stories"),
            projectValue = "$95 per person",
            isFeature = true,
            tags = listOf("Food", "Culture", "Markets"),
            testimonial = ProjectTestimonial(
                clientName = "Isabella Martinez",
                clientTitle = "Food Blogger from Italy",
                content = "An incredible culinary journey! The food tour showcased authentic local flavors and the cooking demonstration was hands-on and fun. Perfect for food lovers!",
                rating = 5.0f
            ),
            metrics = ProjectMetrics(
                performanceImprovement = "+100% taste satisfaction",
                userGrowth = "+80% food tour bookings",
                revenueIncrease = "+70% restaurant partnerships",
                timeToMarket = "Daily availability"
            )
        )
    )
}

private fun getSampleComparisonCompanies(): List<ComparisonCompany> {
    return listOf(
        ComparisonCompany(
            id = "mock_001",
            name = "Coastal Paradise Resort",
            logoResId = 0,
            rating = 4.7f,
            reviewCount = 423,
            priceRange = PriceRange.PREMIUM,
            location = "Pacific Coast, CA",
            specialties = listOf("Beach Activities", "Water Sports", "Luxury Accommodations"),
            yearsInBusiness = 8,
            teamSize = "50+ staff",
            responseTime = "< 2 hours",
            certifications = listOf("Eco-Tourism Certified", "Safety Standards", "Hospitality Excellence"),
            portfolioCount = 120,
            clientSatisfaction = 0.95f
        ),
        ComparisonCompany(
            id = "comp_mountain_retreat",
            name = "Mountain Adventure Retreat",
            logoResId = 0,
            rating = 4.6f,
            reviewCount = 298,
            priceRange = PriceRange.MODERATE,
            location = "Rocky Mountains, CO",
            specialties = listOf("Hiking Trails", "Rock Climbing", "Wildlife Tours"),
            yearsInBusiness = 12,
            teamSize = "30+ guides",
            responseTime = "< 3 hours",
            certifications = listOf("Adventure Tourism", "Safety Certified", "Environmental Steward"),
            portfolioCount = 180,
            clientSatisfaction = 0.92f
        ),
        ComparisonCompany(
            id = "comp_cultural_village",
            name = "Historic Cultural Village",
            logoResId = 0,
            rating = 4.4f,
            reviewCount = 156,
            priceRange = PriceRange.BUDGET,
            location = "Heritage Valley, TX",
            specialties = listOf("Cultural Tours", "Traditional Crafts", "Local Cuisine"),
            yearsInBusiness = 5,
            teamSize = "15+ locals",
            responseTime = "< 4 hours",
            certifications = listOf("Cultural Heritage", "Authentic Experience", "Community Based"),
            portfolioCount = 75,
            clientSatisfaction = 0.89f
        ),
        ComparisonCompany(
            id = "comp_luxury_resort",
            name = "Grand Luxury Resort & Spa",
            logoResId = 0,
            rating = 4.9f,
            reviewCount = 567,
            priceRange = PriceRange.LUXURY,
            location = "Napa Valley, CA",
            specialties = listOf("Luxury Spa", "Fine Dining", "Wine Tours"),
            yearsInBusiness = 15,
            teamSize = "100+ staff",
            responseTime = "< 1 hour",
            certifications = listOf("5-Star Rating", "Luxury Travel", "Wine Country Certified"),
            portfolioCount = 250,
            clientSatisfaction = 0.97f
        ),
        ComparisonCompany(
            id = "comp_urban_explorer",
            name = "Urban Explorer Hub",
            logoResId = 0,
            rating = 4.5f,
            reviewCount = 234,
            priceRange = PriceRange.MODERATE,
            location = "Downtown Seattle, WA",
            specialties = listOf("City Tours", "Food Scene", "Nightlife"),
            yearsInBusiness = 7,
            teamSize = "25+ guides",
            responseTime = "< 3 hours",
            certifications = listOf("City Tourism", "Food Safety", "Entertainment Licensed"),
            portfolioCount = 95,
            clientSatisfaction = 0.91f
        )
    )
}
