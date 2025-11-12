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

class ArtistDetailViewModel(application: Application) : AndroidViewModel(application) {
    
    private val userPreferences = UserPreferences(application)
    
    private val _uiState = MutableStateFlow(ArtistDetailUiState())
    val uiState: StateFlow<ArtistDetailUiState> = _uiState.asStateFlow()
    
    fun loadArtist(artistId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                // Create mock artist data since backend doesn't have individual artist endpoints yet
                val mockArtist = createMockArtist(artistId)
                val mockEvents = createMockEvents(artistId)
                val isFavorite = userPreferences.favoriteArtists.value.contains(artistId)
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    artist = mockArtist,
                    upcomingEvents = mockEvents,
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
    
    private fun createMockArtist(artistId: String): Artist {
        // Create mock artist data for any ID
        // Use a hash of the ID to generate consistent but varied data
        val hash = artistId.hashCode()
        val artistNames = listOf("Kurt Riley", "Randy Travis", "Alex Morgan", "Sarah Chen", "David Wilson")
        val genresList = listOf(
            listOf("Cold Wave", "Riddim", "Metapop"),
            listOf("Country", "Gospel"),
            listOf("Indie Rock", "Alternative"),
            listOf("Electronic", "Ambient"),
            listOf("Folk", "Acoustic")
        )
        val images = listOf(
            "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=2340&q=80",
            "https://images.unsplash.com/photo-1516280440614-37939bbacd81?ixlib=rb-4.0.3&auto=format&fit=crop&w=2340&q=80",
            "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?ixlib=rb-4.0.3&auto=format&fit=crop&w=2340&q=80"
        )
        
        val nameIndex = kotlin.math.abs(hash) % artistNames.size
        val genreIndex = kotlin.math.abs(hash) % genresList.size
        val imageIndex = kotlin.math.abs(hash) % images.size
        
        return Artist(
            id = artistId,
            name = artistNames[nameIndex],
            imageUrl = images[imageIndex],
            genres = genresList[genreIndex],
            bio = "${artistNames[nameIndex]} is a talented musician known for their unique style and captivating performances.",
            spotifyId = "${artistId}_spotify",
            popularity = 50 + (kotlin.math.abs(hash) % 50)
        )
    }
    
    private fun createMockEvents(artistId: String): List<Event> {
        // Create mock events for the artist
        return listOf(
            Event(
                id = "${artistId}_event_1",
                name = "Live Concert",
                imageUrl = "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=2340&q=80",
                date = "December 15, 2025 • 8:00 PM",
                venue = Venue(
                    id = "venue_1",
                    name = "The Music Hall",
                    address = "123 Music St",
                    city = City(
                        id = "city_1",
                        name = "Ithaca",
                        state = "NY",
                        country = "USA",
                        latitude = 42.4440,
                        longitude = -76.5019
                    )
                ),
                artists = listOf(createMockArtist(artistId)),
                ticketUrl = "https://example.com/tickets",
                description = "An amazing live performance."
            )
        )
    }
    
    fun toggleFavorite() {
        val currentArtist = _uiState.value.artist ?: return
        val currentFavorite = _uiState.value.isFavorite
        
        viewModelScope.launch {
            try {
                if (currentFavorite) {
                    userPreferences.removeFavoriteArtist(currentArtist.id)
                } else {
                    userPreferences.addFavoriteArtist(currentArtist.id)
                }
                
                _uiState.value = _uiState.value.copy(
                    isFavorite = !currentFavorite
                )
            } catch (e: Exception) {
                // Revert on error
                _uiState.value = _uiState.value.copy(
                    isFavorite = currentFavorite
                )
            }
        }
    }
    
}

data class ArtistDetailUiState(
    val isLoading: Boolean = false,
    val artist: Artist? = null,
    val upcomingEvents: List<Event> = emptyList(),
    val isFavorite: Boolean = false,
    val error: String? = null
)
