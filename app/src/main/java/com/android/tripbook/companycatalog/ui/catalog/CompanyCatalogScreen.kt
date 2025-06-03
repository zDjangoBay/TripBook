/*
- Company catalogs are displayed on this screen
- it integrates search functionalities and intuitive dynamic UI
 */
package com.android.tripbook.companycatalog.ui.catalog
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.android.tripbook.companycatalog.model.CompanyRepository
import com.android.tripbook.companycatalog.model.Company
import com.android.tripbook.companycatalog.ui.components.ViewModeToggleButtons
import com.android.tripbook.companycatalog.ui.components.TopBar
import com.android.tripbook.companycatalog.ui.components.EmptyState // Import EmptyState


