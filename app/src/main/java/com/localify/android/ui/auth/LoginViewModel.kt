package com.localify.android.ui.auth

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localify.android.data.network.EmailLoginRequest
import com.localify.android.data.network.EmailVerificationRequest
import com.localify.android.data.network.NetworkModule
import com.localify.android.data.network.TokenExchangeRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

data class LoginUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val email: String = "",
    val emailNonce: String? = null,
    val emailCode: String = "",
    val spotifyExpectedState: String? = null
)

class LoginViewModel : ViewModel() {

    private val apiService = NetworkModule.apiService

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val spotifyRedirectUri = "localify://auth/spotify"

    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(email = email, error = null)
    }

    fun updateEmailCode(code: String) {
        _uiState.value = _uiState.value.copy(emailCode = code, error = null)
    }

    fun sendEmailLoginCode() {
        val email = _uiState.value.email.trim()
        if (email.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Please enter your email")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val resp = apiService.sendEmailVerificationToken(EmailVerificationRequest(email = email))
                if (!resp.isSuccessful) {
                    val errorBody = resp.errorBody()?.string()
                    throw Exception("Failed to send email code (${resp.code()}) - $errorBody")
                }

                val nonce = resp.body()?.nonce
                if (nonce.isNullOrBlank()) {
                    throw Exception("Missing nonce")
                }

                _uiState.value = _uiState.value.copy(isLoading = false, emailNonce = nonce)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message ?: "Failed to send code")
            }
        }
    }

    fun verifyEmailLogin(onSuccess: () -> Unit) {
        val nonce = _uiState.value.emailNonce
        val code = _uiState.value.emailCode.trim()
        if (nonce.isNullOrBlank()) {
            _uiState.value = _uiState.value.copy(error = "Please request a code first")
            return
        }
        if (code.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Please enter the code")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val resp = apiService.emailLogin(EmailLoginRequest(nonce = nonce, code = code))
                if (!resp.isSuccessful) {
                    val errorBody = resp.errorBody()?.string()
                    throw Exception("Email login failed (${resp.code()}) - $errorBody")
                }

                val auth = resp.body() ?: throw Exception("Missing auth response")
                NetworkModule.storeAuth(auth)

                _uiState.value = LoginUiState()
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message ?: "Email login failed")
            }
        }
    }

    fun continueAsGuest(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                ensureGuestAuth()
                _uiState.value = _uiState.value.copy(isLoading = false)
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message ?: "Failed to continue")
            }
        }
    }

    fun startSpotifyLogin(context: Context) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                ensureGuestAuth()

                val state = UUID.randomUUID().toString()
                val resp = apiService.spotifyLink(redirect = spotifyRedirectUri, state = state)
                if (!resp.isSuccessful) {
                    val errorBody = resp.errorBody()?.string()
                    throw Exception("Failed to start Spotify login (${resp.code()}) - $errorBody")
                }

                val url = resp.body()?.trim().orEmpty()
                if (url.isBlank()) {
                    throw Exception("Missing Spotify login URL")
                }

                _uiState.value = _uiState.value.copy(isLoading = false, spotifyExpectedState = state)

                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message ?: "Spotify login failed")
            }
        }
    }

    fun handleSpotifyRedirect(uri: Uri, onSuccess: () -> Unit) {
        val error = uri.getQueryParameter("error")
        if (!error.isNullOrBlank()) {
            _uiState.value = _uiState.value.copy(error = error)
            return
        }

        val tokenFromCallback = uri.getQueryParameter("token")
        val secretFromCallback = uri.getQueryParameter("secret")

        val codeFromCallback = uri.getQueryParameter("code")
        val stateFromCallback = uri.getQueryParameter("state")

        val tokenToExchange = tokenFromCallback ?: codeFromCallback
        val secretToExchange = secretFromCallback ?: stateFromCallback

        if (tokenToExchange.isNullOrBlank() || secretToExchange.isNullOrBlank()) {
            _uiState.value = _uiState.value.copy(error = "Missing Spotify callback parameters")
            return
        }

        val expectedState = _uiState.value.spotifyExpectedState
        if (!expectedState.isNullOrBlank() && !stateFromCallback.isNullOrBlank() && expectedState != stateFromCallback) {
            _uiState.value = _uiState.value.copy(error = "Spotify state mismatch")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val resp = apiService.exchangeToken(TokenExchangeRequest(token = tokenToExchange, secret = secretToExchange))
                if (!resp.isSuccessful) {
                    val errorBody = resp.errorBody()?.string()
                    throw Exception("Spotify login failed (${resp.code()}) - $errorBody")
                }

                val auth = resp.body() ?: throw Exception("Missing auth response")
                NetworkModule.storeAuth(auth)

                _uiState.value = LoginUiState()
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message ?: "Spotify login failed")
            }
        }
    }

    private suspend fun ensureGuestAuth() {
        if (NetworkModule.hasValidAuth()) return

        val resp = apiService.createGuestUser()
        if (!resp.isSuccessful) {
            val errorBody = resp.errorBody()?.string()
            throw Exception("Failed to create guest (${resp.code()}) - $errorBody")
        }

        val auth = resp.body() ?: throw Exception("Missing guest auth")
        NetworkModule.storeAuth(auth)
    }
}
