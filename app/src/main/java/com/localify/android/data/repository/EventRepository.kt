package com.localify.android.data.repository

import com.localify.android.data.models.Event

@Suppress("UNUSED_PARAMETER")
class EventRepository {

    suspend fun getFeaturedEvents(): List<Event> {
        // This is now deprecated - use HomeRepository for recommendations
        throw Exception("Use HomeRepository for event recommendations")
    }
    
    suspend fun getEventsByIds(_eventIds: List<String>): List<Event> {
        // This is now deprecated - use HomeRepository for event recommendations
        throw Exception("Use HomeRepository for event recommendations")
    }
}
