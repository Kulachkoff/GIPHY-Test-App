package com.chililabs.giphytest.domain.usecase

import com.chililabs.giphytest.domain.model.AppTheme
import com.chililabs.giphytest.domain.repo.AppSettingsRepository
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetThemeUseCaseTest {

    private lateinit var repository: AppSettingsRepository
    private lateinit var useCase: GetThemeUseCase
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        useCase = GetThemeUseCase(repository, testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `invoke returns Light theme when repository emits Light`() = runTest(testDispatcher) {
        // Given
        coEvery { repository.theme } returns flowOf(AppTheme.Light)

        // When
        val results = mutableListOf<AppTheme>()
        useCase().collect { results.add(it) }

        // Then
        assertEquals(1, results.size)
        assertEquals(AppTheme.Light, results.first())
    }

    @Test
    fun `invoke returns Dark theme when repository emits Dark`() = runTest(testDispatcher) {
        // Given
        coEvery { repository.theme } returns flowOf(AppTheme.Dark)

        // When
        val results = mutableListOf<AppTheme>()
        useCase().collect { results.add(it) }

        // Then
        assertEquals(1, results.size)
        assertEquals(AppTheme.Dark, results.first())
    }

    @Test
    fun `invoke returns System theme when repository emits System`() = runTest(testDispatcher) {
        // Given
        coEvery { repository.theme } returns flowOf(AppTheme.System)

        // When
        val results = mutableListOf<AppTheme>()
        useCase().collect { results.add(it) }

        // Then
        assertEquals(1, results.size)
        assertEquals(AppTheme.System, results.first())
    }

    @Test
    fun `invoke emits multiple values when repository emits multiple values`() = runTest(testDispatcher) {
        // Given
        coEvery { repository.theme } returns flowOf(
            AppTheme.System,
            AppTheme.Light,
            AppTheme.Dark
        )

        // When
        val results = mutableListOf<AppTheme>()
        useCase().collect { results.add(it) }

        // Then
        assertEquals(3, results.size)
        assertEquals(AppTheme.System, results[0])
        assertEquals(AppTheme.Light, results[1])
        assertEquals(AppTheme.Dark, results[2])
    }
}

