package com.localify.android

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.localify.android.ui.auth.LoginScreen
import com.localify.android.ui.detail.ArtistDetailScreen
import com.localify.android.ui.detail.EventDetailScreen
import com.localify.android.ui.home.HomeScreen
import com.localify.android.ui.favorites.FavoritesScreen
import com.localify.android.ui.search.SearchScreen
import com.localify.android.ui.profile.ProfileScreen
import com.localify.android.ui.onboarding.OnboardingScreen
import com.localify.android.ui.theme.LocalifyTheme
import com.localify.android.data.local.UserPreferences
import com.localify.android.data.network.NetworkModule

enum class Screen {
    LOGIN,
    ONBOARDING,
    HOME,
    FAVORITES,
    SEARCH,
    PROFILE,
    ARTIST_DETAIL,
    EVENT_DETAIL
}

class MainActivity : ComponentActivity() {
    private lateinit var userPreferences: UserPreferences

    private val pendingAuthUri = mutableStateOf<Uri?>(null)
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NetworkModule.init(this)
        userPreferences = UserPreferences(this)

        pendingAuthUri.value = intent?.data
        
        setContent {
            LocalifyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Check if user is already logged in and has completed onboarding
                    val isLoggedIn by userPreferences.isLoggedIn.collectAsState()
                    val hasCompletedOnboarding by userPreferences.hasCompletedOnboarding.collectAsState()
                    
                    val initialScreen = when {
                        !isLoggedIn -> Screen.LOGIN
                        !hasCompletedOnboarding -> Screen.ONBOARDING
                        else -> Screen.HOME
                    }
                    
                    var currentScreen by remember { mutableStateOf(initialScreen) }
                    var selectedArtistId by remember { mutableStateOf("") }
                    var selectedEventId by remember { mutableStateOf("") }
                    
                    when (currentScreen) {
                        Screen.LOGIN -> {
                            val authUri = pendingAuthUri.value
                            LoginScreen(
                                spotifyCallbackUri = authUri,
                                onConsumeSpotifyCallback = { pendingAuthUri.value = null },
                                onLoginSuccess = {
                                    userPreferences.setLoggedIn(true)
                                    currentScreen = Screen.ONBOARDING
                                }
                            )
                        }
                        
                        Screen.ONBOARDING -> {
                            OnboardingScreen(
                                onComplete = {
                                    userPreferences.setOnboardingCompleted(true)
                                    currentScreen = Screen.HOME
                                },
                                onCancel = {
                                    userPreferences.setLoggedIn(false)
                                    currentScreen = Screen.LOGIN
                                }
                            )
                        }
                        
                        Screen.HOME -> {
                            HomeScreen(
                                onNavigateToArtistDetail = { artistId ->
                                    if (artistId.isNotEmpty()) {
                                        selectedArtistId = artistId
                                        currentScreen = Screen.ARTIST_DETAIL
                                    }
                                },
                                onNavigateToEventDetail = { eventId ->
                                    selectedEventId = eventId
                                    currentScreen = Screen.EVENT_DETAIL
                                },
                                onNavigateToFavorites = {
                                    currentScreen = Screen.FAVORITES
                                },
                                onNavigateToSearch = {
                                    currentScreen = Screen.SEARCH
                                },
                                onNavigateToProfile = {
                                    currentScreen = Screen.PROFILE
                                }
                            )
                        }
                        
                        Screen.FAVORITES -> {
                            FavoritesScreen(
                                onNavigateToEventDetail = { eventId ->
                                    selectedEventId = eventId
                                    currentScreen = Screen.EVENT_DETAIL
                                },
                                onNavigateToArtistDetail = { artistId ->
                                    if (artistId.isNotEmpty()) {
                                        selectedArtistId = artistId
                                        currentScreen = Screen.ARTIST_DETAIL
                                    }
                                },
                                onNavigateToHome = { currentScreen = Screen.HOME },
                                onNavigateToSearch = { currentScreen = Screen.SEARCH },
                                onNavigateToProfile = { currentScreen = Screen.PROFILE }
                            )
                        }
                        
                        Screen.SEARCH -> {
                            SearchScreen(
                                onNavigateToEventDetail = { eventId ->
                                    selectedEventId = eventId
                                    currentScreen = Screen.EVENT_DETAIL
                                },
                                onNavigateToArtistDetail = { artistId ->
                                    if (artistId.isNotEmpty()) {
                                        selectedArtistId = artistId
                                        currentScreen = Screen.ARTIST_DETAIL
                                    }
                                },
                                onNavigateToHome = { currentScreen = Screen.HOME },
                                onNavigateToFavorites = { currentScreen = Screen.FAVORITES },
                                onNavigateToProfile = { currentScreen = Screen.PROFILE }
                            )
                        }
                        
                        Screen.PROFILE -> {
                            ProfileScreen(
                                onNavigateToHome = { currentScreen = Screen.HOME },
                                onNavigateToFavorites = { currentScreen = Screen.FAVORITES },
                                onNavigateToSearch = { currentScreen = Screen.SEARCH },
                                onNavigateToLogin = { 
                                    currentScreen = Screen.LOGIN 
                                }
                            )
                        }
                        
                        Screen.ARTIST_DETAIL -> {
                            ArtistDetailScreen(
                                artistId = selectedArtistId,
                                onNavigateBack = {
                                    currentScreen = Screen.HOME
                                },
                                onNavigateToEventDetail = { eventId ->
                                    selectedEventId = eventId
                                    currentScreen = Screen.EVENT_DETAIL
                                },
                                onNavigateToArtistDetail = { artistId ->
                                    selectedArtistId = artistId
                                    currentScreen = Screen.ARTIST_DETAIL
                                }
                            )
                        }
                        
                        Screen.EVENT_DETAIL -> {
                            EventDetailScreen(
                                eventId = selectedEventId,
                                onNavigateBack = {
                                    currentScreen = Screen.HOME
                                },
                                onNavigateToArtistDetail = { artistId ->
                                    selectedArtistId = artistId
                                    currentScreen = Screen.ARTIST_DETAIL
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        pendingAuthUri.value = intent.data
    }
}
