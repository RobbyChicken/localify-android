package com.localify.android.data.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.UUID

interface ApiService {
    @GET("/v1/@me")
    suspend fun getMe(): Response<UserDetailsV1Response>

    @PATCH("/v1/@me")
    suspend fun patchMe(
        @Body body: PatchUserDetailsRequest
    ): Response<UserDetailsV1Response>

    @DELETE("/v1/@me")
    suspend fun deleteMe(): Response<Unit>

    @GET("/v1/@me/cities/nearest")
    suspend fun getUserNearestCities(): Response<UserNearestCitiesV1Response>

    @GET("/v1/@me/cities/{cityId}/artist-recommendations")
    suspend fun getArtistRecommendations(
        @Path("cityId") cityId: String,
        @Query("limit") limit: Int = 20
    ): Response<List<ArtistRecResponse>>
    
    @POST("/v1/@me/cities/{cityId}/event-recommendations")
    suspend fun getEventRecommendations(
        @Path("cityId") cityId: String,
        @retrofit2.http.Body body: EventRecommendationsRequest
    ): Response<List<EventRecResponse>>
    
    @GET("/v1/@me/cities")
    suspend fun getUserCities(
        
    ): Response<UserCityResponse>

    @PUT("/v1/@me/cities/{cityId}")
    suspend fun putUserCity(
        @Path("cityId") cityId: String,
        @Body body: AddCityRequest
    ): Response<UserCityV1Response>

    @PATCH("/v1/@me/cities/{cityId}")
    suspend fun patchUserCity(
        @Path("cityId") cityId: String,
        @Body body: PatchUserCityRequest
    ): Response<UserCityV1Response>

    @DELETE("/v1/@me/cities/{cityId}")
    suspend fun deleteUserCity(
        @Path("cityId") cityId: String
    ): Response<String>

    @POST("/v1/@me/cities/{cityId}/playlist-async")
    suspend fun createCityPlaylistAsync(
        @Path("cityId") cityId: String
    ): Response<PlaylistV1Response>
    
    @PUT("/v1/@me/cities/{cityId}/onboarding")
    suspend fun addUserCity(
        @Path("cityId") cityId: String,
        @retrofit2.http.Body body: AddCityRequest
    ): Response<UserCityItem>
    
    @GET("/v1/cities/search")
    suspend fun searchCities(
        @Query("q") query: String,
        @Query("limit") limit: Int = 10
    ): Response<List<CityResponse>>
    
    @GET("/v1/artists/search")
    suspend fun searchArtists(
        @Query("q") query: String,
        @Query("limit") limit: Int = 5
    ): Response<List<ArtistSearchResponse>>
    
    @PUT("/v1/@me/seeds")
    suspend fun addUserSeeds(
        @retrofit2.http.Body body: SeedsRequest
    ): Response<List<SeedResponse>>

    @GET("/v1/@me/seeds")
    suspend fun getUserSeeds(): Response<List<SeedV1Response>>

    @GET("/v1/@me/seeds/all")
    suspend fun getUserSeedsAll(): Response<List<SeedV1Response>>

    @PUT("/v1/@me/seeds/{seedId}")
    suspend fun addSeed(
        @Path("seedId") seedId: String
    ): Response<Unit>

    @DELETE("/v1/@me/seeds/{seedId}")
    suspend fun deleteSeed(
        @Path("seedId") seedId: String
    ): Response<Unit>

    @POST("/v1/@me/seeds/{seedId}")
    suspend fun unBlacklistSeed(
        @Path("seedId") seedId: String
    ): Response<Unit>

    @GET("/v1/@me/search-history")
    suspend fun getSearchHistory(): Response<List<String>>

    @DELETE("/v1/@me/search-history")
    suspend fun deleteSearchHistory(): Response<Unit>

    @PUT("/v1/@me/{type}/{id}/favorite")
    suspend fun addFavorite(
        @Path("type") type: String,
        @Path("id") id: String
    ): Response<Unit>

    @DELETE("/v1/@me/{type}/{id}/favorite")
    suspend fun removeFavorite(
        @Path("type") type: String,
        @Path("id") id: String
    ): Response<Unit>
    
    @GET("/v1/@me/artists/favorites")
    suspend fun getFavoriteArtists(
        
    ): Response<PageableArtistResponse>

    @GET("/v1/@me/artists/favorites")
    suspend fun getFavoriteArtistsV1(
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): Response<PageableResponseV1<ArtistV1Response>>

    @GET("/v1/@me/venues/favorites")
    suspend fun getFavoriteVenuesV1(
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): Response<PageableResponseV1<VenueV1Response>>

    @GET("/v1/@me/events/favorites")
    suspend fun getFavoriteEventsV1(
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): Response<PageableResponseV1<EventV1Response>>
    
    @GET("/v1/@me/events/favorites/upcoming")
    suspend fun getFavoriteUpcomingEvents(
        
    ): Response<PageableEventResponse>
    
    @GET("/v1/@me/events/favorites/past")
    suspend fun getFavoritePastEvents(
        
    ): Response<PageableEventResponse>

    @GET("/v1/@me/events/favorites/upcoming")
    suspend fun getFavoriteUpcomingEventsV1(
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): Response<PageableResponseV1<EventV1Response>>

    @GET("/v1/@me/events/favorites/past")
    suspend fun getFavoritePastEventsV1(
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): Response<PageableResponseV1<EventV1Response>>

    @GET("/v1/artists/{artistId}")
    suspend fun getArtistV1(
        @Path("artistId") artistId: String
    ): Response<ComplexArtistV1Response>

    @GET("/v1/artists/{artistId}/events")
    suspend fun getArtistEventsV1(
        @Path("artistId") artistId: String
    ): Response<ArtistEventsV1Response>

    @GET("/v1/artists/{artistId}/cities")
    suspend fun getArtistCitiesV1(
        @Path("artistId") artistId: String
    ): Response<List<CityV1Response>>

    @POST("/v1/artists/{artistId}/cities")
    suspend fun addArtistCityV1(
        @Path("artistId") artistId: String,
        @Body body: AddArtistCityRequest
    ): Response<CityV1Response>

    @GET("/v1/events/{eventId}")
    suspend fun getEventV1(
        @Path("eventId") eventId: String
    ): Response<EventV1Response>

    @GET("/v1/search")
    suspend fun searchV1(
        @Query("q") query: String,
        @Query("autoSearchSpotify") autoSearchSpotify: Boolean? = null
    ): Response<SearchV1Response>

    @GET("/v1/genres/curated")
    suspend fun getCuratedGenresV1(): Response<List<GenreV1Response>>

    @GET("/v1/genres/top")
    suspend fun getTopGenresV1(): Response<List<TopGenresV1Response>>

    @GET("/v1/artists/popular")
    suspend fun getPopularArtistsForGenresV1(
        @Query("genres") genres: String
    ): Response<List<ArtistV1Response>>

    @GET("/v1/cities/{cityId}")
    suspend fun getCityDetailsV1(
        @Path("cityId") cityId: String
    ): Response<CityV1Response>

    @GET("/v1/cities/{cityId}/artists")
    suspend fun getCityArtistsV1(
        @Path("cityId") cityId: String,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): Response<PageableCityArtistV1Response>

    @GET("/v1/cities/{cityId}/events")
    suspend fun getCityEventsV1(
        @Path("cityId") cityId: String,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): Response<PageableResponseV1<EventV1Response>>

    @GET("/v1/cities/{cityId}/venues")
    suspend fun getCityVenuesV1(
        @Path("cityId") cityId: String,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): Response<PageableResponseV1<VenueV1Response>>

    @GET("/v1/venues/{venueId}")
    suspend fun getVenueV1(
        @Path("venueId") venueId: String
    ): Response<VenueV1Response>

    @GET("/v1/venues/{venueId}/upcoming-events")
    suspend fun getVenueUpcomingEventsV1(
        @Path("venueId") venueId: String
    ): Response<List<EventV1Response>>

    @PUT("/v1/@me/feedback")
    suspend fun submitFeedback(
        @Body body: FeedbackRequest
    ): Response<Unit>
    
    @POST("/v1/auth/guest")
    suspend fun createGuestUser(): Response<AuthResponse>

    @POST("/v1/auth/token")
    suspend fun exchangeToken(
        @Body body: TokenExchangeRequest
    ): Response<AuthResponse>

    @POST("/v1/auth/apple/token")
    suspend fun exchangeAppleToken(
        @Body body: AppleTokenExchangeRequest
    ): Response<AuthResponse>

    @POST("/v1/auth/refresh")
    suspend fun refreshAuth(
        @retrofit2.http.Body body: RefreshAuthRequest
    ): Response<AuthResponse>

    @POST("/v1/auth/email/send-token")
    suspend fun sendEmailVerificationToken(
        @Body body: EmailVerificationRequest
    ): Response<EmailVerificationV1Response>

    @POST("/v1/auth/email/login")
    suspend fun emailLogin(
        @Body body: EmailLoginRequest
    ): Response<AuthResponse>

    @GET("/v1/@me/spotify/link")
    suspend fun spotifyLink(
        @Query("redirect") redirect: String,
        @Query("state") state: String
    ): Response<String>

    @POST("/v1/@me/email/link")
    suspend fun emailLink(
        @Body body: EmailLoginRequest
    ): Response<MergeV1Response>

    @POST("/v1/@me/email")
    suspend fun updateEmail(
        @Body body: EmailLoginRequest
    ): Response<MergeV1Response>

    @POST("/v1/@me/merge")
    suspend fun mergeAccount(
        @Body body: MergeAccountRequest
    ): Response<AuthResponse>

    @GET("/v1/@me/mergeable-account")
    suspend fun getMergeableAccount(
        @Query("mergeToken") mergeToken: String
    ): Response<MergeableAccountV1Response>
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

data class RefreshAuthRequest(
    val token: String
)

data class EmailVerificationRequest(
    val email: String
)

data class EmailLoginRequest(
    val nonce: String,
    val code: String
)

data class PatchUserDetailsRequest(
    val name: String? = null,
    val emailOptIn: Boolean? = null,
    val playlistsIncludeLocalOnly: Boolean? = null,
    val generateSpotifyPlaylists: Boolean? = null
)

data class PatchUserCityRequest(
    val selected: Boolean,
    val radius: Double
)

data class AddArtistCityRequest(
    val cityId: String
)

data class MergeAccountRequest(
    val mergeRequestId: String,
    val token: String,
    val changeEmail: Boolean? = null
)

data class FeedbackRequest(
    val entry: String,
    val email: String
)

data class TokenExchangeRequest(
    val token: String,
    val secret: String
)

data class AppleTokenExchangeRequest(
    val token: String,
    val name: String? = null
)

data class UserDetailsV1Response(
    val id: String,
    val name: String,
    val email: String?,
    val appleId: String?,
    val spotifyId: String?,
    val accountCreationDate: Long,
    val profileImage: String?,
    val spotifyProfileImage: String?,
    val playlistLocalSongsPerSeed: Int?,
    val anonymousUser: Boolean,
    val emailConnected: Boolean,
    val appleConnected: Boolean,
    val spotifyConnected: Boolean,
    val emailVerified: Boolean,
    val emailOptIn: Boolean,
    val isAdmin: Boolean,
    val isTeamMember: Boolean,
    val playlistUseSeedSongs: Boolean,
    val playlistGeneration: Boolean
)

data class UserNearestCitiesV1Response(
    val cities: List<CityV1Response>,
    val fallbackResponse: Boolean
)

data class UserCityV1Response(
    val id: String,
    val name: String,
    val zoneCode: String?,
    val timeFrame: String?,
    val startDate: Long?,
    val endDate: Long?,
    val radius: Double,
    val selectedAt: Long?,
    val sortBy: String?,
    val spotifyPlaylist: String?,
    val countryCode: String?
)

data class PlaylistV1Response(
    val id: String,
    val name: String,
    val description: String,
    val spotifyId: String
)

data class SeedV1Response(
    val id: String,
    val name: String,
    val image: String?,
    val appleId: String?,
    val spotifyId: String?,
    val isBlacklisted: Boolean
)

data class GenreV1Response(
    val id: String,
    val name: String
)

data class TopGenresV1Response(
    val id: String,
    val name: String,
    val topArtists: List<ArtistV1Response>
)

data class ArtistV1Response(
    val id: String,
    val name: String,
    val image: String?,
    val appleId: String?,
    val spotifyId: String?,
    val city: ArtistCityV1Response? = null
)

data class BasicArtistV1Response(
    val id: String,
    val name: String,
    val image: String?,
    val appleId: String?,
    val spotifyId: String?
)

data class ArtistCityV1Response(
    val id: String,
    val name: String,
    val zoneCode: String?,
    val countryCode: String?,
    val friendlyName: String?,
    val latitude: Double?,
    val longitude: Double?,
    val applePlaylistId: String?,
    val spotifyPlaylistId: String?
)

data class SimilarArtistV1Response(
    val id: String,
    val name: String,
    val score: Double,
    val image: String?
)

data class ComplexArtistV1Response(
    val id: String,
    val name: String,
    val image: String?,
    val appleId: String?,
    val spotifyId: String?,
    val genres: List<GenreV1Response>,
    val similarArtists: List<BasicArtistV1Response>,
    val isFavorite: Boolean,
    val topSongPreview: String?,
    val isSeed: Boolean?
)

data class ArtistEventsV1Response(
    val city: CityV1Response,
    val otherEvents: List<EventV1Response>,
    val nearbyEvents: List<EventV1Response>
)

data class EventV1Response(
    val id: String,
    val name: String,
    val startTime: Long,
    val endTime: Long?,
    val lowPrice: Double?,
    val highPrice: Double?,
    val ticketUrl: String?,
    val isFavorite: Boolean,
    val applePlaylistId: String?,
    val spotifyPlaylistId: String?,
    val venue: VenueV1Response,
    val topArtists: List<ArtistV1Response>
)

data class VenueV1Response(
    val id: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val city: CityV1Response,
    val type: String?
)

data class CityV1Response(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val zoneCode: String?,
    val countryCode: String?,
    val numberOfArtists: Int?,
    val numberTotalVenues: Int?,
    val numberUpcomingEvents: Int?,
    val spotifyPlaylistId: String?,
    val applePlaylistId: String?,
    val isUserLocalCity: Boolean?
)

data class SearchV1Response(
    val artists: List<ArtistV1Response>,
    val events: List<EventV1Response>,
    val venues: List<VenueV1Response>,
    val cities: List<CityV1Response>
)

data class PageableResponseV1<T>(
    val empty: Boolean? = null,
    val first: Boolean? = null,
    val last: Boolean? = null,
    val number: Int? = null,
    val numberOfElements: Int? = null,
    val size: Int? = null,
    val totalElements: Int? = null,
    val totalPages: Int? = null,
    val content: List<T>? = null
)

data class PageableCityArtistV1Response(
    val empty: Boolean? = null,
    val first: Boolean? = null,
    val last: Boolean? = null,
    val number: Int? = null,
    val numberOfElements: Int? = null,
    val size: Int? = null,
    val totalElements: Int? = null,
    val totalPages: Int? = null,
    val content: List<CityArtistV1Response>? = null
)

data class CityArtistV1Response(
    val id: String,
    val name: String,
    val image: String?,
    val relation: String?,
    val isFavorite: Boolean
)

data class EmailVerificationV1Response(
    val nonce: String
)

data class MergeableAccountPreviewV1Response(
    val name: String,
    val email: String,
    val spotifyName: String?
)

data class MergeableAccountV1Response(
    val id: String,
    val fromUserId: String,
    val toUserId: String,
    val authToken: String,
    val expiresAt: Long,
    val fromUserName: String,
    val fromUserEmail: String,
    val fromUserCreatedAt: Long,
    val fromUserSpotifyUsername: String?,
    val mergedAccountPreview: MergeableAccountPreviewV1Response
)

data class MergeV1Response(
    val authCredentials: AuthResponse?,
    val mergeableAccount: MergeableAccountV1Response?,
    val spotifyRedirect: String?
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
