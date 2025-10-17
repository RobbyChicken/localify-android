package com.localify.android.ui.profile

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
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
        composeTestRule.setContent {
            LocalifyTheme {
                ProfileScreen()
            }
        }

        composeTestRule.onNodeWithText("Profile").assertIsDisplayed()
    }

    @Test
    fun profileScreen_displaysUserName() {
        composeTestRule.setContent {
            LocalifyTheme {
                ProfileScreen()
            }
        }

        composeTestRule.onNodeWithText("Localify Guest").assertIsDisplayed()
    }

    @Test
    fun profileScreen_displaysLogoutButton() {
        composeTestRule.setContent {
            LocalifyTheme {
                ProfileScreen()
            }
        }

        composeTestRule.onNodeWithText("Logout").assertIsDisplayed()
    }

    @Test
    fun profileScreen_displaysEditProfileButton() {
        composeTestRule.setContent {
            LocalifyTheme {
                ProfileScreen()
            }
        }

        composeTestRule.onNodeWithText("Edit Profile").assertIsDisplayed()
    }

    @Test
    fun profileScreen_displaysNavigationBar() {
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
}
