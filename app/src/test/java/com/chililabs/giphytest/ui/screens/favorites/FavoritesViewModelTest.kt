package com.chililabs.giphytest.ui.screens.favorites

import app.cash.turbine.test
import com.chililabs.giphytest.domain.handler.Result
import com.chililabs.giphytest.domain.model.Gif
import com.chililabs.giphytest.domain.usecase.GetFavoritesUseCase
import com.chililabs.giphytest.utils.ext.invokeAndWait
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesViewModelTest {

    private lateinit var getFavorites: GetFavoritesUseCase
    private lateinit var viewModel: FavoritesViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getFavorites = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    private fun createViewModel(): FavoritesViewModel {
        return FavoritesViewModel(getFavorites)
    }

    @Test
    fun `initial state loads favorites successfully`() = runTest(testDispatcher) {
        // Given
        val favorites = listOf(
            Gif(id = "1", title = "GIF 1"),
            Gif(id = "2", title = "GIF 2")
        )
        coEvery { getFavorites() } returns flowOf(Result.Success(favorites))

        // When
        invokeAndWait { viewModel = createViewModel() }

        // Then
        assertEquals(favorites, viewModel.state.value.items)
        assertFalse(viewModel.state.value.isEmpty)
    }

    @Test
    fun `initial state shows empty when no favorites`() = runTest(testDispatcher) {
        // Given
        coEvery { getFavorites() } returns flowOf(Result.Success(emptyList()))

        // When
        invokeAndWait { viewModel = createViewModel() }

        // Then
        assertTrue(viewModel.state.value.items.isEmpty())
        assertTrue(viewModel.state.value.isEmpty)
    }

    @Test
    fun `initial state handles error correctly`() = runTest(testDispatcher) {
        // Given
        val error = RuntimeException("Database error")
        coEvery { getFavorites() } returns flowOf(Result.Error(error))

        // When
        invokeAndWait { viewModel = createViewModel() }

        // Then
        assertTrue(viewModel.state.value.items.isEmpty())
        assertTrue(viewModel.state.value.isEmpty)
    }

    @Test
    fun `ItemClicked event sends NavigateToDetails effect`() = runTest(testDispatcher) {
        // Given
        coEvery { getFavorites() } returns flowOf(Result.Success(emptyList()))
        invokeAndWait { viewModel = createViewModel() }

        // When
        viewModel.onEvent(FavoritesEvent.ItemClicked("gif-id-123"))

        // Then
        viewModel.effects.test {
            val effect = awaitItem()
            assertTrue(effect is FavoritesEffect.NavigateToDetails)
            assertEquals("gif-id-123", (effect as FavoritesEffect.NavigateToDetails).gifId)
        }
    }

    @Test
    fun `Refresh event does nothing`() = runTest(testDispatcher) {
        // Given
        coEvery { getFavorites() } returns flowOf(Result.Success(emptyList()))
        invokeAndWait { viewModel = createViewModel() }

        val initialState = viewModel.state.value

        // When
        invokeAndWait { viewModel.onEvent(FavoritesEvent.Refresh) }

        // Then
        assertEquals(initialState, viewModel.state.value)
    }
}

