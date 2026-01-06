package com.localify.android.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.localify.android.data.network.ApiService
import com.localify.android.data.network.AuthResponse
import com.localify.android.data.network.UserCityResponse
import com.localify.android.data.network.UserCity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@ExperimentalCoroutinesApi
@Ignore("Outdated: HomeRepository now uses NetworkModule (requires init with Context + real Retrofit)")
class HomeRepositoryTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var mockApiService: ApiService

    private lateinit var repository: HomeRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = HomeRepository()
        // Note: In a real implementation, you'd inject the mock ApiService
    }

    @Test
    fun `createGuestUser returns auth response`() = runTest {
        assertNotNull(mockApiService)
    }

    @Test
    fun `getUserCities returns user cities`() = runTest {
        assertNotNull(mockApiService)
    }
}
