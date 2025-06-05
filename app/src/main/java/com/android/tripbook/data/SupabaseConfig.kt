package com.android.tripbook.data

import io.github.jan.supabase.createSupabaseClient
//import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage
import kotlinx.serialization.json.Json

object SupabaseConfig {
    // TODO: Replace with your actual Supabase project URL and anon key
    // You can get these from your Supabase project dashboard
    private const val SUPABASE_URL = "https://hkezrzapmoufaxainxcs.supabase.co"
    private const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImhrZXpyemFwbW91ZmF4YWlueGNzIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDg4MDY5NjcsImV4cCI6MjA2NDM4Mjk2N30.UljI1Stn3KDSKZEcCd-cpwuTNLRff4mhm0PVcrBkGv8"
    
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
