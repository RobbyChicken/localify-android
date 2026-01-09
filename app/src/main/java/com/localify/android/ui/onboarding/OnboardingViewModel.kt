package com.localify.android.ui.onboarding

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.localify.android.data.network.CityResponse
import com.localify.android.data.network.GenreV1Response
import com.localify.android.data.network.ArtistV1Response
import com.localify.android.data.network.AuthResponse
import com.localify.android.data.network.NetworkModule
import com.localify.android.data.network.AddCityRequest
import com.localify.android.data.network.SeedsRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

data class OnboardingUiState(
    val isLoadingCities: Boolean = false,
    val cityResults: List<CityResponse> = emptyList(),
    val isLoadingGenres: Boolean = false,
    val curatedGenres: List<GenreV1Response> = emptyList(),
    val isLoadingArtists: Boolean = false,
    val popularArtists: List<ArtistV1Response> = emptyList(),
    val isCompleting: Boolean = false,
    val error: String? = null
)

class OnboardingViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService = NetworkModule.apiService

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun searchCities(query: String) {
        if (query.length < 2) {
            _uiState.value = _uiState.value.copy(cityResults = emptyList(), isLoadingCities = false)
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingCities = true, error = null)
            try {
                val response = callWithGuestAuthRetry { apiService.searchCities(query = query, limit = 10) }
                if (!response.isSuccessful) {
                    val errorBody = response.errorBody()?.string()
                    throw Exception("Failed to search cities (${response.code()}) - $errorBody")
                }

                _uiState.value = _uiState.value.copy(
                    isLoadingCities = false,
                    cityResults = response.body().orEmpty()
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoadingCities = false,
                    cityResults = emptyList(),
                    error = e.message ?: "Failed to search cities"
                )
            }
        }
    }

    fun loadCuratedGenres() {
        if (_uiState.value.curatedGenres.isNotEmpty() || _uiState.value.isLoadingGenres) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingGenres = true, error = null)
            try {
                val response = callWithGuestAuthRetry { apiService.getCuratedGenresV1() }
                if (!response.isSuccessful) {
                    val errorBody = response.errorBody()?.string()
                    throw Exception("Failed to load curated genres (${response.code()}) - $errorBody")
                }

                _uiState.value = _uiState.value.copy(
                    isLoadingGenres = false,
                    curatedGenres = response.body().orEmpty()
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoadingGenres = false,
                    curatedGenres = emptyList(),
                    error = e.message ?: "Failed to load genres"
                )
            }
        }
    }

    fun loadPopularArtistsForGenres(genreIds: Set<String>) {
        if (genreIds.isEmpty()) {
            _uiState.value = _uiState.value.copy(popularArtists = emptyList(), isLoadingArtists = false)
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingArtists = true, error = null)
            try {
                val response = callWithGuestAuthRetry {
                    apiService.getPopularArtistsForGenresV1(genres = genreIds.joinToString(","))
                }
                if (!response.isSuccessful) {
                    val errorBody = response.errorBody()?.string()
                    throw Exception("Failed to load popular artists (${response.code()}) - $errorBody")
                }

                _uiState.value = _uiState.value.copy(
                    isLoadingArtists = false,
                    popularArtists = response.body().orEmpty()
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoadingArtists = false,
                    popularArtists = emptyList(),
                    error = e.message ?: "Failed to load artists"
                )
            }
        }
    }

    fun completeOnboarding(cityId: String, radius: Double, seedIds: List<String>, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCompleting = true, error = null)
            try {
                val cityResponse = callWithGuestAuthRetry { apiService.addUserCity(cityId, AddCityRequest(radius)) }
                if (!cityResponse.isSuccessful) {
                    val errorBody = cityResponse.errorBody()?.string()
                    throw Exception("Failed to save city (${cityResponse.code()}) - $errorBody")
                }

                val seedsResponse = callWithGuestAuthRetry { apiService.addUserSeeds(SeedsRequest(seeds = seedIds)) }
                if (!seedsResponse.isSuccessful) {
                    val errorBody = seedsResponse.errorBody()?.string()
                    throw Exception("Failed to save seeds (${seedsResponse.code()}) - $errorBody")
                }

                _uiState.value = _uiState.value.copy(isCompleting = false)
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isCompleting = false,
                    error = e.message ?: "Failed to complete onboarding"
                )
            }
        }
    }

    private suspend fun <T> callWithGuestAuthRetry(
        block: suspend () -> Response<T>
    ): Response<T> {
        val initial = block()
        if (initial.code() != 401) return initial

        val guest = apiService.createGuestUser()
        if (guest.isSuccessful) {
            val auth: AuthResponse? = guest.body()
            if (auth != null) {
                NetworkModule.storeAuth(auth)
                return block()
            }
        }

        return initial
    }
}
