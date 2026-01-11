package com.localify.android.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localify.android.data.models.City
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import com.localify.android.data.models.Event
import com.localify.android.data.models.Artist
import com.localify.android.data.models.Venue
import com.localify.android.data.network.NetworkModule
import com.localify.android.data.network.SearchV1Response
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class SearchResults(
    val artists: List<Artist> = emptyList(),
    val events: List<Event> = emptyList(),
    val venues: List<Venue> = emptyList(),
    val cities: List<City> = emptyList()
)

data class SearchUiState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val searchResults: SearchResults = SearchResults(),
    val error: String? = null
)

class SearchViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val apiService by lazy { NetworkModule.apiService }
    private var searchJob: Job? = null
    
    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        
        if (query.isNotBlank()) {
            performSearch(query)
        } else {
            searchJob?.cancel()
            _uiState.value = _uiState.value.copy(
                searchResults = SearchResults(),
                isLoading = false
            )
        }
    }
    
    private fun performSearch(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                delay(300)

                val response = callWithGuestAuthRetry {
                    apiService.searchV1(query = query, autoSearchSpotify = null)
                }
                if (!response.isSuccessful) {
                    val errorBody = response.errorBody()?.string()
                    throw Exception("Search failed (${response.code()}) - $errorBody")
                }

                val body = response.body() ?: SearchV1Response(
                    artists = emptyList(),
                    events = emptyList(),
                    venues = emptyList(),
                    cities = emptyList()
                )

                val mappedArtists = body.artists.map { it.toUiArtist() }
                val mappedVenues = body.venues.map { it.toUiVenue() }
                val mappedCities = body.cities.map { it.toUiCity() }
                val mappedEvents = body.events.map { it.toUiEvent() }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    searchResults = SearchResults(
                        artists = mappedArtists,
                        events = mappedEvents,
                        venues = mappedVenues,
                        cities = mappedCities
                    )
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Search failed"
                )
            }
        }
    }

    private suspend fun <T> callWithGuestAuthRetry(block: suspend () -> Response<T>): Response<T> {
        if (!NetworkModule.hasValidAuth()) {
            val guest = apiService.createGuestUser()
            if (guest.isSuccessful) {
                val auth = guest.body()
                if (auth != null) NetworkModule.storeAuth(auth)
            }
        }

        val initial = block()
        if (initial.code() != 401) return initial

        val guest = apiService.createGuestUser()
        if (guest.isSuccessful) {
            val auth = guest.body()
            if (auth != null) {
                NetworkModule.storeAuth(auth)
                return block()
            }
        }

        return initial
    }

    private fun com.localify.android.data.network.ArtistV1Response.toUiArtist(): Artist {
        return Artist(
            id = id,
            name = name,
            imageUrl = image ?: "",
            genres = emptyList(),
            bio = "",
            spotifyId = spotifyId ?: "",
            popularity = 0
        )
    }

    private fun com.localify.android.data.network.CityV1Response.toUiCity(): City {
        return City(
            id = id,
            name = name,
            state = zoneCode ?: "",
            country = countryCode ?: "",
            latitude = latitude,
            longitude = longitude
        )
    }

    private fun com.localify.android.data.network.VenueV1Response.toUiVenue(): Venue {
        return Venue(
            id = id,
            name = name,
            address = address,
            city = city.toUiCity()
        )
    }

    private fun com.localify.android.data.network.EventV1Response.toUiEvent(): Event {
        val isoUtc = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val dateString = try {
            isoUtc.format(Date(startTime))
        } catch (_: Exception) {
            ""
        }

        return Event(
            id = id,
            name = name,
            imageUrl = topArtists.firstOrNull()?.image ?: "",
            date = dateString,
            venue = venue.toUiVenue(),
            artists = topArtists.map { it.toUiArtist() },
            ticketUrl = ticketUrl ?: "",
            description = ""
        )
    }
}
