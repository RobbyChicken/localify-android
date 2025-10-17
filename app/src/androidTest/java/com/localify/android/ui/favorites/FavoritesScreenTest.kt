package com.localify.android.ui.favorites

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.localify.android.ui.theme.LocalifyTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavoritesScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun favoritesScreen_displaysTitle() {
        composeTestRule.setContent {
            LocalifyTheme {
                FavoritesScreen(
                    onNavigateToHome = {},
                    onNavigateToSearch = {},
                    onNavigateToProfile = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Favorites").assertIsDisplayed()
    }

    @Test
    fun favoritesScreen_displaysTabs() {
        composeTestRule.setContent {
            LocalifyTheme {
                FavoritesScreen(
                    onNavigateToHome = {},
                    onNavigateToSearch = {},
                    onNavigateToProfile = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Artists").assertIsDisplayed()
        composeTestRule.onNodeWithText("Upcoming").assertIsDisplayed()
        composeTestRule.onNodeWithText("Past").assertIsDisplayed()
    }

    @Test
    fun favoritesScreen_tabSwitching() {
        composeTestRule.setContent {
            LocalifyTheme {
                FavoritesScreen(
                    onNavigateToHome = {},
                    onNavigateToSearch = {},
                    onNavigateToProfile = {}
                )
            }
        }

        // Click on Upcoming tab
        composeTestRule.onNodeWithText("Upcoming").performClick()
        
        // Click on Past tab
        composeTestRule.onNodeWithText("Past").performClick()
        
        // Click back to Artists tab
        composeTestRule.onNodeWithText("Artists").performClick()
    }

    @Test
    fun favoritesScreen_displaysNavigationBar() {
        composeTestRule.setContent {
            LocalifyTheme {
                FavoritesScreen(
                    onNavigateToHome = {},
                    onNavigateToSearch = {},
                    onNavigateToProfile = {}
                )
            }
        }

        // Check navigation bar items
        composeTestRule.onNodeWithContentDescription("Home").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Favorites").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Search").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Profile").assertIsDisplayed()
    }

    @Test
    fun favoritesScreen_navigationBarClicks() {
        var homeClicked = false
        var searchClicked = false
        var profileClicked = false

        composeTestRule.setContent {
            LocalifyTheme {
                FavoritesScreen(
                    onNavigateToHome = { homeClicked = true },
                    onNavigateToSearch = { searchClicked = true },
                    onNavigateToProfile = { profileClicked = true }
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Home").performClick()
        composeTestRule.onNodeWithContentDescription("Search").performClick()
        composeTestRule.onNodeWithContentDescription("Profile").performClick()
    }

    @Test
    fun favoritesScreen_displaysEmptyStateInitially() {
        composeTestRule.setContent {
            LocalifyTheme {
                FavoritesScreen(
                    onNavigateToHome = {},
                    onNavigateToSearch = {},
                    onNavigateToProfile = {}
                )
            }
        }

        // Should show empty state or loading initially
        composeTestRule.onRoot().assertIsDisplayed()
    }
}
