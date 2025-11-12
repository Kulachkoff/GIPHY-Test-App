package com.chililabs.giphytest.ui.screens.search

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.chililabs.giphytest.domain.handler.Result
import com.chililabs.giphytest.domain.usecase.GetAutocompleteSuggestionsUseCase
import com.chililabs.giphytest.domain.usecase.GetTrendingSearchesUseCase
import com.chililabs.giphytest.utils.ext.emitAndWait
import com.chililabs.giphytest.utils.ext.invokeAndWait
import com.chililabs.giphytest.utils.network.NetworkMonitor
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class SearchViewModelTest {

    private lateinit var networkMonitor: NetworkMonitor
    private lateinit var getTrendingSearchesUseCase: GetTrendingSearchesUseCase
    private lateinit var getAutocompleteSuggestionsUseCase: GetAutocompleteSuggestionsUseCase
    private lateinit var viewModel: SearchViewModel
    private val testDispatcher = StandardTestDispatcher()
    private val networkStateFlow = MutableStateFlow(true)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        networkMonitor = mockk {
            every { isOnline } returns networkStateFlow
        }
        getTrendingSearchesUseCase = mockk()
        getAutocompleteSuggestionsUseCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    private fun createViewModel(): SearchViewModel {
        val savedStateHandle = mockk<SavedStateHandle>(relaxed = true).apply {
            every { this@apply["query"] as String? } returns null
            every { this@apply.set<String>("query", any()) } returns Unit
        }
        return SearchViewModel(
            networkMonitor = networkMonitor,
            savedStateHandle = savedStateHandle,
            getTrendingSearchesUseCase = getTrendingSearchesUseCase,
            getAutocompleteSuggestionsUseCase = getAutocompleteSuggestionsUseCase
        )
    }

    @Test
    fun `initial state is correct`() = runTest(testDispatcher) {
        // Given
        coEvery { getTrendingSearchesUseCase() } returns flowOf(Result.Success(emptyList()))
        coEvery { getAutocompleteSuggestionsUseCase(any()) } returns flowOf(Result.Success(emptyList()))

        // When
        invokeAndWait { viewModel = createViewModel() }

        // Then
        assertEquals("", viewModel.state.value.query)
        assertTrue(viewModel.state.value.isOnline)
    }

    @Test
    fun `QueryChanged event updates query after debounce`() = runTest(testDispatcher) {
        // Given
        coEvery { getTrendingSearchesUseCase() } returns flowOf(Result.Success(emptyList()))
        coEvery { getAutocompleteSuggestionsUseCase(any()) } returns flowOf(Result.Success(emptyList()))
        invokeAndWait { viewModel = createViewModel() }

        // When
        emitAndWait { viewModel.onEvent(SearchEvent.QueryChanged("test")) }

        // Then
        assertEquals("test", viewModel.state.value.query)
    }

    @Test
    fun `QueryChanged event debounces rapid changes`() = runTest(testDispatcher) {
        // Given
        coEvery { getTrendingSearchesUseCase() } returns flowOf(Result.Success(emptyList()))
        coEvery { getAutocompleteSuggestionsUseCase(any()) } returns flowOf(Result.Success(emptyList()))
        invokeAndWait { viewModel = createViewModel() }

        // When
        viewModel.apply {
            onEvent(SearchEvent.QueryChanged("t"))
            onEvent(SearchEvent.QueryChanged("te"))
            onEvent(SearchEvent.QueryChanged("tes"))
            emitAndWait { onEvent(SearchEvent.QueryChanged("test")) }
        }

        // Then
        assertEquals("test", viewModel.state.value.query)
    }

    @Test
    fun `QueryChanged event updates query`() = runTest(testDispatcher) {
        // Given
        coEvery { getTrendingSearchesUseCase() } returns flowOf(Result.Success(emptyList()))
        coEvery { getAutocompleteSuggestionsUseCase(any()) } returns flowOf(Result.Success(emptyList()))
        invokeAndWait { viewModel = createViewModel() }
        
        // When
        emitAndWait { viewModel.onEvent(SearchEvent.QueryChanged("test")) }

        // Then
        assertEquals("test", viewModel.state.value.query)
    }

    @Test
    fun `ItemClicked event sends NavigateToDetails effect`() = runTest(testDispatcher) {
        // Given
        coEvery { getTrendingSearchesUseCase() } returns flowOf(Result.Success(emptyList()))
        coEvery { getAutocompleteSuggestionsUseCase(any()) } returns flowOf(Result.Success(emptyList()))
        invokeAndWait { viewModel = createViewModel() }

        // When
        viewModel.onEvent(SearchEvent.ItemClicked("gif-id-123"))

        // Then
        viewModel.effects.test {
            val effect = awaitItem()
            assertTrue(effect is SearchEffect.NavigateToDetails)
            assertEquals("gif-id-123", (effect as SearchEffect.NavigateToDetails).gifId)
        }
    }

    @Test
    fun `network state updates isOnline`() = runTest(testDispatcher) {
        // Given
        coEvery { getTrendingSearchesUseCase() } returns flowOf(Result.Success(emptyList()))
        coEvery { getAutocompleteSuggestionsUseCase(any()) } returns flowOf(Result.Success(emptyList()))
        invokeAndWait { viewModel = createViewModel() }

        // When
        invokeAndWait { networkStateFlow.value = false }

        // Then
        assertFalse(viewModel.state.value.isOnline)

        // When
        invokeAndWait { networkStateFlow.value = true }

        // Then
        assertTrue(viewModel.state.value.isOnline)
    }

    @Test
    fun `loadTrendingSearches updates state with trending searches on success`() = runTest(testDispatcher) {
        // Given
        val trendingSearches = listOf("cats", "dogs", "funny", "memes")
        coEvery { getTrendingSearchesUseCase() } returns flowOf(Result.Success(trendingSearches))
        coEvery { getAutocompleteSuggestionsUseCase(any()) } returns flowOf(Result.Success(emptyList()))

        // When
        invokeAndWait { viewModel = createViewModel() }
        advanceUntilIdle()

        // Then
        assertEquals(trendingSearches, viewModel.state.value.trendingSearches)
    }

    @Test
    fun `loadTrendingSearches clears trending searches on error`() = runTest(testDispatcher) {
        // Given
        val exception = RuntimeException("Network error")
        coEvery { getTrendingSearchesUseCase() } returns flowOf(Result.Error(exception))
        coEvery { getAutocompleteSuggestionsUseCase(any()) } returns flowOf(Result.Success(emptyList()))

        // When
        invokeAndWait { viewModel = createViewModel() }
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.state.value.trendingSearches.isEmpty())
    }

    @Test
    fun `autocomplete suggestions update state on success`() = runTest(testDispatcher) {
        // Given
        coEvery { getTrendingSearchesUseCase() } returns flowOf(Result.Success(emptyList()))
        val suggestions = listOf("cats", "cat memes", "cat gifs")
        coEvery { getAutocompleteSuggestionsUseCase(any()) } returns flowOf(Result.Success(suggestions))
        invokeAndWait { viewModel = createViewModel() }

        // When
        viewModel.onEvent(SearchEvent.QueryChanged("cat"))
        advanceTimeBy(400) // AUTOCOMPLETE_DEBOUNCE_MS = 300
        advanceUntilIdle()

        // Then
        assertEquals(suggestions, viewModel.state.value.autocompleteSuggestions)
    }

    @Test
    fun `autocomplete suggestions clear on error`() = runTest(testDispatcher) {
        // Given
        coEvery { getTrendingSearchesUseCase() } returns flowOf(Result.Success(emptyList()))
        val exception = RuntimeException("Network error")
        coEvery { getAutocompleteSuggestionsUseCase(any()) } returns flowOf(Result.Error(exception))
        invokeAndWait { viewModel = createViewModel() }

        // When
        viewModel.onEvent(SearchEvent.QueryChanged("test"))
        advanceTimeBy(400) // AUTOCOMPLETE_DEBOUNCE_MS = 300
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.state.value.autocompleteSuggestions.isEmpty())
    }

    @Test
    fun `blank query clears autocomplete suggestions`() = runTest(testDispatcher) {
        // Given
        coEvery { getTrendingSearchesUseCase() } returns flowOf(Result.Success(emptyList()))
        val suggestions = listOf("cats", "cat memes")
        coEvery { getAutocompleteSuggestionsUseCase(any()) } returns flowOf(Result.Success(suggestions))
        invokeAndWait { viewModel = createViewModel() }

        // When - first set a query with suggestions
        viewModel.onEvent(SearchEvent.QueryChanged("cat"))
        advanceTimeBy(400)
        advanceUntilIdle()
        assertEquals(suggestions, viewModel.state.value.autocompleteSuggestions)

        // Then - clear the query
        viewModel.onEvent(SearchEvent.QueryChanged(""))
        advanceTimeBy(400)
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.state.value.autocompleteSuggestions.isEmpty())
    }

    @Test
    fun `LoadTrendingSearches event reloads trending searches`() = runTest(testDispatcher) {
        // Given
        val initialSearches = listOf("cats", "dogs")
        val updatedSearches = listOf("funny", "memes", "reactions")
        coEvery { getTrendingSearchesUseCase() } returnsMany listOf(
            flowOf(Result.Success(initialSearches)),
            flowOf(Result.Success(updatedSearches))
        )
        coEvery { getAutocompleteSuggestionsUseCase(any()) } returns flowOf(Result.Success(emptyList()))
        invokeAndWait { viewModel = createViewModel() }
        advanceUntilIdle()
        assertEquals(initialSearches, viewModel.state.value.trendingSearches)

        // When
        viewModel.onEvent(SearchEvent.LoadTrendingSearches)
        advanceUntilIdle()

        // Then
        assertEquals(updatedSearches, viewModel.state.value.trendingSearches)
    }
}