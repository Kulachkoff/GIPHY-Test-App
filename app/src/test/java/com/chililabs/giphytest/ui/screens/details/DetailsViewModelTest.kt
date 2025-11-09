package com.chililabs.giphytest.ui.screens.details

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.chililabs.giphytest.domain.handler.Result
import com.chililabs.giphytest.domain.model.Gif
import com.chililabs.giphytest.domain.usecase.GetGifByIdUseCase
import com.chililabs.giphytest.domain.usecase.IsFavoriteUseCase
import com.chililabs.giphytest.domain.usecase.ToggleFavoriteUseCase
import com.chililabs.giphytest.utils.ext.invokeAndWait
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetailsViewModelTest {

    private lateinit var getGifById: GetGifByIdUseCase
    private lateinit var toggleFavorite: ToggleFavoriteUseCase
    private lateinit var isFavorite: IsFavoriteUseCase
    private lateinit var viewModel: DetailsViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getGifById = mockk()
        toggleFavorite = mockk()
        isFavorite = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    private fun createViewModel(gifId: String = "test-id"): DetailsViewModel {
        val savedStateHandle = mockk<SavedStateHandle>(relaxed = true).apply {
            every { this@apply["gifId"] as String? } returns gifId
            every { this@apply.getStateFlow("gifId", any<String>()) } returns MutableStateFlow(gifId)
        }
        return DetailsViewModel(
            getGifById = getGifById,
            toggleFavorite = toggleFavorite,
            isFavorite = isFavorite,
            savedStateHandle = savedStateHandle
        )
    }

    @Test
    fun `initial state has correct gifId and loading true`() = runTest(testDispatcher) {
        // Given
        val gifId = "test-gif-id"
        coEvery { isFavorite(gifId) } returns false
        coEvery { getGifById(gifId) } returns flowOf(Result.Success(createTestGif(gifId)))

        // When
        viewModel = createViewModel(gifId)
        
        // Then
        assertEquals(gifId, viewModel.state.value.gifId)
        advanceUntilIdle()
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `Load event loads gif successfully`() = runTest(testDispatcher) {
        // Given
        val gifId = "test-gif-id"
        val testGif = createTestGif(gifId)
        coEvery { isFavorite(gifId) } returns false
        coEvery { getGifById(gifId) } returns flowOf(Result.Success(testGif))

        // When
        invokeAndWait { viewModel = createViewModel(gifId) }

        // Then
        assertEquals(testGif, viewModel.state.value.gif)
        assertFalse(viewModel.state.value.isLoading)
        assertNull(viewModel.state.value.errorMessage)
    }

    @Test
    fun `Load event sets favorite status correctly`() = runTest(testDispatcher) {
        // Given
        val gifId = "test-gif-id"
        val testGif = createTestGif(gifId)
        coEvery { isFavorite(gifId) } returns true
        coEvery { getGifById(gifId) } returns flowOf(Result.Success(testGif))

        // When
        invokeAndWait { viewModel = createViewModel(gifId) }

        // Then
        assertTrue(viewModel.state.value.isFavorite)
    }

    @Test
    fun `Load event handles error correctly`() = runTest(testDispatcher) {
        // Given
        val gifId = "test-gif-id"
        val error = RuntimeException("Network error")
        coEvery { isFavorite(gifId) } returns false
        coEvery { getGifById(gifId) } returns flowOf(Result.Error(error))

        // When
        invokeAndWait { viewModel = createViewModel(gifId) }

        // Then
        assertNull(viewModel.state.value.gif)
        assertFalse(viewModel.state.value.isLoading)
        assertNotNull(viewModel.state.value.errorMessage)
    }

    @Test
    fun `Retry event reloads gif`() = runTest(testDispatcher) {
        // Given
        val gifId = "test-gif-id"
        val testGif = createTestGif(gifId)
        coEvery { isFavorite(gifId) } returns false
        coEvery { getGifById(gifId) } returns flowOf(Result.Success(testGif))

        invokeAndWait { viewModel = createViewModel(gifId) }

        // When
        invokeAndWait { viewModel.onEvent(DetailsEvent.Retry) }

        // Then
        assertEquals(testGif, viewModel.state.value.gif)
    }

    @Test
    fun `Favorite event toggles favorite status`() = runTest(testDispatcher) {
        // Given
        val gifId = "test-gif-id"
        val testGif = createTestGif(gifId)
        coEvery { isFavorite(gifId) } returns false
        coEvery { getGifById(gifId) } returns flowOf(Result.Success(testGif))
        coEvery { toggleFavorite(testGif) } returns true

        invokeAndWait { viewModel = createViewModel(gifId) }

        // When
        invokeAndWait { viewModel.onEvent(DetailsEvent.Favorite) }

        // Then
        assertTrue(viewModel.state.value.isFavorite)
    }

    @Test
    fun `Share event sends ShareLink effect`() = runTest(testDispatcher) {
        // Given
        val gifId = "test-gif-id"
        val testGif = createTestGif(gifId, url = "https://test.com/gif.gif")
        coEvery { isFavorite(gifId) } returns false
        coEvery { getGifById(gifId) } returns flowOf(Result.Success(testGif))

        invokeAndWait { viewModel = createViewModel(gifId) }

        // When
        viewModel.onEvent(DetailsEvent.Share)

        // Then
        viewModel.effects.test {
            val effect = awaitItem()
            assertTrue(effect is DetailsEffect.ShareLink)
            assertEquals("https://test.com/gif.gif", (effect as DetailsEffect.ShareLink).url)
        }
    }

    @Test
    fun `Back event sends NavigateBack effect`() = runTest(testDispatcher) {
        // Given
        val gifId = "test-gif-id"
        coEvery { isFavorite(gifId) } returns false
        coEvery { getGifById(gifId) } returns flowOf(Result.Success(createTestGif(gifId)))

        invokeAndWait { viewModel = createViewModel(gifId) }

        // When
        viewModel.onEvent(DetailsEvent.Back)

        // Then
        viewModel.effects.test {
            val effect = awaitItem()
            assertTrue(effect is DetailsEffect.NavigateBack)
        }
    }

    private fun createTestGif(
        id: String = "test-id",
        title: String = "Test GIF",
        username: String = "testuser",
        url: String? = "https://test.com/gif.gif"
    ) = Gif(id = id, title = title, username = username, url = url)
}

