//package com.android.tripbook.restapi

//import com.google.gson.GsonBuilder
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory

//object ApiClient {
//    private const val BASE_URL = "http://10.0.2.2:8080/" // Replace with your IP or domain
//  private var retrofit: Retrofit? = null

//fun getClient(): Retrofit {
//  if (retrofit == null) {
//    val gson = GsonBuilder().setLenient().create()

//            retrofit = Retrofit.Builder()
//              .baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create(gson))
//          .build()
// }
//return retrofit!!
//    }
//}
