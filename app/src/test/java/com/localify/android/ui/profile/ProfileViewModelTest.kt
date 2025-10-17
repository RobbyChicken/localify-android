package com.localify.android.ui.profile

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class ProfileViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    @Mock
    private lateinit var mockContext: Context

    private lateinit var viewModel: ProfileViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = ProfileViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has correct default values`() {
        val initialState = viewModel.uiState.value
        
        assertEquals("Localify Guest", initialState.userName)
        assertEquals("", initialState.email)
        assertEquals("", initialState.profileImageUrl)
        assertFalse(initialState.isLoggedIn)
        assertFalse(initialState.isEmailConnected)
        assertFalse(initialState.isSpotifyConnected)
        assertFalse(initialState.emailOptIn)
        assertFalse(initialState.generateSpotifyPlaylists)
        assertFalse(initialState.playlistsIncludeLocalOnly)
    }

    @Test
    fun `toggleEmailOptIn updates emailOptIn state`() {
        // When
        viewModel.toggleEmailOptIn(true)

        // Then
        assertTrue(viewModel.uiState.value.emailOptIn)

        // When
        viewModel.toggleEmailOptIn(false)

        // Then
        assertFalse(viewModel.uiState.value.emailOptIn)
    }

    @Test
    fun `toggleSpotifyPlaylists updates generateSpotifyPlaylists state`() {
        // When
        viewModel.toggleSpotifyPlaylists(true)

        // Then
        assertTrue(viewModel.uiState.value.generateSpotifyPlaylists)

        // When
        viewModel.toggleSpotifyPlaylists(false)

        // Then
        assertFalse(viewModel.uiState.value.generateSpotifyPlaylists)
    }

    @Test
    fun `togglePlaylistsIncludeLocal updates playlistsIncludeLocalOnly state`() {
        // When
        viewModel.togglePlaylistsIncludeLocal(true)

        // Then
        assertTrue(viewModel.uiState.value.playlistsIncludeLocalOnly)

        // When
        viewModel.togglePlaylistsIncludeLocal(false)

        // Then
        assertFalse(viewModel.uiState.value.playlistsIncludeLocalOnly)
    }

    @Test
    fun `connectSpotify updates isSpotifyConnected state`() {
        // When
        viewModel.connectSpotify(mockContext)

        // Then
        assertTrue(viewModel.uiState.value.isSpotifyConnected)
    }
}
