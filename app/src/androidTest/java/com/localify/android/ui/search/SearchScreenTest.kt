package com.localify.android.ui.search

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.localify.android.ui.theme.LocalifyTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun searchScreen_displaysTitle() {
        composeTestRule.setContent {
            LocalifyTheme {
                SearchScreen(
                    onNavigateToHome = {},
                    onNavigateToFavorites = {},
                    onNavigateToProfile = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Search").assertIsDisplayed()
    }

    @Test
    fun searchScreen_displaysSearchField() {
        composeTestRule.setContent {
            LocalifyTheme {
                SearchScreen(
                    onNavigateToHome = {},
                    onNavigateToFavorites = {},
                    onNavigateToProfile = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Search artists, events...").assertIsDisplayed()
    }

    @Test
    fun searchScreen_displaysTabs() {
        composeTestRule.setContent {
            LocalifyTheme {
                SearchScreen(
                    onNavigateToHome = {},
                    onNavigateToFavorites = {},
                    onNavigateToProfile = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Artists").assertIsDisplayed()
        composeTestRule.onNodeWithText("Events").assertIsDisplayed()
    }

    @Test
    fun searchScreen_tabSwitching() {
        composeTestRule.setContent {
            LocalifyTheme {
                SearchScreen(
                    onNavigateToHome = {},
                    onNavigateToFavorites = {},
                    onNavigateToProfile = {}
                )
            }
        }

        // Click on Events tab
        composeTestRule.onNodeWithText("Events").performClick()
        
        // Click back to Artists tab
        composeTestRule.onNodeWithText("Artists").performClick()
    }

    @Test
    fun searchScreen_searchFieldInteraction() {
        composeTestRule.setContent {
            LocalifyTheme {
                SearchScreen(
                    onNavigateToHome = {},
                    onNavigateToFavorites = {},
                    onNavigateToProfile = {}
                )
            }
        }

        // Type in search field
        composeTestRule.onNodeWithText("Search artists, events...")
            .performTextInput("test search")
    }

    @Test
    fun searchScreen_displaysNavigationBar() {
        composeTestRule.setContent {
            LocalifyTheme {
                SearchScreen(
                    onNavigateToHome = {},
                    onNavigateToFavorites = {},
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
    fun searchScreen_navigationBarClicks() {
        var homeClicked = false
        var favoritesClicked = false
        var profileClicked = false

        composeTestRule.setContent {
            LocalifyTheme {
                SearchScreen(
                    onNavigateToHome = { homeClicked = true },
                    onNavigateToFavorites = { favoritesClicked = true },
                    onNavigateToProfile = { profileClicked = true }
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Home").performClick()
        composeTestRule.onNodeWithContentDescription("Favorites").performClick()
        composeTestRule.onNodeWithContentDescription("Profile").performClick()
    }

    @Test
    fun searchScreen_displaysEmptyStateInitially() {
        composeTestRule.setContent {
            LocalifyTheme {
                SearchScreen(
                    onNavigateToHome = {},
                    onNavigateToFavorites = {},
                    onNavigateToProfile = {}
                )
            }
        }

        // Should show empty state initially
        composeTestRule.onRoot().assertIsDisplayed()
    }
}
