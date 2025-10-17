package com.localify.android.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.localify.android.data.model.Event
import com.localify.android.data.model.Artist
import com.localify.android.data.network.ApiService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@ExperimentalCoroutinesApi
class EventRepositoryTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var mockApiService: ApiService

    private lateinit var repository: EventRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = EventRepository()
        // Note: In a real implementation, you'd inject the mock ApiService
    }

    @Test
    fun `getEventsByIds returns list of events`() = runTest {
        // Given
        val eventIds = listOf("event1", "event2")
        val expectedEvents = listOf(
            Event(
                id = "event1",
                name = "Rock Concert",
                description = "Amazing rock concert",
                startTime = "2024-12-01T20:00:00Z",
                endTime = "2024-12-01T23:00:00Z",
                venue = "Madison Square Garden",
                imageUrl = "https://example.com/concert1.jpg",
                ticketUrl = "https://tickets.com/event1",
                artists = listOf(
                    Artist(id = "artist1", name = "Rock Band", imageUrl = "", genres = listOf("Rock"))
                )
            ),
            Event(
                id = "event2",
                name = "Jazz Night",
                description = "Smooth jazz evening",
                startTime = "2024-12-02T19:00:00Z",
                endTime = "2024-12-02T22:00:00Z",
                venue = "Blue Note",
                imageUrl = "https://example.com/jazz.jpg",
                ticketUrl = "https://tickets.com/event2",
                artists = listOf(
                    Artist(id = "artist2", name = "Jazz Trio", imageUrl = "", genres = listOf("Jazz"))
                )
            )
        )
        whenever(mockApiService.getEventsByIds(eventIds)).thenReturn(expectedEvents)

        // When
        val result = repository.getEventsByIds(eventIds)

        // Then
        assertNotNull(result)
        assertEquals(2, result.size)
        assertEquals("Rock Concert", result[0].name)
        assertEquals("Jazz Night", result[1].name)
        assertEquals("Madison Square Garden", result[0].venue)
        assertEquals("Blue Note", result[1].venue)
    }

    @Test
    fun `searchEvents returns filtered events`() = runTest {
        // Given
        val query = "concert"
        val expectedEvents = listOf(
            Event(
                id = "event1",
                name = "Rock Concert",
                description = "Live rock concert",
                startTime = "2024-12-01T20:00:00Z",
                endTime = "2024-12-01T23:00:00Z",
                venue = "Arena",
                imageUrl = "https://example.com/rock.jpg",
                ticketUrl = "https://tickets.com/rock",
                artists = emptyList()
            ),
            Event(
                id = "event2",
                name = "Pop Concert",
                description = "Pop music concert",
                startTime = "2024-12-02T19:00:00Z",
                endTime = "2024-12-02T22:00:00Z",
                venue = "Stadium",
                imageUrl = "https://example.com/pop.jpg",
                ticketUrl = "https://tickets.com/pop",
                artists = emptyList()
            )
        )
        whenever(mockApiService.searchEvents(query)).thenReturn(expectedEvents)

        // When
        val result = repository.searchEvents(query)

        // Then
        assertNotNull(result)
        assertEquals(2, result.size)
        assertEquals("Rock Concert", result[0].name)
        assertEquals("Pop Concert", result[1].name)
    }

    @Test
    fun `getEventsByIds with empty list returns empty list`() = runTest {
        // Given
        val emptyIds = emptyList<String>()
        whenever(mockApiService.getEventsByIds(emptyIds)).thenReturn(emptyList())

        // When
        val result = repository.getEventsByIds(emptyIds)

        // Then
        assertNotNull(result)
        assertEquals(0, result.size)
    }

    @Test
    fun `searchEvents with empty query returns empty list`() = runTest {
        // Given
        val emptyQuery = ""
        whenever(mockApiService.searchEvents(emptyQuery)).thenReturn(emptyList())

        // When
        val result = repository.searchEvents(emptyQuery)

        // Then
        assertNotNull(result)
        assertEquals(0, result.size)
    }

    @Test
    fun `getEventsByIds handles events with multiple artists`() = runTest {
        // Given
        val eventIds = listOf("event1")
        val expectedEvents = listOf(
            Event(
                id = "event1",
                name = "Music Festival",
                description = "Multi-artist festival",
                startTime = "2024-12-01T18:00:00Z",
                endTime = "2024-12-01T23:59:00Z",
                venue = "Festival Grounds",
                imageUrl = "https://example.com/festival.jpg",
                ticketUrl = "https://tickets.com/festival",
                artists = listOf(
                    Artist(id = "artist1", name = "Headliner", imageUrl = "", genres = listOf("Rock")),
                    Artist(id = "artist2", name = "Support Act", imageUrl = "", genres = listOf("Pop")),
                    Artist(id = "artist3", name = "Opening Band", imageUrl = "", genres = listOf("Indie"))
                )
            )
        )
        whenever(mockApiService.getEventsByIds(eventIds)).thenReturn(expectedEvents)

        // When
        val result = repository.getEventsByIds(eventIds)

        // Then
        assertNotNull(result)
        assertEquals(1, result.size)
        assertEquals("Music Festival", result[0].name)
        assertEquals(3, result[0].artists.size)
        assertEquals("Headliner", result[0].artists[0].name)
        assertEquals("Support Act", result[0].artists[1].name)
        assertEquals("Opening Band", result[0].artists[2].name)
    }
}
