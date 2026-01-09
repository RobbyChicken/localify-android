package com.localify.android.ui.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.localify.android.data.local.UserPreferences
import com.localify.android.data.models.Artist
import com.localify.android.data.models.City
import com.localify.android.data.models.Event
import com.localify.android.data.models.Venue
import com.localify.android.data.network.NetworkModule
import com.localify.android.data.network.EventV1Response
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EventDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferences = UserPreferences(application)
    private val apiService = NetworkModule.apiService

    private val _uiState = MutableStateFlow(EventDetailUiState())
    val uiState: StateFlow<EventDetailUiState> = _uiState.asStateFlow()

    fun loadEvent(eventId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val response = apiService.getEventV1(eventId)
                if (!response.isSuccessful) {
                    val errorBody = response.errorBody()?.string()
                    throw Exception("Failed to load event (${response.code()}) - $errorBody")
                }

                val eventV1 = response.body() ?: throw Exception("Empty event response")
                val mappedEvent = eventV1.toUiEvent()

                if (eventV1.isFavorite) {
                    userPreferences.addFavoriteEvent(eventId)
                } else {
                    userPreferences.removeFavoriteEvent(eventId)
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    event = mappedEvent,
                    isFavorite = eventV1.isFavorite
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }

    fun toggleFavorite() {
        val event = _uiState.value.event ?: return
        val isFavorite = _uiState.value.isFavorite

        viewModelScope.launch {
            try {
                if (isFavorite) {
                    val resp = apiService.removeFavorite(type = "events", id = event.id)
                    if (!resp.isSuccessful) {
                        val errorBody = resp.errorBody()?.string()
                        throw Exception("Failed to remove favorite (${resp.code()}) - $errorBody")
                    }
                    userPreferences.removeFavoriteEvent(event.id)
                } else {
                    val resp = apiService.addFavorite(type = "events", id = event.id)
                    if (!resp.isSuccessful) {
                        val errorBody = resp.errorBody()?.string()
                        throw Exception("Failed to add favorite (${resp.code()}) - $errorBody")
                    }
                    userPreferences.addFavoriteEvent(event.id)
                }

                _uiState.value = _uiState.value.copy(isFavorite = !isFavorite)
            } catch (_: Exception) {
                _uiState.value = _uiState.value.copy(isFavorite = isFavorite)
            }
        }
    }

    private fun EventV1Response.toUiEvent(): Event {
        val dateText = formatMillis(startTime)
        val venueCity = venue.city

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
                    id = venueCity.id,
                    name = venueCity.name,
                    state = venueCity.zoneCode ?: "",
                    country = venueCity.countryCode ?: "",
                    latitude = venueCity.latitude,
                    longitude = venueCity.longitude
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
            val formatter = SimpleDateFormat("EEEE, MMMM d, yyyy, h:mm a z", Locale.getDefault())
            formatter.format(Date(millis))
        } catch (_: Exception) {
            millis.toString()
        }
    }
}

data class EventDetailUiState(
    val isLoading: Boolean = false,
    val event: Event? = null,
    val isFavorite: Boolean = false,
    val error: String? = null
)
