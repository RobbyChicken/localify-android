package com.localify.android.data.repository

import android.util.Log
import com.localify.android.data.network.NetworkModule
import com.localify.android.data.network.ArtistRecResponse
import com.localify.android.data.network.EventRecResponse
import com.localify.android.data.network.UserCityResponse
import com.localify.android.data.network.AuthResponse
import com.localify.android.data.network.ArtistRecommendationsRequest

class HomeRepository {
    private val apiService by lazy { NetworkModule.apiService }
    
    suspend fun createGuestUser(): AuthResponse {
        Log.d("HomeRepository", "Creating guest user...")
        val response = apiService.createGuestUser()
        Log.d("HomeRepository", "Guest user response: ${response.code()}")
        if (response.isSuccessful) {
            val authResponse = response.body() ?: throw Exception("Empty response body")
            Log.d("HomeRepository", "Guest user created successfully")
            return authResponse
        } else {
            val errorBody = response.errorBody()?.string()
            Log.e("HomeRepository", "Failed to create guest user: ${response.code()}, body: $errorBody")
            throw Exception("Failed to create guest user: ${response.code()} - $errorBody")
        }
    }
    
    suspend fun getUserCities(): UserCityResponse {
        Log.d("HomeRepository", "Getting user cities")
        val response = apiService.getUserCities()
        Log.d("HomeRepository", "User cities response: ${response.code()}")
        if (response.isSuccessful) {
            val cities = response.body()!!
            Log.d("HomeRepository", "Got user cities: current=${cities.current.name}")
            return cities
        } else if (response.code() == 409) {
            Log.d("HomeRepository", "User has no cities (409), setting up default city and seeds")
            
            // Step 1: Add default city (prefer Ithaca, NY)
            val fallbackCityId = "bb5dbc41-213b-45c6-8eb3-523fcb3e85f6" // NYC UUID from staging server
            val defaultCityId = try {
                val search = apiService.searchCities("Ithaca", limit = 10)
                if (search.isSuccessful) {
                    val matches = search.body().orEmpty()
                    val ithaca = matches.firstOrNull { (it.name.contains("Ithaca", ignoreCase = true)) && (it.state?.equals("NY", ignoreCase = true) == true) }
                        ?: matches.firstOrNull { it.name.contains("Ithaca", ignoreCase = true) }
                    ithaca?.id ?: fallbackCityId
                } else {
                    fallbackCityId
                }
            } catch (_: Exception) {
                fallbackCityId
            }

            val addCityResponse = apiService.putUserCity(
                defaultCityId,
                com.localify.android.data.network.AddCityRequest(radius = 50.0)
            )
            if (!addCityResponse.isSuccessful) {
                val errorBody = addCityResponse.errorBody()?.string()
                Log.e("HomeRepository", "Failed to add default city: ${addCityResponse.code()}, body: $errorBody")
                throw Exception("Failed to setup user city: ${addCityResponse.code()} - $errorBody")
            }

            try {
                val select = apiService.patchUserCity(
                    defaultCityId,
                    com.localify.android.data.network.PatchUserCityRequest(selected = true, radius = 50.0)
                )
                if (!select.isSuccessful) {
                    Log.w("HomeRepository", "Default city selection PATCH failed: ${select.code()}")
                }
            } catch (e: Exception) {
                Log.w("HomeRepository", "Default city selection PATCH failed: ${e.message}")
            }
            
            // Step 2: Add default seeds (popular artists)
            try {
                Log.d("HomeRepository", "Adding default seeds for new user")
                val seedsRequest = com.localify.android.data.network.SeedsRequest(
                    seeds = listOf(
                        "1b72e4a5-5d2e-419a-beca-8a32b8e6f32c", // Taylor Swift
                        "6155c0af-f055-426b-8180-220fb26cfbfa", // Taylor Swift Piano Covers  
                        "7e4783bd-405c-43b8-9c6b-4a4a4a07cd5f"  // Taylor Swift Ambientonia
                    )
                )
                val seedsResponse = apiService.addUserSeeds(seedsRequest)
                if (seedsResponse.isSuccessful) {
                    Log.d("HomeRepository", "Successfully added default seeds")
                } else {
                    Log.w("HomeRepository", "Failed to add seeds but continuing: ${seedsResponse.code()}")
                }
            } catch (e: Exception) {
                Log.w("HomeRepository", "Failed to add seeds but continuing: ${e.message}")
            }
            
            // Step 3: Retry getting cities
            Log.d("HomeRepository", "Successfully added default city, retrying getUserCities")
            return getUserCities()
        } else {
            val errorBody = response.errorBody()?.string()
            Log.e("HomeRepository", "Failed to get user cities: ${response.code()}, body: $errorBody")
            throw Exception("Failed to get user cities: ${response.code()} - $errorBody")
        }
    }

    suspend fun setUserCity(cityId: String, radius: Double): UserCityResponse {
        Log.d("HomeRepository", "Setting user city: cityId=$cityId radius=$radius")

        val upsert = apiService.putUserCity(cityId, com.localify.android.data.network.AddCityRequest(radius = radius))
        if (!upsert.isSuccessful) {
            val errorBody = upsert.errorBody()?.string()
            Log.e("HomeRepository", "Failed to set user city: ${upsert.code()}, body: $errorBody")
            throw Exception("Failed to set user city: ${upsert.code()} - $errorBody")
        }

        // Some backends require an explicit selection change.
        try {
            val select = apiService.patchUserCity(
                cityId,
                com.localify.android.data.network.PatchUserCityRequest(selected = true, radius = radius)
            )
            if (!select.isSuccessful) {
                Log.w("HomeRepository", "City selection PATCH failed: ${select.code()}")
            }
        } catch (e: Exception) {
            Log.w("HomeRepository", "City selection PATCH failed: ${e.message}")
        }

        return getUserCities()
    }
    
    suspend fun getArtistRecommendations(cityId: String, limit: Int = 20): List<ArtistRecResponse> {
        try {
            Log.d("HomeRepository", "=== GET ARTIST RECOMMENDATIONS ===")
            Log.d("HomeRepository", "City ID: $cityId")
            Log.d("HomeRepository", "Limit: $limit")
            Log.d("HomeRepository", "Auth token: handled by interceptor")
            
            val response = try {
                apiService.getArtistRecommendations(cityId, limit)
            } catch (e: Exception) {
                Log.e("HomeRepository", "API call failed: ${e.message}", e)
                return emptyList()
            }
            
            // Log raw response details
            val rawResponse = response.raw()
            val responseBody = response.body()
            val errorBody = response.errorBody()?.string()
            
            Log.d("HomeRepository", "=== API RESPONSE ===")
            Log.d("HomeRepository", "URL: ${rawResponse.request.url}")
            Log.d("HomeRepository", "Code: ${rawResponse.code}")
            Log.d("HomeRepository", "Message: ${rawResponse.message}")
            Log.d("HomeRepository", "Is Successful: ${response.isSuccessful}")
            
            if (!response.isSuccessful) {
                Log.e("HomeRepository", "Error Response: $errorBody")
                Log.e("HomeRepository", "Headers: ${rawResponse.headers}")
                return emptyList()
            }
            
            val artists = responseBody ?: return emptyList()
            
            // Log artist details for debugging
            if (artists.isEmpty()) {
                Log.w("HomeRepository", "WARNING: Received empty artist recommendations list")
            } else {
                Log.d("HomeRepository", "Successfully parsed ${artists.size} artists")
                Log.d("HomeRepository", "=== SAMPLE ARTISTS ===")
                
                artists.take(3).forEachIndexed { index, artist ->
                    Log.d("HomeRepository", "${index + 1}. ${artist.name} (ID: ${artist.id})")
                    Log.d("HomeRepository", "   Image: ${artist.image ?: "N/A"}")
                    Log.d("HomeRepository", "   Spotify URL: ${artist.spotifyUrl ?: "N/A"}")
                    Log.d("HomeRepository", "   Genres: ${artist.genres?.joinToString { it.name } ?: "None"}")
                    Log.d("HomeRepository", "   Similar: ${artist.similar?.take(2)?.joinToString { it.name } ?: "None"}")
                }
            }
            
            return artists
            
        } catch (e: Exception) {
            Log.e("HomeRepository", "Error in getArtistRecommendations", e)
            return emptyList()
        }
    }
    
    suspend fun getEventRecommendations(cityId: String, request: com.localify.android.data.network.EventRecommendationsRequest = com.localify.android.data.network.EventRecommendationsRequest()): List<EventRecResponse> {
        Log.d("HomeRepository", "Getting event recommendations for city: $cityId")
        Log.d("HomeRepository", "Request params: startDate=${request.startDate}, endDate=${request.endDate}, limit=${request.limit}")
        
        return try {
            val response = apiService.getEventRecommendations(cityId, request)
            
            if (response.isSuccessful) {
                val events = response.body() ?: emptyList()
                Log.d("HomeRepository", "Successfully fetched ${events.size} events")
                events
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("HomeRepository", "Failed to get event recommendations: ${response.code()}, $errorBody")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("HomeRepository", "Error getting event recommendations", e)
            emptyList()
        }
    }
}
