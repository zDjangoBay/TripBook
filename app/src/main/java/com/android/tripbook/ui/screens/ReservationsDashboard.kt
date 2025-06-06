package com.android.tripbook.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.tripbook.R
import com.android.tripbook.model.Reservation
import com.android.tripbook.model.ReservationFilter
import com.android.tripbook.model.ReservationStatus
import com.android.tripbook.repository.ReservationRepository
import com.android.tripbook.ui.animation.AnimationUtils
import com.android.tripbook.ui.components.*
import com.android.tripbook.ui.theme.AppIcons

import com.android.tripbook.ui.theme.TextPrimary
import com.android.tripbook.ui.theme.TextSecondary
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.StateFlow

/**
 * Main screen for the Reservations Dashboard
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun ReservationsDashboard() {
    val repository = remember { ReservationRepository() }
    var selectedTabIndex by remember { mutableStateOf(0) }
    var isCalendarView by remember { mutableStateOf(false) }
    var showFilterDialog by remember { mutableStateOf(false) }
    var showAddReservationDialog by remember { mutableStateOf(false) }
    var selectedReservationId by remember { mutableStateOf<String?>(null) }

    // Search and sort state
    var searchQuery by remember { mutableStateOf("") }
    var currentSortOption by remember { mutableStateOf(SortOption.DATE_ASC) }

    // Calendar state
    val currentYearMonth = remember { YearMonth.now() }
    var selectedYearMonth by remember { mutableStateOf(currentYearMonth) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var calendarViewMode by remember { mutableStateOf(CalendarViewMode.MONTH) }

    // Observe reservations and filter from repository
    val reservationsFlow = remember { repository.reservations }
    val baseReservations by reservationsFlow.collectAsState()

    // Apply search and sort
    val reservations = remember(baseReservations, searchQuery, currentSortOption) {
        val searchResults = applySearchQuery(baseReservations, searchQuery)
        applySortOption(searchResults, currentSortOption)
    }

    val currentFilterFlow = remember { repository.currentFilter }
    val currentFilter by currentFilterFlow.collectAsState()

    // Apply initial filter based on tab
    LaunchedEffect(selectedTabIndex) {
        when (selectedTabIndex) {
            0 -> repository.applyFilter(ReservationFilter.upcoming())
            1 -> repository.applyFilter(ReservationFilter.past())
            else -> repository.clearFilters()
        }
    }

    // Handle reservation click
    val onReservationClick: (Reservation) -> Unit = { reservation ->
        selectedReservationId = reservation.id
    }

    // Handle add reservation
    val onAddReservationClick: () -> Unit = {
        showAddReservationDialog = true
    }

    // Handle add reservation from calendar
    val onAddReservationFromCalendar: (LocalDate) -> Unit = { date ->
        selectedDate = date
        showAddReservationDialog = true
    }

    Scaffold(
        topBar = {
            Column {
                // Main app bar
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = AppIcons.Flight,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .size(32.dp)
                                    .padding(end = 8.dp)
                                    .then(AnimationUtils.rotateAnimation(degrees = 5f, duration = 2500))
                            )

                            Text(
                                text = "My Reservations",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )

                            // Show active filter indicator if filtering
                            if (currentFilter != ReservationFilter.empty() &&
                                (selectedTabIndex == 0 && currentFilter != ReservationFilter.upcoming() ||
                                 selectedTabIndex == 1 && currentFilter != ReservationFilter.past())) {
                                Badge(
                                    modifier = Modifier.padding(start = 8.dp)
                                ) {
                                    Text("Filtered")
                                }
                            }
                        }
                    },
                    actions = {
                        // Toggle between list and calendar view with animation
                        IconToggleButton(
                            checked = isCalendarView,
                            onCheckedChange = { isCalendarView = it }
                        ) {
                            AnimatedContent(
                                targetState = isCalendarView,
                                transitionSpec = {
                                    fadeIn() + scaleIn() with fadeOut() + scaleOut()
                                },
                                label = "view toggle"
                            ) { isCalendarViewState ->
                                Icon(
                                    imageVector = if (isCalendarViewState) AppIcons.List else AppIcons.Calendar,
                                    contentDescription = if (isCalendarViewState) "Switch to List View" else "Switch to Calendar View",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.then(AnimationUtils.pulseAnimation(pulseFraction = 1.1f, duration = 1500))
                                )
                            }
                        }

                        // Filter button
                        IconButton(
                            onClick = { showFilterDialog = true },
                            modifier = Modifier.animateContentSize()
                        ) {
                            Icon(
                                imageVector = AppIcons.Filter,
                                contentDescription = "Filter Reservations",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = if (currentFilter != ReservationFilter.empty() &&
                                    (selectedTabIndex == 0 && currentFilter != ReservationFilter.upcoming() ||
                                     selectedTabIndex == 1 && currentFilter != ReservationFilter.past())) {
                                    Modifier.then(AnimationUtils.pulseAnimation(duration = 1500))
                                } else {
                                    Modifier
                                }
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )

                // Search and sort bar
                SearchAndSortBar(
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    currentSortOption = currentSortOption,
                    onSortOptionSelected = { currentSortOption = it }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Welcome banner
            WelcomeBanner(
                onBookNowClick = onAddReservationClick
            )

            // Tabs for upcoming/past reservations
            ReservationTabs(
                selectedTabIndex = selectedTabIndex,
                onTabSelected = { selectedTabIndex = it }
            )

            // Content area
            Box(modifier = Modifier.fillMaxSize()) {
                // Content based on view type with animation
                AnimatedContent(
                    targetState = isCalendarView,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(300)) with
                        fadeOut(animationSpec = tween(300))
                    },
                    label = "view content"
                ) { isCalendarViewState ->
                    if (!isCalendarViewState) {
                        // List view
                        Column(modifier = Modifier.fillMaxSize()) {
                            // Section headers
                            if (selectedTabIndex == 0) {
                                SectionHeader(
                                    title = "Upcoming Trips",
                                    subtitle = "Your future adventures",
                                    illustrationResId = R.drawable.travel_illustration_2
                                )
                            } else {
                                SectionHeader(
                                    title = "Past Trips",
                                    subtitle = "Your travel memories",
                                    illustrationResId = R.drawable.travel_illustration_3
                                )
                            }

                            if (reservations.isEmpty()) {
                                // Empty state based on filter and tab
                                if (currentFilter != ReservationFilter.empty() &&
                                    (selectedTabIndex == 0 && currentFilter != ReservationFilter.upcoming() ||
                                     selectedTabIndex == 1 && currentFilter != ReservationFilter.past())) {
                                    // Filtered empty state
                                    FilteredReservationsEmptyState(
                                        onClearFiltersClick = { repository.clearFilters() }
                                    )
                                } else if (selectedTabIndex == 0) {
                                    // Upcoming empty state
                                    UpcomingReservationsEmptyState(
                                        onAddReservationClick = onAddReservationClick
                                    )
                                } else {
                                    // Past empty state
                                    PastReservationsEmptyState()
                                }
                            } else {
                                // Reservation list with animation
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(vertical = 8.dp)
                                ) {
                                    items(
                                        items = reservations,
                                        key = { it.id }
                                    ) { reservation ->
                                        ReservationCard(
                                            reservation = reservation,
                                            onClick = onReservationClick
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        // Enhanced calendar view
                        EnhancedCalendarView(
                            reservations = reservations,
                            onReservationClick = onReservationClick,
                            onAddReservationClick = onAddReservationFromCalendar
                        )
                    }
                }

                // Enhanced FAB for adding new reservation with animation
                ExtendedFloatingActionButton(
                    onClick = onAddReservationClick,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                        .then(AnimationUtils.pulseAnimation(pulseFraction = 1.05f, duration = 2000)),
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    icon = {
                        Icon(
                            imageVector = AppIcons.Add,
                            contentDescription = "Add Reservation",
                            modifier = Modifier.then(AnimationUtils.rotateAnimation(degrees = 5f, duration = 2000))
                        )
                    },
                    text = {
                        Text(
                            text = "New Trip",
                            modifier = Modifier.animateContentSize()
                        )
                    },
                    expanded = !isCalendarView
                )
            }
        }

        // Filter dialog
        if (showFilterDialog) {
            FilterDialog(
                currentFilter = currentFilter,
                onFilterApplied = { filter ->
                    repository.applyFilter(filter)
                },
                onDismiss = { showFilterDialog = false }
            )
        }

        // Add reservation dialog
        if (showAddReservationDialog) {
            AddReservationDialog(
                repository = repository,
                onDismiss = { showAddReservationDialog = false },
                onReservationAdded = { /* Handle reservation added */ }
            )
        }

        // Reservation details screen
        if (selectedReservationId != null) {
            ReservationDetailsScreen(
                reservationId = selectedReservationId!!,
                repository = repository,
                onNavigateBack = { selectedReservationId = null }
            )
        }
    }
}


