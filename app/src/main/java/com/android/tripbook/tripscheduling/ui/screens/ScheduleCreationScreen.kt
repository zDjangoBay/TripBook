package com.android.tripbook.tripscheduling.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.tripbook.tripscheduling.viewmodel.ScheduleCreationViewModel
import java.time.LocalDate

@Composable
fun ScheduleCreationScreen(
    viewModel: ScheduleCreationViewModel = viewModel<ScheduleCreationViewModel>(),
    onNavigateBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }//date picker will be added so value is left as var for now

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Selected Date: $selectedDate")

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (title.isNotBlank()) {
                    viewModel.createSchedule(title, selectedDate, 0.0, 0.0)//add correct lat and lang later

                    onNavigateBack()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Schedule")
        }
    }
}
