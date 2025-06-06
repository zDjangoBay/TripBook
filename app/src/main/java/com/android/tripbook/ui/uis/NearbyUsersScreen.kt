package com.android.tripbook.ui.uis


import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.tripbook.viewmodel.UserViewModel
import java.time.LocalDate

data class User(
    val id: Int,
    val name: String,
    val destination: String,
    val travelStyle: String? = null,
    val companions: String? = null,
    val budget: Int? = null,
    val accommodation: String? = null,
    val transportation: String? = null,
    val activities: String? = null,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null
)

@Composable
fun NearbyUsersScreen(
    onBackClick: () -> Unit,
    onPreferencesClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel = viewModel()
) {
    val users by userViewModel.users.collectAsState()
    val isLoading by userViewModel.isLoading.collectAsState()
    val error by userViewModel.error.collectAsState()
    var searchText by remember { mutableStateOf("") }
    var isEditEnabled by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        userViewModel.fetchUsers()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF667EEA),
                        Color(0xFF764BA2)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            // Header with Back Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Nearby Users",
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Text(
                text = "Find travelers near you",
                style = TextStyle(
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f)
                ),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Enable Edit Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = { isEditEnabled = !isEditEnabled },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(if (isEditEnabled) "Disable Edit" else "Enable Edit")
                }
            }

            // Search Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(Color.White, RoundedCornerShape(25.dp))
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color(0xFF9CA3AF),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    BasicTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        textStyle = TextStyle(
                            fontSize = 16.sp,
                            color = Color.Black
                        ),
                        decorationBox = { innerTextField ->
                            Box(
                                contentAlignment = Alignment.CenterStart,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                if (searchText.isEmpty()) {
                                    Text(
                                        text = "Search users...",
                                        color = Color(0xFF9CA3AF),
                                        fontSize = 16.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Error State with Retry Button
            if (!error.isNullOrEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudOff,
                            contentDescription = "Connection Error",
                            tint = Color(0xFFDC2626),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Connection Lost",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = error!!,
                            fontSize = 14.sp,
                            color = Color(0xFF6B7280),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { userViewModel.fetchUsers() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF667EEA)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Retrying...",
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Retry",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Retry",
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            } else {
                // User List
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(
                        users.filter { user ->
                            if (searchText.isEmpty()) true
                            else user.name.contains(searchText, ignoreCase = true) ||
                                    user.destination.contains(searchText, ignoreCase = true)
                        }.takeIf { searchText.isEmpty() }?.take(4) ?: users.filter { user ->
                            user.name.contains(searchText, ignoreCase = true) ||
                                    user.destination.contains(searchText, ignoreCase = true)
                        }
                    ) { user ->
                        UserCard(
                            user = user,
                            isEditEnabled = isEditEnabled,
                            onEditName = { newName -> userViewModel.updateUserName(user.id, newName) },
                            onEditDestination = { newDestination -> userViewModel.updateUserDestination(user.id, newDestination) },
                            onPreferencesClick = { onPreferencesClick(user.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UserCard(
    user: User,
    isEditEnabled: Boolean,
    onEditName: (String) -> Unit,
    onEditDestination: (String) -> Unit,
    onPreferencesClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color.Black.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            var name by remember { mutableStateOf(user.name) }
            var destination by remember { mutableStateOf(user.destination) }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isEditEnabled) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            onEditName(it)
                        },
                        label = { Text("Name") },
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Text(
                        text = user.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A202C)
                    )
                }
                IconButton(onClick = onPreferencesClick) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Preferences",
                        tint = Color(0xFF667EEA)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📍",
                    fontSize = 14.sp,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                if (isEditEnabled) {
                    OutlinedTextField(
                        value = destination,
                        onValueChange = {
                            destination = it
                            onEditDestination(it)
                        },
                        label = { Text("Destination") },
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Text(
                        text = user.destination,
                        fontSize = 14.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }
            user.travelStyle?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Style: $it",
                    fontSize = 14.sp,
                    color = Color(0xFF64748B)
                )
            }
            user.budget?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Budget: $$it",
                    fontSize = 14.sp,
                    color = Color(0xFF64748B)
                )
            }
            user.activities?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Activities: $it",
                    fontSize = 14.sp,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}

