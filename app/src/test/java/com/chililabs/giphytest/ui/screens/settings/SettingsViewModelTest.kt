package com.chililabs.giphytest.ui.screens.settings

import com.chililabs.giphytest.domain.model.AppTheme
import com.chililabs.giphytest.domain.usecase.GetThemeUseCase
import com.chililabs.giphytest.domain.usecase.SetThemeUseCase
import com.chililabs.giphytest.utils.ext.emitAndWait
import com.chililabs.giphytest.utils.ext.invokeAndWait
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private lateinit var getThemeUseCase: GetThemeUseCase
    private lateinit var setThemeUseCase: SetThemeUseCase
    private lateinit var viewModel: SettingsViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getThemeUseCase = mockk()
        setThemeUseCase = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    private fun createViewModel(): SettingsViewModel {
        return SettingsViewModel(
            getThemeUseCase = getThemeUseCase,
            setThemeUseCase = setThemeUseCase
        )
    }

    @Test
    fun `initial state has System theme`() = runTest(testDispatcher) {
        // Given
        coEvery { getThemeUseCase() } returns flowOf(AppTheme.System)

        // When
        invokeAndWait { viewModel = createViewModel() }
        advanceUntilIdle()

        // Then
        assertEquals(AppTheme.System, viewModel.state.value.theme)
    }

    @Test
    fun `initial state updates when theme flow emits Light`() = runTest(testDispatcher) {
        // Given
        coEvery { getThemeUseCase() } returns flowOf(AppTheme.Light)

        // When
        invokeAndWait { viewModel = createViewModel() }
        advanceUntilIdle()

        // Then
        assertEquals(AppTheme.Light, viewModel.state.value.theme)
    }

    @Test
    fun `initial state updates when theme flow emits Dark`() = runTest(testDispatcher) {
        // Given
        coEvery { getThemeUseCase() } returns flowOf(AppTheme.Dark)

        // When
        invokeAndWait { viewModel = createViewModel() }
        advanceUntilIdle()

        // Then
        assertEquals(AppTheme.Dark, viewModel.state.value.theme)
    }

    @Test
    fun `state updates when theme changes from System to Light`() = runTest(testDispatcher) {
        // Given
        val themeFlow = kotlinx.coroutines.flow.MutableStateFlow(AppTheme.System)
        coEvery { getThemeUseCase() } returns themeFlow
        invokeAndWait { viewModel = createViewModel() }
        advanceUntilIdle()
        assertEquals(AppTheme.System, viewModel.state.value.theme)

        // When
        themeFlow.value = AppTheme.Light
        advanceUntilIdle()

        // Then
        assertEquals(AppTheme.Light, viewModel.state.value.theme)
    }

    @Test
    fun `ThemeChanged event calls setThemeUseCase with Light theme`() = runTest(testDispatcher) {
        // Given
        coEvery { getThemeUseCase() } returns flowOf(AppTheme.System)
        invokeAndWait { viewModel = createViewModel() }
        advanceUntilIdle()

        // When
        emitAndWait { viewModel.onEvent(SettingsEvent.ThemeChanged(AppTheme.Light)) }
        advanceUntilIdle()

        // Then
        coVerify(exactly = 1) { setThemeUseCase(AppTheme.Light) }
    }

    @Test
    fun `ThemeChanged event calls setThemeUseCase with Dark theme`() = runTest(testDispatcher) {
        // Given
        coEvery { getThemeUseCase() } returns flowOf(AppTheme.System)
        invokeAndWait { viewModel = createViewModel() }
        advanceUntilIdle()

        // When
        emitAndWait { viewModel.onEvent(SettingsEvent.ThemeChanged(AppTheme.Dark)) }
        advanceUntilIdle()

        // Then
        coVerify(exactly = 1) { setThemeUseCase(AppTheme.Dark) }
    }

    @Test
    fun `ThemeChanged event calls setThemeUseCase with System theme`() = runTest(testDispatcher) {
        // Given
        coEvery { getThemeUseCase() } returns flowOf(AppTheme.Light)
        invokeAndWait { viewModel = createViewModel() }
        advanceUntilIdle()

        // When
        emitAndWait { viewModel.onEvent(SettingsEvent.ThemeChanged(AppTheme.System)) }
        advanceUntilIdle()

        // Then
        coVerify(exactly = 1) { setThemeUseCase(AppTheme.System) }
    }

    @Test
    fun `state updates reactively when theme flow emits multiple values`() = runTest(testDispatcher) {
        // Given
        val themeFlow = kotlinx.coroutines.flow.MutableStateFlow(AppTheme.System)
        coEvery { getThemeUseCase() } returns themeFlow
        invokeAndWait { viewModel = createViewModel() }
        advanceUntilIdle()

        // When - change theme multiple times
        themeFlow.value = AppTheme.Light
        advanceUntilIdle()
        assertEquals(AppTheme.Light, viewModel.state.value.theme)

        themeFlow.value = AppTheme.Dark
        advanceUntilIdle()
        assertEquals(AppTheme.Dark, viewModel.state.value.theme)

        themeFlow.value = AppTheme.System
        advanceUntilIdle()

        // Then
        assertEquals(AppTheme.System, viewModel.state.value.theme)
    }
}

