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
    private const val SUPABASE_URL = ""
    private const val SUPABASE_ANON_KEY = ""
    
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
