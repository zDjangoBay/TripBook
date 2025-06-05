/*
This composable streamlines user contact interactions with one-tap actions,
 */
package com.android.tripbook.companycatalog.ui.detail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext // Import LocalContext
import androidx.compose.ui.unit.dp
import com.android.tripbook.companycatalog.model.CompanyContact
import com.android.tripbook.companycatalog.ui.components.SectionTitle
import com.android.tripbook.ui.theme.Purple700 // Import Purple700 for heading and button colors
import androidx.core.net.toUri

