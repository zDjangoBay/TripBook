package com.example.tripbooktest.viewmodel

import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class CounterViewModelTest {

    private lateinit var viewModel: CounterViewModel

    @Before
    fun setup() {
        viewModel = CounterViewModel()
    }

    @Test
    fun increment_increasesCounterValue() = runTest {
        viewModel.increment()
        assertEquals(1, viewModel.counter.value)
    }

    @Test
    fun reset_setsCounterToZero() = runTest {
        viewModel.increment()
        viewModel.reset()
        assertEquals(0, viewModel.counter.value)
    }
}
