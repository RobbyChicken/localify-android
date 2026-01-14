package com.localify.android.ui.profile

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.core.app.ApplicationProvider
import com.localify.android.data.network.NetworkModule
import com.localify.android.ui.theme.LocalifyTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun profileScreen_displaysTitle() {
        NetworkModule.init(ApplicationProvider.getApplicationContext())
        composeTestRule.setContent {
            LocalifyTheme {
                ProfileScreen()
            }
        }

        composeTestRule.onNodeWithText("Profile").assertIsDisplayed()
    }

    @Test
    fun profileScreen_displaysMemberSinceLabel() {
        NetworkModule.init(ApplicationProvider.getApplicationContext())
        composeTestRule.setContent {
            LocalifyTheme {
                ProfileScreen()
            }
        }

        composeTestRule.onNode(
            hasText("Member since", substring = true)
        ).assertIsDisplayed()
    }

    @Test
    fun profileScreen_displaysLogoutButton() {
        NetworkModule.init(ApplicationProvider.getApplicationContext())
        composeTestRule.setContent {
            LocalifyTheme {
                ProfileScreen()
            }
        }

        composeTestRule.onNodeWithText("Logout").assertIsDisplayed()
    }

    @Test
    fun profileScreen_hidesEditProfileButtonForGuest() {
        NetworkModule.init(ApplicationProvider.getApplicationContext())
        composeTestRule.setContent {
            LocalifyTheme {
                ProfileScreen()
            }
        }

        composeTestRule.onNodeWithText("Edit Profile").assertDoesNotExist()
    }

    @Test
    fun profileScreen_displaysNavigationBar() {
        NetworkModule.init(ApplicationProvider.getApplicationContext())
        composeTestRule.setContent {
            LocalifyTheme {
                ProfileScreen()
            }
        }

        // Check for navigation items
        composeTestRule.onNodeWithText("Home").assertIsDisplayed()
        composeTestRule.onNodeWithText("Favorites").assertIsDisplayed()
        composeTestRule.onNodeWithText("Search").assertIsDisplayed()
        composeTestRule.onNodeWithText("Profile").assertIsDisplayed()
    }

    @Test
    fun profileScreen_logoutButtonClickable() {
        var logoutClicked = false

        NetworkModule.init(ApplicationProvider.getApplicationContext())
        
        composeTestRule.setContent {
            LocalifyTheme {
                ProfileScreen(
                    onNavigateToLogin = { logoutClicked = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Logout").performClick()
        // Note: In a real test, you'd verify the logout action occurred
    }

    @Test
    fun profileScreen_myCitiesDialog_opensAndCloses() {
        NetworkModule.init(ApplicationProvider.getApplicationContext())
        composeTestRule.setContent {
            LocalifyTheme {
                ProfileScreen()
            }
        }

        composeTestRule.onNodeWithText("My Cities").assertIsDisplayed().performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("My Cities").assertIsDisplayed()
        composeTestRule.onNodeWithText("Close").assertIsDisplayed().performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Close").assertDoesNotExist()
    }

    @Test
    fun profileScreen_myFamiliarArtistsDialog_opensAndCloses() {
        NetworkModule.init(ApplicationProvider.getApplicationContext())
        composeTestRule.setContent {
            LocalifyTheme {
                ProfileScreen()
            }
        }

        composeTestRule.onNodeWithText("My Familiar Artists").assertIsDisplayed().performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("My Familiar Artists").assertIsDisplayed()
        composeTestRule.onNodeWithText("Close").assertIsDisplayed().performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Close").assertDoesNotExist()
    }
}
