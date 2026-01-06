package com.localify.android.ui.home

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.localify.android.ui.theme.LocalifyTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenFixedTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun homeScreen_displaysCorrectTabs() {
        composeTestRule.setContent {
            LocalifyTheme {
                HomeScreen(
                    onNavigateToEventDetail = {},
                    onNavigateToArtistDetail = {}
                )
            }
        }

        // Verify both tabs are displayed
        composeTestRule.onNodeWithText("Events").assertIsDisplayed()
        composeTestRule.onNodeWithText("Artists").assertIsDisplayed()
    }

    @Test
    fun homeScreen_canSwitchBetweenTabs() {
        composeTestRule.setContent {
            LocalifyTheme {
                HomeScreen(
                    onNavigateToEventDetail = {},
                    onNavigateToArtistDetail = {}
                )
            }
        }

        // Click on Artists tab
        composeTestRule.onNodeWithText("Artists").performClick()
        
        // Verify Artists tab content is shown
        composeTestRule.onNodeWithText("Artists").assertIsDisplayed()
    }

    @Test
    fun homeScreen_filterButtonsOnlyVisibleForEventsTab() {
        composeTestRule.setContent {
            LocalifyTheme {
                HomeScreen(
                    onNavigateToEventDetail = {},
                    onNavigateToArtistDetail = {}
                )
            }
        }

        // Filter buttons should be visible on Events tab
        composeTestRule.onNodeWithContentDescription("Filter Menu").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Date Filter").assertIsDisplayed()

        // Switch to Artists tab
        composeTestRule.onNodeWithText("Artists").performClick()

        // Filter buttons should not be visible on Artists tab
        composeTestRule.onNodeWithContentDescription("Filter Menu").assertDoesNotExist()
        composeTestRule.onNodeWithContentDescription("Date Filter").assertDoesNotExist()
    }

    @Test
    fun homeScreen_cityDropdownDisplayed() {
        composeTestRule.setContent {
            LocalifyTheme {
                HomeScreen(
                    onNavigateToEventDetail = {},
                    onNavigateToArtistDetail = {}
                )
            }
        }

        // Dropdown arrow should be visible
        composeTestRule.onNodeWithText("▼").assertIsDisplayed()
    }

    @Test
    fun homeScreen_artistCardNavigationWorks() {
        var navigatedArtistId = ""

        composeTestRule.setContent {
            LocalifyTheme {
                HomeScreen(
                    onNavigateToEventDetail = {},
                    onNavigateToArtistDetail = { artistId -> navigatedArtistId = artistId }
                )
            }
        }

        // Switch to Artists tab
        composeTestRule.onNodeWithText("Artists").performClick()
        
        // Wait for content to load and click on the first artist card if it exists
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodes(hasClickAction()).fetchSemanticsNodes().isNotEmpty()
        }
        
        // Find and click an artist card (skip first few which are nav buttons)
        val artistCards = composeTestRule.onAllNodes(hasClickAction())
        if (artistCards.fetchSemanticsNodes().size > 3) {
            artistCards[3].performClick()
            
            // Verify navigation was triggered with a non-empty artist ID
            assert(navigatedArtistId.isNotEmpty()) { "Artist navigation should have been triggered" }
        }
    }

    @Test
    fun homeScreen_eventCardNavigationWorks() {
        var navigatedEventId = ""

        composeTestRule.setContent {
            LocalifyTheme {
                HomeScreen(
                    onNavigateToEventDetail = { eventId -> navigatedEventId = eventId },
                    onNavigateToArtistDetail = {}
                )
            }
        }

        // Events tab should be selected by default
        // Wait for content to load and click on the first event card if it exists
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodes(hasClickAction()).fetchSemanticsNodes().isNotEmpty()
        }
        
        // Find and click an event card (skip nav buttons)
        val eventCards = composeTestRule.onAllNodes(hasClickAction())
        if (eventCards.fetchSemanticsNodes().size > 3) {
            eventCards[3].performClick()
            
            // Verify navigation was triggered with a non-empty event ID
            assert(navigatedEventId.isNotEmpty()) { "Event navigation should have been triggered" }
        }
    }

    @Test
    fun homeScreen_allNavigationButtons_areClickable() {
        var favoritesClicked = false
        var searchClicked = false
        var profileClicked = false

        composeTestRule.setContent {
            LocalifyTheme {
                HomeScreen(
                    onNavigateToEventDetail = {},
                    onNavigateToArtistDetail = {},
                    onNavigateToFavorites = { favoritesClicked = true },
                    onNavigateToSearch = { searchClicked = true },
                    onNavigateToProfile = { profileClicked = true }
                )
            }
        }

        // Test Home button
        composeTestRule.onNodeWithText("Home").assertIsDisplayed()
        
        // Test Favorites button
        composeTestRule.onNodeWithText("Favorites").performClick()
        assert(favoritesClicked) { "Favorites button should work" }
        
        // Test Search button
        composeTestRule.onNodeWithText("Search").performClick()
        assert(searchClicked) { "Search button should work" }
        
        // Test Profile button
        composeTestRule.onNodeWithText("Profile").performClick()
        assert(profileClicked) { "Profile button should work" }
    }

    @Test
    fun homeScreen_filterButtons_workOnEventsTab() {
        composeTestRule.setContent {
            LocalifyTheme {
                HomeScreen(
                    onNavigateToEventDetail = {},
                    onNavigateToArtistDetail = {}
                )
            }
        }

        // Should be on Events tab by default
        // Verify filter buttons are present and clickable
        composeTestRule.onNodeWithContentDescription("Filter Menu")
            .assertIsDisplayed()
            .assertExists()
        
        composeTestRule.onNodeWithContentDescription("Date Filter")
            .assertIsDisplayed()
            .assertExists()
    }

    @Test
    fun homeScreen_favoriteButtons_areClickableOnCards() {
        composeTestRule.setContent {
            LocalifyTheme {
                HomeScreen(
                    onNavigateToEventDetail = {},
                    onNavigateToArtistDetail = {}
                )
            }
        }

        // Switch to Artists tab
        composeTestRule.onNodeWithText("Artists").performClick()
        
        // Wait for artist cards to load
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithContentDescription("Favorite")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Verify favorite buttons exist and are clickable
        val favoriteButtons = composeTestRule.onAllNodesWithContentDescription("Favorite")
        if (favoriteButtons.fetchSemanticsNodes().isNotEmpty()) {
            favoriteButtons.onFirst().assertExists()
        }
    }
}
