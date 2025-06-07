package com.android.tripbook.data

import com.android.tripbook.model.Comment
import java.text.SimpleDateFormat
import java.util.*

object SampleComments {

    fun generateMockComments(reviewId: Int): List<Comment> {
        val random = Random(reviewId.toLong()) // Use reviewId as seed for consistency

        val sampleComments = listOf(
            "This mountain landscape is absolutely breathtaking! The rolling hills covered in morning mist create such a serene atmosphere. I spent hours just sitting here absorbing the tranquility and watching the sunrise paint the sky in brilliant oranges and pinks.",
            "The coastal views from this cliff are nothing short of spectacular! Watching the waves crash against the rocky shoreline while seabirds dance in the ocean breeze was mesmerizing. The sound of the ocean is so therapeutic and grounding.",
            "This forest trail leads to some of the most pristine wilderness I've ever experienced. The ancient trees tower overhead creating a natural cathedral, and the dappled sunlight filtering through the canopy creates magical moments throughout the hike.",
            "The sunset from this vantage point is legendary for good reason! As the golden hour approaches, the entire valley below is bathed in warm light, and the distant mountains create a stunning silhouette against the vibrant sky. Pure magic!",
            "This hidden waterfall is a true gem tucked away in the wilderness. The crystal-clear water cascades down moss-covered rocks into a pristine pool below. The negative ions from the falling water make you feel instantly refreshed and energized.",
            "The architectural details of this historic site tell stories of centuries past. Every stone has been carefully placed, and you can almost feel the history seeping through the weathered walls. The craftsmanship is absolutely remarkable.",
            "These hiking trails offer some of the most rewarding panoramic views I've encountered. The gradual ascent through diverse ecosystems - from dense forests to alpine meadows - makes every step worthwhile when you reach those breathtaking overlooks.",
            "The value here is incredible considering the pristine natural beauty you get access to. Clean facilities, well-maintained trails, and knowledgeable staff who genuinely care about conservation make this a responsible traveler's dream destination.",
            "Visiting during spring was absolutely perfect timing! The wildflowers were in full bloom, creating carpets of vibrant colors across the meadows. The mild temperatures and gentle breezes made every outdoor activity absolutely delightful.",
            "The local artisan market here is a cultural treasure trove! Each handmade piece tells a story of traditional craftsmanship passed down through generations. The authentic local crafts and warm hospitality make for unforgettable souvenir shopping.",
            "This glacial lake reflects the surrounding peaks like a perfect mirror on calm days. The crystal-clear water is so pure you can see straight to the bottom, and the contrast between the turquoise water and snow-capped mountains is simply stunning.",
            "The wildlife viewing opportunities here are unparalleled! We spotted eagles soaring overhead, deer grazing in meadows, and even caught glimpses of more elusive creatures. The biodiversity in this ecosystem is truly remarkable.",
            "This ancient oak grove feels like stepping into a fairy tale. Some of these majestic trees have been standing for hundreds of years, their gnarled branches creating natural sculptures against the sky. The sense of timelessness is overwhelming.",
            "The stargazing from this remote location is absolutely mind-blowing! With minimal light pollution, the Milky Way stretches across the entire sky in brilliant detail. Bring a blanket and prepare to be humbled by the vastness of the universe.",
            "This hidden canyon reveals geological wonders at every turn. The layered rock formations tell the story of millions of years of Earth's history, and the play of light and shadow throughout the day creates an ever-changing natural art gallery."
        )

        val sampleNames = listOf(
            "Carine Njoume", "Luc Mbarga", "Evelyne Nkem", "Samuel Etoundi",
            "Vanessa Tchoumi", "Roland Diko", "Brenda Fomban", "Thierry Ayissi",
            "Ariane Mbella", "Cedric Nguimfack", "Nadine Mvondo", "Eric Ewane",
            "Delphine Ngono", "Alain Njouon", "Flora Mengue", "Steve Mbah")

        val sampleImages = listOf(
            "https://th.bing.com/th/id/OIP.2Bn8PHaM9eU7DfLw52SnVAHaFj?w=227&h=180&c=7&r=0&o=5&dpr=1.5&pid=1.7",
            "https://th.bing.com/th/id/OIP.fSYcT2bD8ZjFkGm4LtshPgHaE8?w=266&h=180&c=7&r=0&o=5&dpr=1.5&pid=1.7",
            "https://th.bing.com/th/id/OIP.xM1hNzk4QwgE1WQeR2X0DwHaE9?w=299&h=200&c=7&r=0&o=5&dpr=1.5&pid=1.7",
            "https://th.bing.com/th/id/OIP.VUbInj4AffElK3y4RegrWQHaFj?w=191&h=180&c=7&r=0&o=5&dpr=1.5&pid=1.7",
            "https://th.bing.com/th/id/OIP.Kt97ifgrSkMNlZNXYG2OsQHaE7?w=235&h=180&c=7&r=0&o=5&dpr=1.5&pid=1.7",
            "https://www.bing.com/images/search?view=detailV2&ccid=SG0fE4e7&id=04876B16B9BD017518FED7C46A5FD7545E46CD54&thid=OIP.SG0fE4e76SYj8lkMnY20lQHaEK&mediaurl=https%3a%2f%2fblogger.googleusercontent.com%2fimg%2fb%2fR29vZ2xl%2fAVvXsEhpDvwPcstSO0X1kNqFnfmeSjjShUHLawFrvk0LUdp_iT5Mvz_eMDWkfYdf650LMIy9zaUn26j-4W4Sbg7UCZxzPjLlLzQpdypbZohm7X18x59LV9Eceky22cDGgegBVRdB-k6-upbRgPJlTRxcBpYBQGPVNHnSLHCd0EZFEQ9yq-ywWAtrUXQSFOwbSA%2fs16000%2ffamous-historical-cultural-and-religious-tourist-places-to-visit-in-cameroon-1.jpg&cdnurl=https%3a%2f%2fth.bing.com%2fth%2fid%2fR.486d1f1387bbe92623f2590c9d8db495%3frik%3dVM1GXlTXX2rE1w%26pid%3dImgRaw%26r%3d0&exph=720&expw=1280&q=Cameroon+Beautiful+Touristic+Sites&simid=607998878658203877&FORM=IRPRST&ck=2ABC40C56381FDD14CA44050AC0747A5&selectedIndex=8&itb=0",
            "https://www.bing.com/images/search?q=Toristic+Things+in+Cameroon&FORM=IRBPRS",
            "https://www.bing.com/images/search?view=detailV2&ccid=NgFRADsY&id=B0CFFF71682ECB6B3676F6EA8F91BA52AA427514&thid=OIP.NgFRADsYZeLjyW0UCZa3KgHaDn&mediaurl=https%3a%2f%2fseeafricatoday.com%2fwp-content%2fuploads%2f2022%2f01%2fYaounder.jpg&cdnurl=https%3a%2f%2fth.bing.com%2fth%2fid%2fR.360151003b1865e2e3c96d140996b72a%3frik%3dFHVCqlK6kY%252fq9g%26pid%3dImgRaw%26r%3d0&exph=621&expw=1271&q=images+of+cameroontouristic+sites&simid=607988197100504207&FORM=IRPRST&ck=B120CCEC5A56DD0AFCEC405E424088B8&selectedIndex=65&itb=0",
            "https://www.bing.com/images/search?view=detailV2&ccid=Y2VU%2fN3j&id=C4884FAADB1EB5E672CD2B0E3A21387125766DE8&thid=OIP.Y2VU_N3j1B815xY2Z8Ip1AHaE8&mediaurl=https%3a%2f%2fi.pinimg.com%2foriginals%2f4c%2f24%2f15%2f4c2415327a2b86849af289d8f39dce52.jpg&cdnurl=https%3a%2f%2fth.bing.com%2fth%2fid%2fR.636554fcdde3d41f35e7163667c229d4%3frik%3d6G12JXE4IToOKw%26pid%3dImgRaw%26r%3d0&exph=667&expw=1000&q=images+of+cameroontouristic+sites&simid=608037473248902803&FORM=IRPRST&ck=C7EDBA123A74FC28878BA834658BC1D3&selectedIndex=29&itb=0",
            "https://th.bing.com/th?q=Pictures+of+Touristic+Sites+in+Camerooon&w=120&h=120&c=1&rs=1&qlt=90&cb=1&dpr=1.5&pid=InlineBlock&mkt=en-WW&cc=CM&setlang=en&adlt=moderate&t=1&mw=247",
            "https://images.pexels.com/photos/10707457/pexels-photo-10707457.jpeg?cs=srgb&dl=pexels-ada-mbita-10707457.jpg&fm=jpg",
            "https://www.bing.com/images/search?view=detailV2&ccid=8NN7dnVc&id=08D7DE6E4519174FBD2C5292FEEF2941D5C70DF4&thid=OIP.8NN7dnVcYC6B3slHNhidMwHaCg&mediaurl=https%3a%2f%2fproject-house.net%2fwp-content%2fuploads%2f2021%2f03%2fWhatsApp-Image-2021-10-01-at-8.38.17-PM-1.jpeg&cdnurl=https%3a%2f%2fth.bing.com%2fth%2fid%2fR.f0d37b76755c602e81dec94736189d33%3frik%3d9A3H1UEp7%252f6SUg%26pid%3dImgRaw%26r%3d0&exph=365&expw=1080&q=images+of+cameroon+beach&simid=608050547122199148&FORM=IRPRST&ck=0FAC2BFA6C24850BC8147847A01B9EDF&selectedIndex=10&itb=0",
            "https://th.bing.com/th?q=Cameroon+Beautiful+Touristic+Sites&w=120&h=120&c=1&rs=1&qlt=90&cb=1&dpr=1.5&pid=InlineBlock&mkt=en-WW&cc=CM&setlang=en&adlt=moderate&t=1&mw=247",
            "https://th.bing.com/th/id/R.771cf252dbe193c414f7181a107c78ab?rik=v1TGS7fZ5QRFkQ&pid=ImgRaw&r=0",
            "https://th.bing.com/th?q=Touristic+Sites+in+Cameroon+Douala&w=120&h=120&c=1&rs=1&qlt=90&cb=1&dpr=1.5&pid=InlineBlock&mkt=en-WW&cc=CM&setlang=en&adlt=moderate&t=1&mw=247",
            null, null, null // Some comments without images
        )

        val numberOfComments = random.nextInt(4) + 3 // 3-6 comments per review

        return (0 until numberOfComments).map { index ->
            val daysAgo = random.nextInt(29) + 1 // 1-29 days ago
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -daysAgo)

            Comment(
                id = "${reviewId}_$index",
                text = sampleComments[random.nextInt(sampleComments.size)],
                imageUri = sampleImages[random.nextInt(sampleImages.size)],
                timestamp = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault()).format(calendar.time),
                authorName = sampleNames[random.nextInt(sampleNames.size)]
            )
        }.sortedByDescending { it.timestamp } // Most recent first
    }
}