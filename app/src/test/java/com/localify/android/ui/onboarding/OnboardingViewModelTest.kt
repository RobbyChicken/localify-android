package com.localify.android.ui.onboarding

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import com.localify.android.data.network.ApiService
import com.localify.android.data.network.CityResponse
import com.localify.android.data.network.NetworkModule
import com.localify.android.data.network.SeedResponse
import com.localify.android.data.network.UserCityItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import retrofit2.Response
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class OnboardingViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var apiService: ApiService
    private lateinit var application: Application
    private lateinit var viewModel: OnboardingViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        application = ApplicationProvider.getApplicationContext()
        NetworkModule.init(application)

        apiService = mock()
        viewModel = OnboardingViewModel(application, apiService, ioDispatcher = testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `searchCities ignores queries shorter than 2`() {
        viewModel.searchCities("a")
        val state = viewModel.uiState.value
        assertFalse(state.isLoadingCities)
        assertTrue(state.cityResults.isEmpty())
    }

    @Test
    fun `searchCities normalizes state from city name when missing`() = runTest {
        whenever(apiService.searchCities(any(), any())).thenReturn(
            Response.success(
                listOf(
                    CityResponse(
                        id = "1",
                        name = "Austin, TX",
                        state = null,
                        country = "US",
                        latitude = 30.0,
                        longitude = -97.0
                    )
                )
            )
        )

        viewModel.searchCities("au")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoadingCities)
        assertEquals(1, state.cityResults.size)
        assertEquals("Austin", state.cityResults.first().name)
        assertEquals("TX", state.cityResults.first().state)
    }

    @Test
    fun `completeOnboarding success calls onSuccess and clears isCompleting`() = runTest {
        whenever(apiService.addUserCity(any(), any())).thenReturn(
            Response.success(
                UserCityItem(
                    id = "c1",
                    name = "Ithaca",
                    state = "NY",
                    country = "US",
                    latitude = 0.0,
                    longitude = 0.0,
                    radius = 25.0,
                    selected = true
                )
            )
        )
        whenever(apiService.addUserSeeds(any())).thenReturn(
            Response.success(
                listOf(
                    SeedResponse(id = "s1", name = "Artist", image = null, appleId = null, spotifyId = null, isBlacklisted = false)
                )
            )
        )

        var successCalled = false
        viewModel.completeOnboarding("c1", 25.0, listOf("s1")) { successCalled = true }

        advanceUntilIdle()

        assertTrue(successCalled)
        assertFalse(viewModel.uiState.value.isCompleting)
    }
}
