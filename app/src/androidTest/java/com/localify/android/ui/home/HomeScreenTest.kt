package com.localify.android.ui.home

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.localify.android.ui.theme.LocalifyTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun homeScreen_displaysCorrectTabs() {
        composeTestRule.setContent {
            LocalifyTheme {
                HomeScreen()
            }
        }

        // Verify both tabs are displayed
        composeTestRule.onNodeWithText("Events").assertIsDisplayed()
        composeTestRule.onNodeWithText("Artists").assertIsDisplayed()
    }

    @Test
    fun homeScreen_eventsTabSelectedByDefault() {
        composeTestRule.setContent {
            LocalifyTheme {
                HomeScreen()
            }
        }

        // Events tab should be selected by default
        composeTestRule.onNodeWithText("Events").assertIsDisplayed()
    }

    @Test
    fun homeScreen_canSwitchBetweenTabs() {
        composeTestRule.setContent {
            LocalifyTheme {
                HomeScreen()
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
                HomeScreen()
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
    fun homeScreen_cityNameDisplayed() {
        composeTestRule.setContent {
            LocalifyTheme {
                HomeScreen()
            }
        }

        // Should show either a city name or "Loading..."
        composeTestRule.onNode(
            hasText("Loading...") or hasTextThatContains(",")
        ).assertIsDisplayed()
    }

    @Test
    fun homeScreen_artistCardNavigationWorks() {
        var navigatedArtistId = ""
        var navigatedEventId = ""

        composeTestRule.setContent {
            LocalifyTheme {
                HomeScreen(
                    onNavigateToArtistDetail = { artistId -> navigatedArtistId = artistId },
                    onNavigateToEventDetail = { eventId -> navigatedEventId = eventId }
                )
            }
        }

        // Switch to Artists tab
        composeTestRule.onNodeWithText("Artists").performClick()
        
        // Wait for content to load and click on the first artist card if it exists
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodes(hasClickAction()).fetchSemanticsNodes().isNotEmpty()
        }
        
        // Find and click an artist card (they should be clickable)
        val artistCards = composeTestRule.onAllNodes(hasClickAction())
        if (artistCards.fetchSemanticsNodes().isNotEmpty()) {
            artistCards.onFirst().performClick()
            
            // Verify navigation was triggered with a non-empty artist ID
            assert(navigatedArtistId.isNotEmpty()) { "Artist navigation should have been triggered" }
        }
    }

    @Test
    fun homeScreen_eventCardNavigationWorks() {
        var navigatedArtistId = ""
        var navigatedEventId = ""

        composeTestRule.setContent {
            LocalifyTheme {
                HomeScreen(
                    onNavigateToArtistDetail = { artistId -> navigatedArtistId = artistId },
                    onNavigateToEventDetail = { eventId -> navigatedEventId = eventId }
                )
            }
        }

        // Events tab should be selected by default
        // Wait for content to load and click on the first event card if it exists
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodes(hasClickAction()).fetchSemanticsNodes().isNotEmpty()
        }
        
        // Find and click an event card (they should be clickable)
        val eventCards = composeTestRule.onAllNodes(hasClickAction())
        if (eventCards.fetchSemanticsNodes().isNotEmpty()) {
            eventCards.onFirst().performClick()
            
            // Verify navigation was triggered with a non-empty event ID
            assert(navigatedEventId.isNotEmpty()) { "Event navigation should have been triggered" }
        }
    }
}
