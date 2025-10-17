package com.localify.android.data.repository

import com.localify.android.data.models.Event
import com.localify.android.data.network.NetworkModule

class EventRepository {
    
    private val apiService = NetworkModule.apiService
    
    suspend fun getFeaturedEvents(): List<Event> {
        // This is now deprecated - use HomeRepository for recommendations
        throw Exception("Use HomeRepository for event recommendations")
    }
    
    suspend fun getEventsByIds(eventIds: List<String>): List<Event> {
        // This is now deprecated - use HomeRepository for event recommendations
        throw Exception("Use HomeRepository for event recommendations")
    }
}
