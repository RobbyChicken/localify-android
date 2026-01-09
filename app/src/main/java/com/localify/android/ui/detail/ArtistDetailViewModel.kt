package com.localify.android.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.localify.android.data.models.Artist
import com.localify.android.data.models.Event
import com.localify.android.data.models.City
import com.localify.android.data.models.Venue
import com.localify.android.data.local.UserPreferences
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.localify.android.data.network.NetworkModule
import com.localify.android.data.network.EventV1Response
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ArtistDetailViewModel(application: Application) : AndroidViewModel(application) {
    
    private val userPreferences = UserPreferences(application)
    private val apiService = NetworkModule.apiService
    
    private val _uiState = MutableStateFlow(ArtistDetailUiState())
    val uiState: StateFlow<ArtistDetailUiState> = _uiState.asStateFlow()
    
    fun loadArtist(artistId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                val artistResponse = apiService.getArtistV1(artistId)
                if (!artistResponse.isSuccessful) {
                    val errorBody = artistResponse.errorBody()?.string()
                    throw Exception("Failed to load artist (${artistResponse.code()}) - $errorBody")
                }

                val citiesResponse = apiService.getArtistCitiesV1(artistId)
                if (!citiesResponse.isSuccessful) {
                    val errorBody = citiesResponse.errorBody()?.string()
                    throw Exception("Failed to load artist cities (${citiesResponse.code()}) - $errorBody")
                }

                val eventsResponse = apiService.getArtistEventsV1(artistId)
                if (!eventsResponse.isSuccessful) {
                    val errorBody = eventsResponse.errorBody()?.string()
                    throw Exception("Failed to load artist events (${eventsResponse.code()}) - $errorBody")
                }

                val artistV1 = artistResponse.body() ?: throw Exception("Empty artist response")
                val citiesV1 = citiesResponse.body().orEmpty()
                val eventsV1 = eventsResponse.body()

                val artist = Artist(
                    id = artistV1.id,
                    name = artistV1.name,
                    imageUrl = artistV1.image ?: "",
                    genres = artistV1.genres.map { it.name },
                    bio = "",
                    spotifyId = artistV1.spotifyId ?: "",
                    popularity = 0
                )

                val upcomingEvents = buildList {
                    eventsV1?.nearbyEvents?.forEach { add(it.toUiEvent()) }
                    eventsV1?.otherEvents?.forEach { add(it.toUiEvent()) }
                }.distinctBy { it.id }

                val localCities = citiesV1.map { it.name }

                val similarArtists = artistV1.similarArtists.map {
                    Artist(
                        id = it.id,
                        name = it.name,
                        imageUrl = it.image ?: "",
                        genres = emptyList(),
                        bio = "",
                        spotifyId = "",
                        popularity = 0
                    )
                }

                val isFavorite = userPreferences.getFavoriteArtistsSnapshot().contains(artistId)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    artist = artist,
                    upcomingEvents = upcomingEvents,
                    localCities = localCities,
                    similarArtists = similarArtists,
                    isFavorite = isFavorite
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }
    
    private fun EventV1Response.toUiEvent(): Event {
        val dateText = formatMillis(startTime)
        return Event(
            id = id,
            name = name,
            imageUrl = "",
            date = dateText,
            venue = Venue(
                id = venue.id,
                name = venue.name,
                address = venue.address,
                city = City(
                    id = venue.city.id,
                    name = venue.city.name,
                    state = "",
                    country = venue.city.countryCode ?: "",
                    latitude = venue.city.latitude,
                    longitude = venue.city.longitude
                )
            ),
            artists = topArtists.map {
                Artist(
                    id = it.id,
                    name = it.name,
                    imageUrl = it.image ?: "",
                    genres = emptyList(),
                    bio = "",
                    spotifyId = it.spotifyId ?: "",
                    popularity = 0
                )
            },
            ticketUrl = ticketUrl ?: "",
            description = ""
        )
    }

    private fun formatMillis(millis: Long): String {
        return try {
            val formatter = SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault())
            formatter.format(Date(millis))
        } catch (_: Exception) {
            millis.toString()
        }
    }
    
    fun toggleFavorite() {
        val currentArtist = _uiState.value.artist ?: return
        val currentFavorite = _uiState.value.isFavorite
        
        viewModelScope.launch {
            try {
                val nextFavorite = !currentFavorite
                _uiState.value = _uiState.value.copy(isFavorite = nextFavorite)

                if (nextFavorite) {
                    userPreferences.addFavoriteArtist(currentArtist.id)
                    val resp = callWithGuestAuthRetry {
                        apiService.addFavorite(type = "artists", id = currentArtist.id)
                    }
                    if (!resp.isSuccessful) throw Exception("Failed to add favorite (${resp.code()})")
                } else {
                    userPreferences.removeFavoriteArtist(currentArtist.id)
                    val resp = callWithGuestAuthRetry {
                        apiService.removeFavorite(type = "artists", id = currentArtist.id)
                    }
                    if (!resp.isSuccessful) throw Exception("Failed to remove favorite (${resp.code()})")
                }
            } catch (e: Exception) {
                if (currentFavorite) {
                    userPreferences.addFavoriteArtist(currentArtist.id)
                } else {
                    userPreferences.removeFavoriteArtist(currentArtist.id)
                }
                _uiState.value = _uiState.value.copy(isFavorite = currentFavorite)
            }
        }
    }

    private suspend fun <T> callWithGuestAuthRetry(block: suspend () -> Response<T>): Response<T> {
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
    
}

data class ArtistDetailUiState(
    val isLoading: Boolean = false,
    val artist: Artist? = null,
    val upcomingEvents: List<Event> = emptyList(),
    val localCities: List<String> = emptyList(),
    val similarArtists: List<Artist> = emptyList(),
    val isFavorite: Boolean = false,
    val error: String? = null
)
