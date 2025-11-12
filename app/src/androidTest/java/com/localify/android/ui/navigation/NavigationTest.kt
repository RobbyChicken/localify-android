package com.localify.android.ui.navigation

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.localify.android.ui.detail.ArtistDetailScreen
import com.localify.android.ui.detail.EventDetailScreen
import com.localify.android.ui.theme.LocalifyTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun artistDetailScreen_displaysCorrectContent() {
        composeTestRule.setContent {
            LocalifyTheme {
                ArtistDetailScreen(
                    artistId = "test_artist_id",
                    onNavigateBack = { },
                    onNavigateToEventDetail = { }
                )
            }
        }

        // Verify back button is displayed
        composeTestRule.onNodeWithContentDescription("Back").assertIsDisplayed()
        
        // Verify loading state or content is displayed
        composeTestRule.onNode(
            hasText("Loading...") or hasContentDescription("Artist image")
        ).assertExists()
    }

    @Test
    fun eventDetailScreen_displaysCorrectContent() {
        composeTestRule.setContent {
            LocalifyTheme {
                EventDetailScreen(
                    eventId = "test_event_id",
                    onNavigateBack = { },
                    onNavigateToArtistDetail = { }
                )
            }
        }

        // Verify back button is displayed
        composeTestRule.onNodeWithContentDescription("Back").assertIsDisplayed()
        
        // Verify event content is displayed (using mock data)
        composeTestRule.onNodeWithText("Randy Travis").assertIsDisplayed()
        composeTestRule.onNodeWithText("Broome County Forum Theatre").assertIsDisplayed()
        composeTestRule.onNodeWithText("Binghamton, NY").assertIsDisplayed()
    }

    @Test
    fun artistDetailScreen_backButtonWorks() {
        var backPressed = false

        composeTestRule.setContent {
            LocalifyTheme {
                ArtistDetailScreen(
                    artistId = "test_artist_id",
                    onNavigateBack = { backPressed = true },
                    onNavigateToEventDetail = { }
                )
            }
        }

        // Click back button
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        
        // Verify navigation callback was triggered
        assert(backPressed) { "Back navigation should have been triggered" }
    }

    @Test
    fun eventDetailScreen_backButtonWorks() {
        var backPressed = false

        composeTestRule.setContent {
            LocalifyTheme {
                EventDetailScreen(
                    eventId = "test_event_id",
                    onNavigateBack = { backPressed = true },
                    onNavigateToArtistDetail = { }
                )
            }
        }

        // Click back button
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        
        // Verify navigation callback was triggered
        assert(backPressed) { "Back navigation should have been triggered" }
    }

    @Test
    fun eventDetailScreen_artistCardNavigationWorks() {
        var navigatedArtistId = ""

        composeTestRule.setContent {
            LocalifyTheme {
                EventDetailScreen(
                    eventId = "test_event_id",
                    onNavigateBack = { },
                    onNavigateToArtistDetail = { artistId -> navigatedArtistId = artistId }
                )
            }
        }

        // Wait for content to load
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onNodeWithText("Performing Artists").isDisplayed()
        }

        // Find and click on an artist card in the performing artists section
        composeTestRule.onNodeWithText("Randy Travis").performClick()
        
        // Verify navigation was triggered
        assert(navigatedArtistId.isNotEmpty()) { "Artist navigation should have been triggered" }
    }

    @Test
    fun artistDetailScreen_favoriteButtonWorks() {
        composeTestRule.setContent {
            LocalifyTheme {
                ArtistDetailScreen(
                    artistId = "test_artist_id",
                    onNavigateBack = { },
                    onNavigateToEventDetail = { }
                )
            }
        }

        // Wait for content to load
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithContentDescription("Add to favorites").fetchSemanticsNodes().isNotEmpty() ||
            composeTestRule.onAllNodesWithContentDescription("Remove from favorites").fetchSemanticsNodes().isNotEmpty()
        }

        // Click favorite button (either add or remove)
        val favoriteButton = composeTestRule.onNode(
            hasContentDescription("Add to favorites") or hasContentDescription("Remove from favorites")
        )
        
        if (favoriteButton.isDisplayed()) {
            favoriteButton.performClick()
            // The button should still exist after clicking (state change)
            favoriteButton.assertExists()
        }
    }

    @Test
    fun eventDetailScreen_bookmarkButtonWorks() {
        composeTestRule.setContent {
            LocalifyTheme {
                EventDetailScreen(
                    eventId = "test_event_id",
                    onNavigateBack = { },
                    onNavigateToArtistDetail = { }
                )
            }
        }

        // Wait for content to load
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithContentDescription("Add bookmark").fetchSemanticsNodes().isNotEmpty() ||
            composeTestRule.onAllNodesWithContentDescription("Remove bookmark").fetchSemanticsNodes().isNotEmpty()
        }

        // Click bookmark button (either add or remove)
        val bookmarkButton = composeTestRule.onNode(
            hasContentDescription("Add bookmark") or hasContentDescription("Remove bookmark")
        )
        
        if (bookmarkButton.isDisplayed()) {
            bookmarkButton.performClick()
            // The button should still exist after clicking (state change)
            bookmarkButton.assertExists()
        }
    }
}
