package com.chililabs.giphytest.domain.usecase

import com.chililabs.giphytest.domain.model.AppTheme
import com.chililabs.giphytest.domain.repo.AppSettingsRepository
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SetThemeUseCaseTest {

    private lateinit var repository: AppSettingsRepository
    private lateinit var useCase: SetThemeUseCase
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        useCase = SetThemeUseCase(repository, testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `invoke calls repository setTheme with Light theme`() = runTest(testDispatcher) {
        // Given
        coEvery { repository.setTheme(any()) } returns Unit

        // When
        useCase(AppTheme.Light)

        // Then
        coVerify(exactly = 1) { repository.setTheme(AppTheme.Light) }
    }

    @Test
    fun `invoke calls repository setTheme with Dark theme`() = runTest(testDispatcher) {
        // Given
        coEvery { repository.setTheme(any()) } returns Unit

        // When
        useCase(AppTheme.Dark)

        // Then
        coVerify(exactly = 1) { repository.setTheme(AppTheme.Dark) }
    }

    @Test
    fun `invoke calls repository setTheme with System theme`() = runTest(testDispatcher) {
        // Given
        coEvery { repository.setTheme(any()) } returns Unit

        // When
        useCase(AppTheme.System)

        // Then
        coVerify(exactly = 1) { repository.setTheme(AppTheme.System) }
    }

    @Test
    fun `invoke calls repository setTheme multiple times with different themes`() = runTest(testDispatcher) {
        // Given
        coEvery { repository.setTheme(any()) } returns Unit

        // When
        useCase(AppTheme.Light)
        useCase(AppTheme.Dark)
        useCase(AppTheme.System)

        // Then
        coVerify(exactly = 1) { repository.setTheme(AppTheme.Light) }
        coVerify(exactly = 1) { repository.setTheme(AppTheme.Dark) }
        coVerify(exactly = 1) { repository.setTheme(AppTheme.System) }
    }
}

