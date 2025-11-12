package com.chililabs.giphytest.domain.usecase

import com.chililabs.giphytest.domain.handler.Result
import com.chililabs.giphytest.domain.repo.TrendingSearchesRepository
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetAutocompleteSuggestionsUseCaseTest {

    private lateinit var repository: TrendingSearchesRepository
    private lateinit var useCase: GetAutocompleteSuggestionsUseCase
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        useCase = GetAutocompleteSuggestionsUseCase(repository, testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `invoke returns Success with autocomplete suggestions`() = runTest(testDispatcher) {
        // Given
        val query = "cat"
        val suggestions = listOf("cats", "cat memes", "cat gifs", "cat videos")
        coEvery { repository.getAutocompleteSuggestions(query) } returns flowOf(suggestions)

        // When
        val results = mutableListOf<Result<List<String>>>()
        useCase(query).collect { results.add(it) }

        // Then
        assertTrue(results.isNotEmpty())
        assertTrue(results.first() is Result.Success)
        val successResult = results.first() as Result.Success
        assertEquals(suggestions, successResult.data)
    }

    @Test
    fun `invoke returns Success with empty list when no suggestions`() = runTest(testDispatcher) {
        // Given
        val query = "xyz123"
        coEvery { repository.getAutocompleteSuggestions(query) } returns flowOf(emptyList())

        // When
        val results = mutableListOf<Result<List<String>>>()
        useCase(query).collect { results.add(it) }

        // Then
        assertTrue(results.isNotEmpty())
        assertTrue(results.first() is Result.Success)
        val successResult = results.first() as Result.Success
        assertTrue(successResult.data.isEmpty())
    }

    @Test
    fun `invoke returns Error when repository throws exception`() = runTest(testDispatcher) {
        // Given
        val query = "test"
        val exception = RuntimeException("Network error")
        coEvery { repository.getAutocompleteSuggestions(query) } returns flow {
            throw exception
        }

        // When
        val results = mutableListOf<Result<List<String>>>()
        useCase(query).collect { results.add(it) }

        // Then
        assertTrue(results.isNotEmpty())
        assertTrue(results.first() is Result.Error)
        val errorResult = results.first() as Result.Error
        assertEquals(exception, errorResult.error)
    }
}

