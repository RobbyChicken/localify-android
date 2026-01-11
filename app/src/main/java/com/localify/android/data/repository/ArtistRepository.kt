package com.localify.android.data.repository

import com.localify.android.data.models.Artist
import com.localify.android.data.models.Event

@Suppress("UNUSED_PARAMETER")
class ArtistRepository {
    suspend fun getArtistsByIds(_artistIds: List<String>): List<Artist> {
        // This is now deprecated - use HomeRepository for recommendations
        throw Exception("Use HomeRepository for artist recommendations")
    }
    
    suspend fun getArtist(_artistId: String): Result<Artist> {
        throw Exception("Use HomeRepository for artist data")
    }
    
    suspend fun getArtistEvents(_artistId: String): Result<List<com.localify.android.data.models.Event>> {
        throw Exception("Use HomeRepository for event data")
    }
    
    suspend fun favoriteArtist(_artistId: String): Result<Unit> {
        throw Exception("Favorites not implemented yet")
    }
    
    suspend fun unfavoriteArtist(_artistId: String): Result<Unit> {
        throw Exception("Favorites not implemented yet")
    }
    
    suspend fun isArtistFavorited(_artistId: String): Result<Boolean> {
        return try {
            // TODO: Replace with real API call when backend is ready
            Result.failure(Exception("No internet connection. Please check your network and try again."))
        } catch (e: Exception) {
            Result.failure(Exception("No internet connection. Please check your network and try again."))
        }
    }
}
