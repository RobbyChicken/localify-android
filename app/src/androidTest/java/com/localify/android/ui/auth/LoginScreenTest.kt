package com.localify.android.ui.auth

import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.localify.android.data.network.NetworkModule
import com.localify.android.ui.theme.LocalifyTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Before
    fun setup() {
        NetworkModule.init(ApplicationProvider.getApplicationContext())
    }

    @Test
    fun loginScreen_displaysAllLoginButtons() {
        composeTestRule.setContent {
            LocalifyTheme {
                LoginScreen(onLoginSuccess = { })
            }
        }

        composeTestRule.onNodeWithText("Sign in with Google").assertIsDisplayed()
        composeTestRule.onNodeWithText("Login with Spotify").assertIsDisplayed()
        composeTestRule.onNodeWithText("Log in with Email").assertIsDisplayed()
        composeTestRule.onNodeWithText("Continue as Guest").assertIsDisplayed()
    }

    @Test
    fun loginScreen_emailDialog_opensAndCloses() {
        composeTestRule.setContent {
            LocalifyTheme {
                LoginScreen(onLoginSuccess = { })
            }
        }

        composeTestRule.onNodeWithText("Log in with Email").assertIsDisplayed().performClick()
        composeTestRule.onNodeWithText("Log in with Email").assertIsDisplayed()
        composeTestRule.onNodeWithText("Close").assertIsDisplayed().performClick()
        composeTestRule.onNodeWithText("Close").assertDoesNotExist()
    }

    @Test
    fun loginScreen_googleMissingClientId_showsError() {
        composeTestRule.setContent {
            LocalifyTheme {
                LoginScreen(onLoginSuccess = { })
            }
        }

        val clientId = composeTestRule.activity.getString(com.localify.android.R.string.google_web_client_id).trim()
        composeTestRule.onNodeWithText("Sign in with Google").assertIsDisplayed()

        if (clientId.isBlank()) {
            composeTestRule.onNodeWithText("Sign in with Google").performClick()
            composeTestRule.onNodeWithText("Missing GOOGLE_WEB_CLIENT_ID configuration").assertIsDisplayed()
        }
    }

    @Test
    fun loginScreen_spotifyCallbackError_isShown() {
        val errorUri = Uri.parse("localify://auth/spotify?error=access_denied")

        composeTestRule.setContent {
            LocalifyTheme {
                LoginScreen(
                    onLoginSuccess = { },
                    spotifyCallbackUri = errorUri,
                    onConsumeSpotifyCallback = { }
                )
            }
        }

        composeTestRule.onNodeWithText("access_denied").assertIsDisplayed()
    }
}
