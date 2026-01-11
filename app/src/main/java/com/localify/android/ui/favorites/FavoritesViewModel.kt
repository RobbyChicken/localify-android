package com.localify.android.ui.favorites

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.localify.android.data.models.City
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.localify.android.data.models.Event
import com.localify.android.data.models.Artist
import com.localify.android.data.models.Venue
import com.localify.android.data.local.UserPreferences
import com.localify.android.data.network.NetworkModule
import com.localify.android.data.network.ArtistResponse
import com.localify.android.data.network.ArtistV1Response
import com.localify.android.data.network.AuthResponse
import com.localify.android.data.network.EventV1Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import retrofit2.Response

data class FavoritesUiState(
    val isLoading: Boolean = false,
    val favoriteArtists: List<Artist> = emptyList(),
    val upcomingEvents: List<Event> = emptyList(),
    val pastEvents: List<Event> = emptyList(),
    val selectedTab: FavoritesTab = FavoritesTab.ARTISTS,
    val error: String? = null
)

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {
    
    private val userPreferences = UserPreferences(application)
    private val apiService = NetworkModule.apiService
    
    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    private companion object {
        const val FAVORITES_PAGE = 1
        const val FAVORITES_LIMIT = 20
    }
    
    init {
        println("DEBUG: FavoritesViewModel - Initializing")
    }
    
    fun loadFavorites() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                // Add a small delay to show loading indicator
                kotlinx.coroutines.delay(300)
                
                loadFavoritesData()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }
    
    private fun loadFavoritesImmediate() {
        viewModelScope.launch {
            try {
                loadFavoritesData()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }
    
    private suspend fun loadFavoritesData() {
        try {
            var favoriteArtistsResponse = callWithGuestAuthRetry { apiService.getFavoriteArtists() }
            if (!favoriteArtistsResponse.isSuccessful) {
                val errorBody = favoriteArtistsResponse.errorBody()?.string()
                throw Exception("Failed to load favorite artists (${favoriteArtistsResponse.code()}) - $errorBody")
            }

            var upcomingEventsResponse = callWithGuestAuthRetry { apiService.getFavoriteUpcomingEventsV1(page = FAVORITES_PAGE, limit = FAVORITES_LIMIT) }
            if (!upcomingEventsResponse.isSuccessful && upcomingEventsResponse.code() != 404) {
                val errorBody = upcomingEventsResponse.errorBody()?.string()
                throw Exception("Failed to load favorite upcoming events (${upcomingEventsResponse.code()}) - $errorBody")
            }

            var pastEventsResponse = callWithGuestAuthRetry { apiService.getFavoritePastEventsV1(page = FAVORITES_PAGE, limit = FAVORITES_LIMIT) }
            if (!pastEventsResponse.isSuccessful && pastEventsResponse.code() != 404) {
                val errorBody = pastEventsResponse.errorBody()?.string()
                throw Exception("Failed to load favorite past events (${pastEventsResponse.code()}) - $errorBody")
            }

            var favoriteArtists = favoriteArtistsResponse.body()?.content.orEmpty().map { it.toUiArtist() }
            var upcomingEvents = if (upcomingEventsResponse.isSuccessful) {
                upcomingEventsResponse.body()?.content.orEmpty().map { it.toUiEvent() }
            } else {
                emptyList()
            }
            var pastEvents = if (pastEventsResponse.isSuccessful) {
                pastEventsResponse.body()?.content.orEmpty().map { it.toUiEvent() }
            } else {
                emptyList()
            }

            // Some environments return 404 for the upcoming/past endpoints.
            // Fall back to fetching the full favorites list and split locally.
            if (upcomingEventsResponse.code() == 404 || pastEventsResponse.code() == 404) {
                val allEventsResp = callWithGuestAuthRetry { apiService.getFavoriteEventsV1(page = FAVORITES_PAGE, limit = FAVORITES_LIMIT) }
                if (allEventsResp.isSuccessful) {
                    val all = allEventsResp.body()?.content.orEmpty()
                    val now = System.currentTimeMillis()
                    val upcoming = all.filter { it.startTime >= now }
                    val past = all.filter { it.startTime < now }
                    upcomingEvents = upcoming.map { it.toUiEvent() }
                    pastEvents = past.map { it.toUiEvent() }
                }
            }

            val backendArtistIds = favoriteArtists.map { it.id }.toSet()
            val backendEventIds = (upcomingEvents + pastEvents).map { it.id }.toSet()

            val localArtistIds = userPreferences.getFavoriteArtistsSnapshot()
            val localEventIds = userPreferences.getFavoriteEventsSnapshot()

            val missingArtistsOnBackend = localArtistIds - backendArtistIds
            val missingEventsOnBackend = localEventIds - backendEventIds

            // If user favorited items elsewhere (local prefs) but backend is missing them, push to backend,
            // then refetch so we can display real objects.
            if (missingArtistsOnBackend.isNotEmpty() || missingEventsOnBackend.isNotEmpty()) {
                missingArtistsOnBackend.forEach { id ->
                    val resp = callWithGuestAuthRetry { apiService.addFavorite(type = "artists", id = id) }
                    if (!resp.isSuccessful) {
                        println("DEBUG: Failed to sync favorite artist $id (${resp.code()})")
                    }
                }
                missingEventsOnBackend.forEach { id ->
                    val resp = callWithGuestAuthRetry { apiService.addFavorite(type = "events", id = id) }
                    if (!resp.isSuccessful) {
                        println("DEBUG: Failed to sync favorite event $id (${resp.code()})")
                    }
                }

                favoriteArtistsResponse = callWithGuestAuthRetry { apiService.getFavoriteArtists() }
                upcomingEventsResponse = callWithGuestAuthRetry { apiService.getFavoriteUpcomingEventsV1(page = FAVORITES_PAGE, limit = FAVORITES_LIMIT) }
                pastEventsResponse = callWithGuestAuthRetry { apiService.getFavoritePastEventsV1(page = FAVORITES_PAGE, limit = FAVORITES_LIMIT) }

                favoriteArtists = favoriteArtistsResponse.body()?.content.orEmpty().map { it.toUiArtist() }
                upcomingEvents = if (upcomingEventsResponse.isSuccessful) {
                    upcomingEventsResponse.body()?.content.orEmpty().map { it.toUiEvent() }
                } else {
                    emptyList()
                }
                pastEvents = if (pastEventsResponse.isSuccessful) {
                    pastEventsResponse.body()?.content.orEmpty().map { it.toUiEvent() }
                } else {
                    emptyList()
                }

                if (upcomingEventsResponse.code() == 404 || pastEventsResponse.code() == 404) {
                    val allEventsResp = callWithGuestAuthRetry { apiService.getFavoriteEventsV1(page = FAVORITES_PAGE, limit = FAVORITES_LIMIT) }
                    if (allEventsResp.isSuccessful) {
                        val all = allEventsResp.body()?.content.orEmpty()
                        val now = System.currentTimeMillis()
                        val upcoming = all.filter { it.startTime >= now }
                        val past = all.filter { it.startTime < now }
                        upcomingEvents = upcoming.map { it.toUiEvent() }
                        pastEvents = past.map { it.toUiEvent() }
                    }
                }
            }

            // Last resort: if backend didn't return any events but local prefs has event IDs,
            // fetch each event by id to display something.
            if (upcomingEvents.isEmpty() && pastEvents.isEmpty() && localEventIds.isNotEmpty()) {
                val resolved = localEventIds.mapNotNull { id ->
                    val resp = callWithGuestAuthRetry { apiService.getEventV1(id) }
                    if (resp.isSuccessful) resp.body() else null
                }
                val now = System.currentTimeMillis()
                upcomingEvents = resolved.filter { it.startTime >= now }.map { it.toUiEvent() }
                pastEvents = resolved.filter { it.startTime < now }.map { it.toUiEvent() }
            }

            // Keep local prefs as a cache, but do not delete local favorites based on backend.
            favoriteArtists.forEach { userPreferences.addFavoriteArtist(it.id) }
            (upcomingEvents + pastEvents).forEach { userPreferences.addFavoriteEvent(it.id) }

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                favoriteArtists = favoriteArtists,
                upcomingEvents = upcomingEvents,
                pastEvents = pastEvents,
                error = null
            )
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                favoriteArtists = emptyList(),
                upcomingEvents = emptyList(),
                pastEvents = emptyList(),
                error = e.message ?: "Failed to load favorites"
            )
        }
    }

    fun setFavoriteArtist(artistId: String, shouldBeFavorite: Boolean) {
        viewModelScope.launch {
            try {
                if (shouldBeFavorite) {
                    val resp = callWithGuestAuthRetry { apiService.addFavorite(type = "artists", id = artistId) }
                    if (!resp.isSuccessful) {
                        val errorBody = resp.errorBody()?.string()
                        throw Exception("Failed to add favorite artist (${resp.code()}) - $errorBody")
                    }
                    userPreferences.addFavoriteArtist(artistId)
                } else {
                    val resp = callWithGuestAuthRetry { apiService.removeFavorite(type = "artists", id = artistId) }
                    if (!resp.isSuccessful) {
                        val errorBody = resp.errorBody()?.string()
                        throw Exception("Failed to remove favorite artist (${resp.code()}) - $errorBody")
                    }
                    userPreferences.removeFavoriteArtist(artistId)
                }

                loadFavoritesImmediate()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message ?: "Failed to update favorite")
            }
        }
    }

    fun setFavoriteEvent(eventId: String, shouldBeFavorite: Boolean) {
        viewModelScope.launch {
            try {
                if (shouldBeFavorite) {
                    val resp = callWithGuestAuthRetry { apiService.addFavorite(type = "events", id = eventId) }
                    if (!resp.isSuccessful) {
                        val errorBody = resp.errorBody()?.string()
                        throw Exception("Failed to add favorite event (${resp.code()}) - $errorBody")
                    }
                    userPreferences.addFavoriteEvent(eventId)
                } else {
                    val resp = callWithGuestAuthRetry { apiService.removeFavorite(type = "events", id = eventId) }
                    if (!resp.isSuccessful) {
                        val errorBody = resp.errorBody()?.string()
                        throw Exception("Failed to remove favorite event (${resp.code()}) - $errorBody")
                    }
                    userPreferences.removeFavoriteEvent(eventId)
                }

                loadFavoritesImmediate()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message ?: "Failed to update favorite")
            }
        }
    }

    private fun ArtistResponse.toUiArtist(): Artist {
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

    private fun ArtistV1Response.toUiArtistFromV1(): Artist {
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

    private fun EventV1Response.toUiEvent(): Event {
        val dateText = formatMillis(startTime)

        return Event(
            id = id,
            name = name,
            imageUrl = topArtists.firstOrNull()?.image ?: "",
            date = dateText,
            venue = Venue(
                id = venue.id,
                name = venue.name,
                address = venue.address,
                city = City(
                    id = venue.city.id,
                    name = venue.city.name,
                    state = venue.city.zoneCode ?: "",
                    country = venue.city.countryCode ?: "",
                    latitude = venue.city.latitude,
                    longitude = venue.city.longitude
                )
            ),
            artists = topArtists.map { it.toUiArtistFromV1() },
            ticketUrl = ticketUrl ?: "",
            description = ""
        )
    }

    private fun formatMillis(millis: Long): String {
        return try {
            val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
            formatter.format(Date(millis))
        } catch (_: Exception) {
            millis.toString()
        }
    }
    
    fun selectTab(tab: FavoritesTab) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
    }

    private suspend fun <T> callWithGuestAuthRetry(
        block: suspend () -> Response<T>
    ): Response<T> {
        val initial = block()
        if (initial.code() != 401) return initial

        val guest = apiService.createGuestUser()
        if (guest.isSuccessful) {
            val auth: AuthResponse? = guest.body()
            if (auth != null) {
                NetworkModule.storeAuth(auth)
                return block()
            }
        }

        return initial
    }
}
