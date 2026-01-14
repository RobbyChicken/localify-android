package com.localify.android.ui.onboarding

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.localify.android.data.network.CityResponse
import com.localify.android.ui.theme.LocalifyTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CitySearchScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun citySearchScreen_showsCityAndStateWhenAvailable() {
        val cities = listOf(
            CityResponse(
                id = "1",
                name = "Ithaca",
                state = "NY",
                country = "US",
                latitude = 0.0,
                longitude = 0.0
            )
        )

        composeTestRule.setContent {
            LocalifyTheme {
                CitySearchScreen(
                    searchText = "ith",
                    onSearchTextChanged = { },
                    filteredCities = cities,
                    selectedCityId = "",
                    onCitySelected = { _, _ -> }
                )
            }
        }

        composeTestRule.onNodeWithText("Ithaca, NY").assertIsDisplayed()
    }

    @Test
    fun citySearchScreen_showsCityOnlyWhenStateMissing() {
        val cities = listOf(
            CityResponse(
                id = "1",
                name = "Ithaca",
                state = null,
                country = "US",
                latitude = 0.0,
                longitude = 0.0
            )
        )

        composeTestRule.setContent {
            LocalifyTheme {
                CitySearchScreen(
                    searchText = "ith",
                    onSearchTextChanged = { },
                    filteredCities = cities,
                    selectedCityId = "",
                    onCitySelected = { _, _ -> }
                )
            }
        }

        composeTestRule.onNodeWithText("Ithaca").assertIsDisplayed()
    }

    @Test
    fun citySearchScreen_clickingResult_passesCityLabel() {
        var selectedId = ""
        var selectedLabel = ""

        val cities = listOf(
            CityResponse(
                id = "1",
                name = "Ithaca",
                state = "NY",
                country = "US",
                latitude = 0.0,
                longitude = 0.0
            )
        )

        composeTestRule.setContent {
            LocalifyTheme {
                CitySearchScreen(
                    searchText = "ith",
                    onSearchTextChanged = { },
                    filteredCities = cities,
                    selectedCityId = "",
                    onCitySelected = { id, label ->
                        selectedId = id
                        selectedLabel = label
                    }
                )
            }
        }

        composeTestRule.onNodeWithText("Ithaca, NY").performClick()
        assert(selectedId == "1")
        assert(selectedLabel == "Ithaca, NY")
    }
}
