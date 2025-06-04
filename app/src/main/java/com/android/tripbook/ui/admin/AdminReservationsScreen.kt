package com.android.tripbook.ui.admin

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.runtime.Composable
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.libraries.mapsplatform.transportation.consumer.model.Route
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.sin
import kotlin.random.Random

// Couleurs avec gradients et effets
object AppColors {
    val BrightPurple = Color(0xFF8A2BE2)
    val SoftPurple = Color(0xFFD8BFD8)
    val DarkPurple = Color(0xFF4B0082)
    val AccentPurple = Color(0xFF9932CC)
    val White = Color(0xFFFFFFFF)
    val LightGray = Color(0xFFF8F9FA)
    val MediumGray = Color(0xFFE9ECEF)
    val DarkGray = Color(0xFF6C757D)
    val Success = Color(0xFF28A745)
    val Warning = Color(0xFFFFC107)
    val Danger = Color(0xFFDC3545)
    
    val PurpleGradient = listOf(BrightPurple, AccentPurple, DarkPurple)
    val SoftGradient = listOf(SoftPurple, White)
}

// Modèle de données enrichi
data class Reservation(
    val id: String,
    val dateReservation: Date,
    val nombrePlaces: Int,
    val heureDepart: String,
    val nomUtilisateur: String,
    val numeroTelephone: String,
    val lieuDepart: String,
    val lieuArrivee: String,
    val prix: Double,
    val status: ReservationStatus = ReservationStatus.NOUVELLE,
    val priority: Priority = Priority.NORMALE
)

enum class ReservationStatus(val label: String, val color: Color) {
    NOUVELLE("Nouvelle", AppColors.Success),
    EN_COURS("En cours", AppColors.Warning),
    CONFIRMEE("Confirmée", AppColors.BrightPurple),
    ANNULEE("Annulée", AppColors.Danger)
}

enum class Priority(val label: String, val color: Color) {
    HAUTE("Haute", AppColors.Danger),
    NORMALE("Normale", AppColors.BrightPurple),
    BASSE("Basse", AppColors.DarkGray)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AdminReservationsScreen() {
    var reservations by remember { mutableStateOf(generateAdvancedReservations()) }
    var selectedFilter by remember { mutableStateOf(ReservationStatus.NOUVELLE) }
    var isLoading by remember { mutableStateOf(false) }
    var showStats by remember { mutableStateOf(true) }
    
    val lazyListState = rememberLazyListState()
    
    // Animation pour les nouvelles réservations
    LaunchedEffect(Unit) {
        while (true) {
            delay(Random.nextLong(5000, 15000))
            isLoading = true
            delay(500)
            val newReservation = generateRandomReservation()
            reservations = listOf(newReservation) + reservations
            isLoading = false
            lazyListState.animateScrollToItem(0)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        AppColors.LightGray,
                        AppColors.White,
                        AppColors.LightGray
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // En-tête dynamique avec animations
            AnimatedHeader(isLoading = isLoading)
            
            // Filtres interactifs
            FilterSection(
                selectedFilter = selectedFilter,
                onFilterChanged = { selectedFilter = it },
                reservations = reservations
            )
            
            // Statistiques animées
            AnimatedVisibility(
                visible = showStats,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                DynamicStatsSection(reservations = reservations)
            }
            
            // Toggle pour les stats
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = { showStats = !showStats }
                ) {
                    Text(
                        text = if (showStats) "Masquer stats" else "Afficher stats",
                        color = AppColors.BrightPurple
                    )
                }
            }
            
            // Liste des réservations avec animations
            LazyColumn(
                state = lazyListState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val filteredReservations = reservations.filter { it.status == selectedFilter }
                
                items(
                    items = filteredReservations,
                    key = { it.id }
                ) { reservation ->
                    AnimatedReservationCard(
                        reservation = reservation,
                        onStatusChange = { newStatus ->
                            reservations = reservations.map {
                                if (it.id == reservation.id) it.copy(status = newStatus)
                                else it
                            }
                        }
                    )
                }
                
                if (filteredReservations.isEmpty()) {
                    item {
                        EmptyStateCard(selectedFilter)
                    }
                }
            }
        }
        
        // Indicateur de loading flottant
        if (isLoading) {
            FloatingLoadingIndicator()
        }
    }
}

@Composable
fun AnimatedHeader(isLoading: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "header")
    val shimmer by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer"
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = AppColors.PurpleGradient.map { 
                            it.copy(alpha = 0.8f + 0.2f * shimmer) 
                        },
                        start = Offset(0f, 0f),
                        end = Offset(1000f, 1000f)
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icône animée
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(
                            AppColors.White.copy(alpha = 0.2f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Dashboard,
                        contentDescription = "Dashboard",
                        tint = AppColors.White,
                        modifier = Modifier
                            .size(32.dp)
                            .rotate(if (isLoading) 360f * shimmer else 0f)
                    )
                }
                
                Spacer(modifier = Modifier.width(20.dp))
                
                Column {
                    Text(
                        text = "TripBook Admin",
                        color = AppColors.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Gestion des réservations en temps réel",
                        color = AppColors.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                    
                    // Indicateur de statut
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    if (isLoading) AppColors.Warning else AppColors.Success,
                                    CircleShape
                                )
                                .scale(if (isLoading) 1.2f else 1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isLoading) "Synchronisation..." else "En ligne",
                            color = AppColors.White.copy(alpha = 0.9f),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSection(
    selectedFilter: ReservationStatus,
    onFilterChanged: (ReservationStatus) -> Unit,
    reservations: List<Reservation>
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(ReservationStatus.values()) { status ->
            val count = reservations.count { it.status == status }
            val isSelected = selectedFilter == status
            
            FilterChip(
                selected = isSelected,
                onClick = { onFilterChanged(status) },
                label = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = status.label,
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                        if (count > 0) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (isSelected) AppColors.White else status.color,
                                        CircleShape
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = count.toString(),
                                    color = if (isSelected) status.color else AppColors.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = status.color,
                    selectedLabelColor = AppColors.White
                ),
                modifier = Modifier.animateContentSize()
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DynamicStatsSection(reservations: List<Reservation>) {
    val totalRevenu = reservations.filter { it.status != ReservationStatus.ANNULEE }
        .sumOf { it.prix }
    val totalPlaces = reservations.sumOf { it.nombrePlaces }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Statistiques du jour",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.BrightPurple,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AnimatedStatCard(
                    title = "Réservations",
                    value = reservations.size.toString(),
                    subtitle = "Total",
                    color = AppColors.BrightPurple,
                    icon = Icons.Default.BookOnline,
                    modifier = Modifier.weight(1f)
                )
                
                AnimatedStatCard(
                    title = "Places",
                    value = totalPlaces.toString(),
                    subtitle = "Vendues",
                    color = AppColors.Success,
                    icon = Icons.Default.EventSeat,
                    modifier = Modifier.weight(1f)
                )
                
                AnimatedStatCard(
                    title = "Revenus",
                    value = "${totalRevenu.toInt()}",
                    subtitle = "FCFA",
                    color = AppColors.Warning,
                    icon = Icons.Default.AttachMoney,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun AnimatedStatCard(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    val animatedValue by animateIntAsState(
        targetValue = value.toIntOrNull() ?: 0,
        animationSpec = tween(1000, easing = EaseOutBounce),
        label = "stat_animation"
    )
    
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = animatedValue.toString(),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            
            Text(
                text = title,
                fontSize = 12.sp,
                color = AppColors.DarkGray
            )
            
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = AppColors.DarkGray.copy(alpha = 0.7f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimatedReservationCard(
    reservation: Reservation,
    onStatusChange: (ReservationStatus) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    var showStatusMenu by remember { mutableStateOf(false) }
    
    val cardScale by animateFloatAsState(
        targetValue = if (isExpanded) 1.02f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "card_scale"
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(cardScale)
            .clickable { isExpanded = !isExpanded }
            .shadow(
                elevation = if (isExpanded) 16.dp else 8.dp,
                shape = RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = AppColors.White
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // En-tête avec gradient
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(reservation.priority.color, CircleShape)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = "#${reservation.id.takeLast(6)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = AppColors.BrightPurple
                    )
                }
                
                // Status badge cliquable
                Box {
                    Badge(
                        containerColor = reservation.status.color,
                        modifier = Modifier.clickable { showStatusMenu = true }
                    ) {
                        Text(
                            text = reservation.status.label,
                            color = AppColors.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    DropdownMenu(
                        expanded = showStatusMenu,
                        onDismissRequest = { showStatusMenu = false }
                    ) {
                        ReservationStatus.values().forEach { status ->
                            DropdownMenuItem(
                                text = { Text(status.label) },
                                onClick = {
                                    onStatusChange(status)
                                    showStatusMenu = false
                                }
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Informations principales avec icônes colorées
            InfoRow(Icons.Default.Person, "Client", reservation.nomUtilisateur, AppColors.BrightPurple)
            InfoRow(Icons.Default.Phone, "Téléphone", reservation.numeroTelephone, AppColors.Success)
            InfoRow(Icons.Default.Route, "Route", "${reservation.lieuDepart} → ${reservation.lieuArrivee}", AppColors.AccentPurple)
            InfoRow(Icons.Default.Schedule, "Départ", reservation.heureDepart, AppColors.Warning)
            InfoRow(Icons.Default.EventSeat, "Places", "${reservation.nombrePlaces}", AppColors.DarkPurple)
            InfoRow(Icons.Default.Payments, "Prix", "${reservation.prix.toInt()} FCFA", AppColors.Success)
            
            // Détails expandables
            AnimatedVisibility(
                visible = isExpanded,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Divider(color = AppColors.MediumGray, thickness = 1.dp)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Détails supplémentaires",
                        fontWeight = FontWeight.Bold,
                        color = AppColors.BrightPurple,
                        fontSize = 14.sp
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    InfoRow(
                        Icons.Default.AccessTime, 
                        "Réservé le", 
                        SimpleDateFormat("dd/MM/yyyy à HH:mm", Locale.FRENCH).format(reservation.dateReservation),
                        AppColors.DarkGray
                    )
                    InfoRow(Icons.Default.Flag, "Priorité", reservation.priority.label, reservation.priority.color)
                    
                    // Actions rapides
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { /* Action call */ },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Appeler", fontSize = 12.sp)
                        }
                        
                        OutlinedButton(
                            onClick = { /* Action message */ },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Message, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("SMS", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = AppColors.DarkGray,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun EmptyStateCard(filter: ReservationStatus) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AppColors.LightGray),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.SearchOff,
                contentDescription = "Aucune réservation",
                tint = AppColors.DarkGray,
                modifier = Modifier.size(48.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Aucune réservation ${filter.label.lowercase()}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = AppColors.DarkGray,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "Les nouvelles réservations apparaîtront automatiquement ici",
                fontSize = 12.sp,
                color = AppColors.DarkGray.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun FloatingLoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopEnd
    ) {
        Card(
            modifier = Modifier
                .padding(16.dp)
                .size(56.dp),
            colors = CardDefaults.cardColors(containerColor = AppColors.BrightPurple),
            shape = CircleShape,
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = AppColors.White,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

// Fonction pour générer des réservations avancées
fun generateAdvancedReservations(): List<Reservation> {
    val villes = listOf(
        "Yaoundé", "Douala", "Bafoussam", "Bamenda", "Garoua", 
        "Maroua", "Ngaoundéré", "Bertoua", "Ebolowa", "Kribi",
        "Limbe", "Buea", "Dschang", "Kumba", "Foumban"
    )
    
    val noms = listOf(
        "Jean Baptiste Mballa", "Marie Claire Ngo", "Paul Biya Essomba",
        "Christine Fouda", "Emmanuel Njoya", "Fatima Mahamat",
        "Pierre Tchoua", "Aïcha Bello", "Joseph Atangana", "Sylvie Kamdem",
        "André Onana", "Bénédicte Mbom", "Célestin Tawamba", "Delphine Kamga"
    )

    return (1..8).map { i ->
        val depart = villes.random()
        var arrivee = villes.random()
        while (arrivee == depart) {
            arrivee = villes.random()
        }
        
        val places = (1..6).random()
        val prixBase = when {
            depart in listOf("Yaoundé", "Douala") && arrivee in listOf("Yaoundé", "Douala") -> 2500
            depart in listOf("Yaoundé", "Douala") -> 5000
            else -> 3500
        }
        
        Reservation(
            id = "TRB${System.currentTimeMillis()}${i}",
            dateReservation = Date(System.currentTimeMillis() - (i * 1800000L)),
            nombrePlaces = places,
            heureDepart = "${(6..20).random()}:${listOf("00", "15", "30", "45").random()}",
            nomUtilisateur = noms.random(),
            numeroTelephone = "+237 6${(90..99).random()} ${(100..999).random()} ${(100..999).random()}",
            lieuDepart = depart,
            lieuArrivee = arrivee,
            prix = prixBase * places + Random.nextInt(-500, 1000).toDouble(),
            status = ReservationStatus.values().random(),
            priority = if (places >= 4) Priority.HAUTE else Priority.values().random()
        )
    }.sortedByDescending { it.dateReservation }
}

fun generateRandomReservation(): Reservation {
    val villes = listOf(
        "Yaoundé", "Douala", "Bafoussam", "Bamenda", "Garoua", 
        "Maroua", "Ngaoundéré", "Bertoua", "Ebolowa", "Kribi"
    )
    
    val noms = listOf(
        "Hervé Ndong", "Patricia Ekomo", "Samuel Biya", "Claudine Essono",
        "François Manga", "Georgette Abena", "Marcel Olinga", "Rose Ateba"
    )
    
    val depart = villes.random()
    var arrivee = villes.random()
    while (arrivee == depart) {
        arrivee = villes.random()
    }
    
    val places = (1..5).random()
    val prixBase = if (depart in listOf("Yaoundé", "Douala")) 5000 else 3500
    
    return Reservation(
        id = "NEW${System.currentTimeMillis()}",
        dateReservation = Date(),
        nombrePlaces = places,
        heureDepart = "${(8..18).random()}:${listOf("00", "30").random()}",
        nomUtilisateur = noms.random(),
        numeroTelephone = "+237 6${(90..99).random()} ${(100..999).random()} ${(100..999).random()}",
        lieuDepart = depart,
        lieuArrivee = arrivee,
        prix = prixBase * places + Random.nextInt(-300, 800).toDouble(),
        status = ReservationStatus.NOUVELLE,
        priority = if (places >= 4) Priority.HAUTE else Priority.NORMALE
    )
}
