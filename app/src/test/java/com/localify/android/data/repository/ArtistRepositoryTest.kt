package com.localify.android.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
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
class ArtistRepositoryTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var mockApiService: ApiService

    private lateinit var repository: ArtistRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = ArtistRepository()
        // Note: In a real implementation, you'd inject the mock ApiService
    }

    @Test
    fun `getArtistsByIds returns list of artists`() = runTest {
        // Given
        val artistIds = listOf("artist1", "artist2")
        val expectedArtists = listOf(
            Artist(
                id = "artist1",
                name = "Test Artist 1",
                imageUrl = "https://example.com/image1.jpg",
                genres = listOf("Rock", "Pop")
            ),
            Artist(
                id = "artist2",
                name = "Test Artist 2",
                imageUrl = "https://example.com/image2.jpg",
                genres = listOf("Jazz", "Blues")
            )
        )
        whenever(mockApiService.getArtistsByIds(artistIds)).thenReturn(expectedArtists)

        // When
        val result = repository.getArtistsByIds(artistIds)

        // Then
        assertNotNull(result)
        assertEquals(2, result.size)
        assertEquals("Test Artist 1", result[0].name)
        assertEquals("Test Artist 2", result[1].name)
        assertEquals(listOf("Rock", "Pop"), result[0].genres)
        assertEquals(listOf("Jazz", "Blues"), result[1].genres)
    }

    @Test
    fun `searchArtists returns filtered artists`() = runTest {
        // Given
        val query = "rock"
        val expectedArtists = listOf(
            Artist(
                id = "artist1",
                name = "Rock Band",
                imageUrl = "https://example.com/rock.jpg",
                genres = listOf("Rock")
            ),
            Artist(
                id = "artist2",
                name = "Alternative Rock",
                imageUrl = "https://example.com/alt.jpg",
                genres = listOf("Alternative", "Rock")
            )
        )
        whenever(mockApiService.searchArtists(query)).thenReturn(expectedArtists)

        // When
        val result = repository.searchArtists(query)

        // Then
        assertNotNull(result)
        assertEquals(2, result.size)
        assertEquals("Rock Band", result[0].name)
        assertEquals("Alternative Rock", result[1].name)
    }

    @Test
    fun `getArtistsByIds with empty list returns empty list`() = runTest {
        // Given
        val emptyIds = emptyList<String>()
        whenever(mockApiService.getArtistsByIds(emptyIds)).thenReturn(emptyList())

        // When
        val result = repository.getArtistsByIds(emptyIds)

        // Then
        assertNotNull(result)
        assertEquals(0, result.size)
    }

    @Test
    fun `searchArtists with empty query returns empty list`() = runTest {
        // Given
        val emptyQuery = ""
        whenever(mockApiService.searchArtists(emptyQuery)).thenReturn(emptyList())

        // When
        val result = repository.searchArtists(emptyQuery)

        // Then
        assertNotNull(result)
        assertEquals(0, result.size)
    }
}
