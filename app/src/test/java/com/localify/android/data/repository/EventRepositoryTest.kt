package com.localify.android.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.MockitoAnnotations
import kotlin.test.assertFailsWith

@ExperimentalCoroutinesApi
class EventRepositoryTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: EventRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = EventRepository()
    }

    @Test
    fun `getFeaturedEvents throws because repository is deprecated`() = runTest {
        assertFailsWith<Exception> {
            repository.getFeaturedEvents()
        }
    }

    @Test
    fun `getEventsByIds throws because repository is deprecated`() = runTest {
        assertFailsWith<Exception> {
            repository.getEventsByIds(listOf("event1"))
        }
    }
}
