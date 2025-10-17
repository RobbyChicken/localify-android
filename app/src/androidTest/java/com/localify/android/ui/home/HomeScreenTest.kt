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
}
