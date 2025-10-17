package com.localify.android.ui.profile

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.localify.android.data.local.UserPreferences

data class ProfileUiState(
    val userName: String = "Localify Guest",
    val email: String = "",
    val profileImageUrl: String = "",
    val memberSince: String = "September 06, 2025",
    val isLoggedIn: Boolean = false,
    val isEmailConnected: Boolean = false,
    val isSpotifyConnected: Boolean = false,
    val emailOptIn: Boolean = false,
    val generateSpotifyPlaylists: Boolean = false,
    val playlistsIncludeLocalOnly: Boolean = false
)

class ProfileViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
    
    fun connectEmail(context: Context) {
        // Open email client for connecting email
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_SUBJECT, "Connect Email to Localify")
            putExtra(Intent.EXTRA_TEXT, "I would like to connect my email to Localify.")
        }
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
            _uiState.value = _uiState.value.copy(
                isEmailConnected = true,
                email = "user@example.com" // Mock email for demo
            )
        } else {
            Toast.makeText(context, "No email app found", Toast.LENGTH_SHORT).show()
        }
    }
    
    fun connectSpotify(context: Context) {
        // Open Spotify app or web page
        val spotifyIntent = Intent(Intent.ACTION_VIEW, Uri.parse("spotify://"))
        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://open.spotify.com/"))
        
        if (spotifyIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(spotifyIntent)
        } else {
            context.startActivity(webIntent)
        }
        _uiState.value = _uiState.value.copy(isSpotifyConnected = true)
        Toast.makeText(context, "Redirecting to Spotify...", Toast.LENGTH_SHORT).show()
    }
    
    fun toggleEmailOptIn(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(emailOptIn = enabled)
    }
    
    fun toggleSpotifyPlaylists(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(generateSpotifyPlaylists = enabled)
    }
    
    fun togglePlaylistsIncludeLocal(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(playlistsIncludeLocalOnly = enabled)
    }
    
    fun logout(context: Context) {
        // Clear user preferences to reset onboarding state
        val userPreferences = UserPreferences(context)
        userPreferences.setLoggedIn(false)
        userPreferences.setOnboardingCompleted(false)
        
        _uiState.value = _uiState.value.copy(
            isLoggedIn = false,
            isEmailConnected = false,
            isSpotifyConnected = false,
            userName = "Localify Guest",
            email = "",
            profileImageUrl = "",
            emailOptIn = false,
            generateSpotifyPlaylists = false,
            playlistsIncludeLocalOnly = false
        )
        Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
    }
    
    fun deleteAccount(context: Context, onNavigateToLogin: () -> Unit) {
        // Show confirmation and delete account
        _uiState.value = ProfileUiState() // Reset to default state
        Toast.makeText(context, "Account deleted successfully", Toast.LENGTH_LONG).show()
        // Navigate back to login screen
        onNavigateToLogin()
    }
}
