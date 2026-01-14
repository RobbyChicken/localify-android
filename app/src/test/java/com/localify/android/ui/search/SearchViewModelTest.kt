package com.localify.android.ui.search

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import com.localify.android.data.network.ApiService
import com.localify.android.data.network.ArtistV1Response
import com.localify.android.data.network.AuthResponse
import com.localify.android.data.network.CityV1Response
import com.localify.android.data.network.EventV1Response
import com.localify.android.data.network.NetworkModule
import com.localify.android.data.network.SearchV1Response
import com.localify.android.data.network.VenueV1Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import retrofit2.Response
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class SearchViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var apiService: ApiService
    private lateinit var viewModel: SearchViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        val context = ApplicationProvider.getApplicationContext<Context>()
        NetworkModule.init(context)
        NetworkModule.storeAuth(AuthResponse(token = "t", refreshToken = "r", expiresIn = 3600))

        apiService = mock()
        viewModel = SearchViewModel(apiService = apiService, debounceMs = 0)
    }

    @After
    fun tearDown() {
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @Test
    fun `updateSearchQuery blank clears results`() {
        viewModel.updateSearchQuery("")

        val state = viewModel.uiState.value
        assertEquals("", state.searchQuery)
        assertFalse(state.isLoading)
        assertTrue(state.searchResults.artists.isEmpty())
        assertTrue(state.searchResults.events.isEmpty())
        assertTrue(state.searchResults.venues.isEmpty())
        assertTrue(state.searchResults.cities.isEmpty())
    }

    @Test
    fun `updateSearchQuery success maps results and clears loading`() = runTest {
        val city = CityV1Response(
            id = "city1",
            name = "Ithaca",
            latitude = 0.0,
            longitude = 0.0,
            zoneCode = "NY",
            countryCode = "US",
            numberOfArtists = null,
            numberTotalVenues = null,
            numberUpcomingEvents = null,
            spotifyPlaylistId = null,
            applePlaylistId = null,
            isUserLocalCity = null
        )

        val artist = ArtistV1Response(
            id = "a1",
            name = "Artist",
            image = null,
            appleId = null,
            spotifyId = "sp",
            city = null
        )

        val venue = VenueV1Response(
            id = "v1",
            name = "Venue",
            address = "Addr",
            latitude = 0.0,
            longitude = 0.0,
            city = city,
            type = null
        )

        val event = EventV1Response(
            id = "e1",
            name = "Event",
            startTime = 0L,
            endTime = null,
            lowPrice = null,
            highPrice = null,
            ticketUrl = null,
            isFavorite = false,
            applePlaylistId = null,
            spotifyPlaylistId = null,
            venue = venue,
            topArtists = listOf(artist)
        )

        whenever(apiService.searchV1(any(), anyOrNull())).thenReturn(
            Response.success(
                SearchV1Response(
                    artists = listOf(artist),
                    events = listOf(event),
                    venues = listOf(venue),
                    cities = listOf(city)
                )
            )
        )

        viewModel.updateSearchQuery("test")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertEquals("test", state.searchQuery)
        assertEquals(1, state.searchResults.artists.size)
        assertEquals(1, state.searchResults.events.size)
        assertEquals(1, state.searchResults.venues.size)
        assertEquals(1, state.searchResults.cities.size)
    }
}
