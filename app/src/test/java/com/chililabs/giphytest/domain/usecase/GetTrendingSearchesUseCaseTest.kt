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
class GetTrendingSearchesUseCaseTest {

    private lateinit var repository: TrendingSearchesRepository
    private lateinit var useCase: GetTrendingSearchesUseCase
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        useCase = GetTrendingSearchesUseCase(repository, testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `invoke returns Success with trending searches list`() = runTest(testDispatcher) {
        // Given
        val trendingSearches = listOf("cats", "dogs", "funny", "memes", "reactions")
        coEvery { repository.getTrendingSearches() } returns flowOf(trendingSearches)

        // When
        val results = mutableListOf<Result<List<String>>>()
        useCase().collect { results.add(it) }

        // Then
        assertTrue(results.isNotEmpty())
        assertTrue(results.first() is Result.Success)
        val successResult = results.first() as Result.Success
        assertEquals(trendingSearches, successResult.data)
    }

    @Test
    fun `invoke returns Success with empty list when no trending searches`() = runTest(testDispatcher) {
        // Given
        coEvery { repository.getTrendingSearches() } returns flowOf(emptyList())

        // When
        val results = mutableListOf<Result<List<String>>>()
        useCase().collect { results.add(it) }

        // Then
        assertTrue(results.isNotEmpty())
        assertTrue(results.first() is Result.Success)
        val successResult = results.first() as Result.Success
        assertTrue(successResult.data.isEmpty())
    }

    @Test
    fun `invoke returns Error when repository throws exception`() = runTest(testDispatcher) {
        // Given
        val exception = RuntimeException("Network error")
        coEvery { repository.getTrendingSearches() } returns flow {
            throw exception
        }

        // When
        val results = mutableListOf<Result<List<String>>>()
        useCase().collect { results.add(it) }

        // Then
        assertTrue(results.isNotEmpty())
        assertTrue(results.first() is Result.Error)
        val errorResult = results.first() as Result.Error
        assertEquals(exception, errorResult.error)
    }
}

