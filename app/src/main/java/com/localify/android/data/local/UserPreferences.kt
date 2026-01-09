package com.localify.android.data.local

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserPreferences(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "localify_user_prefs", 
        Context.MODE_PRIVATE
    )
    
    private val _isLoggedIn = MutableStateFlow(getBoolean(KEY_IS_LOGGED_IN, false))
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()
    
    private val _username = MutableStateFlow(getString(KEY_USERNAME, ""))
    val username: StateFlow<String> = _username.asStateFlow()
    
    private val _selectedCity = MutableStateFlow(getString(KEY_SELECTED_CITY, "Ithaca, NY"))
    val selectedCity: StateFlow<String> = _selectedCity.asStateFlow()
    
    private val _selectedRadius = MutableStateFlow(getFloat(KEY_SELECTED_RADIUS, 25f))
    val selectedRadius: StateFlow<Float> = _selectedRadius.asStateFlow()
    
    private val _selectedGenres = MutableStateFlow(getStringSet(KEY_SELECTED_GENRES, emptySet()))
    val selectedGenres: StateFlow<Set<String>> = _selectedGenres.asStateFlow()
    
    private val _selectedArtists = MutableStateFlow(getStringSet(KEY_SELECTED_ARTISTS, emptySet()))
    val selectedArtists: StateFlow<Set<String>> = _selectedArtists.asStateFlow()
    
    private val _favoriteArtists = MutableStateFlow(getStringSet(KEY_FAVORITE_ARTISTS, emptySet()))
    val favoriteArtists: StateFlow<Set<String>> = _favoriteArtists.asStateFlow()
    
    private val _favoriteEvents = MutableStateFlow(getStringSet(KEY_FAVORITE_EVENTS, emptySet()))
    val favoriteEvents: StateFlow<Set<String>> = _favoriteEvents.asStateFlow()
    
    private val _isSpotifyConnected = MutableStateFlow(getBoolean(KEY_SPOTIFY_CONNECTED, false))
    val isSpotifyConnected: StateFlow<Boolean> = _isSpotifyConnected.asStateFlow()
    
    private val _isEmailConnected = MutableStateFlow(getBoolean(KEY_EMAIL_CONNECTED, false))
    val isEmailConnected: StateFlow<Boolean> = _isEmailConnected.asStateFlow()
    
    private val _emailOptIn = MutableStateFlow(getBoolean(KEY_EMAIL_OPT_IN, false))
    val emailOptIn: StateFlow<Boolean> = _emailOptIn.asStateFlow()
    
    private val _playlistGeneration = MutableStateFlow(getBoolean(KEY_PLAYLIST_GENERATION, false))
    val playlistGeneration: StateFlow<Boolean> = _playlistGeneration.asStateFlow()
    
    private val _hasCompletedOnboarding = MutableStateFlow(getBoolean(KEY_ONBOARDING_COMPLETED, false))
    val hasCompletedOnboarding: StateFlow<Boolean> = _hasCompletedOnboarding.asStateFlow()

    fun getFavoriteArtistsSnapshot(): Set<String> {
        return getStringSet(KEY_FAVORITE_ARTISTS, emptySet())
    }

    fun getFavoriteEventsSnapshot(): Set<String> {
        return getStringSet(KEY_FAVORITE_EVENTS, emptySet())
    }
    
    // Save functions
    fun setLoggedIn(isLoggedIn: Boolean) {
        prefs.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply()
        _isLoggedIn.value = isLoggedIn
    }
    
    fun setUsername(username: String) {
        putString(KEY_USERNAME, username)
        _username.value = username
    }
    
    fun setSelectedCity(city: String) {
        putString(KEY_SELECTED_CITY, city)
        _selectedCity.value = city
    }
    
    fun setSelectedRadius(radius: Float) {
        putFloat(KEY_SELECTED_RADIUS, radius)
        _selectedRadius.value = radius
    }
    
    fun setSelectedGenres(genres: Set<String>) {
        putStringSet(KEY_SELECTED_GENRES, genres)
        _selectedGenres.value = genres
    }
    
    fun setSelectedArtists(artists: Set<String>) {
        putStringSet(KEY_SELECTED_ARTISTS, artists)
        _selectedArtists.value = artists
    }
    
    fun addFavoriteArtist(artistId: String) {
        val current = _favoriteArtists.value.toMutableSet()
        current.add(artistId)
        putStringSet(KEY_FAVORITE_ARTISTS, current)
        _favoriteArtists.value = current
        println("DEBUG: Added artist $artistId to favorites. Current: $current")
    }
    
    fun removeFavoriteArtist(artistId: String) {
        val current = _favoriteArtists.value.toMutableSet()
        current.remove(artistId)
        putStringSet(KEY_FAVORITE_ARTISTS, current)
        _favoriteArtists.value = current
        println("DEBUG: Removed artist $artistId from favorites. Current: $current")
    }
    
    fun addFavoriteEvent(eventId: String) {
        val current = _favoriteEvents.value.toMutableSet()
        current.add(eventId)
        putStringSet(KEY_FAVORITE_EVENTS, current)
        _favoriteEvents.value = current
        println("DEBUG: Added event $eventId to favorites. Current: $current")
    }
    
    fun removeFavoriteEvent(eventId: String) {
        val current = _favoriteEvents.value.toMutableSet()
        current.remove(eventId)
        putStringSet(KEY_FAVORITE_EVENTS, current)
        _favoriteEvents.value = current
        println("DEBUG: Removed event $eventId from favorites. Current: $current")
    }
    
    fun setSpotifyConnected(connected: Boolean) {
        putBoolean(KEY_SPOTIFY_CONNECTED, connected)
        _isSpotifyConnected.value = connected
    }
    
    fun setEmailConnected(connected: Boolean) {
        putBoolean(KEY_EMAIL_CONNECTED, connected)
        _isEmailConnected.value = connected
    }
    
    fun setEmailOptIn(optIn: Boolean) {
        putBoolean(KEY_EMAIL_OPT_IN, optIn)
        _emailOptIn.value = optIn
    }
    
    fun setPlaylistGeneration(enabled: Boolean) {
        putBoolean(KEY_PLAYLIST_GENERATION, enabled)
        _playlistGeneration.value = enabled
    }
    
    fun clearAllData() {
        prefs.edit().clear().apply()
        _isLoggedIn.value = false
        _username.value = ""
        _selectedCity.value = "Ithaca, NY"
        _selectedRadius.value = 25f
        _selectedGenres.value = emptySet()
        _selectedArtists.value = emptySet()
        _favoriteArtists.value = emptySet()
        _favoriteEvents.value = emptySet()
        _isSpotifyConnected.value = false
        _isEmailConnected.value = false
        _emailOptIn.value = false
        _playlistGeneration.value = false
        _hasCompletedOnboarding.value = false
    }
    
    // Helper functions
    private fun putString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }
    
    private fun getString(key: String, defaultValue: String): String {
        return prefs.getString(key, defaultValue) ?: defaultValue
    }
    
    private fun putBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }
    
    private fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return prefs.getBoolean(key, defaultValue)
    }
    
    private fun putFloat(key: String, value: Float) {
        prefs.edit().putFloat(key, value).apply()
    }
    
    private fun getFloat(key: String, defaultValue: Float): Float {
        return prefs.getFloat(key, defaultValue)
    }
    
    private fun putStringSet(key: String, value: Set<String>) {
        prefs.edit().putStringSet(key, value).apply()
    }
    
    private fun getStringSet(key: String, defaultValue: Set<String>): Set<String> {
        return prefs.getStringSet(key, defaultValue) ?: defaultValue
    }
    
    fun setOnboardingCompleted(completed: Boolean) {
        putBoolean(KEY_ONBOARDING_COMPLETED, completed)
        _hasCompletedOnboarding.value = completed
    }
    
    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USERNAME = "username"
        private const val KEY_SELECTED_CITY = "selected_city"
        private const val KEY_SELECTED_RADIUS = "selected_radius"
        private const val KEY_SELECTED_GENRES = "selected_genres"
        private const val KEY_SELECTED_ARTISTS = "selected_artists"
        private const val KEY_FAVORITE_ARTISTS = "favorite_artists"
        private const val KEY_FAVORITE_EVENTS = "favorite_events"
        private const val KEY_SPOTIFY_CONNECTED = "spotify_connected"
        private const val KEY_EMAIL_CONNECTED = "email_connected"
        private const val KEY_EMAIL_OPT_IN = "email_opt_in"
        private const val KEY_PLAYLIST_GENERATION = "playlist_generation"
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
    }
}
