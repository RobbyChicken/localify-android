package com.localify.android.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.Dispatchers
import com.localify.android.data.network.ArtistRecResponse
import com.localify.android.data.network.EventRecResponse
import com.localify.android.data.network.EventRecommendationsRequest
import com.localify.android.data.network.UserCity
import com.localify.android.data.network.NetworkModule
import com.localify.android.data.repository.HomeRepository

enum class HomeTab {
    EVENTS, ARTISTS
}

data class HomeUiState(
    val isLoading: Boolean = false,
    val events: List<EventRecResponse> = emptyList(),
    val artists: List<ArtistRecResponse> = emptyList(),
    val selectedTab: HomeTab = HomeTab.EVENTS,
    val currentCity: UserCity? = null,
    val error: String? = null,
    val authToken: String? = null,
    val allEvents: List<EventRecResponse> = emptyList(),
    val selectedTimeFrame: String = "All upcoming",
    val startDate: String? = null,
    val endDate: String? = null
)

class HomeViewModel(
    private val homeRepository: HomeRepository = HomeRepository()
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        initializeApp()
    }
    
    private fun initializeApp() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                println("DEBUG: Starting app initialization...")

                val authToken = if (NetworkModule.hasValidAuth()) {
                    println("DEBUG: Using existing stored auth token")
                    NetworkModule.getAccessToken()
                } else {
                    println("DEBUG: Creating guest user...")
                    val authResponse = homeRepository.createGuestUser()
                    NetworkModule.storeAuth(authResponse)
                    println("DEBUG: Got auth token: ${authResponse.token.take(20)}...")
                    authResponse.token
                }
                
                // Get user cities
                println("DEBUG: Getting user cities...")
                val userCities = homeRepository.getUserCities()
                val currentCity = userCities.current
                println("DEBUG: Got current city: ${currentCity.name}")
                
                _uiState.value = _uiState.value.copy(
                    authToken = authToken,
                    currentCity = currentCity
                )
                
                // Load recommendations for current city
                println("DEBUG: Loading recommendations...")
                loadRecommendations()
                
            } catch (e: Exception) {
                println("DEBUG: FULL ERROR STACK TRACE:")
                e.printStackTrace()
                
                val detailedError = when {
                    e.message?.contains("UnknownHostException") == true -> "Network error: Unable to resolve host"
                    e.message?.contains("ConnectException") == true -> "Connection failed: Server may be down"
                    e.message?.contains("SSLException") == true -> "SSL/HTTPS error: Certificate issue"
                    e.message?.contains("SocketTimeoutException") == true -> "Request timeout: Server not responding"
                    e.message?.contains("401") == true -> "Authentication failed"
                    e.message?.contains("404") == true -> "API endpoint not found"
                    e.message?.contains("500") == true -> "Server error"
                    else -> "Failed to load data: ${e.message}"
                }
                _uiState.value = _uiState.value.copy(isLoading = false, error = detailedError)
            }
        }
    }

    fun changeCity(cityId: String, radiusMiles: Double) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                val cities = homeRepository.setUserCity(cityId = cityId, radius = radiusMiles)
                _uiState.value = _uiState.value.copy(currentCity = cities.current)
                loadRecommendations()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to change city"
                )
            }
        }
    }
    
    fun loadRecommendations() {
        viewModelScope.launch {
            val currentState = _uiState.value
            val cityId = currentState.currentCity?.id?.toString()
            
            println("DEBUG: loadRecommendations() - Starting with cityId: $cityId")
            
            if (cityId == null) {
                val errorMsg = "Missing city information. City ID: $cityId"
                println("DEBUG: $errorMsg")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = errorMsg
                )
                return@launch
            }
            
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                println("DEBUG: Loading artist and event recommendations...")
                
                // Load artist recommendations
                val artists = try {
                    println("DEBUG: Starting artist recommendations fetch...")
                    val artists = homeRepository.getArtistRecommendations(cityId)
                    println("DEBUG: Successfully fetched ${artists.size} artists")
                    
                    // Log artist details for debugging
                    if (artists.isNotEmpty()) {
                        println("DEBUG: === ARTISTS ===")
                        artists.forEachIndexed { index, artist ->
                            println("${index + 1}. ${artist.name} (ID: ${artist.id})")
                            println("   Image: ${artist.image}")
                            println("   Spotify URL: ${artist.spotifyUrl}")
                            println("   Genres: ${artist.genres?.joinToString { it.name } ?: "None"}")
                        }
                    } else {
                        println("WARNING: No artists returned from API")
                    }
                    artists
                } catch (e: Exception) {
                    println("ERROR: Failed to fetch artists: ${e.message}")
                    e.printStackTrace()
                    emptyList<ArtistRecResponse>()
                }
                
                // Load event recommendations
                val events = try {
                    println("DEBUG: Starting event recommendations fetch...")
                    val request = EventRecommendationsRequest(
                        limit = 20,
                        startDate = currentState.startDate,
                        endDate = currentState.endDate
                    )
                    val events = homeRepository.getEventRecommendations(cityId, request)
                    println("DEBUG: Successfully loaded ${events.size} events")
                    events.map { it.normalizeTimestamps() }
                } catch (e: Exception) {
                    println("ERROR: Failed to fetch events: ${e.message}")
                    e.printStackTrace()
                    emptyList<EventRecResponse>()
                }
                
                // Update UI state with the loaded data
                val filteredEvents = filterEvents(events, currentState.selectedTimeFrame, currentState.startDate, currentState.endDate)
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    artists = artists,
                    allEvents = events,
                    events = filteredEvents,
                    error = if (artists.isEmpty() && events.isEmpty()) "No data available" else null
                )
                
                println("DEBUG: UI state updated with ${artists.size} artists and ${filteredEvents.size} events")
                
            } catch (e: Exception) {
                val errorMsg = "Failed to load recommendations: ${e.message ?: "Unknown error"}"
                println("ERROR: $errorMsg")
                e.printStackTrace()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = errorMsg
                )
            }
        }
    }
    
    fun selectTab(tab: HomeTab) {
        println("DEBUG: Tab selected: $tab")
        _uiState.value = _uiState.value.copy(selectedTab = tab)
        
        // Load recommendations for the selected tab
        loadRecommendations()
    }
    
    fun retry() {
        initializeApp()
    }
    
    fun applyDateFilter(timeFrame: String, startDate: String? = null, endDate: String? = null) {
        val currentState = _uiState.value
        val filteredEvents = filterEvents(currentState.allEvents, timeFrame, startDate, endDate)
        
        _uiState.value = currentState.copy(
            selectedTimeFrame = timeFrame,
            startDate = startDate,
            endDate = endDate,
            events = filteredEvents
        )
    }
    
    private fun filterEvents(
        events: List<EventRecResponse>,
        timeFrame: String,
        startDate: String?,
        endDate: String?
    ): List<EventRecResponse> {
        val now = System.currentTimeMillis()
        
        return when (timeFrame) {
            "Custom" -> {
                if (startDate != null && endDate != null) {
                    val startMillis = parseDate(startDate)
                    val endMillis = parseDate(endDate)
                    events.filter { event ->
                        val eventDate = parseEventDate(event.startTime)
                        eventDate in startMillis..endMillis
                    }
                } else {
                    events
                }
            }
            "One week" -> {
                val oneWeekFromNow = now + (7 * 24 * 60 * 60 * 1000L)
                events.filter { event ->
                    val eventDate = parseEventDate(event.startTime)
                    eventDate in now..oneWeekFromNow
                }
            }
            "One month" -> {
                val oneMonthFromNow = now + (30 * 24 * 60 * 60 * 1000L)
                events.filter { event ->
                    val eventDate = parseEventDate(event.startTime)
                    eventDate in now..oneMonthFromNow
                }
            }
            "Three months" -> {
                val threeMonthsFromNow = now + (90 * 24 * 60 * 60 * 1000L)
                events.filter { event ->
                    val eventDate = parseEventDate(event.startTime)
                    eventDate in now..threeMonthsFromNow
                }
            }
            "All upcoming" -> {
                events.filter { event ->
                    val eventDate = parseEventDate(event.startTime)
                    eventDate >= now
                }
            }
            else -> events
        }
    }
    
    private fun parseDate(dateString: String): Long {
        return try {
            val formatter = java.text.SimpleDateFormat("MMM d, yyyy", java.util.Locale.getDefault())
            formatter.parse(dateString)?.time ?: System.currentTimeMillis()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }
    
    private fun parseEventDate(startTime: Long): Long {
        return normalizeEpochMillis(startTime)
    }

    private fun normalizeEpochMillis(value: Long): Long {
        // If backend returns epoch seconds, convert to millis.
        return if (value in 1..999_999_999_999L) value * 1000L else value
    }

    private fun EventRecResponse.normalizeTimestamps(): EventRecResponse {
        val normalizedStart = normalizeEpochMillis(startTime)
        val normalizedCreatedAt = normalizeEpochMillis(createdAt)
        return if (normalizedStart == startTime && normalizedCreatedAt == createdAt) {
            this
        } else {
            copy(startTime = normalizedStart, createdAt = normalizedCreatedAt)
        }
    }
}
