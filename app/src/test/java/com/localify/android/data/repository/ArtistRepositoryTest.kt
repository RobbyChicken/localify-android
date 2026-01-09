package com.localify.android.data.repository

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertThrows
import org.junit.Test

class ArtistRepositoryTest {

    private val repository = ArtistRepository()

    @Test
    fun `getArtistsByIds throws because repository is deprecated`() {
        assertThrows(Exception::class.java) {
            runBlocking {
                repository.getArtistsByIds(listOf("artist1", "artist2"))
            }
        }
    }

    @Test
    fun `getArtist throws because repository is deprecated`() {
        assertThrows(Exception::class.java) {
            runBlocking {
                repository.getArtist("artist1")
            }
        }
    }

    @Test
    fun `getArtistEvents throws because repository is deprecated`() {
        assertThrows(Exception::class.java) {
            runBlocking {
                repository.getArtistEvents("artist1")
            }
        }
    }
}
