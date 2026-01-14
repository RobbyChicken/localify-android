package com.localify.android.ui.profile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localify.android.data.network.ApiService
import com.localify.android.data.network.NetworkModule
import com.localify.android.data.network.PatchUserDetailsRequest
import com.localify.android.data.network.UserDetailsV1Response
import com.localify.android.data.network.UserCityResponse
import com.localify.android.data.network.SeedV1Response
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.localify.android.data.local.UserPreferences
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
    val playlistsIncludeLocalOnly: Boolean = false,
    val myCities: List<String> = emptyList(),
    val myFamiliarArtists: List<String> = emptyList(),
    val isLoadingCities: Boolean = false,
    val isLoadingFamiliarArtists: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

class ProfileViewModel(
    private val apiService: ApiService = NetworkModule.apiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private suspend fun ensureGuestAuth() {
        if (NetworkModule.hasValidAuth()) return
        val guest = apiService.createGuestUser()
        if (guest.isSuccessful) {
            val auth = guest.body()
            if (auth != null) {
                NetworkModule.storeAuth(auth)
            }
        }
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                ensureGuestAuth()
                val response = apiService.getMe()
                if (!response.isSuccessful) {
                    val errorBody = response.errorBody()?.string()
                    throw Exception("Failed to load profile (${response.code()}) - $errorBody")
                }
                val body = response.body() ?: throw Exception("Missing profile response")
                val preservedLocalOnly = _uiState.value.playlistsIncludeLocalOnly
                _uiState.value = body.toUiState(preservedLocalOnly).copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load profile"
                )
            }
        }
    }

    fun loadMyCities(context: Context) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingCities = true, error = null)
            try {
                ensureGuestAuth()
                val response = apiService.getUserCities()
                if (!response.isSuccessful) {
                    val errorBody = response.errorBody()?.string()
                    throw Exception("Failed to load cities (${response.code()}) - $errorBody")
                }

                val body: UserCityResponse = response.body() ?: throw Exception("Missing cities response")
                val cities = buildList {
                    add(body.current.name)
                    body.others.orEmpty().forEach { add(it.name) }
                }.distinct()

                _uiState.value = _uiState.value.copy(isLoadingCities = false, myCities = cities)
            } catch (e: Exception) {
                val fallback = UserPreferences(context).selectedCity.value
                val fallbackList = if (fallback.isNotBlank()) listOf(fallback) else emptyList()
                _uiState.value = _uiState.value.copy(
                    isLoadingCities = false,
                    myCities = fallbackList,
                    error = e.message ?: "Failed to load cities"
                )
            }
        }
    }

    fun loadMyFamiliarArtists(context: Context) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingFamiliarArtists = true, error = null)
            try {
                ensureGuestAuth()
                val response = apiService.getUserSeedsAll()
                if (!response.isSuccessful) {
                    val errorBody = response.errorBody()?.string()
                    throw Exception("Failed to load familiar artists (${response.code()}) - $errorBody")
                }

                val seeds: List<SeedV1Response> = response.body().orEmpty()
                val artists = seeds.map { it.name }.distinct()

                _uiState.value = _uiState.value.copy(isLoadingFamiliarArtists = false, myFamiliarArtists = artists)
            } catch (e: Exception) {
                val fallback = UserPreferences(context).selectedArtists.value.toList()
                _uiState.value = _uiState.value.copy(
                    isLoadingFamiliarArtists = false,
                    myFamiliarArtists = fallback,
                    error = e.message ?: "Failed to load familiar artists"
                )
            }
        }
    }

    fun connectEmail(context: Context) {
        // Open email client for connecting email
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_SUBJECT, "Connect Email to Localify")
            putExtra(Intent.EXTRA_TEXT, "I would like to connect my email to Localify.")
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
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

        spotifyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        webIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        if (spotifyIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(spotifyIntent)
        } else {
            context.startActivity(webIntent)
        }
        _uiState.value = _uiState.value.copy(isSpotifyConnected = true)
        Toast.makeText(context, "Redirecting to Spotify...", Toast.LENGTH_SHORT).show()
    }

    fun setEmailOptIn(enabled: Boolean) {
        val previous = _uiState.value
        _uiState.value = previous.copy(emailOptIn = enabled, error = null)

        viewModelScope.launch {
            patchMe(
                body = PatchUserDetailsRequest(emailOptIn = enabled),
                onFailure = { msg ->
                    _uiState.value = previous.copy(error = msg)
                }
            )
        }
    }

    fun setGenerateSpotifyPlaylists(enabled: Boolean) {
        val previous = _uiState.value
        _uiState.value = previous.copy(generateSpotifyPlaylists = enabled, error = null)

        viewModelScope.launch {
            patchMe(
                body = PatchUserDetailsRequest(generateSpotifyPlaylists = enabled),
                onFailure = { msg ->
                    _uiState.value = previous.copy(error = msg)
                }
            )
        }
    }

    fun setPlaylistsIncludeLocalOnly(enabled: Boolean) {
        val previous = _uiState.value
        _uiState.value = previous.copy(playlistsIncludeLocalOnly = enabled, error = null)

        viewModelScope.launch {
            patchMe(
                body = PatchUserDetailsRequest(playlistsIncludeLocalOnly = enabled),
                onFailure = { msg ->
                    _uiState.value = previous.copy(error = msg)
                }
            )
        }
    }

    fun logout(context: Context) {
        // Clear user preferences to reset onboarding state
        val userPreferences = UserPreferences(context)
        NetworkModule.clearAuth()
        userPreferences.clearAllData()

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
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val response = apiService.deleteMe()
                if (!response.isSuccessful) {
                    val errorBody = response.errorBody()?.string()
                    throw Exception("Failed to delete account (${response.code()}) - $errorBody")
                }

                NetworkModule.clearAuth()
                UserPreferences(context).clearAllData()
                _uiState.value = ProfileUiState()
                Toast.makeText(context, "Account deleted successfully", Toast.LENGTH_LONG).show()
                onNavigateToLogin()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to delete account"
                )
            }
        }
    }

    private suspend fun patchMe(
        body: PatchUserDetailsRequest,
        onFailure: (String) -> Unit
    ) {
        try {
            val response = apiService.patchMe(body)
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()
                onFailure("Failed to update profile (${response.code()}) - $errorBody")
                return
            }

            val updated = response.body()
            if (updated != null) {
                val preservedLocalOnly = _uiState.value.playlistsIncludeLocalOnly
                _uiState.value = updated.toUiState(preservedLocalOnly).copy(error = null)
            }
        } catch (e: Exception) {
            onFailure(e.message ?: "Failed to update profile")
        }
    }

    private fun UserDetailsV1Response.toUiState(
        preservedPlaylistsIncludeLocalOnly: Boolean
    ): ProfileUiState {
        val createdAtMs = if (accountCreationDate < 1_000_000_000_000L) accountCreationDate * 1000L else accountCreationDate
        val memberSince = SimpleDateFormat("MMMM dd, yyyy", Locale.US).format(Date(createdAtMs))
        val profileImage = profileImage ?: spotifyProfileImage ?: ""

        return ProfileUiState(
            userName = name,
            email = email.orEmpty(),
            profileImageUrl = profileImage,
            memberSince = memberSince,
            isLoggedIn = !anonymousUser,
            isEmailConnected = emailConnected,
            isSpotifyConnected = spotifyConnected,
            emailOptIn = emailOptIn,
            generateSpotifyPlaylists = playlistGeneration,
            playlistsIncludeLocalOnly = preservedPlaylistsIncludeLocalOnly
        )
    }
}
