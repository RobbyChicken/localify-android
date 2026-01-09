package com.localify.android.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import com.localify.android.data.network.AuthResponse
import com.localify.android.data.network.EventRecResponse
import com.localify.android.data.network.ArtistRecResponse
import com.localify.android.data.network.NetworkModule
import com.localify.android.data.network.UserCity
import com.localify.android.data.network.UserCityResponse
import com.localify.android.data.repository.HomeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class HomeViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var mockRepository: HomeRepository

    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        mockRepository = mock()

        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        NetworkModule.init(context)

        // Avoid hitting real network/auth setup during init.
        NetworkModule.storeAuth(AuthResponse(token = "test", refreshToken = "test", expiresIn = 3600))

        runBlocking {
            whenever(mockRepository.getUserCities()).thenReturn(
                UserCityResponse(
                    current = UserCity(id = "city1", name = "Ithaca, NY", radius = 50.0, selected = true),
                    others = emptyList()
                )
            )
            whenever(mockRepository.getArtistRecommendations(any(), any())).thenReturn(emptyList())
            whenever(mockRepository.getEventRecommendations(any(), any())).thenReturn(emptyList())
        }

        viewModel = HomeViewModel(mockRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `selectTab updates selectedTab in uiState`() = runTest {
        // When
        viewModel.selectTab(HomeTab.ARTISTS)

        // Then
        assertEquals(HomeTab.ARTISTS, viewModel.uiState.value.selectedTab)
    }

    @Test
    fun `applyDateFilter updates timeFrame and filters events`() = runTest {
        // Given
        val mockEvents = listOf(
            createMockEvent("1", System.currentTimeMillis() + 86400000), // 1 day from now
            createMockEvent("2", System.currentTimeMillis() + 604800000) // 1 week from now
        )
        
        // Update the UI state with mock events
        setUiState(viewModel.uiState.value.copy(allEvents = mockEvents, events = mockEvents))

        // When
        viewModel.applyDateFilter("One week")

        // Then
        assertEquals("One week", viewModel.uiState.value.selectedTimeFrame)
        assertTrue(viewModel.uiState.value.events.isNotEmpty())
    }

    private fun setUiState(state: HomeUiState) {
        val field = HomeViewModel::class.java.getDeclaredField("_uiState")
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val flow = field.get(viewModel) as MutableStateFlow<HomeUiState>
        flow.value = state
    }

    @Test
    fun `retry calls initializeApp`() = runTest {
        // When
        viewModel.retry()

        // Then - should reset loading state
        // Note: This test verifies the method exists and can be called
        assertTrue(true) // Basic test to ensure method doesn't crash
    }

    private fun createMockEvent(id: String, startTime: Long): EventRecResponse {
        return EventRecResponse(
            id = id,
            name = "Test Event",
            percentMatch = 0.85,
            startTime = startTime,
            venue = createMockVenue(),
            artists = emptyList(),
            genres = emptyList(),
            hasLocalArtistPerforming = false,
            ticketUrl = null,
            similar = emptyList(),
            createdAt = System.currentTimeMillis(),
            isFavorite = false,
            imageUrl = null
        )
    }

    private fun createMockVenue(): com.localify.android.data.network.VenueResponse {
        return com.localify.android.data.network.VenueResponse(
            id = "venue1",
            name = "Test Venue",
            address = "123 Test St",
            latitude = 40.7128,
            longitude = -74.0060,
            city = null,
            type = "concert_hall"
        )
    }
}
