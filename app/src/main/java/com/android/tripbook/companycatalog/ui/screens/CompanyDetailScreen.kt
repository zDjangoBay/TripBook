package com.android.tripbook.companycatalog.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.tripbook.companycatalog.data.MockCompanyData

@Composable
fun CompanyDetailScreen(companyId: String) {
    val company = MockCompanyData.companies.find { it.id == companyId }

    if (company == null) {
        Text("Company not found")
    } else {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
        ) {
            Text(text = company.name, style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = company.description, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Services Offered:", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            for (service in company.servicesOffered) {
                Text("- $service", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
