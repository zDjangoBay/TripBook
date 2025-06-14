package com.tripbook.userprofilendedilan

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.tripbook.userprofilevaldes.ProfileActivity

@Composable
fun UserProfileNdeDilanEntryPoint() {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = {
                context.startActivity(Intent(context, ProfileActivity::class.java))
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Open Profile Screen")
        }
    }
}

object UserProfileModule {
    fun openProfile(context: Context) {
        context.startActivity(Intent(context, ProfileActivity::class.java))
    }
}