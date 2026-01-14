package com.localify.android.ui.auth

import android.content.Context
import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import com.localify.android.data.network.ApiService
import com.localify.android.data.network.AuthResponse
import com.localify.android.data.network.EmailVerificationV1Response
import com.localify.android.data.network.NetworkModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import retrofit2.Response
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class LoginViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var apiService: ApiService
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        val context = ApplicationProvider.getApplicationContext<Context>()
        NetworkModule.init(context)
        NetworkModule.clearAuth()

        apiService = mock()

        runBlocking {
            whenever(apiService.createGuestUser()).thenReturn(
                Response.success(AuthResponse(token = "guest", refreshToken = "guest", expiresIn = 3600))
            )
            whenever(apiService.sendEmailVerificationToken(any())).thenReturn(
                Response.success(EmailVerificationV1Response(nonce = "nonce123"))
            )
        }

        viewModel = LoginViewModel(apiService = apiService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `sendEmailLoginCode with blank email sets error and does not load`() {
        viewModel.updateEmail("")
        viewModel.sendEmailLoginCode()

        val state = viewModel.uiState.value
        assertEquals("Please enter your email", state.error)
        assertFalse(state.isLoading)
    }

    @Test
    fun `sendEmailLoginCode success stores nonce and clears loading`() = runTest {
        viewModel.updateEmail("test@example.com")
        viewModel.sendEmailLoginCode()

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("nonce123", state.emailNonce)
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `handleSpotifyRedirect with error parameter sets error`() {
        val uri = Uri.parse("localify://auth/spotify?error=access_denied")
        viewModel.handleSpotifyRedirect(uri) { }

        assertEquals("access_denied", viewModel.uiState.value.error)
    }

    @Test
    fun `handleSpotifyRedirect state mismatch sets error`() = runTest {
        val context = mock<Context>()
        doNothing().whenever(context).startActivity(any())

        whenever(apiService.spotifyLink(any(), any())).thenReturn(Response.success("https://example.com"))

        viewModel.startSpotifyLogin(context)
        advanceUntilIdle()

        val expected = viewModel.uiState.value.spotifyExpectedState
        assertTrue(!expected.isNullOrBlank())

        val mismatched = Uri.parse("localify://auth/spotify?code=abc&state=not-$expected")
        viewModel.handleSpotifyRedirect(mismatched) { }

        assertEquals("Spotify state mismatch", viewModel.uiState.value.error)
    }
}
