package com.android.tripbook

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.android.tripbook.ui.components.BaseScaffold
import com.android.tripbook.ui.navigation.MainNavGraph
import com.android.tripbook.ui.theme.TripBookTheme
import com.android.tripbook.test.SimpleDatabaseTest
import com.android.tripbook.database.TripBookDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 🧪 DATABASE TEST - COMMENTED OUT FOR TEAM COLLABORATION
        // 📝 INSTRUCTIONS: Uncomment the code below to activate database testing
        // 🎯 PURPOSE: Allows team members to use mock data without database interference
        // 🚀 TO ACTIVATE: Remove the /* and */ comment blocks around the database test code

        /*
        // 🧪 DATABASE TEST - Force database test to run and add test data
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val database = TripBookDatabase.getDatabase(this@MainActivity)

                // Force add test data
                SimpleDatabaseTest.runCompleteTest(database)

                // 🗑️ DELETE EXISTING DOUALA TRIP (ID: 999) if it exists
                try {
                    database.tripDao().deleteTripById(999)
                    android.util.Log.d("DatabaseTest", "🗑️ DELETED Douala trip (ID: 999) from database")
                } catch (e: Exception) {
                    android.util.Log.d("DatabaseTest", "ℹ️ Douala trip (ID: 999) not found or already deleted")
                }

                android.util.Log.d("DatabaseTest", "✅ Database test completed successfully")

            } catch (e: Exception) {
                android.util.Log.e("DatabaseTest", "❌ Database test failed: ${e.message}")
            }
        }
        */
        setContent {
            TripBookTheme {
                val navController = rememberNavController()
                var isLoading by remember { mutableStateOf(false) }

        // Immediately launch HomeActivity
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)

        // Finish MainActivity so user can't navigate back to it
        finish()
    }
}
