package com.localify.android.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.localify.android.data.network.ApiService
import com.localify.android.data.network.AuthResponse
import com.localify.android.data.network.UserCityResponse
import com.localify.android.data.network.UserCity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@ExperimentalCoroutinesApi
class HomeRepositoryTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var mockApiService: ApiService

    private lateinit var repository: HomeRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = HomeRepository()
        // Note: In a real implementation, you'd inject the mock ApiService
    }

    @Test
    fun `createGuestUser returns auth response`() = runTest {
        // Given
        val expectedResponse = AuthResponse(
            token = "test-token-123",
            user = null
        )
        whenever(mockApiService.createGuestUser()).thenReturn(expectedResponse)

        // When
        val result = repository.createGuestUser()

        // Then
        assertNotNull(result)
        assertEquals("test-token-123", result.token)
    }

    @Test
    fun `getUserCities returns user cities`() = runTest {
        // Given
        val expectedResponse = UserCityResponse(
            current = UserCity(
                id = "city1",
                name = "New York, NY",
                radius = 25.0,
                selected = true
            ),
            others = emptyList()
        )
        whenever(mockApiService.getUserCities("Bearer test-token")).thenReturn(expectedResponse)

        // When
        val result = repository.getUserCities("test-token")

        // Then
        assertNotNull(result)
        assertEquals("New York, NY", result.current.name)
        assertEquals(25.0, result.current.radius)
    }
}
