package com.localify.android.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.MockitoAnnotations
import kotlin.test.assertFailsWith

@ExperimentalCoroutinesApi
class ArtistRepositoryTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: ArtistRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = ArtistRepository()
    }

    @Test
    fun `getArtistsByIds throws because repository is deprecated`() = runTest {
        assertFailsWith<Exception> {
            repository.getArtistsByIds(listOf("artist1", "artist2"))
        }
    }

    @Test
    fun `getArtist throws because repository is deprecated`() = runTest {
        assertFailsWith<Exception> {
            repository.getArtist("artist1")
        }
    }

    @Test
    fun `getArtistEvents throws because repository is deprecated`() = runTest {
        assertFailsWith<Exception> {
            repository.getArtistEvents("artist1")
        }
    }
}
