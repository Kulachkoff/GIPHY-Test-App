package com.chililabs.giphytest.ui.screens.search

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.chililabs.giphytest.utils.ext.emitAndWait
import com.chililabs.giphytest.utils.ext.invokeAndWait
import com.chililabs.giphytest.utils.network.NetworkMonitor
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
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
class SearchViewModelTest {

    private lateinit var networkMonitor: NetworkMonitor
    private lateinit var viewModel: SearchViewModel
    private val testDispatcher = StandardTestDispatcher()
    private val networkStateFlow = MutableStateFlow(true)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        networkMonitor = mockk {
            every { isOnline } returns networkStateFlow
        }
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
            savedStateHandle = savedStateHandle
        )
    }

    @Test
    fun `initial state is correct`() = runTest(testDispatcher) {
        // When
        invokeAndWait { viewModel = createViewModel() }

        // Then
        assertEquals("", viewModel.state.value.query)
        assertTrue(viewModel.state.value.isOnline)
        assertFalse(viewModel.state.value.isLoadingFirstPage)
        assertEquals(null, viewModel.state.value.errorMessage)
    }

    @Test
    fun `QueryChanged event updates query after debounce`() = runTest(testDispatcher) {
        // Given
        invokeAndWait { viewModel = createViewModel() }

        // When
        emitAndWait { viewModel.onEvent(SearchEvent.QueryChanged("test")) }

        // Then
        assertEquals("test", viewModel.state.value.query)
    }

    @Test
    fun `QueryChanged event debounces rapid changes`() = runTest(testDispatcher) {
        // Given
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
    fun `QueryChanged event clears error message`() = runTest(testDispatcher) {
        // Given
        invokeAndWait { viewModel = createViewModel() }
        
        // When
        emitAndWait { viewModel.onEvent(SearchEvent.QueryChanged("test")) }

        // Then
        assertEquals(null, viewModel.state.value.errorMessage)
    }

    @Test
    fun `ItemClicked event sends NavigateToDetails effect`() = runTest(testDispatcher) {
        // Given
        invokeAndWait { viewModel = createViewModel() }

        // When
        viewModel.onEvent(SearchEvent.ItemClicked("gif-id-123"))

        // Then
        viewModel.effects.test {
            val effect = awaitItem()
            assertTrue(effect is SearchEffect.NavigateToDetails)
            assertEquals("gif-id-123", (effect as SearchEffect.NavigateToDetails).id)
        }
    }

    @Test
    fun `Retry event re-emits current query`() = runTest(testDispatcher) {
        // Given
        invokeAndWait { viewModel = createViewModel() }
        
        emitAndWait { viewModel.onEvent(SearchEvent.QueryChanged("test")) }

        val initialQuery = viewModel.state.value.query

        // When
        emitAndWait { viewModel.onEvent(SearchEvent.Retry) }

        // Then
        assertEquals(initialQuery, viewModel.state.value.query)
    }

    @Test
    fun `network state updates isOnline`() = runTest(testDispatcher) {
        // Given
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
}