 package com.android.tripbook.data.model


 import com.google.gson.annotations.SerializedName

 data class Agency(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("logo_url")
    val logoUrl: String,
    @SerializedName("rating")
    val rating: Double,
    @SerializedName("reviews_count")
    val reviewsCount: Int,
    @SerializedName("contact_email")
    val contactEmail: String,
    @SerializedName("contact_phone")
    val contactPhone: String,
    @SerializedName("servicesOffered")
    val servicesOffered: List<String>,
    @SerializedName("isVerified")
    val isVerified: Boolean,
    @SerializedName("minPrice")
    val minPrice: Double,
    @SerializedName("maxPrice")
    val maxPrice: Double,
  ) 

  data class BookingRequest(
    @SerializedName("agency_id")
    val agencyId: String,
    @SerializedName("service_id")
    val serviceId: String,
    @SerializedName("user_id")
    val userId: String,
     @SerializedName("booking_Details")
    val bookingDetails: String
 
  )

    data class BookingResponse(
        @SerializedName("booking_id")
        val bookingId: String,
        @SerializedName("status")
        val status: String,
        @SerializedName("message")
        val message: String
    )