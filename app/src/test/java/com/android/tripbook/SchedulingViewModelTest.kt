package com.android.tripbook

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.android.tripbook.Schedule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.threeten.bp.LocalDate
import kotlin.test.assertIs

@ExperimentalCoroutinesApi
class SchedulingViewModelTest {
    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    private lateinit var viewModel: SchedulingViewModel
    private lateinit var repository: FakeTripScheduleRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeTripScheduleRepository()
        viewModel = SchedulingViewModel(repository, testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial load sets loading then success state`() = runTest {
        // Initial state is Loading
        assertIs<SchedulingViewModel.ScheduleUiState.Loading>(viewModel.uiState.value)

        // Advance time to complete loading
        testDispatcher.scheduler.advanceUntilIdle()

        // Verify final state is Success
        val successState = assertIs<SchedulingViewModel.ScheduleUiState.Success>(
            viewModel.uiState.value
        )
        assertTrue(successState.schedules.isEmpty())
    }

    @Test
    fun `refreshData reloads data from repository`() = runTest {
        val testSchedule = Schedule(
            id = "1",
            title = "Test Trip",
            startDate = LocalDate.now(),
            endDate = LocalDate.now().plusDays(7),
            destinations = emptyList(),
            activities = emptyList()
        )

        repository.setSchedules(listOf(testSchedule))
        viewModel.refreshData()

        testDispatcher.scheduler.advanceUntilIdle()

        val successState = assertIs<SchedulingViewModel.ScheduleUiState.Success>(
            viewModel.uiState.value
        )
        assertEquals(1, successState.schedules.size)
    }

    @Test
    fun `validateScheduleDates returns true when end date is after start date`() {
        val today = LocalDate.now()
        val tomorrow = today.plusDays(1)

        assertTrue(viewModel.validateScheduleDates(today, tomorrow))
    }
}

// Fake repository implementation
class FakeTripScheduleRepository : TripScheduleRepository {
    private var schedules = emptyList<Schedule>()

    fun setSchedules(newSchedules: List<Schedule>) {
        schedules = newSchedules
    }

    override suspend fun getAllSchedules(): List<Schedule> = schedules

    override suspend fun getScheduleById(id: String): Schedule? =
        schedules.firstOrNull { it.id == id }
}