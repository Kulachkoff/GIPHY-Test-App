package com.chililabs.giphytest.domain.usecase

import com.chililabs.giphytest.domain.handler.Result
import com.chililabs.giphytest.domain.model.Gif
import com.chililabs.giphytest.domain.repo.FavoritesRepository
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
class GetFavoritesUseCaseTest {

    private lateinit var repository: FavoritesRepository
    private lateinit var useCase: GetFavoritesUseCase
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        useCase = GetFavoritesUseCase(repository, testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `invoke returns Success with favorites list`() = runTest(testDispatcher) {
        // Given
        val favorites = listOf(
            Gif(id = "1", title = "GIF 1"),
            Gif(id = "2", title = "GIF 2"),
            Gif(id = "3", title = "GIF 3")
        )
        coEvery { repository.getFavorites() } returns flowOf(favorites)

        // When
        val results = mutableListOf<Result<List<Gif>>>()
        useCase().collect { results.add(it) }

        // Then
        assertTrue(results.isNotEmpty())
        assertTrue(results.first() is Result.Success)
        val successResult = results.first() as Result.Success
        assertEquals(favorites, successResult.data)
    }

    @Test
    fun `invoke returns Success with empty list when no favorites`() = runTest(testDispatcher) {
        // Given
        coEvery { repository.getFavorites() } returns flowOf(emptyList())

        // When
        val results = mutableListOf<Result<List<Gif>>>()
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
        val exception = RuntimeException("Database error")
        coEvery { repository.getFavorites() } returns flow {
            throw exception
        }

        // When
        val results = mutableListOf<Result<List<Gif>>>()
        useCase().collect { results.add(it) }

        // Then
        assertTrue(results.isNotEmpty())
        assertTrue(results.first() is Result.Error)
        val errorResult = results.first() as Result.Error
        assertEquals(exception, errorResult.error)
    }
}

