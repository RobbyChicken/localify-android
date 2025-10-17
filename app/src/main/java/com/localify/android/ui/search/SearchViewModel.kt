package com.localify.android.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import com.localify.android.data.models.Event
import com.localify.android.data.models.Artist
import com.localify.android.data.repository.EventRepository
import com.localify.android.data.repository.ArtistRepository

data class SearchResults(
    val artists: List<Artist> = emptyList(),
    val events: List<Event> = emptyList()
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
    
    private val eventRepository = EventRepository()
    
    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        
        if (query.isNotBlank()) {
            performSearch(query)
        } else {
            _uiState.value = _uiState.value.copy(
                searchResults = SearchResults(),
                isLoading = false
            )
        }
    }
    
    private fun performSearch(query: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                // Add a small delay to simulate network request
                delay(300)
                
                // Mock search results - in real app this would call API
                val allArtists = getMockArtists()
                val allEvents = eventRepository.getFeaturedEvents()
                
                val filteredArtists = allArtists.filter { artist ->
                    artist.name.contains(query, ignoreCase = true) ||
                    artist.genres.any { it.contains(query, ignoreCase = true) }
                }
                
                val filteredEvents = allEvents.filter { event ->
                    event.name.contains(query, ignoreCase = true) ||
                    event.artists.any { it.name.contains(query, ignoreCase = true) } ||
                    event.venue.name.contains(query, ignoreCase = true) ||
                    event.venue.city.name.contains(query, ignoreCase = true)
                }
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    searchResults = SearchResults(
                        artists = filteredArtists,
                        events = filteredEvents
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
    
    private fun getMockArtists(): List<Artist> {
        return listOf(
            Artist(
                id = "artist1",
                name = "The National",
                imageUrl = "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=2340&q=80",
                bio = "American indie rock band",
                genres = listOf("Indie Rock", "Alternative"),
                spotifyId = "2cCUtGK9sDU2EoElnk0GNB",
                popularity = 85
            ),
            Artist(
                id = "artist2",
                name = "Phoebe Bridgers",
                imageUrl = "https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=2340&q=80",
                bio = "American indie folk singer-songwriter",
                genres = listOf("Indie Folk", "Alternative"),
                spotifyId = "1r1uxoy19fzMxunt3ONAkG",
                popularity = 92
            ),
            Artist(
                id = "artist3",
                name = "Robert Glasper",
                imageUrl = "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=2340&q=80",
                bio = "American jazz pianist and producer",
                genres = listOf("Jazz", "Neo-Soul"),
                spotifyId = "5cM1PvItlR21WUyBnsdMcn",
                popularity = 68
            ),
            Artist(
                id = "artist4",
                name = "Arctic Monkeys",
                imageUrl = "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=2340&q=80",
                bio = "British rock band",
                genres = listOf("Rock", "Indie"),
                spotifyId = "7Ln80lUS6He07XvHI8qqHH",
                popularity = 85
            ),
            Artist(
                id = "artist5",
                name = "Billie Eilish",
                imageUrl = "https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=2340&q=80",
                bio = "American singer-songwriter",
                genres = listOf("Pop", "Alternative"),
                spotifyId = "6qqNVTkY8uBg9cP3Jd8DAH",
                popularity = 92
            )
        )
    }
}
