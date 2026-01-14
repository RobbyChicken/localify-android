package com.localify.android.ui.profile

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class ProfileViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var context: Context

    private lateinit var viewModel: ProfileViewModel

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
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
        viewModel.setEmailOptIn(true)

        // Then
        assertTrue(viewModel.uiState.value.emailOptIn)

        // When
        viewModel.setEmailOptIn(false)

        // Then
        assertFalse(viewModel.uiState.value.emailOptIn)
    }

    @Test
    fun `toggleSpotifyPlaylists updates generateSpotifyPlaylists state`() {
        // When
        viewModel.setGenerateSpotifyPlaylists(true)

        // Then
        assertTrue(viewModel.uiState.value.generateSpotifyPlaylists)

        // When
        viewModel.setGenerateSpotifyPlaylists(false)

        // Then
        assertFalse(viewModel.uiState.value.generateSpotifyPlaylists)
    }

    @Test
    fun `togglePlaylistsIncludeLocal updates playlistsIncludeLocalOnly state`() {
        // When
        viewModel.setPlaylistsIncludeLocalOnly(true)

        // Then
        assertTrue(viewModel.uiState.value.playlistsIncludeLocalOnly)

        // When
        viewModel.setPlaylistsIncludeLocalOnly(false)

        // Then
        assertFalse(viewModel.uiState.value.playlistsIncludeLocalOnly)
    }

    @Test
    fun `connectSpotify updates isSpotifyConnected state`() {
        // When
        viewModel.connectSpotify(context)

        // Then
        assertTrue(viewModel.uiState.value.isSpotifyConnected)
    }
}
