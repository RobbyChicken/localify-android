package com.localify.android.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.localify.android.data.models.Artist
import com.localify.android.data.models.Event
import com.localify.android.data.repository.ArtistRepository
import com.localify.android.data.repository.AuthRepository
import com.localify.android.data.network.ApiService
import com.localify.android.data.local.UserPreferences
import android.app.Application
import androidx.lifecycle.AndroidViewModel

class ArtistDetailViewModel(application: Application) : AndroidViewModel(application) {
    
    private val userPreferences = UserPreferences(application)
    private val artistRepository = ArtistRepository()
    
    private val _uiState = MutableStateFlow(ArtistDetailUiState())
    val uiState: StateFlow<ArtistDetailUiState> = _uiState.asStateFlow()
    
    fun loadArtist(artistId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                // Load artist data
                val artistResult = artistRepository.getArtist(artistId)
                val eventsResult = artistRepository.getArtistEvents(artistId)
                val favoriteResult = Result.success(userPreferences.favoriteArtists.value.contains(artistId))
                
                if (artistResult.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        artist = artistResult.getOrNull(),
                        upcomingEvents = eventsResult.getOrNull() ?: emptyList(),
                        isFavorite = favoriteResult.getOrNull() ?: false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Failed to load artist"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
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
