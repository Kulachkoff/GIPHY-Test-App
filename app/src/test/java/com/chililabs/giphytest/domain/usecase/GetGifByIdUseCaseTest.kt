package com.chililabs.giphytest.domain.usecase

import com.chililabs.giphytest.domain.handler.Result
import com.chililabs.giphytest.domain.model.Gif
import com.chililabs.giphytest.domain.repo.GifsRepository
import com.chililabs.giphytest.utils.exception.GifNotFoundException
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
class GetGifByIdUseCaseTest {

    private lateinit var repository: GifsRepository
    private lateinit var useCase: GetGifByIdUseCase
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        useCase = GetGifByIdUseCase(repository, testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `invoke returns Success when gif is found`() = runTest(testDispatcher) {
        // Given
        val gifId = "test-gif-id"
        val expectedGif = Gif(
            id = gifId,
            title = "Test GIF",
            username = "testuser",
            url = "https://test.com/gif.gif"
        )
        coEvery { repository.getById(gifId) } returns flowOf(expectedGif)

        // When
        val results = mutableListOf<Result<Gif>>()
        useCase(gifId).collect { results.add(it) }
        
        // Then
        assertTrue(results.isNotEmpty())
        assertTrue(results.first() is Result.Success)
        val successResult = results.first() as Result.Success
        assertEquals(expectedGif, successResult.data)
    }

    @Test
    fun `invoke returns Error when gif is not found`() = runTest(testDispatcher) {
        // Given
        val gifId = "non-existent-id"
        coEvery { repository.getById(gifId) } returns flowOf(null)

        // When
        val results = mutableListOf<Result<Gif>>()
        useCase(gifId).collect { results.add(it) }

        // Then
        assertTrue(results.isNotEmpty())
        assertTrue(results.first() is Result.Error)
        val errorResult = results.first() as Result.Error
        assertTrue(errorResult.error is GifNotFoundException)
    }

    @Test
    fun `invoke returns Error when repository throws exception`() = runTest(testDispatcher) {
        // Given
        val gifId = "test-id"
        val exception = RuntimeException("Network error")
        coEvery { repository.getById(gifId) } returns flow {
            throw exception
        }

        // When
        val results = mutableListOf<Result<Gif>>()
        useCase(gifId).collect { results.add(it) }

        // Then
        assertTrue(results.isNotEmpty())
        assertTrue(results.first() is Result.Error)
        val errorResult = results.first() as Result.Error
        assertEquals(exception, errorResult.error)
    }
}

