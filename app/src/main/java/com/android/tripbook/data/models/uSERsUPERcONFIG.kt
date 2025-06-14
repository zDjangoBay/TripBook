package com.android.tripbook.data.models

import io.github.jan.supabase.createSupabaseClient
//import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage
import kotlinx.serialization.json.Json

object uSERsUPERcONFIG {
    // TODO: Replace with your actual Supabase project URL and anon key
    // You can get these from your Supabase project dashboard
    private const val SUPABASE_URL = "https://ajwptveukgghbmjaukpl.supabase.co"
    private const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImFqd3B0dmV1a2dnaGJtamF1a3BsIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDkyMTEyOTUsImV4cCI6MjA2NDc4NzI5NX0.00zZ-H7Vci5maIj8hwjLL2naod5S1hPNLY4IPWgwugw"

    val client = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_ANON_KEY
    ) {
        install(Postgrest)
        //    install(GoTrue)
        install(Realtime)
        install(Storage)
    }
}