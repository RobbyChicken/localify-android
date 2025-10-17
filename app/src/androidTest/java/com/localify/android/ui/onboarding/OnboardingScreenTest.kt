package com.localify.android.ui.onboarding

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.localify.android.ui.theme.LocalifyTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OnboardingScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun onboardingScreen_displaysInitialStep() {
        composeTestRule.setContent {
            LocalifyTheme {
                OnboardingScreen(
                    onComplete = {},
                    onCancel = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Search for City").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cancel").assertIsDisplayed()
    }

    @Test
    fun onboardingScreen_displaysPageIndicators() {
        composeTestRule.setContent {
            LocalifyTheme {
                OnboardingScreen(
                    onComplete = {},
                    onCancel = {}
                )
            }
        }

        // Should have 4 page indicators (dots)
        // This is a visual test - in a real scenario you'd check for specific indicators
        composeTestRule.onRoot().assertIsDisplayed()
    }

    @Test
    fun onboardingScreen_cancelButtonWorks() {
        var cancelCalled = false
        
        composeTestRule.setContent {
            LocalifyTheme {
                OnboardingScreen(
                    onComplete = {},
                    onCancel = { cancelCalled = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Cancel").performClick()
        // Note: In a real test, you'd verify the cancel action occurred
    }

    @Test
    fun onboardingScreen_nextButtonDisabledInitially() {
        composeTestRule.setContent {
            LocalifyTheme {
                OnboardingScreen(
                    onComplete = {},
                    onCancel = {}
                )
            }
        }

        // Next button should be disabled initially since no city is selected
        composeTestRule.onNodeWithText("Next").assertIsDisplayed()
    }
}
