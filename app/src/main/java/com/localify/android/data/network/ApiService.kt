package com.localify.android.data.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.UUID

interface ApiService {
    @GET("/v1/@me/cities/{cityId}/artist-recommendations")
    suspend fun getArtistRecommendations(
        @Header("Authorization") authHeader: String,
        @Path("cityId") cityId: String,
        @Query("limit") limit: Int = 20
    ): Response<List<ArtistRecResponse>>
    
    @POST("/v1/@me/cities/{cityId}/event-recommendations")
    suspend fun getEventRecommendations(
        @Header("Authorization") authHeader: String,
        @Path("cityId") cityId: String,
        @retrofit2.http.Body body: EventRecommendationsRequest
    ): Response<List<EventRecResponse>>
    
    @GET("/v1/@me/cities")
    suspend fun getUserCities(
        @Header("Authorization") authHeader: String
    ): Response<UserCityResponse>
    
    @PUT("/v1/@me/cities/{cityId}/onboarding")
    suspend fun addUserCity(
        @Header("Authorization") authHeader: String,
        @Path("cityId") cityId: String,
        @retrofit2.http.Body body: AddCityRequest
    ): Response<UserCityItem>
    
    @GET("/v1/cities/search")
    suspend fun searchCities(
        @Header("Authorization") authHeader: String,
        @Query("q") query: String,
        @Query("limit") limit: Int = 10
    ): Response<List<CityResponse>>
    
    @GET("/v1/artists/search")
    suspend fun searchArtists(
        @Header("Authorization") authHeader: String,
        @Query("q") query: String,
        @Query("limit") limit: Int = 5
    ): Response<List<ArtistSearchResponse>>
    
    @PUT("/v1/@me/seeds")
    suspend fun addUserSeeds(
        @Header("Authorization") authHeader: String,
        @retrofit2.http.Body body: SeedsRequest
    ): Response<List<SeedResponse>>
    
    @GET("/v1/@me/artists/favorites")
    suspend fun getFavoriteArtists(
        @Header("Authorization") authHeader: String
    ): Response<PageableArtistResponse>
    
    @GET("/v1/@me/events/favorites/upcoming")
    suspend fun getFavoriteUpcomingEvents(
        @Header("Authorization") authHeader: String
    ): Response<PageableEventResponse>
    
    @GET("/v1/@me/events/favorites/past")
    suspend fun getFavoritePastEvents(
        @Header("Authorization") authHeader: String
    ): Response<PageableEventResponse>
    
    @POST("/v1/auth/guest")
    suspend fun createGuestUser(): Response<AuthResponse>
}


data class ArtistRecResponse(
    val id: String,
    val name: String,
    val image: String?,
    val genres: List<GenreResponse>?,
    val spotifyUrl: String?,
    val similar: List<ArtistResponse>?
)

data class EventRecResponse(
    val id: String,
    val name: String,
    val percentMatch: Double,
    val startTime: Long,
    val venue: VenueResponse,
    val artists: List<ArtistResponse>,
    val genres: List<GenreResponse>,
    val hasLocalArtistPerforming: Boolean,
    val ticketUrl: String?,
    val similar: List<ArtistResponse>,
    val createdAt: Long,
    val isFavorite: Boolean,
    val imageUrl: String?
)

data class VenueResponse(
    val id: String,
    val name: String,
    val address: String?,
    val latitude: Double?,
    val longitude: Double?,
    val city: CityResponse?,
    val type: String?
)

data class ArtistResponse(
    val id: String,
    val name: String,
    val image: String?,
    val appleId: String?,
    val spotifyId: String?,
    val isLocal: Boolean?,
    val spotifyImage: String?,
    val spotifyPopularity: Int?,
    val spotifyTopTrackPreview: String?
)

data class UserCityResponse(
    val current: UserCity,
    val others: List<UserCity>?
)

data class UserCity(
    val id: String,
    val name: String,
    val radius: Double,
    val selected: Boolean
)

data class PageableArtistResponse(
    val content: List<ArtistResponse>
)

data class PageableEventResponse(
    val content: List<EventResponse>
)

data class EventResponse(
    val id: UUID,
    val name: String,
    val date: String,
    val venue: VenueResponse?
)

data class AuthResponse(
    val token: String,
    val refreshToken: String,
    val expiresIn: Int
)

data class AddCityRequest(
    val radius: Double
)

data class CityResponse(
    val id: String,
    val name: String,
    val state: String?,
    val country: String,
    val latitude: Double,
    val longitude: Double
)

data class UserCityItem(
    val id: String,
    val name: String,
    val state: String?,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val radius: Double,
    val selected: Boolean
)

data class SeedsRequest(
    val seeds: List<String>
)

data class SeedResponse(
    val id: String,
    val name: String,
    val image: String?,
    val appleId: String?,
    val spotifyId: String?,
    val isBlacklisted: Boolean
)

data class ArtistSearchResponse(
    val id: String,
    val name: String,
    val image: String?,
    val appleId: String?,
    val spotifyId: String?
)

data class GenreResponse(
    val id: String,
    val name: String
)

data class ArtistRecommendationsRequest(
    val limit: Int = 10
)

data class EventRecommendationsRequest(
    val startDate: String? = null,
    val endDate: String? = null,
    val limit: Int = 10
)
