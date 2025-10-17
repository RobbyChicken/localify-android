package com.localify.android.ui.favorites

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import com.localify.android.data.models.Event
import com.localify.android.data.models.Artist
import com.localify.android.data.local.UserPreferences
import com.localify.android.data.repository.HomeRepository
import com.localify.android.data.network.ArtistRecResponse
import com.localify.android.data.network.EventRecResponse

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
    private val homeRepository = HomeRepository()
    
    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()
    
    init {
        println("DEBUG: FavoritesViewModel - Initializing")
        
        // Load initial data
        loadFavorites()
        
        // Observe favorite changes and reload data automatically using combine to avoid race conditions
        viewModelScope.launch {
            combine(
                userPreferences.favoriteArtists,
                userPreferences.favoriteEvents
            ) { artistIds, eventIds ->
                println("DEBUG: FavoritesViewModel - StateFlow changed - Artists: $artistIds, Events: $eventIds")
                Pair(artistIds, eventIds)
            }.collect { (artistIds, eventIds) ->
                println("DEBUG: FavoritesViewModel - Reloading data for Artists: $artistIds, Events: $eventIds")
                loadFavoritesImmediate()
            }
        }
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
        val favoriteArtistIds = userPreferences.favoriteArtists.value
        val favoriteEventIds = userPreferences.favoriteEvents.value
        
        println("DEBUG: ===== LOADING FAVORITES DATA =====")
        println("DEBUG: Raw artistIds from UserPreferences: $favoriteArtistIds")
        println("DEBUG: Raw eventIds from UserPreferences: $favoriteEventIds")
        
        try {
            // For now, create mock Artist objects from the stored IDs
            // In a real app, you'd fetch from an API or local database
            val favoriteArtists = favoriteArtistIds.map { artistId ->
                Artist(
                    id = artistId,
                    name = "Favorited Artist", // Placeholder name
                    imageUrl = "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=2340&q=80",
                    genres = listOf("Unknown"),
                    bio = "A favorited artist from your collection",
                    spotifyId = "spotify_$artistId",
                    popularity = 85
                )
            }
            
            // For events, create mock Event objects
            val favoriteEvents = favoriteEventIds.map { eventId ->
                Event(
                    id = eventId,
                    name = "Favorited Event",
                    imageUrl = "https://images.unsplash.com/photo-1501386761578-eac5c94b800a?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=2070&q=80",
                    date = "2024-12-01T19:00:00Z",
                    venue = com.localify.android.data.models.Venue(
                        id = "venue1",
                        name = "Local Venue",
                        address = "123 Main St",
                        city = com.localify.android.data.models.City(
                            id = "city1",
                            name = "Ithaca",
                            state = "NY",
                            country = "US",
                            latitude = 42.4440,
                            longitude = -76.5019
                        )
                    ),
                    artists = listOf(
                        Artist(
                            id = "artist1",
                            name = "Event Artist",
                            imageUrl = "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=2340&q=80",
                            genres = listOf("Rock"),
                            bio = "Artist performing at this event",
                            spotifyId = "spotify_artist1",
                            popularity = 80
                        )
                    ),
                    ticketUrl = "https://example.com/tickets/$eventId",
                    description = "A favorited event from your collection"
                )
            }
            
            println("DEBUG: Created ${favoriteArtists.size} favorite artists and ${favoriteEvents.size} favorite events")
            
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                favoriteArtists = favoriteArtists,
                upcomingEvents = favoriteEvents,
                pastEvents = emptyList(),
                error = null
            )
        } catch (e: Exception) {
            println("DEBUG: Error loading favorites: ${e.message}")
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                favoriteArtists = emptyList(),
                upcomingEvents = emptyList(),
                pastEvents = emptyList(),
                error = e.message ?: "Failed to load favorites"
            )
        }
        
        println("DEBUG: ===== END LOADING FAVORITES DATA =====")
    }
    
    fun selectTab(tab: FavoritesTab) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
    }
}
