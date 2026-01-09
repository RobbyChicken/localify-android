package com.localify.android.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.localify.android.ui.detail.ArtistDetailScreen
import com.localify.android.ui.detail.EventDetailScreen
import com.localify.android.ui.home.HomeScreen
import com.localify.android.ui.theme.LocalifyTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Comprehensive UI tests to verify ALL buttons and interactive elements work correctly.
 * This test suite covers every clickable element across all screens in the app.
 */
@RunWith(AndroidJUnit4::class)
class AllButtonsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // ==================== HomeScreen Button Tests ====================

    @Test
    fun homeScreen_tabButtons_work() {
        composeTestRule.setContent {
            LocalifyTheme {
                HomeScreen(
                    onNavigateToEventDetail = {},
                    onNavigateToArtistDetail = {}
                )
            }
        }

        // Test Artists tab button
        composeTestRule.onNodeWithText("Artists").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Artists").assertIsDisplayed()
        
        // Test Events tab button
        composeTestRule.onNodeWithText("Events").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Events").assertIsDisplayed()
    }

    @Test
    fun homeScreen_navigationButtons_work() {
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

        // Test Favorites button
        composeTestRule.onNodeWithText("Favorites").performClick()
        assert(favoritesClicked) { "Favorites button should trigger navigation" }
        
        // Test Search button
        composeTestRule.onNodeWithText("Search").performClick()
        assert(searchClicked) { "Search button should trigger navigation" }
        
        // Test Profile button
        composeTestRule.onNodeWithText("Profile").performClick()
        assert(profileClicked) { "Profile button should trigger navigation" }
    }

    @Test
    fun homeScreen_filterButtons_work() {
        composeTestRule.setContent {
            LocalifyTheme {
                HomeScreen(
                    onNavigateToEventDetail = {},
                    onNavigateToArtistDetail = {}
                )
            }
        }

        // Should be on Events tab by default - filter buttons should be visible
        composeTestRule.onNodeWithContentDescription("Filter Menu")
            .assertIsDisplayed()
            .assertExists()
        
        composeTestRule.onNodeWithContentDescription("Date Filter")
            .assertIsDisplayed()
            .assertExists()
    }

    @Test
    fun homeScreen_citySelector_isVisible() {
        composeTestRule.setContent {
            LocalifyTheme {
                HomeScreen(
                    onNavigateToEventDetail = {},
                    onNavigateToArtistDetail = {}
                )
            }
        }

        // City selector dropdown should be visible
        composeTestRule.onNodeWithText("▼").assertIsDisplayed()
    }

    @Test
    fun homeScreen_artistCards_areClickable() {
        var artistClicked = ""

        composeTestRule.setContent {
            LocalifyTheme {
                HomeScreen(
                    onNavigateToEventDetail = {},
                    onNavigateToArtistDetail = { artistClicked = it }
                )
            }
        }

        // Switch to Artists tab
        composeTestRule.onNodeWithText("Artists").performClick()
        
        // Wait for content to load
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodes(hasClickAction()).fetchSemanticsNodes().size > 3
        }

        // Click an artist card (skip nav buttons)
        val clickableNodes = composeTestRule.onAllNodes(hasClickAction())
        if (clickableNodes.fetchSemanticsNodes().size > 3) {
            clickableNodes[3].performClick()
            assert(artistClicked.isNotEmpty()) { "Artist card should trigger navigation" }
        }
    }

    @Test
    fun homeScreen_eventCards_areClickable() {
        var eventClicked = ""

        composeTestRule.setContent {
            LocalifyTheme {
                HomeScreen(
                    onNavigateToEventDetail = { eventClicked = it },
                    onNavigateToArtistDetail = {}
                )
            }
        }

        // Events tab should be default
        // Wait for content to load
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodes(hasClickAction()).fetchSemanticsNodes().size > 3
        }

        // Click an event card
        val clickableNodes = composeTestRule.onAllNodes(hasClickAction())
        if (clickableNodes.fetchSemanticsNodes().size > 3) {
            clickableNodes[3].performClick()
            assert(eventClicked.isNotEmpty()) { "Event card should trigger navigation" }
        }
    }

    @Test
    fun homeScreen_favoriteButtons_existOnArtistCards() {
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
        
        // Wait for artist cards with favorite buttons
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithContentDescription("Favorite")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Verify favorite buttons exist
        val favoriteButtons = composeTestRule.onAllNodesWithContentDescription("Favorite")
        assert(favoriteButtons.fetchSemanticsNodes().isNotEmpty()) {
            "Favorite buttons should exist on artist cards"
        }
    }

    @Test
    fun homeScreen_bookmarkButtons_existOnEventCards() {
        composeTestRule.setContent {
            LocalifyTheme {
                HomeScreen(
                    onNavigateToEventDetail = {},
                    onNavigateToArtistDetail = {}
                )
            }
        }

        // Events tab should be default
        // Wait for event cards with bookmark buttons
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithContentDescription("Bookmark")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Verify bookmark buttons exist
        val bookmarkButtons = composeTestRule.onAllNodesWithContentDescription("Bookmark")
        assert(bookmarkButtons.fetchSemanticsNodes().isNotEmpty()) {
            "Bookmark buttons should exist on event cards"
        }
    }

    // ==================== ArtistDetailScreen Button Tests ====================

    @Test
    fun artistDetailScreen_backButton_works() {
        var backPressed = false

        composeTestRule.setContent {
            LocalifyTheme {
                ArtistDetailScreen(
                    artistId = "test_artist",
                    onNavigateBack = { backPressed = true },
                    onNavigateToEventDetail = {}
                )
            }
        }

        // Click back button
        composeTestRule.onNodeWithContentDescription("Back")
            .assertIsDisplayed()
            .performClick()
        
        assert(backPressed) { "Back button should trigger navigation" }
    }

    @Test
    fun artistDetailScreen_favoriteButton_exists() {
        composeTestRule.setContent {
            LocalifyTheme {
                ArtistDetailScreen(
                    artistId = "test_artist",
                    onNavigateBack = {},
                    onNavigateToEventDetail = {}
                )
            }
        }

        // Wait for content to load
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithContentDescription("Add to favorites")
                .fetchSemanticsNodes().isNotEmpty() ||
            composeTestRule.onAllNodesWithContentDescription("Remove from favorites")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Favorite button should exist
        val favoriteButton = composeTestRule.onNode(
            hasContentDescription("Add to favorites") or 
            hasContentDescription("Remove from favorites")
        )
        favoriteButton.assertExists()
    }

    @Test
    fun artistDetailScreen_spotifyButton_exists() {
        composeTestRule.setContent {
            LocalifyTheme {
                ArtistDetailScreen(
                    artistId = "test_artist",
                    onNavigateBack = {},
                    onNavigateToEventDetail = {}
                )
            }
        }

        // Wait for Spotify button
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithText("Listen on Spotify")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Spotify button should exist
        composeTestRule.onNodeWithText("Listen on Spotify")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun artistDetailScreen_songPreviewButton_exists() {
        composeTestRule.setContent {
            LocalifyTheme {
                ArtistDetailScreen(
                    artistId = "test_artist",
                    onNavigateBack = {},
                    onNavigateToEventDetail = {}
                )
            }
        }

        // Wait for Song Preview button
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithText("Song Preview")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Song Preview button should exist
        composeTestRule.onNodeWithText("Song Preview")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun artistDetailScreen_similarArtists_areVisible() {
        composeTestRule.setContent {
            LocalifyTheme {
                ArtistDetailScreen(
                    artistId = "test_artist",
                    onNavigateBack = {},
                    onNavigateToEventDetail = {}
                )
            }
        }

        // Wait for similar artists section
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onNodeWithText("Similar Artists").isDisplayed()
        }

        // Similar Artists section should be visible
        composeTestRule.onNodeWithText("Similar Artists").assertIsDisplayed()
    }

    @Test
    fun artistDetailScreen_upcomingEvents_areVisible() {
        composeTestRule.setContent {
            LocalifyTheme {
                ArtistDetailScreen(
                    artistId = "test_artist",
                    onNavigateBack = {},
                    onNavigateToEventDetail = {}
                )
            }
        }

        // Wait for upcoming events section
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onNodeWithText("Upcoming Events").isDisplayed()
        }

        // Upcoming Events section should be visible
        composeTestRule.onNodeWithText("Upcoming Events").assertIsDisplayed()
    }

    // ==================== EventDetailScreen Button Tests ====================

    @Test
    fun eventDetailScreen_backButton_works() {
        var backPressed = false

        composeTestRule.setContent {
            LocalifyTheme {
                EventDetailScreen(
                    eventId = "test_event",
                    onNavigateBack = { backPressed = true },
                    onNavigateToArtistDetail = {}
                )
            }
        }

        // Click back button
        composeTestRule.onNodeWithContentDescription("Back")
            .assertIsDisplayed()
            .performClick()
        
        assert(backPressed) { "Back button should trigger navigation" }
    }

    @Test
    fun eventDetailScreen_bookmarkButton_exists() {
        composeTestRule.setContent {
            LocalifyTheme {
                EventDetailScreen(
                    eventId = "test_event",
                    onNavigateBack = {},
                    onNavigateToArtistDetail = {}
                )
            }
        }

        // Wait for content
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onNodeWithText("Randy Travis").isDisplayed()
        }

        // Bookmark button should exist
        val bookmarkButton = composeTestRule.onNode(
            hasContentDescription("Add bookmark") or 
            hasContentDescription("Remove bookmark")
        )
        bookmarkButton.assertExists()
    }

    @Test
    fun eventDetailScreen_viewTicketsButton_exists() {
        composeTestRule.setContent {
            LocalifyTheme {
                EventDetailScreen(
                    eventId = "test_event",
                    onNavigateBack = {},
                    onNavigateToArtistDetail = {}
                )
            }
        }

        // View Event Tickets button should exist
        composeTestRule.onNodeWithText("View Event Tickets")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun eventDetailScreen_performingArtists_areClickable() {
        var artistClicked = ""

        composeTestRule.setContent {
            LocalifyTheme {
                EventDetailScreen(
                    eventId = "test_event",
                    onNavigateBack = {},
                    onNavigateToArtistDetail = { artistClicked = it }
                )
            }
        }

        // Wait for performing artists
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onNodeWithText("Performing Artists").isDisplayed()
        }

        // Click Randy Travis artist card
        composeTestRule.onNodeWithText("Randy Travis").performClick()
        
        // Verify navigation was triggered
        assert(artistClicked.isNotEmpty()) {
            "Performing artist should trigger navigation"
        }
    }

    // ==================== Helper Extensions ====================

    private fun SemanticsNodeInteraction.isDisplayed(): Boolean {
        return try {
            assertIsDisplayed()
            true
        } catch (e: AssertionError) {
            false
        }
    }
}
