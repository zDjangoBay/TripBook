package com.example.tripbooktest.data.repository

import com.example.tripbooktest.data.ApiService
import com.example.tripbooktest.data.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.mock
import org.junit.Assert.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class UserRepositoryTest {

    private lateinit var apiService: ApiService
    private lateinit var userRepository: UserRepository

    @Before
    fun setup() {
        apiService = mock()
        userRepository = UserRepository(apiService)
    }

    @Test
    fun `fetchUser returns correct user`() = runTest {
        val mockUser = User(id = "1", name = "John Doe")
        `when`(apiService.getUserById("1")).thenReturn(mockUser)

        val result = userRepository.fetchUser("1")

        assertEquals(mockUser, result)
        verify(apiService).getUserById("1")
    }

}
